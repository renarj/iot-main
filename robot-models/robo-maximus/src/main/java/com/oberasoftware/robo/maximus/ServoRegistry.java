package com.oberasoftware.robo.maximus;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ServoRegistry {
    private final Multimap<String, ThingKey> servoRegister = ArrayListMultimap.create();
    private final Map<String, ThingKey> thingMap = new HashMap<>();

    public void addServo(String controllerId, String thingId, String servoId, String robotId) {
        ThingKey key = getKey(controllerId, thingId, servoId, robotId);
        servoRegister.put(servoId, key);
        thingMap.put(controllerId + "." + thingId, key);
    }

    public ThingKey getThing(String controllerId, String thingId) {
        return thingMap.get(controllerId + "." + thingId);
    }

    public List<ThingKey> getThings(String servoId) {
        return Lists.newArrayList(servoRegister.get(servoId));
    }

    private ThingKey getKey(String controllerId, String thingId, String servoId, String robotId) {
        return new ThingKey(controllerId, thingId, servoId, robotId);
    }
}
