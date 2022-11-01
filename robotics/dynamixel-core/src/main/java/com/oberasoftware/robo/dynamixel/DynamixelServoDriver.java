package com.oberasoftware.robo.dynamixel;

import com.google.common.base.Stopwatch;
import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.robotics.Robot;
import com.oberasoftware.iot.core.robotics.commands.*;
import com.oberasoftware.iot.core.robotics.servo.Servo;
import com.oberasoftware.iot.core.robotics.servo.ServoCommand;
import com.oberasoftware.iot.core.robotics.servo.ServoDriver;
import com.oberasoftware.robo.core.ConverterUtil;
import com.oberasoftware.robo.core.commands.RebootCommand;
import com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv1.DynamixelV1ReturnPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2ReturnPacket;
import com.oberasoftware.robo.dynamixel.protocolv2.handlers.AbstractDynamixelHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.oberasoftware.robo.dynamixel.protocolv2.DynamixelV2CommandPacket.bb2hex;

/**
 * @author Renze de Vries
 */
@Component
@Primary
public class DynamixelServoDriver implements ServoDriver {
    private static final Logger LOG = LoggerFactory.getLogger(DynamixelServoDriver.class);

    private static final int MAX_ID = 240;
    public static final String PORT = "port";
    public static final int OPENCR_MODEL = 116;

    @Autowired
    private SerialDynamixelConnector connector;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private BulkWriteMovementHandler bulkWriteMovementHandler;

    @Autowired
    private DynamixelServoMovementHandler servoMovementHandler;


    @Value("${protocol.v2.enabled:false}")
    private boolean v2Enabled;

    @Autowired
    private DynamixelTorgueManager torgueManager;

    @Autowired
    private LocalEventBus eventBus;

    private Map<String, Servo> servos = new HashMap<>();

    private String portName;

    @Override
    public void activate(Robot robot, Map<String, String> properties) {
        this.portName = properties.get(PORT);
        connector.connect(portName);

        boolean motorsFound;
        if(properties.containsKey("motors")) {
            String motorIdentifiers = properties.get("motors");

            motorsFound = !scan(parseMotorIdentifiers(motorIdentifiers), true).isEmpty();
        } else {
            motorsFound = !scan(true).isEmpty();
        }

        if(!motorsFound) {
            LOG.error("Could not find any servos");
        }
    }

    private IntStream parseMotorIdentifiers(String motorIdentifiers) {
        String[] motorIds = motorIdentifiers.split(",");
        int[] servoIds = new int[motorIds.length];
        for(int i=0; i<motorIds.length; i++) {
            String id = motorIds[i].trim();
            if(id.length() > 0) {
                servoIds[i] = ConverterUtil.toSafeInt(id);
            }
        }
        return IntStream.of(servoIds);
    }

    @Override
    public void shutdown() {
        this.connector.disconnect();
    }

    public List<Servo> scan() {
        return scan(false);
    }

    public List<Servo> scan(boolean register) {
        return scan(IntStream.range(1, MAX_ID), register);
    }

