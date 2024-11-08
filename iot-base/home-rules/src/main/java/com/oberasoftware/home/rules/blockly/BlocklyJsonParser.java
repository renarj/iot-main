package com.oberasoftware.home.rules.blockly;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.List;

//@Component
public class BlocklyJsonParser implements BlocklyParser {
    private static final Logger LOG = LoggerFactory.getLogger(BlocklyJsonParser.class);

    private final BlockFactory blockFactory;

    public BlocklyJsonParser(BlockFactory blockFactory) {
        this.blockFactory = blockFactory;
    }

    @Override
    public Rule toRule(String blocklyJson) throws BlocklyParseException {
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(blocklyJson, JsonObject.class);
        JsonObject blocksObject = jsonObject.getAsJsonObject("blocks");
        JsonArray blocksArray = blocksObject.getAsJsonArray("blocks");

        Type blocklyListType = new TypeToken<List<BlocklyObject>>() {}.getType();
        List<BlocklyObject> blocklyObjects = gson.fromJson(blocksArray, blocklyListType);

        if(blocklyObjects.size() > 1) {
            throw new BlocklyParseException("Only one rule block is allowed");
        } else {
            var b = blocklyObjects.get(0);
            LOG.info("Evaluated block: {} with id: {}", b.getType(), b.getId());
            try {
                Rule rule = (Rule)blockFactory.getParser(b.getType()).transform(blockFactory, b);
                return rule;
            } catch (BlocklyParseException e) {
                LOG.error("Could not evaluate block: {}", b, e);
                throw new BlocklyParseException("Could not evaluate block", e);
            }
        }
    }
}
