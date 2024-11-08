package com.oberasoftware.home.rules.blockly.json;

public class ExtraState {
    public ExtraState() {
    }

    private int elseIfCount;
    private boolean hasElse;

    // Getters and setters
    public int getElseIfCount() { return elseIfCount; }
    public void setElseIfCount(int elseIfCount) { this.elseIfCount = elseIfCount; }

    public boolean isHasElse() { return hasElse; }
    public void setHasElse(boolean hasElse) { this.hasElse = hasElse; }

    @Override
    public String toString() {
        return "ExtraState{" +
                "elseIfCount=" + elseIfCount +
                ", hasElse=" + hasElse +
                '}';
    }
}
