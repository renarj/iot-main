package com.oberasoftware.robo.hcontrol;

import com.oberasoftware.iot.core.robotics.sensors.AnalogPort;
import com.oberasoftware.iot.core.robotics.sensors.PortListener;
import com.oberasoftware.iot.core.robotics.sensors.VoltageValue;
import com.pi4j.gpio.extension.ads.ADS1115GpioProvider;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.gpio.event.GpioPinListenerAnalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Renze de Vries
 */
public class ADSAnalogPort implements AnalogPort {
    private static final Logger LOG = LoggerFactory.getLogger(ADSAnalogPort.class);
    private List<PortListener<VoltageValue>> listeners = new CopyOnWriteArrayList<>();

    private final String axis;
    private final boolean reversed;

    protected ADSAnalogPort(String axis, boolean reversed, ADS1115GpioProvider provider, GpioPinAnalogInput input) {
        this.axis = axis;
        this.reversed = reversed;
        GpioPinListenerAnalog listener = event -> {
            double value = event.getValue();
            double percent =  ((value * 100) / ADS1115GpioProvider.ADS1115_RANGE_MAX_VALUE);
            double voltage = provider.getProgrammableGainAmplifier(event.getPin()).getVoltage() * (percent/100);

            LOG.info("Received voltage: {} on input: {} sending to listeners: {}", voltage, input.getName(), listeners);
            listeners.forEach(l -> l.receive((VoltageValue) () -> voltage));
        };
        LOG.info("Registering listener for port: {}", input);
        input.addListener(listener);
    }

    public String getAxis() {
        return axis;
    }

    public boolean isReversed() {
        return this.reversed;
    }

    @Override
    public void listen(PortListener<VoltageValue> portListener) {
        listeners.add(portListener);
    }
}
