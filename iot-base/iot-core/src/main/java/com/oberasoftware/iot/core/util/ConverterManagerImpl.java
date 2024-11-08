package com.oberasoftware.iot.core.util;

import com.google.common.reflect.TypeToken;
import com.oberasoftware.iot.core.exceptions.ConversionException;
import com.oberasoftware.iot.core.robotics.converters.Converter;
import com.oberasoftware.iot.core.robotics.converters.ConverterManager;
import com.oberasoftware.iot.core.robotics.converters.ConverterResult;
import com.oberasoftware.iot.core.robotics.converters.TypeConverter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Arrays.asList;

/**
 * @author Renze de Vries
 */
@Component
public class ConverterManagerImpl implements ConverterManager {
    private static final Logger LOG = LoggerFactory.getLogger(ConverterManagerImpl.class);

    private static final String KEY_FORMAT = "%s.%s";

    @Autowired(required = false)
    private List<Converter> messageConverters;

    private final Map<String, ConverterPlaceHolder> mappedConverters = new HashMap<>();

    @PostConstruct
    public void initialize() {
        if(messageConverters != null) {
            messageConverters.forEach(mc -> processMethods(mc, mc.getClass().getMethods()));
        }
    }

    private void processMethods(Converter converter, Method[] methods) {
        asList(methods).forEach(m -> {
            TypeConverter tc = m.getAnnotation(TypeConverter.class);
            if(tc != null) {
                Class<?>[] params = m.getParameterTypes();
                if(params.length == 1) {
                    Class<?> sourceType = params[0];

                    var returnType = m.getReturnType();
                    if(!tc.targetClass().equals(Object.class)) {
                        returnType = tc.targetClass();
                    }

                    String key = format(KEY_FORMAT, sourceType, returnType);
                    LOG.info("Registered a converted: {} with method: {} in converter: {}", key, m.getName(), converter);

                    mappedConverters.put(key, new ConverterPlaceHolder(converter, m));
                }
            }
        });
    }

    @Override
    public <T, S> ConverterResult<T> convert(S s, Class<T> targetClass) {
        String targetType = targetClass.toString();

        ConverterPlaceHolder placeHolder = findConverter(s, targetType);
        if(placeHolder != null) {
            Method convertMethod = placeHolder.getMethod();
            try {
                Object result = convertMethod.invoke(placeHolder.getConverter(), s);
                if(result instanceof Collection<?>) {
                    var list = ((Collection<?>)result).stream().map(targetClass::cast).collect(Collectors.toList());
                    return new ConverterResult<>(list);
                } else {
                    return new ConverterResult<>(targetClass.cast(result));
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new ConversionException("Unable to convert type: " + s, e);
            }
        } else {
            throw new ConversionException("No converter found for type: " + s);
        }
    }

    private <S> ConverterPlaceHolder findConverter(S s, String targetType) {
        for(TypeToken typeToken : TypeToken.of(s.getClass()).getTypes()) {
            String sourceType = typeToken.getRawType().toString();
            ConverterPlaceHolder placeHolder = mappedConverters.get(format(KEY_FORMAT, sourceType, targetType));
            if (placeHolder != null) {
                return placeHolder;
            }
        }

        return null;
    }

    private class ConverterPlaceHolder {
        private final Converter converter;
        private final Method method;

        public ConverterPlaceHolder(Converter converter, Method method) {
            this.converter = converter;
            this.method = method;
        }

        public Converter getConverter() {
            return converter;
        }

        public Method getMethod() {
            return method;
        }
    }
}
