package com.oberasoftware.home.rules.blockly.json;

public class BlocklyInput {
    private String name;
    private BlocklyObject block;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BlocklyObject getBlock() { return block; }
    public void setBlock(BlocklyObject block) { this.block = block; }

    @Override
    public String toString() {
        return "BlocklyInput{" +
                "name='" + name + '\'' +
                ", block=" + block.getId() +
                '}';
    }
}