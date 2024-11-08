package com.oberasoftware.home.rules.blockly;

import com.oberasoftware.home.rules.blockly.json.BlocklyObject;

public interface BlockParser<T> {
    boolean supportsType(String type);

    T transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException;
}
