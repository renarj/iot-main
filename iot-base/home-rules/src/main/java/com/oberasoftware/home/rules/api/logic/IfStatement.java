package com.oberasoftware.home.rules.api.logic;

import com.oberasoftware.home.rules.api.Statement;

import java.util.List;
import java.util.Objects;

/**
 * @author Renze de Vries
 */
public class IfStatement implements Statement {
    private List<IfBranch> branches;

    public IfStatement(List<IfBranch> branches) {
        this.branches = branches;
    }

    public IfStatement() {
    }

    public List<IfBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<IfBranch> branches) {
        this.branches = branches;
    }

    @Override
    public String toString() {
        return "IfBlock{" +
                "branches=" + branches +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IfStatement ifBlock = (IfStatement) o;

        return !(!Objects.equals(branches, ifBlock.branches));

    }

    @Override
    public int hashCode() {
        return branches != null ? branches.hashCode() : 0;
    }
}
