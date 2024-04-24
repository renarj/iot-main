$(document).ready(function() {
    retrieveServiceUrls(function() {
        initPage();
    })
});

function initPage() {
    loadControllers();
    loadPlugins();

    $("#addController").click(function() {
        let controllerId = $("#controllerId").val();
        let orgId = $("#orgId").val();

        let controllerData = {
            "controllerId" : controllerId,
            "orgId" : orgId
        }

        let jsonData = JSON.stringify(controllerData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: thingSvcUrl + "/api/controllers(" + controllerId + ")", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
                console.log("Posted Controller successfully")

                $('#controllerForm').modal('hide');

            }})
    })

    $("#removeController").click(function() {
        let controllerId = getControllerId();
        $("#removeControllerLabel").html("'" + controllerId + "'");
        $("#confirmDeleteDialog").modal('show');
    })

    $("#removeControllerConfirm").click(function() {
        let controllerId = getControllerId();

        console.log("Deleting Controller: " + controllerId);
        $.ajax({url: thingSvcUrl + "/api/controllers(" + controllerId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Removed Controller successfully");
                location.href = "/web/admin/controllers";
            }}).fail(function(jqXHR) {
                console.log("Failed to delete controller: " + controllerId);
        });
    })

    $("#addPlugin").click(function() {
        let controllerId = getControllerId();
        let selectedPlugin = $("#pluginList").find('option:selected').val();

        let data = {
            "pluginId" : selectedPlugin
        }
        let jsonData = JSON.stringify(data);
        console.log("Installing plugin: " + jsonData)
        $.ajax({url: thingSvcUrl + "/api/controllers(" + controllerId + ")/plugins", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
                console.log("Installed Plugin successfully")

                $('#pluginForm').modal('hide');
            }})
    });
}

function getControllerId() {
    let root = $("#root")
    return root.attr("controllerId");
}

function loadControllers() {
    $.get(thingSvcUrl + "/api/controllers", function(data) {
        $.each(data, function (i, controller) {
            let data = {
                "controllerId": controller.controllerId
            };

            renderAndAppend("controllerTemplate", data, "controllerList");
        })

        let controllerId = getControllerId();
        if(controllerId !== undefined) {
            $(".controllerButton[controllerId=" + controllerId + "]").addClass('active');

            loadController(controllerId);
        }
    });
}

function loadPlugins() {
    let pluginList = $("#pluginList");

    $.get(thingSvcUrl + "/api/system/plugins", function(data) {
        $.each(data, function (i, plugin) {
            pluginList.append(new Option(plugin.pluginId, plugin.pluginId, false, false));
        })
    })
}

function loadController(controllerId) {
    $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")", function(data) {
        let thingData = {
            "controllerId" : data.controllerId,
            "orgId": data.orgId,
            "properties": data.properties
        }
        renderAndAppend("controllerDetailTemplate", thingData,"thingDetails");

        $("#thingListDetailLabel").html(controllerId);

        $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/children", function(data) {
            $.each(data, function (i, child) {
                let data = {
                    "controllerId": controllerId,
                    "thingId": child.thingId
                };

                renderAndAppend("thingTemplate", data, "thingList");
            })

            if(data.length === 0) {
                $("#thingList").html("No Children")
            }
        });

    });
}
