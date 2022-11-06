package com.oberasoftware.robo.cloud.converters;

import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.converters.ThingValueToMQTTConverter;
import com.oberasoftware.iot.core.events.impl.ThingValueEventImpl;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.model.states.Value;
import com.oberasoftware.iot.core.model.states.ValueImpl;
import com.oberasoftware.iot.core.robotics.RobotRegistry;
import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import com.oberasoftware.iot.core.robotics.events.RobotValueEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class RobotValueEventConverter implements Converter {
    private static final Logger LOG = getLogger( RobotValueEventConverter.class );

    @Autowired
    private RobotRegistry robotRegistry;

    @Autowired
    private ThingValueToMQTTConverter deviceValueToMQTTConverter;

    @TypeConverter(targetClass = MQTTMessage.class)
    public List<MQTTMessage> convert(RobotValueEvent event) {
        LOG.debug("Received a robot value event: {}", event);

        var controllerId = robotRegistry.getDefaultRobot().getName();
        return event.getValues().entrySet().stream().map(es -> {
            Value value = convertValue(es.getValue());
            var thingValue = new ThingValueEventImpl(controllerId, event.getSourcePath(), value, es.getKey());

            return deviceValueToMQTTConverter.convert(thingValue);
        }).collect(Collectors.toList());
    }

    private Value convertValue(Object unknownTypedValue) {
        var valueType = switch(unknownTypedValue) {
            case Integer i -> VALUE_TYPE.NUMBER;
            case Double d -> VALUE_TYPE.DECIMAL;
            case Boolean b -> VALUE_TYPE.BOOLEAN;
            default -> VALUE_TYPE.STRING;
        };
        if(valueType == VALUE_TYPE.STRING) {
            return new ValueImpl(valueType, unknownTypedValue.toString());
        } else {
            return new ValueImpl(valueType, unknownTypedValue);
        }
    }
}
