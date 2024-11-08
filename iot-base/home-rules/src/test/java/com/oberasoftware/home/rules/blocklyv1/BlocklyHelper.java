package com.oberasoftware.home.rules.blocklyv1;

import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import com.oberasoftware.home.rules.api.general.Rule;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.BlocklyParser;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * @author Renze de Vries
 */
public class BlocklyHelper {
    public static Rule parseXMLRule(BlocklyParser parser, String ruleFile) throws BlocklyParseException, IOException {
        var blocklyRuleXML = loadResource(ruleFile);

        return parser.toRule(blocklyRuleXML);
    }

    public static Rule parseJsonRule(BlocklyParser parser, String ruleFile) throws BlocklyParseException, IOException {
        var blocklyJson = loadResource(ruleFile);

        return parser.toRule(blocklyJson);
    }

    private static String loadResource(String resource) throws IOException {
        CharSource s = Resources.asCharSource(BlocklyHelper.class.getResource(resource), Charset.defaultCharset());
        return s.read();
    }
}
