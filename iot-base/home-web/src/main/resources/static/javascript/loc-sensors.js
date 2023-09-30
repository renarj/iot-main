$(document).ready(function() {
    console.log("Document loaded");

    loadControllers();

    $("#addSensorBtn").click(function(event) {
        event.preventDefault();

        let currentController = $(".controllerButton.active").attr("controllerId");
        console.log("Showing form: " + currentController)

        $.get("/api/controllers", function(data){
            if(!isEmpty(data)) {
                let list = $("#formControllerList");
                list.empty();

                $.each(data, function (i, ci) {
                    let selected = false;
                    if(currentController === ci.controllerId) {
                        selected = true;

                        loadCommandStations(ci.controllerId);
                    }

                    list.append(new Option(ci.controllerId, ci.controllerId, selected, selected));
                })
            }
        });
    });

    $("#addSensorBtnForm").click(function(event) {
        event.preventDefault();

        let thingId = $("#uniqueId").val();
        let controllerId = $("#formControllerList").find('option:selected').val();
        let stationId = $("#stationList").find('option:selected').val();
        let sensorType = $("#sensorType").find('option:selected').val();
        let name = $("#sensorName").val();
        let sensorAddress = $("#sensorAddress").val();
        let sensorLength = $("#sensorLength").val();

        let sensorData = {
            "thingId" : thingId,
            "controllerId" : controllerId,
            "pluginId" : "trainAutomationExtension",
            "parentId" : stationId,
            "friendlyName" : name,
            "type":"sensor",
            "attributes": ["occupied", "speed", "direction", "loc"],
            "properties": {
                "type" : sensorType,
                "address" : sensorAddress,
                "length": sensorLength
            }
        }

        let jsonData = JSON.stringify(sensorData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: "/api/controllers(" + controllerId + ")/things", type: "POST", data: jsonData, dataType: "json",
            contentType: "application/json; charset=utf-8", success: function() {
                console.log("Posted Sensor successfully")

                $('#sensorForm').modal('hide');
            }})
    });
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

            loadSensors(controllerId);
        })
    });
}

function loadSensors(controllerId) {
    $.get("/api/controllers(" + controllerId + ")/plugins(trainAutomationExtension)/things?type=sensor", function(data) {
        $("#sensorList").empty();

        $.each(data, function (i, sensor) {
            let sensorData = {
                "controllerId": sensor.controllerId,
                "thingId": sensor.thingId,
                "name": sensor.friendlyName,
                "address": sensor.properties.address,
                "commandStation": sensor.parentId,
                "type" : sensor.properties.type,
                "length" : sensor.properties.length
            };

            renderAndAppend("sensorTemplate", sensorData, "sensorList");
        })
    });
}

function loadCommandStations(controllerId) {
    $.get("/api/controllers(" + controllerId + ")/plugins(trainAutomationExtension)/things?type=commandstation", function(data){
        let stationList = $("#stationList");
        stationList.empty();

        $.each(data, function (i, commandStation) {
            let stationId = commandStation.thingId;

            stationList.append(new Option(stationId, stationId, false, false));
        })
    })
}
