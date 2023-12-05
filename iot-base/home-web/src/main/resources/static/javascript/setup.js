$(document).ready(function() {
    retrieveServiceUrls(pageInit);
});

function pageInit() {
    console.log("Document loaded");

    let controllerId = getControllerId();
    let thingId = getThingId();
    if(controllerId !== undefined && thingId !== undefined) {
        $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/things(" + thingId + ")", function(data) {
            let root = $("#root");
            root.attr("schemaId", data.templateId);
            root.attr("pluginId", data.pluginId);

            loadPlugins();

            loadSetupDialog(data.templateId, data);

            $("#addThing").click(function() {
                saveThing();
            })
        });
    } else {
        initPluginAndSchema();
    }

}

function initPluginAndSchema() {
    loadPlugins();

    let schemaId = getSchemaId();
    if(schemaId !== undefined) {
        loadSetupDialog(schemaId);
    }

    $("#addThing").click(function() {
        saveThing();
    })
}

function getControllerId() {
    return $("#root").attr("controllerId");
}
function getThingId() {
    return $("#root").attr("thingId");
}

function saveThing() {
    let thingId = $("#thingId").val();
    let name = $("#friendlyName").val();
    let controllerId = getSelectedController();
    let pluginId = $("#pluginid").val();
    let type = $("#type").val();
    let parentId = getSelectedParent();
    let schemaId = $("#thingForm").attr("schemaId");
    let properties = {};
    $(".thingProperty").each(function(index) {
        let propertyKey = $(this).attr("id");
        properties[propertyKey] = $(this).val();
    })

    let data = {
        "thingId": thingId,
        "controllerId" : controllerId,
        "friendlyName" : name,
        "type" : type,
        "templateId" : schemaId,
        "pluginId" : pluginId,
        "parentId": parentId,
        "properties" : properties
    }
    let jsonData = JSON.stringify(data);
    console.log("Posting data: " + jsonData);
    $.ajax({url: thingSvcUrl + "/api/controllers(" + controllerId + ")/things", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Posted Thing successfully")

            location.href = "/web/admin/things/controllers(" + controllerId + ")/things(" + thingId + ")";
        }})
}

function loadSetupDialog(schemaId, thingData) {
    let pluginId = getPluginId();

    $.get(thingSvcUrl + "/api/system/plugins(" + pluginId + ")/schemas(" + schemaId + ")", function(data) {
        $("#detailPanel").removeClass("fade");
        loadControllers(pluginId, getControllerId());

        $("#thingForm").attr("schemaId", data.schemaId)

        $("#templateText").html(data.template);
        $("#pluginid").val(pluginId);
        $("#type").val(data.type);

        $.each(data.properties, function (key, val) {
            renderAndAppend("propertyTemplate", {"field": key}, "thingForm")
            let field = $("#" + key);
            if(val.fieldType === "STATIC_DEFAULT") {
                field.prop( "disabled", true);
                field.val(val.defaultValue);
            }
            if(val.fieldType === "TEXT") {
                field.val(val.defaultValue);
            }
        })

        if(thingData) {
            loadExistingData(thingData);
        }
    })

    $("#controllerList").change(function () {
        let controllerId = getSelectedController();
        loadParentList(controllerId, pluginId);
    })
}

function loadParentList(controllerId, pluginId, selectedParent) {
    console.log("Loading parents")
    let parentList = $("#parentList");
    $.get(thingSvcUrl + "/api/controllers("+controllerId+")/things(" + pluginId + ")", function(data) {
        if(data.thingId === pluginId && data.type === "plugin") {
            if(selectedParent && selectedParent === data.thingId) {
                parentList.append(new Option(data.thingId, data.thingId, true, true));
            } else {
                parentList.append(new Option(data.thingId, data.thingId, false, false));
            }
        }
    }).fail(function(jqXHR) {
        console.log("Plugin: " + pluginId + " not found on controller: " + controllerId);
    });
}

function loadExistingData(data) {
    $("#thingId").val(data.thingId);
    $("#friendlyName").val(data.friendlyName);
    $("#controllerList").val(data.controllerId);
    $("#pluginid").val(data.pluginId);
    loadControllers(data.pluginId, data.controllerId);
    loadParentList(data.controllerId, data.pluginId, data.parentId);

    $.each(data.properties, function (key, val) {
        console.log("Rendering property: " + key + " with value: " + val);
        $("#" + key).val(val);
    });
}

function getSelectedController() {
    return $("#controllerList").find('option:selected').val();
}
function getSelectedParent() {
    return $("#parentList").find('option:selected').val();
}

function loadControllers(pluginId, selectedController) {
    $.get(thingSvcUrl + "/api/controllers", function(data){
        if(!isEmpty(data)) {
            let list = $("#controllerList");
            list.empty();

            if(data.length > 1) {
                list.append(new Option("", ""));
            }

            $.each(data, function (i, ci) {
                if(selectedController && ci.controllerId === selectedController) {
                    list.append(new Option(ci.controllerId, ci.controllerId, true, true));
                } else {
                    list.append(new Option(ci.controllerId, ci.controllerId, false, false));
                }

            })

            loadParentList(getSelectedController(), pluginId);
        }
    });
}


function loadPlugins() {
    $("#pluginList").empty();

    $.get(thingSvcUrl + "/api/system/plugins", function(data){
        $.each(data, function (i, pi) {
            let data = {
                "pluginId" : pi.pluginId,
                "name" : pi.friendlyName
            }
            renderAndAppend("pluginTemplate", data, "pluginList");
        })

        let pluginId = getPluginId();
        if(pluginId !== undefined) {
            $(".pluginButton[pluginId='" + pluginId + "']").addClass('active');
            $("#labelPlugin").html(pluginId);
            $("#schemaPanel").removeClass("fade");
            loadSchemas(pluginId);
        }
    })
}

function loadSchemas(pluginId) {
    $("#schemaList").empty();
    $.get(thingSvcUrl + "/api/system/plugins(" + pluginId + ")/schemas", function(data) {
        $.each(data, function (i, si) {
            let data = {
                "pluginId": si.pluginId,
                "schemaId": si.schemaId
            }
            renderAndAppend("schemaTemplate", data, "schemaList");
        })
        let schemaId = getSchemaId();
        if(schemaId !== undefined) {
            $(".schemaButton[schemaId='" + schemaId + "']").addClass('active');
        }
    })

}


function getPluginId() {
    let root = $("#root")
    return root.attr("pluginId");
}
function getSchemaId() {
    let root = $("#root")
    return root.attr("schemaId");
}
