package com.oberasoftware.home.agent.core.handlers.converters;

import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.ItemValueCommand;
import com.oberasoftware.iot.core.commands.impl.ValueCommandImpl;
import com.oberasoftware.iot.core.legacymodel.VALUE_TYPE;
import com.oberasoftware.iot.core.legacymodel.Value;
import com.oberasoftware.iot.core.legacymodel.impl.ValueImpl;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.lang.Double.parseDouble;
import static java.lang.Long.parseLong;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class ValueCommandConverter implements CommandConverter<BasicCommand, ItemValueCommand> {
    private static final Logger LOG = getLogger(ValueCommandConverter.class);

    @Override
    @ConverterType(commandType = "value")
    public ItemValueCommand map(BasicCommand source) {
        Map<String, String> properties = source.getProperties();
        String itemId = source.getItemId();

        Map<String, Value> values = new HashMap<>();
        properties.forEach((k, v) -> {
            LOG.debug("Converting key: {} and value: {}", k, v);
            Optional<Long> number = getNumber(v);
            Optional<Double> decimal = getDecimal(v);

            VALUE_TYPE type = number.isPresent() ? VALUE_TYPE.NUMBER : decimal.isPresent() ? VALUE_TYPE.DECIMAL : VALUE_TYPE.STRING;
            Object decodedValue = number.isPresent() ? number.get() : decimal.isPresent() ? decimal.get() : v;

            values.put(k, new ValueImpl(type, decodedValue));
        });

        return new ValueCommandImpl(source.getControllerId(), itemId, values);
    }

    private Optional<Long> getNumber(String value) {
        try {
            return of(parseLong(value));
        } catch(NumberFormatException e) {
            return empty();
        }
    }

    private Optional<Double> getDecimal(String value) {
        try {
            return of(parseDouble(value));
        } catch(NumberFormatException e) {
            return empty();
        }
    }
}
