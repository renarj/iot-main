package com.oberasoftware.home.agent.core.handlers;

import com.oberasoftware.base.event.EventHandler;
import com.oberasoftware.base.event.EventSubscribe;
import com.oberasoftware.iot.core.commands.BasicCommand;
import com.oberasoftware.iot.core.commands.ItemCommand;
import com.oberasoftware.home.agent.core.handlers.converters.CommandConverter;
import com.oberasoftware.home.agent.core.handlers.converters.ConverterType;
import com.oberasoftware.iot.core.events.impl.ItemCommandEvent;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.*;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author renarj
 */
@Component
public class BasicCommandHandler implements EventHandler {
    private static final Logger LOG = getLogger(BasicCommandHandler.class);


    @Autowired(required = false)
    private List<CommandConverter<BasicCommand, ? extends ItemCommand>> commandConverters;

    private Map<String, CommandConverter<BasicCommand, ? extends ItemCommand>> commandConverterMap = new HashMap<>();

    @PostConstruct
    public void mapConverters() {
        if(commandConverters!= null && !commandConverters.isEmpty()) {
            commandConverters.forEach(c -> {
                Optional<Method> annotatedMethod = Arrays.stream(c.getClass().getMethods())
                        .filter(m -> m.getDeclaredAnnotation(ConverterType.class) != null)
                        .findFirst();

                if(annotatedMethod.isPresent()) {
                    Method method = annotatedMethod.get();
                    String commandType = method.getAnnotation(ConverterType.class).commandType();

                    LOG.info("Mapper found for commandType: {} on method: {}", commandType, method.getName());
                    commandConverterMap.put(commandType, c);
                }
            });

        }
    }

    @EventSubscribe
    public ItemCommandEvent receive(BasicCommand basicCommand) {
        LOG.info("Received a basic command: {}", basicCommand);

        String commandType = basicCommand.getCommandType();

        if(commandConverterMap.containsKey(commandType)) {
            CommandConverter<BasicCommand, ? extends ItemCommand> converter = commandConverterMap.get(commandType);

            ItemCommand command = converter.map(basicCommand);
            LOG.info("Converted: {} to command: {} sending to automation bus", basicCommand, command);

            return new ItemCommandEvent(command.getThingId(), command);
        } else {
            LOG.info("No converter available for command type: {} on command: {}", commandType, basicCommand);
            return null;
        }
    }
}
