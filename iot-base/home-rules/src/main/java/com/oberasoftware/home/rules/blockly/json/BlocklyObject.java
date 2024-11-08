package com.oberasoftware.home.rules.blockly.json;

import java.util.Map;

public class BlocklyObject {
    private String type;
    private String id;
    private Map<String, String> fields;
    private Map<String, BlocklyInput> inputs;
    private BlockWrapper next;
    private ExtraState extraState;

    // Getters and setters
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Map<String, String> getFields() { return fields; }
    public void setFields(Map<String, String> fields) { this.fields = fields; }

    public Map<String, BlocklyInput> getInputs() { return inputs; }
    public void setInputs(Map<String, BlocklyInput> inputs) { this.inputs = inputs; }

    public BlockWrapper getNext() { return next; }
    public void setNext(BlockWrapper next) { this.next = next; }

    public ExtraState getExtraState() { return extraState; }
    public void setExtraState(ExtraState extraState) { this.extraState = extraState; }

    @Override
    public String toString() {
        return "BlocklyObject{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                ", fields=" + fields +
                ", inputs=" + inputs +
                ", next=" + next +
                ", extraState=" + extraState +
                '}';
    }
}

