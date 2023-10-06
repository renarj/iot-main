$(document).ready(function() {
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
        $.ajax({url: "/api/system/plugins", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
                console.log("Posted Plugin successfully")

                $('#pluginForm').modal('hide');
                loadPlugins();
            }})
    });

    $("#removePluginButton").click(function (){
        let pluginId = this.getAttribute('pluginId');
        console.log("Removing plugin: " + pluginId);

        $.ajax({url: "/api/system/plugins(" + pluginId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Removed Plugin successfully");
                loadPlugins();
                $("#schemaList").empty();
            }});
    });

    $("#addRow").click(function() {
        console.log("Adding row")
        renderAndAppend("propertyTemplate", {}, "propertyList");
    });

    $("#addSchema").click(function () {

    });
});

function loadPlugins() {
    $("#pluginList").empty();

    $.get("/api/system/plugins", function(data){
        $.each(data, function (i, pi) {
            let data = {
                "pluginId" : pi.pluginId,
                "name" : pi.friendlyName
            }
            renderAndAppend("pluginTemplate", data, "pluginList");
        })

        let root = $("#root")
        let pluginId = root.attr("pluginId");
        if(pluginId !== undefined) {
            $(".pluginButton[pluginId='" + pluginId + "']").addClass('active');
            $("#labelPlugin").html(pluginId);
            $("#removePluginButton").attr("pluginId", pluginId);
            $("#schemaPanel").removeClass("fade");

            loadSchemas(pluginId);
        }
    })
}




function loadSchemas(pluginId) {
    $("#schemaList").empty();
    $.get("/api/system/plugins(" + pluginId + ")/schemas", function(data) {
        $.each(data, function (i, si) {
            let data = {
                "pluginId": si.pluginId,
                "schemaId": si.schemaId
            }
            renderAndAppend("schemaTemplate", data, "schemaList");
        })
        let root = $("#root")
        let schemaId = root.attr("schemaId");
        if(schemaId !== undefined) {
            $(".schemaButton[schemaId=" + schemaId + "]").addClass('active');
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
    $.get("/api/system/plugins(" + pluginId + ")/schemas(" + schemaId + ")", function(sData) {
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