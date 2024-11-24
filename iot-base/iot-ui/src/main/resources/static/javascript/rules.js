$(document).ready(function() {
    retrieveServiceUrls(pageInit);
});

function pageInit() {
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
        let blocklyJson = JSON.stringify(state);
        console.log("JSON: " + JSON.stringify(state, null, 2));

        var rule = {
            "controllerId" : controllerId
        };

        var ruleId = $("#editRule");
        if(ruleId) {
            rule["id"] = ruleId.attr("ruleId");
        }
        rule["blocklyData"] = blocklyJson;

        var jsonData = JSON.stringify(rule);
        console.log("Posting data: " + jsonData);

        $.ajax({url: thingSvcUrl + "/api/rules", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Posted Rule successfully");

            location.reload(true);
        }, error: function(xhr, status, error) {
            console.log("Request error: " + error + " reason: " + xhr.responseText);

            var responseData = xhr.responseText;
            var json = JSON.parse(responseData);

            console.log("Error parsing: " + json.message);

            $("#errorReason").text("Error processing blockly diagram: " + json.message);
            $("#errorModal").modal('toggle');
        }})
    });

    $(document).on("click", ".resetRule", function (event) {
        event.preventDefault();

        console.log("Clearing workspace")
        workspace.clear();
    });

    loadBlockly(function() {
        loadRules();
    });
}

let workspace;

function loadBlockly(callback) {
    initDefaultBlocks();
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

    loadBlocks(callback);
}


function loadBlocks(callback) {
    let controllerId = $("#editor").attr("controllerId");


    fetchBlocks($("#ItemToolbox"), thingSvcUrl + "/api/controllers(" + controllerId + ")/things", function() {
        fetchBlocks($("#MotionToolBox"), thingSvcUrl + "/api/controllers(" + controllerId +" )/schemas(Motion)/things", function() {
            callback();
        })
    });
}

function fetchBlocks(toolbox, url, callback) {
    console.log("Loading things from URL: " + url);
    $.get(url, function (data) {
        $.each(data, function (i, row) {
            console.debug("Found a Thing: " + row.thingId)
            let controllerId = row.controllerId;
            let thingId = row.thingId;
            let blockId = row.controllerId + "." + row.thingId;
            let name = "Thing: " + thingId;

            createItemBlock(blockId, name);

            appendToToolbox(workspace, toolbox, blockId);
        });

        callback();
    });
}

function loadEditRule(blocklyData) {
    console.log("Loading edit rule: " + blocklyData);
    Blockly.serialization.workspaces.load(JSON.parse(blocklyData), workspace);
}

function loadRules() {
    let controllerId = getControllerId();
    $.get(thingSvcUrl + "/api/rules/controller(" + controllerId + ")", function(data) {
        let ruleId = getRuleId();

        $.each(data, function (i, rule) {
            let data = {
                "controllerId": rule.controllerId,
                "ruleId": rule.name
            };

            renderAndAppend("ruleTemplate", data, "rules");

            if(ruleId === rule.name) {
                loadEditRule(rule.blocklyData);
            }
        })


        if(ruleId !== undefined) {
            $(".ruleButton[ruleId=" + ruleId + "]").addClass('active');
        }
    })
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

function getRuleId() {
    let root = $("#root")
    return root.attr("ruleId");
}


