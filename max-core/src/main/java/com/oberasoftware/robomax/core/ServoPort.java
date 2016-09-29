package com.oberasoftware.robomax.core;

import com.oberasoftware.robo.api.sensors.DirectPort;
import com.oberasoftware.robo.api.sensors.PortListener;
import com.oberasoftware.robo.api.servo.ServoProperty;
import com.oberasoftware.robo.api.servo.events.ServoUpdateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Renze de Vries
 */
public class ServoPort implements DirectPort<PositionValue> {
    private static final Logger LOG = LoggerFactory.getLogger(ServoPort.class);

    private List<PortListener<PositionValue>> listeners = new CopyOnWriteArrayList<>();

    @Override
    public void listen(PortListener<PositionValue> portListener) {
        this.listeners.add(portListener);
    }

    protected void notify(ServoUpdateEvent servoUpdateEvent) {
        LOG.info("Servo port event: {}", servoUpdateEvent);
        int position = servoUpdateEvent.getServoData().getValue(ServoProperty.POSITION);

        listeners.forEach(l -> {
            LOG.info("Notifying listener: {}", l);
            l.receive(new PositionValue(servoUpdateEvent.getServoId(), position));
        });
    }
}
