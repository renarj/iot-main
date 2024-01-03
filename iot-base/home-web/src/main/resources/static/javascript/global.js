let thingSvcUrl = "";
let stateSvcUrl = "";
let commandSvcUrl = "";

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
