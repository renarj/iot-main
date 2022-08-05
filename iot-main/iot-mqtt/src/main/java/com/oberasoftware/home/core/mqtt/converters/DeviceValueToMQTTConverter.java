package com.oberasoftware.home.core.mqtt.converters;

import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import com.oberasoftware.iot.core.events.DeviceValueEvent;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageImpl;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class DeviceValueToMQTTConverter implements Converter {

    //controller/device/label
    private static final String TOPIC_FORMAT = "/states/%s/%s/%s";

    @TypeConverter
    public MQTTMessage convert(DeviceValueEvent deviceValueEvent) {
        Value value = deviceValueEvent.getValue();
        String topic = String.format(TOPIC_FORMAT, deviceValueEvent.getControllerId(),
                deviceValueEvent.getDeviceId(), deviceValueEvent.getLabel());
        String message = value.toString();

        return new MQTTMessageImpl(topic, message);
    }
}
