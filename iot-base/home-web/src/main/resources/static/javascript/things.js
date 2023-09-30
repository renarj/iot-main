$(document).ready(function() {
    loadControllers();
});

function loadControllers() {
    $.get("/api/controllers", function(data) {
        $.each(data, function (i, controller) {
            let data = {
                "controllerId": controller.controllerId
            };

            renderAndAppend("controllerTemplate", data, "controllerList");
        })

        let root = $("#root")
        let controllerId = root.attr("controllerId");
        let thingId = root.attr("thingId");
        if(controllerId !== undefined) {
            $(".controllerButton[controllerId=" + controllerId + "]").addClass('active');

            if(thingId !== undefined) {
                loadThing(controllerId, thingId);
            } else {
                loadControllerThings(controllerId);
            }
        }
    });
}

function loadControllerThings(controllerId) {
    $("#thingListLabel").html(controllerId);
    $("#thingList").empty();
    loadSelectedControllerDetail(controllerId);

    $.get("/api/controllers(" + controllerId + ")/children", function(data) {
        $.each(data, function (i, child) {
            let data = {
                "controllerId": controllerId,
                "thingId": child.thingId
            };

            renderAndAppend("thingTemplate", data, "thingList");
        })
    });
}

function loadSelectedControllerDetail(controllerId) {
    $("#thingDetails").empty();
    $("#breadCrumb").empty();
    $("#thingListDetailLabel").html(controllerId);
    $.get("/api/controllers(" + controllerId + ")", function(data) {
        let thingData = {
            "controllerId" : data.controllerId,
            "orgId": data.orgId,
            "proeprties": data.properties
        }
        renderAndAppend("controllerDetailTemplate", thingData,"thingDetails");
    });
}

function renderBreadCrumb(controllerId, parentId, thingId) {
    let data = {
        "controllerId": controllerId,
        "thingId": thingId
    };
    if(controllerId !== parentId) {
        data["parentId"] = parentId;
    }

    $("#breadCrumb").empty();
    renderAndAppend("breadCrumbTemplate", data, "breadCrumb");
}

function loadThing(controllerId, thingId) {
    $("#thingListLabel").html(thingId);
    $("#thingListDetailLabel").html(thingId);
    $("#thingList").empty();

    $.get("/api/controllers(" + controllerId + ")/things(" + thingId + ")", function(data) {
        renderBreadCrumb(controllerId, data.parentId, data.thingId);

        $("#thingDetails").empty();
        let thingData = {
            "controllerId" : data.controllerId,
            "thingId" : data.thingId,
            "type": data.type,
            "name": data.friendlyName,
            "attributes": data.attributes,
            "properties": data.properties
        }
        renderAndAppend("thingDetailTemplate", thingData,"thingDetails");

        $("#stateTemplate").empty();
        $.get("/api/state/controllers(" + controllerId + ")/things(" + thingId + ")", function (stateData) {
            $.each(stateData.stateItems, function (i, stateItem) {
                console.log("Render stateItem: " + stateItem.attribute);
                let stateData = {
                    "a" : stateItem.attribute,
                    "v" : stateItem.value.value
                }

                renderAndAppend("stateDetailTemplate", stateData,"stateList");
            });
        }).fail(function(jqXHR) {
            $("#stateList").html("No known State");
        });

        $.get("/api/controllers(" + controllerId + ")/children(" + thingId + ")", function(data) {
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