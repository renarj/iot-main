package com.oberasoftware.robomax.core;

import com.oberasoftware.home.api.converters.Converter;
import com.oberasoftware.home.api.converters.TypeConverter;
import com.oberasoftware.home.api.impl.types.ValueImpl;
import com.oberasoftware.home.api.types.VALUE_TYPE;
import com.oberasoftware.home.api.types.Value;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Renze de Vries
 */
@Component
public class ServoPositionEventConverter implements Converter {
    private static final Logger LOG = LoggerFactory.getLogger(ServoPositionEventConverter.class);

    @TypeConverter
    public MQTTMessage converter(ServoPositionEvent event) {
        LOG.info("Converterting event: {} to mqtt", event);

        Value value = new ValueImpl(VALUE_TYPE.NUMBER, event.getValue().getRaw());

        MQTTMessage message = MQTTMessageBuilder.from(value)
                .controller(event.getControllerId())
                .channel(event.getItemId())
                .label(event.getLabel())
                .build();

        LOG.info("Sending Servo position MQTT message: {}", value, message);
        return message;
    }
}
