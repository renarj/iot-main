package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.Condition;
import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.logic.IfBranch;
import com.oberasoftware.home.rules.api.logic.IfStatement;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class IfBlockParser implements BlockParser<IfStatement> {

    @Override
    public boolean supportsType(String type) {
        return "controls_if".equals(type);
    }

    @Override
    public IfStatement transform(BlockFactory factory, BlocklyObject block) throws BlocklyParseException {
        var extraState = block.getExtraState();
        int elseCount = extraState != null ? extraState.getElseIfCount() : 0;
        boolean hasElse = extraState != null && extraState.isHasElse();

        List<IfBranch> branches = new ArrayList<>();
        for(int i=0; i<=elseCount; i++) {
            String ifName = "IF" + i;
            String statementName = "DO" + i;

            branches.add(getBranch(factory, block, ifName, statementName));

        }

        return new IfStatement(branches);
    }

    private IfBranch getBranch(BlockFactory factory, BlocklyObject ifBlock, String ifBranchName, String statementName) throws BlocklyParseException {
        var ifStatement = BlockUtils.safeGetInput(ifBlock, ifBranchName);
        var doStatement = BlockUtils.safeGetInput(ifBlock, statementName);

        BlockParser<Condition> ifVisitor = factory.getParser(ifStatement.getType());
        Condition condition = ifVisitor.transform(factory, ifStatement);

        var statementBlocks = BlockUtils.getChainAsList(doStatement);
        var statements = statementBlocks.stream().map(s -> (Statement) factory.getParser(s.getType()).transform(factory, s)).toList();

        return new IfBranch(condition, statements);
    }
}
