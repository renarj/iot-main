package com.oberasoftware.robot.nao.sensors;

import com.aldebaran.qi.Session;
import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.robotics.sensors.EventSource;
import com.oberasoftware.iot.core.robotics.sensors.TriggerValue;
import com.oberasoftware.robot.nao.SensorManager;
import com.oberasoftware.robot.nao.TriggerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaoTouchSensorPort extends NaoMemoryPort<TriggerValue> implements EventHandler {
    private static final Logger LOG = LoggerFactory.getLogger(NaoTouchSensorPort.class);

    public NaoTouchSensorPort(Session session, SensorManager sensorManager) {
        super(session, sensorManager);
    }

    @Override
    public void close() {

    }

    @Override
    public void initialize() {
        getSensorManager().registerListener(this);
    }

    @EventSubscribe
    @EventSource({"FrontTactilTouched", "MiddleTactilTouched", "RearTactilTouched"})
    public void receive(TriggerEvent triggerEvent) {
        LOG.info("Head was touched on spot: {} is on: {}", triggerEvent.getAttribute(), triggerEvent.isOn());

        notify(new TriggerValue() {
            @Override
            public String getSource() {
                return triggerEvent.getAttribute();
            }

            @Override
            public Boolean getRaw() {
                return triggerEvent.isOn();
            }
        });
    }
}
