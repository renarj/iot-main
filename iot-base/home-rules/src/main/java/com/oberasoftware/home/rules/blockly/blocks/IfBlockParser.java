package com.oberasoftware.home.rules.blockly.blocks;

import com.oberasoftware.home.rules.api.Condition;
import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.logic.IfBranch;
import com.oberasoftware.home.rules.api.logic.IfStatement;
import com.oberasoftware.home.rules.blockly.BlockFactory;
import com.oberasoftware.home.rules.blockly.BlockParser;
import com.oberasoftware.home.rules.blockly.BlockUtils;
import com.oberasoftware.home.rules.blockly.BlocklyParseException;
import com.oberasoftware.home.rules.blockly.json.BlocklyObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

            branches.add(getBranch(factory, block, Optional.of(ifName), statementName));
        }
        if(hasElse) {
            branches.add(getBranch(factory, block, Optional.empty(), "ELSE"));
        }

        return new IfStatement(branches);
    }

    private IfBranch getBranch(BlockFactory factory, BlocklyObject ifBlock, Optional<String> ifBranchName, String statementName) throws BlocklyParseException {
        var doStatement = BlockUtils.safeGetInput(ifBlock, statementName);

        Condition optionalCondition = null;
        if(ifBranchName.isPresent()) {
            var ifStatementBlock = BlockUtils.safeGetInput(ifBlock, ifBranchName.get());
            BlockParser<Condition> ifVisitor = factory.getParser(ifStatementBlock.getType());
            optionalCondition = ifVisitor.transform(factory, ifStatementBlock);
        }

        var statementBlocks = BlockUtils.getChainAsList(doStatement);
        var statements = statementBlocks.stream().map(s -> (Statement) factory.getParser(s.getType()).transform(factory, s)).toList();

        return new IfBranch(optionalCondition, statements);
    }
}
