package com.oberasoftware.robo.hcontrol;

import com.google.common.base.Stopwatch;
import com.oberasoftware.home.client.api.CommandServiceClient;
import com.oberasoftware.robo.api.commands.BasicCommand;
import com.oberasoftware.robo.core.model.BasicCommandBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.oberasoftware.robo.hcontrol.InputScaler.convert;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class HandController {
    private static final Logger LOG = getLogger(HandController.class);

    private final ControlState state = new ControlState();

    private final AtomicBoolean cameraMode = new AtomicBoolean(false);

    @Value("${robotId}")
    private String robotId;

    @Autowired
    private CommandServiceClient commandServiceClient;

    @PostConstruct
    public void init() {
        ADS1115JoystickDriver d = new ADS1115JoystickDriver();
        d.activate();

        LOG.info("Driver activated, starting port listen");

        d.getPorts().forEach(p -> p.listen(e -> {
            Double voltage = e.getRaw();
            LOG.info("Converting voltage: {} for port: {}", voltage, p);
            int scaled = p.isReversed() ? -convert(voltage) : convert(voltage);


            boolean mode = cameraMode.get();
            if(p.getAxis().equalsIgnoreCase("tilt") && Math.abs(scaled) > 100) {
                mode = !mode;
                cameraMode.set(mode);
                LOG.info("Flipping camera control mode to: {}", mode);
            }

            state.setState(p.getAxis(), scaled);

            LOG.info("Received a value: {} for axis: {}", scaled, p.getAxis());

            BasicCommandBuilder commandBuilder = BasicCommandBuilder.create(robotId)
                    .item("navigation")
                    .label("input")
                    .property("cameraMode", mode ? "1.0" : "0.0");

            for(String axis : state.getInput().getInputAxis()) {
                commandBuilder.property(axis, state.getInput().getAxis(axis).toString());
            }

            BasicCommand command = commandBuilder.build();

            Stopwatch stopwatch = Stopwatch.createStarted();
            commandServiceClient.sendCommand(command);
            LOG.info("Sending command: {} completed in: {} ms.", command, stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }));
    }
}
