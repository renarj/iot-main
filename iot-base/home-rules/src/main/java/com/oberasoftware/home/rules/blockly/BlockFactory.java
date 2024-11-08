package com.oberasoftware.home.rules.blockly;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BlockFactory {
    private final List<BlockParser<?>> blockParsers;

    public BlockFactory(List<BlockParser<?>> blockParsers) {
        this.blockParsers = blockParsers;
    }

    public <T> BlockParser<T> getParser(String type) throws BlocklyParseException {
        return (BlockParser<T>) blockParsers.stream().filter(p -> p.supportsType(type)).findFirst()
                .orElseThrow(() -> new BlocklyParseException("Unable to find parser for block type: " + type));
    }
}