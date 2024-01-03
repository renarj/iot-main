package com.oberasoftware.home.core.mqtt.converters;

import com.oberasoftware.iot.core.model.ValueTransportMessage;
import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import com.oberasoftware.iot.core.events.ThingValueEvent;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageImpl;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapToJson;

/**
 * @author Renze de Vries
 */
@Component
public class ThingValueToMQTTConverter implements Converter {

    //controller/device/label
    private static final String TOPIC_FORMAT = "iot_states/%s/%s/%s";

    @TypeConverter
    public MQTTMessage convert(ThingValueEvent deviceValueEvent) {
        Value value = deviceValueEvent.getValue();
        String topic = String.format(TOPIC_FORMAT, deviceValueEvent.getControllerId(),
                deviceValueEvent.getThingId(), deviceValueEvent.getAttribute());

        var transportMessage = new ValueTransportMessage(value, deviceValueEvent.getControllerId(),
                deviceValueEvent.getThingId(), deviceValueEvent.getAttribute());
        String json = mapToJson(transportMessage);

        return new MQTTMessageImpl(topic, json);
    }
}
