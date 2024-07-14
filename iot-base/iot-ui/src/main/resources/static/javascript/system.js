$(document).ready(function() {
    console.log("Document loaded");

    loadControllers();

    $("#openModalStation").click(function() {
        $.get("/api/controllers", function(data){
            if(!isEmpty(data)) {
                let list = $("#formControllerList");
                list.empty();

                if(data.length >= 1) {
                    list.append(new Option("No Controller", "No Controller", true, true));
                }

                $.each(data, function (i, ci) {
                    list.append(new Option(ci.controllerId, ci.controllerId, false, false));
                })
            }
        });
    })

    $("#addCSBtn").click(function(event) {
        event.preventDefault();

        let thingId = $("#uniqueId").val();
        let controllerId = $("#formControllerList").find('option:selected').val();
        let stationName = $("#stationName").val();
        let csType = $("#csType").find('option:selected').val();
        let dcIp = $("#dcIp").val();
        let dcPort = $("#dcPort").val();

        let commandStationData = {
            "thingId" : thingId,
            "controllerId" : controllerId,
            "pluginId" : "trainAutomationExtension",
            "parentId" : "trainAutomationExtension",
            "friendlyName" : stationName,
            "type":"commandstation",
            "properties": {
                "commandCenterType" : csType,
                "Z21_HOST" : dcIp,
                "Z21_PORT" : dcPort
            }
        }

        let jsonData = JSON.stringify(commandStationData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: "/api/controllers(" + controllerId + ")/things", type: "POST", data: jsonData, dataType: "json",
            contentType: "application/json; charset=utf-8", success: function() {
                console.log("Posted Command Station successfully")

                $('#commandStationForm').modal('hide');
            }})
    });

    $(".removeStation").on('click', function(event) {
        event.preventDefault();

        let thingId = this.getAttribute('thingId');
        let controllerId = this.getAttribute('controllerId');
        console.log("Command Station: " + thingId + " to be removed on controller: " + controllerId);

        $.ajax({url: "/api/controllers(" + controllerId + ")/things(" + thingId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Removed Command station successfully");
            }});
    })
});

function loadControllers() {
    $.get("/api/controllers", function(data) {
        $.each(data, function (i, controller) {
            let data = {
                "controllerId": controller.controllerId
            };

            renderAndAppend("controllerTemplate", data, "controllerList");
        })

        $(".controllerButton").on('click', function(event) {
            event.preventDefault();

            $(".controllerButton").removeClass('active');

            let controllerId = this.getAttribute('controllerId');
            this.classList.add('active');
            console.log("Clicked on controller: " + controllerId);
            $("#commandStationPanel").removeClass('fade');

            loadCommandStations(controllerId);
        })
    });


}

function loadCommandStations(controllerId) {
    $.get("/api/controllers(" + controllerId + ")/plugins(trainAutomationExtension)/things?type=commandstation", function(data){
        $("#stationList").empty();

        $.each(data, function (i, commandStation) {
            let data = {
                "controllerId": commandStation.controllerId,
                "thingId":commandStation.thingId,
                "name":commandStation.friendlyName
            };

            renderAndAppend("commandStationTemplate", data, "stationList");
        })

        $(".commandStationButton").click(function(event) {
            event.preventDefault();

            $(".commandStationButton").removeClass('active');

            this.classList.add('active');
            let controllerId = this.getAttribute('controllerId');
            let thingId = this.getAttribute('thingId');

            $.get("/api/controllers(" + controllerId + ")/things(" + thingId+ ")", function(thingData) {
                let data = {
                    "controllerId": thingData.controllerId,
                    "thingId":thingData.thingId,
                    "name":thingData.friendlyName,
                    "stationType":thingData.properties.commandCenterType,
                    "ip":thingData.properties.Z21_HOST,
                    "port":thingData.properties.Z21_PORT,
                    "properties":thingData.properties
                }

                $("#contentPanel").empty();
                renderAndAppend("commandStationDetailTemplate", data, "contentPanel");
            });


        });


    })
}