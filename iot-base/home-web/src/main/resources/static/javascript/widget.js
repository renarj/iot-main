let stompClient = null;

function connect() {
    stateSvcUrl = "http://localhost:9006"
    console.log("Connecting to websocket: " + stateSvcUrl);
    let socket = new SockJS(stateSvcUrl + "/ws");
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/state', function(frame){
            handleStateUpdate(JSON.parse(frame.body));
        });
    });
}

function handleStateUpdate(state) {
    console.log("Received a state update: " + JSON.stringify(state));

    let thingId = state.itemId;
    let controllerId = state.controllerId;
    console.log("State update for: " + thingId)

    $.each(state.stateItems, function (i, stateItem) {
        let attribute = stateItem.attribute;

        setStateValue(controllerId, thingId, attribute, stateItem.value.value);
    })
}

function setStateValue(controllerId, thingId, attribute, value) {
    let valueElement = $("[controllerId='" + controllerId + "'][attribute='" + attribute + "'][thingId='" + thingId + "']");
    valueElement.each(function(i, element) {
        let widgetType = element.getAttribute('widgetType');

        if(widgetType === "switch") {
            setSwitchValue(controllerId, thingId, attribute, value);
        } else if(widgetType === "label") {
            setLabelValue(controllerId, thingId, attribute, value);
        } else if(widgetType === "slider") {
            setSliderValue(controllerId, thingId, attribute, value);
        } else {
            console.log("Unknown widget type: " + widgetType);
        }
    });
}

function setLabelValue(controllerId, thingId, attribute, rawValue) {
    console.log("Checking for label for item: " + thingId + " controller: " + controllerId + " with attribute: " + attribute);
    let valueLabels = $("label[thingId='" + thingId + "'][attribute='" + attribute + "'][controllerId='" + controllerId + "']");
    valueLabels.each(function(i, element) {
        if(isNumeric(rawValue)) {
            rawValue = parseFloat(rawValue).toFixed(2);
        }
        element.innerText = rawValue;
    })
}

function setSliderValue(controllerId, thingId, attribute, rawValue) {
    console.log("Checking for Slider for item: " + thingId + " controller: " + controllerId + " with attribute: " + attribute);
    let sliders = $("input[thingId='" + thingId + "'][attribute='" + attribute + "'][controllerId='" + controllerId + "']");
    sliders.each(function(i, element) {
        if(isNumeric(rawValue)) {
            element.value = parseFloat(rawValue).toFixed(2);
        }
    })
}

