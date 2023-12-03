$(document).ready(function() {
    retrieveServiceUrls(function() {
        initPage();
    })
});

function initPage() {
    console.log("Document loaded");
    loadPlugins();

    $("#addPlugin").click(function() {
        let pluginId = $("#pluginId").val()
        let name = $("#pluginName").val();

        let locData = {
            "pluginId" : pluginId,
            "friendlyName" : name
        }

        let jsonData = JSON.stringify(locData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: thingSvcUrl + "/api/system/plugins", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
                console.log("Posted Plugin successfully")

                $('#pluginForm').modal('hide');
                loadPlugins();
            }})
    });

    $("#removePluginButton").click(function (){
        let pluginId = this.getAttribute('pluginId');
        console.log("Removing plugin: " + pluginId);

        $.ajax({url: thingSvcUrl + "/api/system/plugins(" + pluginId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Removed Plugin successfully");
                loadPlugins();
                $("#schemaList").empty();
            }});
    });

    $("#addRow").click(function() {
        console.log("Adding row to list")
        renderAndAppend("propertyTemplate", {
            "uniqueId": Math.floor(Math.random() * Date.now())
        }, "propertyList");

        $(".removeRow").click(function() {
            let rowId = $(this).attr("rowId");
            console.log("Removing row: " + rowId + " from list");
            $("#" + rowId).remove();
        })
    });

    $("#editSchemaButton").click(function() {
        let pluginId = getPluginId();
        let schemaId = getSchemaId();

        $('#schemaForm').modal('show');

        $.get(thingSvcUrl + "/api/system/plugins(" + pluginId + ")/schemas(" + schemaId + ")", function(sData) {
            $("#schemaId").val(schemaId);
            $("#instructionText").val(sData.template);
            $("#schemaType").val(sData.type);

            $("#propertyList").empty();
            $.each(sData.properties, function (key, val) {
                let uniqueId = Math.floor(Math.random() * Date.now());

                data = {
                    "uniqueId": uniqueId,
                    "description": key,
                    "defaultValue": val.defaultValue
                }

                renderAndAppend("propertyTemplate", data, "propertyList");
                $("#" + uniqueId + "-type").val(val.fieldType);
            })
        })
    });

    $("#addSchema").click(function () {
        let schemaId = $("#schemaId").val();
        let template = $("#instructionText").val();
        let type = $("#schemaType").val();
        let pluginId = getPluginId();

        let properties = {};
        $(".propertyRow").each(function(index) {
            let propertyId = $(this).attr("id");
            let property = $("#" + propertyId + "-property").val();
            let propertyType = $("#" + propertyId + "-type").val();
            let propertyDefault = $("#" + propertyId + "-default").val();

            console.log("Property: " + property + " with type: " + propertyType)
            properties[property] = {
                "fieldType":propertyType,
                "defaultValue":propertyDefault
            };
        });
        let schemaData = {
            "schemaId" : schemaId,
            "pluginId" : pluginId,
            "template" : template,
            "type": type,
            "properties" : properties
        }
        let jsonData = JSON.stringify(schemaData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: thingSvcUrl + "/api/system/schemas", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Posted Schema successfully")
                loadPlugins();
                $('#schemaForm').modal('hide');
        }})
    });

    $("#removeSchemaButton").click(function() {
        let pluginId = getPluginId();
        let schemaId = getSchemaId();

        console.log("Deleting schema: '" + schemaId + "' on plugin: " + pluginId);
        $.ajax({url: thingSvcUrl + "/api/system/plugins(" + pluginId + ")/schemas(" + schemaId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Removed Schema successfully");
                loadPlugins();
                $("#schemaList").empty();
                $("#detailPanel").addClass("fade");
                $("#root").removeAttr("schemaId")
            }});
    })
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
            $("#removePluginButton").attr("pluginId", pluginId);
            $("#schemaPanel").removeClass("fade");

            loadSchemas(pluginId);
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


function loadSchemas(pluginId) {
    $("#schemaList").empty();
    $("#schemaDetailPanel").empty();
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
            $("#schemaLabel").html(schemaId);
            $("#detailPanel").removeClass("fade");
            let removeButton = $("#removeSchemaButton");
            removeButton.attr("pluginId", pluginId);
            removeButton.attr("schemaId", schemaId);
            loadSchema(pluginId, schemaId);
        }
    })

}

function loadSchema(pluginId, schemaId) {
    $.get(thingSvcUrl + "/api/system/plugins(" + pluginId + ")/schemas(" + schemaId + ")", function(sData) {
        let data = {
            "schemaId" : sData.schemaId,
            "pluginId" : sData.pluginId,
            "type" : sData.type,
            "template" : sData.template,
            "properties" : sData.properties
        }

        renderAndAppend("schemaDetails", data, "schemaDetailPanel");
    })
}