package com.oberasoftware.robo.hcontrol;

import com.oberasoftware.home.client.api.CommandServiceClient;
import com.oberasoftware.robo.api.commands.BasicCommand;
import com.oberasoftware.robo.core.model.BasicCommandBuilder;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.ReentrantLock;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class HandController {
    private static final Logger LOG = getLogger(HandController.class);

    private static final double MAX_SCALE = 100.0;
    private static final double MAX_VOLTAGE = 4.0;
    private static final double HALF_SCALE = 50.0;
    private static final double SCALE_FACTOR = 2.0;

    private final ReentrantLock lock = new ReentrantLock();

    private final ControlState state = new ControlState();

    @Value("#{target.robot}")
    private String robotId;

    @Autowired
    private CommandServiceClient commandServiceClient;

    @PostConstruct
    public void init() {
        ADS1115JoystickDriver d = new ADS1115JoystickDriver();
        d.activate();

        d.getPorts().forEach(p -> p.listen(e -> {
            Double voltage = e.getRaw();
            int scaled = convert(voltage);
            state.setState(p.getAxis(), scaled);

            LOG.info("Received a value: {} for axis: {}", scaled, p.getAxis());

            BasicCommandBuilder commandBuilder = BasicCommandBuilder.create(robotId)
                    .item("navigation")
                    .label("input");

            for(String axis : state.getInput().getInputAxis()) {
                commandBuilder.property(axis, state.getInput().getAxis(axis).toString());
            }

            BasicCommand command = commandBuilder.build();
            LOG.info("Sending command: {}", command);
            commandServiceClient.sendAsyncCommand(command);
        }));
    }

    private static boolean isButtonPressed(Double val) {
        return val > MAX_VOLTAGE;
    }

    private static final int convert(Double val) {
        double factor = MAX_SCALE / MAX_VOLTAGE;

        Double c = val * factor;

        if(c < HALF_SCALE) {
            return (int)(-(HALF_SCALE - c) * SCALE_FACTOR);
        } else {
            return (int)((c - HALF_SCALE) * SCALE_FACTOR);
        }
    }
}
