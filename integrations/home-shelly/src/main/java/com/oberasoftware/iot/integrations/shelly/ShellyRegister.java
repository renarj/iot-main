package com.oberasoftware.iot.integrations.shelly;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ShellyRegister {

    private final Map<String, ShellyMetadata> registry = new ConcurrentHashMap<>();

    public void addShelly(ShellyMetadata metadata) {
        registry.put(getKey(metadata), metadata);
    }

    public Optional<ShellyMetadata> findShelly(String controllerId, String thingId) {
        return Optional.ofNullable(registry.get(getKey(controllerId, thingId)));
    }

    public List<ShellyMetadata> getAll() {
        return new ArrayList<>(registry.values());
    }

    public int size() {
        return registry.size();
    }

    private String getKey(ShellyMetadata metadata) {
        return getKey(metadata.getControllerId(), metadata.getThingId());
    }

    private String getKey(String controllerId, String thingId) {
        return controllerId + "." + thingId;
    }
}
