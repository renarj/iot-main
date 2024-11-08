package com.oberasoftware.home.rules.blockly.json;

class BlocklyField {
    private String name;
    private String value;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    @Override
    public String toString() {
        return "BlocklyField{" +
                "name='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}