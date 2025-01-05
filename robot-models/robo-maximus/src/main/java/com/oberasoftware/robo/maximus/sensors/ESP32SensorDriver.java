package com.oberasoftware.robo.maximus.sensors;

import com.oberasoftware.base.event.impl.LocalEventBus;
import com.oberasoftware.iot.core.robotics.RobotHardware;
import com.oberasoftware.iot.core.robotics.exceptions.RoboException;
import com.oberasoftware.iot.core.robotics.sensors.SensorDriver;
import com.oberasoftware.robo.dynamixel.ESP32SerialConnector;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.util.concurrent.Uninterruptibles.sleepUninterruptibly;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.slf4j.LoggerFactory.getLogger;

@Component
public class ESP32SensorDriver implements SensorDriver<TeensyPort> {
    private static final Logger LOG = getLogger(ESP32SensorDriver.class);

    private static final int CHECK_INTERVAL = 5000;

    static final String INA_260_CURRENT = "current";
    static final String INA_260_VOLTAGE = "voltage";
    static final String INA_260_POWER = "power";
    static final String LSM_9_DS_1_ROLL = "roll";
    static final String LSM_9_DS_1_PITCH = "pitch";
    static final String LSM_9_DS_1_HEADING = "heading";
    private static final String TEMP = "temp";

    @Autowired
    private ESP32SerialConnector proxySerialConnector;

    @Autowired
    private LocalEventBus eventBus;

    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private final List<TeensyPort> ports = new ArrayList<>();

    @Override
    public List<TeensyPort> getPorts() {
        return newArrayList(ports);
    }

    @Override
    public TeensyPort getPort(String portId) {
        return ports.stream().filter(p -> p.getName().equalsIgnoreCase(portId))
                .findFirst()
                .orElseThrow(() -> new RoboException("Could not find sensor port: " + portId));
    }

    @Override
    public void activate(RobotHardware robot, Map<String, String> properties) {
        ports.add(new TeensyPort(LSM_9_DS_1_ROLL));
        ports.add(new TeensyPort(LSM_9_DS_1_PITCH));
        ports.add(new TeensyPort(LSM_9_DS_1_HEADING));
        ports.add(new TeensyPort(INA_260_CURRENT));
        ports.add(new TeensyPort(INA_260_VOLTAGE));
        ports.add(new TeensyPort(INA_260_POWER));
        ports.add(new TeensyPort(TEMP));

        executorService.submit(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] d = proxySerialConnector.sendAndReceiveCommand("sensors", new HashMap<>(), true);
                    if(d != null && d.length > 0) {
                        notifyPorts(new String(d));
                    }

                    sleepUninterruptibly(CHECK_INTERVAL, MILLISECONDS);
                } catch(Exception e) {
                    LOG.error("", e);
                }
            }
            LOG.info("Thread was interrupted");
        });
    }

    private void notifyPorts(String json) {
        JSONObject jo = new JSONObject(json);
        JSONObject sensors = jo.getJSONObject("sensors");
        for(String key: sensors.keySet()) {
            JSONObject sensor = sensors.getJSONObject(key);
            LOG.info("Sensor: {} data: {}", key, sensor.toString());

            for(String attribute : sensor.keySet()) {
                LOG.debug("Sensor data: {} value: {}", key + "." + attribute, sensor.get(attribute));

                double value = sensor.getDouble(attribute);
                getPort(attribute).notify(value);
            }
        }
    }

    @Override
    public void shutdown() {
        executorService.shutdown();
    }
}
