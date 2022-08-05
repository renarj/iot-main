package com.oberasoftware.robo.maximus.sensors;

import com.oberasoftware.iot.core.robotics.sensors.DirectPort;
import com.oberasoftware.iot.core.robotics.sensors.PortListener;
import org.slf4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.slf4j.LoggerFactory.getLogger;

public class TeensyPort implements DirectPort<DoubleValue> {
    private static final Logger LOG = getLogger(TeensyPort.class);

    private final String name;

    private List<PortListener<DoubleValue>> listeners = new CopyOnWriteArrayList<>();

    public TeensyPort(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void listen(PortListener<DoubleValue> portListener) {
        listeners.add(portListener);
    }

    protected void notify(Double portValue) {
        listeners.forEach(l -> {
            LOG.debug("Notifying listener for port: {} with value: {}", name, l);
            l.receive(new DoubleValue(portValue));
        });

    }
}
