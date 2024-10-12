let thingSvcUrl = "";
let stateSvcUrl = "";
let commandSvcUrl = "";

function getControllerId() {
    let root = $("#root")
    return root.attr("controllerId");
}

function getThingId() {
    let root = $("#root")
    return root.attr("thingId");
}

function sendCommand(data, successCallback) {
    $.ajax({
        url: commandSvcUrl + "/api/command/",
        data: JSON.stringify(data),
        type: "POST",
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            if(successCallback !== undefined) {
                successCallback();
            }
        }
    });
}

function loadControllerTemplate(templateName, controllerList, callback) {
    $.get(thingSvcUrl + "/api/controllers", function(data) {
        $.each(data, function (i, controller) {
            let data = {
                "controllerId": controller.controllerId
            };

            renderAndAppend(templateName, data, controllerList);
        })

        if(callback) {
            callback();
        }
    });
}

function retrieveServiceUrls(callback) {
    $.get("/web/serviceLocations", function(sData) {
        thingSvcUrl = sData.thingSvcUrl;
        stateSvcUrl = sData.stateSvcUrl;
        commandSvcUrl = sData.commandSvcUrl;

        console.log("Loaded state Svc URL: " + stateSvcUrl);
        console.log("Loaded thing Svc URL: " + thingSvcUrl);
        console.log("Loaded Command Svc URL: " + commandSvcUrl);
        callback();
    });
}

function renderTemplate(templateName, data) {
    let templateSource = $('#' + templateName).html();
    let template = Handlebars.compile(templateSource);

    return template(data);
}

function renderAndAppend(templateName, data, elementId) {
    $("#" + elementId).append(renderTemplate(templateName, data));
}

function isEmpty(str) {
    return (!str || 0 === str.length);
}