function setSwitchValue(controllerId, thingId, attribute, rawValue) {
    let switches = $("input[thingId='" + thingId + "'][attribute='" + attribute + "'][controllerId='" + controllerId + "']");
    switches.each(function(i, element) {
        if($.type(rawValue) === "string" && rawValue.toLowerCase() === "true") {
            element.checked = true;
        } else {
            element.checked = $.type(rawValue) === "number" && rawValue > 0;
        }
    });
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function tabChange(e) {
    e.preventDefault();

    let currentDashboard = $("#dashboards").attr("dashboardId");
    $("#dashlink_" + currentDashboard).removeClass('active');

    let targetDashboardId = this.getAttribute('dashboardId');
    $("#dashlink_" + targetDashboardId).addClass('active');

    console.log("Tab change target dash: " + targetDashboardId);

    renderDashboard(targetDashboardId);
}

function tabAdd(e) {
    e.preventDefault();

    $("#createContainerForm").attr("mode", "dashboard");
    $("#addContainerLabel").text("Add a dashboard");

    new bootstrap.Modal("#containerModal").show();

    console.log("Adding tab");
}

function renderDashboardsLinks() {
    $.get("/dashboards/", function(data) {
        let dashboardTabs = $('#dashboards');
        dashboardTabs.empty();

        $.each(data, function(i, item) {
            let tabClass = "notActive";
            if(i === 0) {
                tabClass = "active";
            }

            let data = {"dashboardId": item.id, "dashboardName": item.name, "weight": item.weight, "tabClass" : tabClass};
            dashboardTabs.append(renderTemplate("tabTemplate", data));
        });

        dashboardTabs.append(renderTemplate("addDashButtonTemplate", {}));

        $(".tab").click(tabChange);
        $(".addTab").click(tabAdd);
    });
}

function renderDefaultDashboard() {
    $.get("/dashboards/default", function(data) {
        console.log("Default dashboard: " + data.id);

        renderDashboardWithName(data.name, data.id);
    });
}

function renderDashboard(dashboardId) {
    console.log("Rendering dashboard: " + dashboardId);
    $.get("/dashboards/(" + dashboardId + ")", function(data) {
        renderDashboardWithName(data.name, data.id);
    });
}

function renderDashboardWithName(name, dashboardId) {
    $("#dashboardTitle").text("Dashboard: " + name);
    $("#dashboards").attr("dashboardId", dashboardId);
    $("#container").empty();

    $.get("/ui/dashboard(" + dashboardId + ")/containers", function(data) {
        $.each(data, function(i, item) {
            renderContainer(item);
        })
    });
}

function renderContainerById(containerId) {
    $.get("/ui/containers(" + containerId + ")", function(data) {
        renderContainer(data);
    })
}

function renderContainer(item) {
    let containerId = item.id;
    let name = item.name;

    console.log("Rendering container: " + containerId + " name: " + name);

    let data = {
        "containerId" : containerId,
        "name" : name
    };
    let layout = item.properties.layout;
    let templateName = "containerTemplateList";
    if(layout === "grid") {
        templateName = "containerTemplateGrid";
    }

    let rendered = renderTemplate(templateName, data);
    $("#container").append(rendered);
    let container = $("ul[containerId=" + containerId + "]");
    handleReordering(container);

    renderContainerItems(containerId);
}

function handleReordering(container) {
    container.disableSelection();
    container.sortable({
        revert: true,
        connectWith: ".sortable",
        update: function( event, ui ) {
            console.log("Parent: " + $(this).attr("containerId"));

            let column = $(this).attr("column");

            $(this).children("li").each(function(index) {
                let widgetId = $(this).attr("id");
                console.log("Widget: " + widgetId + " position: " + index);

                $.ajax({url: "/ui/items/(" + widgetId + ")/setProperty(index," + index + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
                $.ajax({url: "/ui/items/(" + widgetId + ")/setProperty(column," + column + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
            });
        },
        receive: function( event, ui ) {
            let containerId = $(this).attr("containerId");
            let widgetId = ui.item.attr("id");

            console.log("Setting Parent: " + containerId + " for widget: " + widgetId);

            $.ajax({url: "/ui/items/(" + widgetId + ")/setParent(" + containerId + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
        }
    });
}

function renderContainerItems(containerId) {
    $.get("/ui/containers(" + containerId + ")/items", function (data) {
        $.each(data, function (i, item) {
            renderWidget(containerId, item);
        })
    })
}

function renderWidget(containerId, item) {
    console.log("Rendering widget: " + item.id + " in container: " + containerId);
    let widgetType = item.widgetType;
    switch (widgetType.toLowerCase()) {
        case "switch":
            renderSwitch(item, containerId);
            break;
        case "dimmer":
            console.log("Not implemented");
            break;
        case "label":
            renderItemLabel(item, containerId);
            break;
        case "color":
            renderColorPicker(containerId, item);
            break;
        case "default":
            renderDefault(containerId, item);
            break;
        default:
            console.log("Unsupported widget type: " + widgetType + " for item: " + item.name);
    }
}

function renderDefault(containerId, item) {
    let data = {
        "containerId" : item.id,
        "name" : item.name
    };
    let templateName = "containerTemplateList";
    let rendered = renderTemplate(templateName, data);

    appendContainer(rendered, 0, 0, item.itemId, containerId);

    $.get(thingSvcUrl + "/api/controllers(" + item.controllerId + ")/things(" + item.thingId + ")", function(data) {
        $.each(data.attributes, function(key, type) {
            if(type === "SWITCH") {
                renderSwitchWidget(item.id, key, data.thingId, data.controllerId, key, 0, 0, item.id);
            } else if(type === "ABS_POSITION" || type === "DEGREES") {
                console.log("Rendering a slider for: " + key + " for default widget for thing: " + data.thingId);
                renderDefaultSlider(item.id, key, data)
            } else {
                console.log("Rendering an attribute: " + key + " for default widget for thing: " + data.thingId);
                renderLabelWidget(key, type, item.itemId + "_" + key, key, data.thingId, data.controllerId, item.id);
            }
        });
    });
}

function renderDefaultSlider(widgetId, key, data) {
    let min = data.properties["min_" + key];
    let max = data.properties["max_" + key];
    if(min === undefined) {
        min = 0;
    }
    if(max === undefined) {
        max = 100;
    }
    renderSliderWidget(widgetId + "_" + key, key, data.thingId, data.controllerId, key, min, max, 0, 0, widgetId);
}

function renderColorPicker(containerId, item) {
    console.log("Rendering color picker widget for item: " + item.name);

    let data = {
        "widgetId": item.id,
        "name": item.name,
        "itemId": item.itemId,
        "index" : item.properties.index
    };

    renderWidgetTemplate("colorTemplate", data, item, containerId);

    let cPicker = $("input[name=" + item.itemId + "_color]");

    cPicker.spectrum({
        change: function(color) {
            handleColorChange(cPicker, color);
        },
        showButtons: false
    });
}

function handleColorChange(picker, color) {
    console.log("Target color: " + color.toHexString());

    let data = {
        "itemId" : picker.attr('itemId'),
        "commandType" : "value",
        "properties" : {
            "rgb" : color.toHexString()
        }
    };
    let jsonData = JSON.stringify(data);
    console.log("Sending command: " + jsonData);

    $.ajax({url: "/command/send", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
        console.log("Posted Command successfully");
    }})

}

function renderSliderWidget(widgetId, name, thingId, controllerId, attribute, min, max, weight, index, containerId) {
    console.log("Rendering slider for item: " + name);
    let data = {
        "widgetId": widgetId,
        "name": name,
        "thingId": thingId,
        "controllerId" : controllerId,
        "label": attribute,
        "weight" : weight,
        "min" : min,
        "max" : max,
        "index" : index
    };
    console.debug("Data: " + JSON.stringify(data));

    renderWidgetTmpl("sliderTemplate", widgetId, data, 0, 0, containerId)
    forceUpdateDeviceState(controllerId, thingId);

    $("#" + widgetId + "_position").change(function() {
        handleSliderEvent(widgetId);
    })
}

function handleSliderEvent(widgetId) {
    let positionEl = $("#" + widgetId + "_position")
    let thingId = positionEl.attr("thingId");
    let controllerId = positionEl.attr("controllerId");
    let attribute = positionEl.attr("attribute");

    let position = positionEl.val();
    let attributes = {};
    attributes[attribute] = position;

    let data = {
        "controllerId" : controllerId,
        "thingId" : thingId,
        "commandType" : "VALUE",
        "attributes" : attributes
    };
    let jsonData = JSON.stringify(data);
    console.log("Sending command for position: " + jsonData);

    $.ajax({url: commandSvcUrl + "/api/command/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", done: function() {
            console.log("Posted Command successfully");
        }})
}

function renderSwitch(item, containerId) {
    renderSwitchWidget(item.id, item.name, item.thingId, item.controllerId, item.properties.label, item.weight, item.properties.index, containerId);
}

function renderSwitchWidget(widgetId, name, thingId, controllerId, attribute, weight, index, containerId) {
    console.log("Rendering switch for item: " + name);
    let data = {
        "widgetId": widgetId,
        "name": name,
        "thingId": thingId,
        "controllerId" : controllerId,
        "label": attribute,
        "weight" : weight,
        "index" : index
    };
    console.debug("Data: " + JSON.stringify(data));

    renderWidgetTmpl("switchTemplate", widgetId, data, 0, 0, containerId)

    $("#" + widgetId + "_switch").change(function() {
        handleSwitchEvent(widgetId);
    })
    forceUpdateDeviceState(controllerId, thingId);
}

function handleSwitchEvent(widgetId) {
    let switchEl = $("#" + widgetId + "_switch")
    let thingId = switchEl.attr("thingId");
    let controllerId = switchEl.attr("controllerId");
    let attribute = switchEl.attr("attribute");

    let command = "false";
    if(switchEl.is(":checked")) {
        command = "true";
    }
    let attributes = {};
    attributes[attribute] = command;

    let data = {
        "controllerId" : controllerId,
        "thingId" : thingId,
        "commandType" : "SWITCH",
        "attributes" : attributes
    };
    let jsonData = JSON.stringify(data);
    console.log("Sending command: " + jsonData);

    $.ajax({url: commandSvcUrl + "/api/command/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", done: function() {
        console.log("Posted Command successfully");
    }})
}

function renderItemLabel(item, containerId) {
    renderLabelWidget(item.properties.label, item.properties.unit, item.id, item.name, item.thingId, item.controllerId, containerId);
}

function renderLabelWidget(attribute, unitType, widgetId, name, thingId, controllerId, containerId) {
    console.log("Rendering label widget for item: " + name + " on thing: " + thingId + " for attribute: " + attribute);

    let data = {
        "widgetId": widgetId,
        "name": name,
        "value": 0,
        "thingId": thingId,
        "controllerId": controllerId,
        "label": attribute,
        "unit": unitType
        // "index" : item.properties.index
    };

    // let rendered = renderTemplate("labelTemplate", data);
    // console.log("rendered: " + rendered)
    // appendContainer(rendered, 0, 0, 0, containerId)
    renderWidgetTmpl("labelTemplate", widgetId, data, 0, 0, containerId)

    forceUpdateDeviceState(controllerId, thingId);
}

function renderWidgetTmpl(templateName, widgetId, data, column, index, containerId) {
    let rendered = renderTemplate(templateName, data);
    console.debug("rendered: " + rendered)
    appendContainer(rendered, index, column, widgetId, containerId)

}

function renderWidgetTemplate(templateName, data, item, containerId) {
    let rendered = renderTemplate(templateName, data);
    appendContainer(rendered, item.id, item.properties.column, item.properties.index, containerId)
}

function forceUpdateDeviceState(controllerId, thingId) {
    console.log("Forcing state update: " + thingId)
    $.get(stateSvcUrl + "/api/state/controllers(" + controllerId + ")/things(" + thingId + ")", function(data){
        if(!isEmpty(data)) {
            handleStateUpdate(data);
        } else {
            console.log("No data for thing: " + thingId)
        }
    }).fail(function (jqXHR, textStatus, error) {
        console.log("Could not retrieve state update: " + textStatus + " and error: " + error);
    });
}

function appendContainer(widgetHtml, index, columnId, widgetId, containerId) {
    // if ($("#" + widgetId).length > 0) {
    //     //widget already exists
    //     console.debug("Widget already exists");
    // } else {
        let container = $("div[containerId=" + containerId + "]");
        let mode = container.attr("mode");
        if(mode === "list") {
            let list = $("ul[containerId=" + containerId + "]");

            console.debug("Drawing a list widget");
            //list.append(widgetHtml);

            insertInColumn(widgetHtml, index, list);
        } else if(mode === "grid") {
            console.debug("Drawing in a grid");

            //var currentColumn = container.attr("currentColumn");
            let column = $("ul[containerId=" + containerId + "][column=" + columnId + "]");
            //column.append(widgetHtml);

            insertInColumn(widgetHtml, index, column);

            let nextColumn = column.attr("next");
            container.attr("currentColumn", nextColumn);
        }
    // }
}

function insertInColumn(widgetHtml, index, column) {
    //var childWidgets = column.children("li");
    column.append(widgetHtml);

    column.sort(function(a,b){
        return a.attr("index") > b.attr("index");
    })
}



function getCurrentColumn(containerId) {
    let container = $("div[containerId=" + containerId + "]");
    let mode = container.attr("mode");
    if(mode === "grid") {
        return container.attr("currentColumn");
    } else {
        return 0;
    }
}

function getCurrentWidgetSize(containerId, columnId) {
    let container = $("div[containerId=" + containerId + "]");
    let mode = container.attr("mode");
    if(mode === "grid") {
        let column = $("ul[containerId=" + containerId + "][column=" + columnId + "]");
        return column.children("li").length;
    } else {
        let list = $("ul[containerId=" + containerId + "]");
        return list.children("li").length;
    }
}

$(document).ready(function() {
    retrieveServiceUrls(function() {
        renderDashboardsLinks();
        renderDefaultDashboard();

        connect();
    })
});