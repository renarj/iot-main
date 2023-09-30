package com.oberasoftware.trainautomation.rocoz21;

import com.google.common.collect.Sets;
import com.oberasoftware.iot.core.exceptions.IOTException;
import com.oberasoftware.iot.core.model.IotThing;
import com.oberasoftware.trainautomation.CommandCenter;
import com.oberasoftware.trainautomation.TrainCommand;
import com.oberasoftware.trainautomation.rocoz21.commandhandlers.Z21CommandHandler;
import com.oberasoftware.trainautomation.rocoz21.commands.RegisterBroadcastCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.oberasoftware.trainautomation.rocoz21.commands.BroadcastCommand.*;

@Component
public class Z21CommandCenter implements CommandCenter {
    private static final Logger LOG = LoggerFactory.getLogger(Z21CommandCenter.class);

    public static final String Z_21 = "z21";

    @Autowired
    private Z21Connector z21Connector;

    @Autowired
    private Z21KeepAlive z21KeepAlive;

    @Autowired
    private List<Z21CommandHandler> commandHandlers;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    public String getId() {
        return Z_21;
    }

    @Override
    public void connect(IotThing commandCenter) {
        var z21Host = commandCenter.getProperty("Z21_HOST");
        var z21Port = Integer.parseInt(commandCenter.getProperty("Z21_PORT"));

        try {
            LOG.info("Iot Thing data has Z21 host: {} and port: {}", z21Host, z21Port);
            z21Connector.connect(z21Host, z21Port);

            LOG.info("Registering to Z21 for broadcast messages");
            z21Connector.send(new RegisterBroadcastCommand(Sets.newHashSet(INFO_MESSAGES, LOCO_INFO_SUBSCRIBE_MODIFIED,
                    LAN_CAN_MESSAGES, LOCONET_MESSAGES)));

            LOG.info("Starting keep alive for Z21");
            executorService.submit(z21KeepAlive);
        } catch (IOTException e) {
            LOG.error("Could not connect to Z21 command center on host: " + z21Host + " and port: " + z21Port, e);
        }
    }

    @Override
    public void handleCommand(TrainCommand command) {
        var commandHandler = commandHandlers.stream().filter(ch -> ch.supportsCommand(command)).findFirst();
        commandHandler.ifPresentOrElse(ch -> {
            ch.action(command);
        }, () -> {
            LOG.error("Could not find command handler for command: {}", command);
        });
    }
}
