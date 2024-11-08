package com.oberasoftware.home.rules.blockly;

import com.google.common.collect.Lists;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;

import java.util.List;

public class BlockUtils {
    public static String safeGetField(BlocklyObject block, String field) throws BlocklyParseException{
        if(!block.getFields().containsKey(field)) {
            throw new BlocklyParseException("Field: " + field + " not found in block: " + block);
        }
        return block.getFields().get(field);
    }

    public static BlocklyObject safeGetInput(BlocklyObject block, String input) throws BlocklyParseException{
        if(!block.getInputs().containsKey(input)) {
            throw new BlocklyParseException("Input: " + input + " not found in block: " + block);
        }
        return block.getInputs().get(input).getBlock();
    }

    public static List<BlocklyObject> getChainAsList(BlocklyObject block) {
        List<BlocklyObject> chainedList = Lists.newArrayList();
        chainedList.add(block);

        if(block.getNext() != null) {
            var next = block.getNext().getBlock();
            chainedList.addAll(getChainAsList(next));
        }
        return chainedList;
    }

    public static String getControllerId(String key) {
        return key.substring(0, key.indexOf("."));
    }

    public static String getThingId(String key) {
        return key.substring(key.indexOf(".") + 1);
    }
}