    public List<Servo> scan(IntStream motorRange, boolean register) {
        LOG.info("Starting servo scan from stream: {}", motorRange);

        List<Servo> foundServos = new ArrayList<>();
        motorRange.forEach((m) -> {
            LOG.debug("Sending ping to motor: {}", m);
            byte[] data = new DynamixelV1CommandPacket(DynamixelInstruction.PING, m).build();
            if(v2Enabled) {
                data = new DynamixelV2CommandPacket(DynamixelInstruction.PING, m).build();
            }
            Stopwatch w = Stopwatch.createStarted();
            byte[] received = connector.sendAndReceive(data);
            w.stop();

            if(received != null && received.length > 0) {
                try {
                    LOG.debug("Received: {} in {} ms. for servo: {}", bb2hex(received), w.elapsed(TimeUnit.MILLISECONDS), m);
                    DynamixelReturnPacket packet;
                    if(v2Enabled) {
                        packet = new DynamixelV2ReturnPacket(received);
                    } else {
                        packet = new DynamixelV1ReturnPacket(received);
                    }
                    if (packet.getErrorCode() == 0) {
                        byte[] params = packet.getParameters();

                        int modelNr = ConverterUtil.byteToInt(params[0], params[1]);
                        LOG.info("Ping received from Servo: {} with data: {} modelNr: {}", m, bb2hex(received), modelNr);

                        if(modelNr != OPENCR_MODEL) {
                            DynamixelServo servo = applicationContext.getBean(DynamixelServo.class, m, modelNr);
                            foundServos.add(servo);

                            if(register) {
                                servos.put(Integer.toString(m), servo);
                            }
                        } else {
                            LOG.info("Found an Open CR board on ID: {}", m);
                        }
                    } else {
                        LOG.info("Servo: {} packet error: {} received: {} reason: {}", m,
                                packet.getErrorCode(), bb2hex(received), AbstractDynamixelHandler.getErrorReason(packet.getErrorCode()));
                    }
                } catch(IllegalArgumentException e) {
                    LOG.error("Could not read servo response on ping");
                }
            } else {
                LOG.info("No Servo detected on ID: {} in {} ms.", m, w.elapsed(TimeUnit.MILLISECONDS));
            }
        });

        LOG.info("Servos found: {}", servos.keySet());
        return foundServos;

    }

//    private int getModelInformation(int motorId) {
//        byte[] modelInfoData = new DynamixelV2CommandPacket(DynamixelInstruction.READ_DATA, motorId)
//                .addInt16Bit(DynamixelV2Address.MODEL_INFORMATION, 0x02).build();
//        byte[] received = connector.sendAndReceive(modelInfoData);
//        DynamixelReturnPacket packet;
//
//        if(v2Enabled) {
//            packet = new DynamixelV2ReturnPacket(received);
//        } else {
//            packet = new DynamixelV1ReturnPacket(received);
//        }
//
//
//    }

    @Override
    public boolean supportsCommand(ServoCommand servoCommand) {

        return false;
    }

    @Override
    public boolean sendCommand(ServoCommand servoCommand) {
        eventBus.publishSync(servoCommand, TimeUnit.MINUTES, 2);
        return false;
    }

    @Override
    public boolean setServoSpeed(String servoId, int speed, Scale scale) {
        servoMovementHandler.receive(new SpeedCommand(servoId, speed, scale));

        return false;
    }

    @Override
    public boolean setTargetPosition(String servoId, int targetPosition, Scale scale) {
        servoMovementHandler.receive(new PositionCommand(servoId, targetPosition, scale));

        return true;
    }

    @Override
    public boolean setPositionAndSpeed(String servoId, int speed, Scale speedScale, int targetPosition, Scale positionScale) {
        servoMovementHandler.receive(new PositionAndSpeedCommand(servoId, targetPosition, positionScale, speed, speedScale));

        return true;
    }

    @Override
    public boolean setTorgue(String s, int limit) {
        torgueManager.setTorgue(s, limit);

        return true;
    }

    @Override
    public boolean setTorgue(String s, boolean b) {
        torgueManager.setTorgue(s, b);
        return true;
    }

    @Override
    public boolean setTorgueAll(boolean state) {
        torgueManager.setTorgueAll(state);

        return true;
    }

    @Override
    public boolean setTorgueAll(boolean state, List<String> servos) {
        torgueManager.setTorgueAll(state, servos);

        return true;
    }

    @Override
    public boolean resetServos() {
        getServos().forEach(s -> sendCommand(new RebootCommand(s.getId())));
        return true;
    }

    @Override
    public boolean resetServo(String servoId) {
        sendCommand(new RebootCommand(servoId));
        return true;
    }

    @Override
    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands) {
        return bulkSetPositionAndSpeed(commands, BulkPositionSpeedCommand.WRITE_MODE.SYNC);
    }

    @Override
    public boolean bulkSetPositionAndSpeed(Map<String, PositionAndSpeedCommand> commands, BulkPositionSpeedCommand.WRITE_MODE mode) {
        bulkWriteMovementHandler.receive(new BulkPositionSpeedCommand(commands, mode));
        return true;
    }

    @Override
    public List<Servo> getServos() {
        return new ArrayList<>(servos.values());
    }

    @Override
    public Servo getServo(String s) {
        return servos.get(s);
    }
}
