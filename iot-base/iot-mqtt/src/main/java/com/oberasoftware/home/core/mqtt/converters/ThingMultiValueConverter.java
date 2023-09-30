package com.oberasoftware.home.core.mqtt.converters;

import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageImpl;
import com.oberasoftware.iot.core.events.ThingMultiValueEvent;
import com.oberasoftware.iot.core.model.ValueTransportMessage;
import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import org.springframework.stereotype.Component;

import static com.oberasoftware.iot.core.util.ConverterHelper.mapToJson;

@Component
public class ThingMultiValueConverter implements Converter {
    private static final String TOPIC_FORMAT = "states/%s/%s";

    @TypeConverter
    public MQTTMessage convert(ThingMultiValueEvent multiValueEvent) {
        String topic = String.format(TOPIC_FORMAT, multiValueEvent.getControllerId(),
                multiValueEvent.getThingId());

        var transportMessage = new ValueTransportMessage(multiValueEvent.getValues(), multiValueEvent.getControllerId(),
                multiValueEvent.getThingId());
        String json = mapToJson(transportMessage);

        return new MQTTMessageImpl(topic, json);
    }
}
