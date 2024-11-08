package com.oberasoftware.home.rules.api.logic;

import com.oberasoftware.home.rules.api.Statement;
import com.oberasoftware.home.rules.api.Condition;

import java.util.List;
import java.util.Objects;

/**
 * @author Renze de Vries
 */
public class IfBranch implements Statement {
    private Condition condition;
    private List<Statement> statements;

    public IfBranch(Condition condition, List<Statement> statements) {
        this.condition = condition;
        this.statements = statements;
    }

    public IfBranch() {
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public void setStatements(List<Statement> statements) {
        this.statements = statements;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IfBranch branch = (IfBranch) o;

        if (!Objects.equals(condition, branch.condition)) return false;
        return statements.equals(branch.statements);

    }

    @Override
    public int hashCode() {
        int result = condition != null ? condition.hashCode() : 0;
        result = 31 * result + statements.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IfBranch{" +
                "condition=" + condition +
                ", statements=" + statements +
                '}';
    }
}
