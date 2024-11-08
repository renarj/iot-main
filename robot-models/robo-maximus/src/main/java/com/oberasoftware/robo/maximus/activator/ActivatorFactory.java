package com.oberasoftware.robo.maximus.activator;

import com.oberasoftware.iot.core.model.IotThing;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ActivatorFactory {

    private final List<Activator> activators;

    public ActivatorFactory(List<Activator> activators) {
        this.activators = activators;
    }

    public Optional<Activator> getActivator(IotThing thing) {
        return activators.stream()
                .filter(a -> a.getSchemaId().equalsIgnoreCase(thing.getSchemaId()))
                .findFirst();
    }
}
