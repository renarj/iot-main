function initDefaultBlocks() {
    console.log("Loading default blocks");
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

    Blockly.Blocks['thingTrigger'] = {
        init: function() {
            this.appendDummyInput()
                .appendField("Thing Changes State");
            this.setPreviousStatement(true);
            this.setNextStatement(true);
            this.setColour(240);
            this.setTooltip('');
        }
    };

    Blockly.Blocks['systemTrigger'] = {
        init: function() {
            this.appendDummyInput()
                .appendField("System Start or Stop");
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

    Blockly.Blocks['attribute'] = {
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
                    ["direction", "direction"],
                    ["position", "position"],
                    ["degrees", "degrees"],
                    ["torgue", "torgue"]
                ]), "label");
            this.setInputsInline(true);
            this.setOutput(true, "String");
            this.setColour(330);
            this.setTooltip('');
        }
    };

    Blockly.Blocks['attribute_text'] = {
        init: function() {
            this.appendDummyInput()
                .appendField("Custom Attribute:")
                .appendField(new Blockly.FieldTextInput(""), "attribute");
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

    Blockly.Blocks['getThingValue'] = {
        init: function() {
            this.appendValueInput("item")
                .setCheck("String")
                .appendField("Get Thing:");
            this.appendValueInput("Attribute")
                .appendField("Attribute:")

            this.setInputsInline(true);
            this.setOutput(true);
            this.setColour(255);
            this.setTooltip('Returns the value of a Thing');
        }
    };

    Blockly.Blocks['setThingValue'] = {
        init: function() {
            this.appendValueInput("item")
                .setCheck("String")
                .appendField("Set Thing:");
            this.appendValueInput("Attribute")
                .appendField("Attribute:");
            this.appendValueInput("value")
                .appendField("to Value:");

            this.setInputsInline(true);
            this.setPreviousStatement(true);
            this.setNextStatement(true);
            this.setColour(165);
            this.setTooltip('Sets the value of a Thing');
        }
    };

    Blockly.Blocks['setThingValues'] = {
        init: function() {
            this.appendValueInput("thing")
                .setCheck("String")
                .appendField("Set Thing:");
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
    Blockly.Blocks['setAttributeValue'] = {
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
                .appendField("Switch Thing:");
            this.appendValueInput("Attribute")
                .appendField("Attribute:");
            this.appendDummyInput()
                .appendField(new Blockly.FieldDropdown([["on", "on"], ["off", "off"]]), "state");
            this.setInputsInline(true);
            this.setPreviousStatement(true);
            this.setNextStatement(true);
            this.setColour(165);
            this.setTooltip('Switch a Thing on or off');
        }
    };
}

function appendToToolbox(workspace, categoryElement, blockId) {
    categoryElement.append("<block type=\"" + blockId + "\"></block>");

    workspace.updateToolbox(document.getElementById('toolbox'));
}
