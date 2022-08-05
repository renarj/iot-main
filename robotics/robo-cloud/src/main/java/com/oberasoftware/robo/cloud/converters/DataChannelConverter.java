package com.oberasoftware.robo.cloud.converters;

import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import com.oberasoftware.home.core.mqtt.MQTTMessage;
import com.oberasoftware.home.core.mqtt.MQTTMessageImpl;
import com.oberasoftware.iot.core.robotics.commands.PingCommand;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class DataChannelConverter implements Converter {
    private static final Logger LOG = getLogger(DataChannelConverter.class);

    @TypeConverter
    public MQTTMessage convert(PingCommand pingCommand) {
        return new MQTTMessageImpl("/data/" + pingCommand.getName() + "/ping","{}");
    }
}
