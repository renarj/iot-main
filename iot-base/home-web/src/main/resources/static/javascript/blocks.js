Blockly.Blocks['rule'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Rule Name")
            .appendField(new Blockly.FieldTextInput(""), "rule_name");
        this.appendStatementInput("ruleTrigger")
            .appendField("Trigger");
        this.appendStatementInput("ruleStatement")
            .appendField("Conditions");
        this.setColour(180);
        this.setTooltip('');
    }
};

Blockly.Blocks['deviceTrigger'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Device Change");
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(240);
        this.setTooltip('');
    }
};

Blockly.Blocks['systemTrigger'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("System Start");
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(240);
        this.setTooltip('');
    }
};

Blockly.Blocks['dayTimeTrigger'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Time Trigger");
        this.appendDummyInput()
            .appendField("Hour")
            .appendField(new Blockly.FieldTextInput(""), "hour");
        this.appendDummyInput()
            .appendField("Minute")
            .appendField(new Blockly.FieldTextInput(""), "minute");

        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(240);
        this.setTooltip('');
    }
};


Blockly.Blocks['onoff'] = {
    init: function() {
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown([["on", "on"], ["off", "off"]]), "state");
        this.setInputsInline(true);
        this.setOutput(true, "String");
        this.setColour(330);
        this.setTooltip('');
    }
};

Blockly.Blocks['trainFunction'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Function Nr")
            .appendField(new Blockly.FieldTextInput("0"), "functionNr");
        this.appendDummyInput()
            .appendField("State")
            .appendField(new Blockly.FieldDropdown([["On","On"], ["Off","Off"], ["Toggle","Toggle"]]), "functionState");
        this.setPreviousStatement(true, null);
        this.setNextStatement(true, null);
        this.setColour(230);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};

Blockly.Blocks['label'] = {
    init: function() {
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown([
                ["power", "power"],
                ["energy", "energy"],
                ["temperature", "temperature"],
                ["movement", "movement"],
                ["occupancy", "occupancy"],
                ["locomotive", "locomotive"],
                ["luminance", "luminance"],
                ["speed", "speed"],
                ["direction", "direction"]
            ]), "label");
        this.setInputsInline(true);
        this.setOutput(true, "String");
        this.setColour(330);
        this.setTooltip('');
    }
};

Blockly.Blocks['label_text'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Custom Label:")
            .appendField(new Blockly.FieldTextInput(""), "label");
        this.setInputsInline(true);
        this.setOutput(true, "String");
        this.setColour(330);
        this.setTooltip('');
    }
};

Blockly.Blocks['text_value'] = {
    init: function() {
        this.appendDummyInput()
            .appendField("Text:")
            .appendField(new Blockly.FieldTextInput(""), "label");
        this.setInputsInline(true);
        this.setOutput(true, "String");
        this.setColour(330);
        this.setTooltip('');
    }
};


Blockly.Blocks['movement'] = {
    init: function() {
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown([
                ["detected", "detected"],
                ["not detected", "not detected"],
                ["occupied", "occupied"],
                ["free", "free"]
            ]), "NAME");
        this.setInputsInline(true);
        this.setOutput(true, "String");
        this.setColour(330);
        this.setTooltip('');
    }
};

Blockly.Blocks['getItemValue'] = {
    init: function() {
        this.appendValueInput("item")
            .setCheck("String")
            .appendField("Get Item:");
        this.appendValueInput("label")
            .appendField("Label:")

        this.setInputsInline(true);
        this.setOutput(true);
        this.setColour(255);
        this.setTooltip('Returns the value of an item');
    }
};

Blockly.Blocks['setItemValue'] = {
    init: function() {
        this.appendValueInput("item")
            .setCheck("String")
            .appendField("Set Item:");
        this.appendValueInput("label")
            .appendField("Label:");
        this.appendValueInput("value")
            .appendField("to Value:");

        this.setInputsInline(true);
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(165);
        this.setTooltip('Sets the value of an item');
    }
};

Blockly.Blocks['setThingValues'] = {
    init: function() {
        this.appendValueInput("thing")
            .setCheck("String")
            .appendField("Set Item:");
        this.appendStatementInput("Values")
            .setCheck(null);
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setInputsInline(true);
        this.setColour(230);
        this.setTooltip("");
        this.setHelpUrl("");
    }
};
Blockly.Blocks['setLabelValue'] = {
    init: function() {
        this.appendValueInput("attribute")
            .appendField("Attribute:");
        this.appendValueInput("Value")
            .appendField("to Value:");

        this.setInputsInline(true);
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(165);
        this.setTooltip('Sets the attribute of a Thing to a Value');
    }
};

Blockly.Blocks['switch_item'] = {
    init: function() {
        this.appendValueInput("item")
            .setCheck("String")
            .appendField("Switch Device:");
        this.appendDummyInput()
            .appendField(new Blockly.FieldDropdown([["on", "on"], ["off", "off"]]), "state");
        this.setInputsInline(true);
        this.setPreviousStatement(true);
        this.setNextStatement(true);
        this.setColour(165);
        this.setTooltip('Switch a device on or off');
    }
};