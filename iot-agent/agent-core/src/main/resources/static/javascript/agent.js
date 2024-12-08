
$(document).ready(function() {
    $("#storeConfig").click(function (e) {
        e.preventDefault();

        var controllerId = $("#controllerId").val();
        var mqttHost = $("#mqttHost").val();
        var mqttPort = $("#mqttPort").val();
        var apiToken = $("#apiToken").val();
        var thingService = $("#thingService").val();
        var stateService = $("#stateService").val();

        console.log("ControllerId: '" + controllerId + "' mqtt details: '" + mqttHost + ":" + mqttPort + "' api token: '" + apiToken + "' service base: '" + thingService + "'");

        var data = {
            "controllerId" : controllerId,
            "mqttHost" : mqttHost,
            "mqttPort" : mqttPort,
            "apiToken" : apiToken,
            "thingService" : thingService,
            "stateService": stateService
        }
        var jsonData = JSON.stringify(data);
        $.ajax({url: "/config", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", done: function() {
                console.log("Posted Configuration Successfully");
            }})

    });
});