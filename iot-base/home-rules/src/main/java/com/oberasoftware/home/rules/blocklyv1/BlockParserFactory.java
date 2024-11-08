package com.oberasoftware.home.rules.blocklyv1;

import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Renze de Vries
 */
@Component
public class BlockParserFactory {
    @Autowired(required = false)
    private List<XMLBlockParser<?>> blockParsers;

    public <T> XMLBlockParser<T> getParser(String type) throws BlocklyParseException {
        XMLBlockParser<?> parser = blockParsers.stream().filter(p -> p.supportsType(type)).findFirst()
                .orElseThrow(() -> new BlocklyParseException("Unable to find parser for block type: " + type));

        return (XMLBlockParser<T>) parser;
    }
}
