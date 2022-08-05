package com.oberasoftware.robo.core.sensors;

import com.oberasoftware.iot.core.robotics.events.SensorEvent;
import com.oberasoftware.iot.core.robotics.sensors.ListenableSensor;
import com.oberasoftware.iot.core.robotics.sensors.SensorListener;
import com.oberasoftware.iot.core.robotics.sensors.SensorValue;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractSensor<T extends SensorValue> implements ListenableSensor<T> {

    private List<SensorListener<T>> sensorListeners = new CopyOnWriteArrayList<>();

    @Override
    public void listen(SensorListener<T> listener) {
        this.sensorListeners.add(listener);
    }


    protected void notifyListeners(SensorEvent<T> event) {
        sensorListeners.forEach(l -> l.receive(event));
    }

}
