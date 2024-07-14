$(document).ready(function() {
    $(document).on("click", ".deleteRule", function(event) {
        event.preventDefault();

        var ruleId = $("#editRule").attr("ruleId");
        console.log("Removing Rule: " + ruleId);

        $.ajax({url: "/rules/(" + ruleId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Removed Rule successfully");

            location.reload(true);
        }});
    });

    $(document).on("click", ".saveRule", function (event) {
        event.preventDefault();

        console.log("Trying to save");

        var controllerId = $(this).attr('controllerId');

        var xml = Blockly.Xml.workspaceToDom(workspace);
        var xml_text = Blockly.Xml.domToPrettyText(xml);
        console.log("XML: " + xml_text);

        const state = Blockly.serialization.workspaces.save(workspace);
        console.log("JSON: " + JSON.stringify(state, null, 2));

        var rule = {
            "controllerId" : controllerId
        };

        var ruleId = $("#editRule");
        if(ruleId) {
            rule["id"] = ruleId.attr("ruleId");
        }


        rule["properties"] = {
            "Blockly" : xml_text
        };


        var jsonData = JSON.stringify(rule);

        // $.ajax({url: "/rules/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
        //     console.log("Posted Rule successfully");
        //
        //     location.reload(true);
        // }, error: function(xhr, status, error) {
        //     console.log("Request error: " + error + " reason: " + xhr.responseText);
        //
        //     var responseData = xhr.responseText;
        //     var json = JSON.parse(responseData);
        //
        //     console.log("Error parsing: " + json.message);
        //
        //     $("#errorReason").text("Error processing blockly diagram: " + json.message);
        //     $("#errorModal").modal('toggle');
        // }})
    });

    $(document).on("click", ".resetRule", function (event) {
        event.preventDefault();



        console.log("Clearing workspace")
        Blockly.mainWorkspace.clear();
    });

    loadBlockly();
});

let workspace;

function loadBlockly() {
    Blockly.Theme.defineTheme('dark', {
        'base': Blockly.Themes.Classic,
        'componentStyles': {
            'workspaceBackgroundColour': '#1e1e1e',
            'toolboxBackgroundColour': 'blackBackground',
            'toolboxForegroundColour': '#fff',
            'flyoutBackgroundColour': '#252526',
            'flyoutForegroundColour': '#ccc',
            'flyoutOpacity': 1,
            'scrollbarColour': '#797979',
            'insertionMarkerColour': '#fff',
            'insertionMarkerOpacity': 0.3,
            'scrollbarOpacity': 0.4,
            'cursorColour': '#d0d0d0',
            'blackBackground': '#333',
        },
    });

    let blocklyDiv = document.getElementById('blocklyDiv');
    let blocklyArea = document.getElementById('blocklyArea');
    workspace = Blockly.inject(blocklyDiv,
        {
            toolbox: document.getElementById('toolbox'),
            trashcan: false,
            scrollbars: false,
            sounds: false,
            theme: "dark"
        }
    );

    // var itemToolbox = $("#ItemToolbox");
    // var groupToolbox = $("#GroupToolbox");
    // var virtualToolbox = $("#VirtualToolbox");

    const onresize = function() {
        // Compute the absolute coordinates and dimensions of blocklyArea.
        let element = blocklyArea;
        let x = 0;
        let y = 0;
        do {
            x += element.offsetLeft;
            y += element.offsetTop;
            element = element.offsetParent;
        } while (element);
        // Position blocklyDiv over blocklyArea.
        blocklyDiv.style.left = x + 'px';
        blocklyDiv.style.top = y + 'px';
        blocklyDiv.style.width = blocklyArea.offsetWidth + 'px';
        blocklyDiv.style.height = blocklyArea.offsetHeight + 'px';
        Blockly.svgResize(workspace);
    };
    window.addEventListener('resize', onresize, false);
    onresize()

    loadBlocks(workspace);
}


function loadBlocks(workspace) {
    let itemToolbox = $("#ItemToolbox");
    let controllerId = $("#editor").attr("controllerId");
    $.get("/api/controllers(" + controllerId + ")/things", function (data) {
        $.each(data, function (i, row) {
            console.log("Found a device: " + row.thingId)
            let pluginId = row.pluginId;
            let controllerId = row.controllerId;
            let thingId = row.thingId;
            let blockId = controllerId + "." + row.thingId;
            let name = "Thing: " + thingId + " controller: " + controllerId + "(" + pluginId + ")";

            createItemBlock(blockId, name);

            appendToToolbox(workspace, itemToolbox, blockId);
        });
    });
}
    // $.get("/groups/", function(data) {
    //     $.each(data, function(i, row) {
    //         console.log("Found a group: " + row.id)
    //         var groupId = row.id;
    //         var name = row.name;
    //         var blockId = "Group." + groupId;
    //
    //         createItemBlock(blockId, name);
    //
    //         appendToToolbox(groupToolbox, blockId);
    //     })
    // }),
    // $.get("/virtualitems/", function(data) {
    //     $.each(data, function(i, row) {
    //         console.log("Found a item: " + row.id)
    //         var groupId = row.id;
    //         var name = row.name;
    //         var blockId = "Item." + groupId;
    //
    //         createItemBlock(blockId, name);
    //
    //         appendToToolbox(virtualToolbox, blockId);
    //     })
    // })
// ).then(function(resp1, resp2){
//         console.log("Devices and Groups are loaded, checking if editing existing rule");
//         loadEditRule();
// });

function loadEditRule() {
    var editDiv = $("#editRule");
    if (editDiv) {
        console.log("Editing existing rule");

        //var xml_text = $("#xmlSource").val();
        var rule = editDiv.html();
        console.log("Loading rule: " + rule);

        var xml = Blockly.Xml.textToDom(rule);
        Blockly.Xml.domToWorkspace(workspace, xml);
    }
}


function createItemBlock(blockId, fieldName) {
    Blockly.Blocks[blockId] = {
        init: function() {
            this.appendDummyInput()
                .appendField(fieldName);
            this.setInputsInline(true);
            this.setOutput(true, "String");
            this.setColour(330);
            this.setTooltip(fieldName);
            this.setHelpUrl('www.oberasoftware.com/haas');
        }
    };
}

function appendToToolbox(workspace, categoryElement, blockId) {
    categoryElement.append("<block type=\"" + blockId + "\"></block>");

    workspace.updateToolbox(document.getElementById('toolbox'));
}