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
        "schemaId" : schemaId,
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
        loadControllers(pluginId, getControllerId(), function() {
            loadProperties(pluginId, thingData, data);
        });
    })

    $("#controllerList").change(function () {
        let controllerId = getSelectedController();
        loadParentList(controllerId, pluginId);

        reloadLinkages();
    })
}

function loadProperties(pluginId, thingData, data) {
    $("#thingForm").attr("schemaId", data.schemaId)

    $("#templateText").html(data.template);
    $("#pluginid").val(pluginId);
    $("#type").val(data.type);

    $.each(data.properties, function (key, val) {
        let defVal = val.defaultValue;
        let field = $("#" + key);

        if(val.fieldType === "LINK") {
            renderAndAppend("propertyLinkTemplate", {"field": key, "schemaId": defVal}, "thingForm")
            loadLinkages(key, defVal);
        } else {
            renderAndAppend("propertyTemplate", {"field": key}, "thingForm")
            if(val.fieldType === "STATIC_DEFAULT") {
                field.prop( "disabled", true);
                field.val(defVal);
            }
            if(val.fieldType === "TEXT") {
                field.val(defVal);
            }
            if(val.fieldType === "DYNAMIC") {
                loadDynamicField();
            }
        }
    })

    if(thingData) {
        loadExistingData(thingData);
    }
}

function reloadLinkages() {
    $(".linkage").each(function(i, e) {
        let field = e.getAttribute("field");
        let schemaId = e.getAttribute("schemaId");

        console.log("Loading linkages for field: " + field + " and plugin: " + plugin);
        loadLinkages("#" + field, plugin);
    })
}

function loadLinkages(field, pluginId) {
    let controllerId = getSelectedController();
    console.log("Loading linkages for field: " + field + " and plugin: " + pluginId + " on controller: " + controllerId);
    $.get(thingSvcUrl + "/api/controllers("+controllerId+")/schemas(" + pluginId + ")/things", function(data) {
        console.log("Retrieved link field items")
        let linkList = $("#" + field);

        $.each(data, function (i, linkItem) {
            console.log("Adding list option: " + linkItem.thingId)
            linkList.append(new Option(linkItem.thingId, linkItem.thingId, false, false));
        })
    }).fail(function(jqXHR) {
        console.log("Linked Items: " + pluginId + " not found on controller: " + controllerId);
    });
}

function loadDynamicField() {

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
    parentList.append(new Option(controllerId, controllerId, false, false));
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

function loadControllers(pluginId, selectedController, callback) {
    $.get(thingSvcUrl + "/api/controllers", function(data){
        if(!isEmpty(data)) {
            let list = $("#controllerList");
            list.empty();

            if(data.length > 1) {
                list.append(new Option("", ""));
            }

            $.each(data, function (i, ci) {
                if(selectedController && ci.controllerId === selectedController || data.length === 1) {
                    list.append(new Option(ci.controllerId, ci.controllerId, true, true));
                } else {
                    list.append(new Option(ci.controllerId, ci.controllerId, false, false));
                }
            })

            loadParentList(getSelectedController(), pluginId);

            callback();
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
