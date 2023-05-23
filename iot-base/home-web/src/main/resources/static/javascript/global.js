function renderTemplate(templateName, data) {
    let templateSource = $('#' + templateName).html();
    let template = Handlebars.compile(templateSource);

    return template(data);
    // Mustache.parse(template);
    // return Mustache.render(template, data);
}

function renderAndAppend(templateName, data, elementId) {
    $("#" + elementId).append(renderTemplate(templateName, data));
}

function isEmpty(str) {
    return (!str || 0 === str.length);
}
