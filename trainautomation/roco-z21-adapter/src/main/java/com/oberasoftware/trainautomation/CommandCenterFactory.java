package com.oberasoftware.trainautomation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CommandCenterFactory {
    @Autowired
    private List<CommandCenter> commandCenters;

    public Optional<CommandCenter> getCommandCenter(String id) {
        return commandCenters.stream().filter(c -> c.getId().equalsIgnoreCase(id)).findFirst();
    }
}
