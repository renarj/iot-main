var stompClient = null;

function connect() {
    console.log("Connecting to websocket");
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/state', function(frame){
            handleStateUpdate(JSON.parse(frame.body));
        });
    });
}

function handleStateUpdate(state) {
    console.log("Received a state update: " + state);

    var itemId = state.itemId;
    var stateItems = state.stateItems;
    $.each(stateItems, function (i, stateItem) {
        var label = stateItem.label;
        if(label == "on-off") {
            console.log("Received an on-off event: " + stateItem.value.value);

            var iSwitch = $("input[name=" + itemId + "_switch]");

            var value = stateItem.value.value;
            if(value == "off") {
                //set switch to off and not trigger event
                iSwitch.bootstrapSwitch("state", false, true);
            } else {
                //set switch to on and not trigger event
                iSwitch.bootstrapSwitch("state", true, true);
            }
        } else if(label == "value") {
            //slider value potentially
            var iDimmer = $("input[name=" + itemId + "_slider]");
            if (iDimmer.length) {
                console.log("Setting slider value: " + stateItem.value.value + " for device: " + itemId)
                iDimmer.slider('setValue', stateItem.value.value);
            } else {
                setLabelValue(itemId, label, stateItem);
            }
        } else if(label == "rgb") {
            console.log("Setting rgb: " + stateItem.value.value + " for device: " + itemId);

            var colorPicker = $("input[name=" + itemId + "_color]");
            colorPicker.spectrum("set", stateItem.value.value);

        } else {
            //most likely a raw value on a label
            setLabelValue(itemId, label, stateItem)
        }
    })
}

function setLabelValue(itemId, label, stateItem) {
    console.log("Checking for label for item: " + itemId + " with label: " + label);
    var valueLabel = $("label[itemId=" + itemId + "][labelId=" + label + "]");

    var rawValue = stateItem.value.value;
    if(isNumeric(rawValue)) {
        rawValue = parseFloat(rawValue).toFixed(2);
    }
    if (valueLabel.length) {
        valueLabel.text(rawValue);
    }

    var graphs = $("li.graph[itemId=" + itemId + "][labelId=" + label + "]");
    if(graphs.length > 0) {
        $.each(graphs, function(i, graph) {
            var widgetId = graph.getAttribute("id");
            console.log("Updating graph with id: " + widgetId);

            var widget = $("#" + widgetId);
            var series = widget.highcharts().series;
            var time = (new Date).getTime();

            console.log("Adding datapoint: " + time + " val: " + rawValue);
            series[0].addPoint([time, parseInt(rawValue)]);
        });
    }
}

function isNumeric(n) {
    return !isNaN(parseFloat(n)) && isFinite(n);
}

function tabChange(e) {
    e.preventDefault();

    var currentDashboard = $("#dashboards").attr("dashboardId");
    $("#dashlink_" + currentDashboard).removeClass('active');

    var targetDashboardId = this.getAttribute('dashboardId');
    $("#dashlink_" + targetDashboardId).addClass('active');

    console.log("Tab change target dash: " + targetDashboardId);

    renderDashboard(targetDashboardId);
}

function tabAdd(e) {
    e.preventDefault();

    $("#createContainerForm").attr("mode", "dashboard");
    $("#addContainerLabel").text("Add a dashboard");

    $("#containerModal").modal('show');

    console.log("Adding tab");
}

function renderDashboardsLinks() {
    $.get("/dashboards/", function(data) {
        var dashboardTabs = $('#dashboards');
        dashboardTabs.empty();

        $.each(data, function(i, item) {
            var tabClass = "notActive";
            if(i == 0) {
                tabClass = "active";
            }

            var data = {"dashboardId": item.id, "dashboardName": item.name, "weight": item.weight, "tabClass" : tabClass};
            dashboardTabs.append(renderTemplate("tabTemplate", data));
        });

        dashboardTabs.append("<li role=\"presentation\"><a class=\"addTab\" href=\"#add\"><span class=\"glyphicon glyphicon-plus\"></span></a></li>");

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
    var containerId = item.id;
    var name = item.name;

    console.log("Rendering container: " + containerId + " name: " + name);

    var data = {
        "containerId" : containerId,
        "name" : name
    };
    var layout = item.properties.layout;
    var templateName = "containerTemplateList";
    if(layout == "grid") {
        templateName = "containerTemplateGrid";
    }

    var rendered = renderTemplate(templateName, data);
    $("#container").append(rendered);
    var container = $("ul[containerId=" + containerId + "]");
    // handleReordering(container);

    renderContainerItems(containerId);
}

function handleReordering(container) {
    container.disableSelection();
    container.sortable({
        revert: true,
        connectWith: ".sortable",
        update: function( event, ui ) {
            console.log("Parent: " + $(this).attr("containerId"));

            var column = $(this).attr("column");

            $(this).children("li").each(function(index) {
                var widgetId = $(this).attr("id");
                console.log("Widget: " + widgetId + " position: " + index);

                $.ajax({url: "/ui/items/(" + widgetId + ")/setProperty(index," + index + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
                $.ajax({url: "/ui/items/(" + widgetId + ")/setProperty(column," + column + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
            });
        },
        receive: function( event, ui ) {
            var containerId = $(this).attr("containerId");
            var widgetId = ui.item.attr("id");

            console.log("Setting Parent: " + containerId + " for widget: " + widgetId);

            $.ajax({url: "/ui/widgets/(" + widgetId + ")/setParent(" + containerId + ")", type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8"});
        }
    });
}

function renderContainerItems(containerId) {
    $.get("/ui/containers(" + containerId + ")/widgets", function (data) {
        $.each(data, function (i, item) {
            renderWidget(containerId, item);
        })
    })
}

function renderWidget(containerId, item) {
    console.log("Rendering widget: " + item.id + " in container: " + containerId);
    var widgetType = item.widgetType;
    switch (widgetType.toLowerCase()) {
        case "drive":
            renderJoystick(item, containerId);
            break;
        // case "dimmer":
        //     renderSlider(item, containerId);
        //     break;
        // case "label":
        //     renderLabel(item, containerId);
        //     break;
        // case "graph":
        //     renderGraph(containerId, item);
        //     break;
        // case "color":
        //     renderColorPicker(containerId, item);
        //     break;
        default:
            console.log("Unsupported widget type: " + widgetType + " for item: " + item.name);
    }
}

function renderJoystick(item, containerId) {
    var robotId = item.properties.robotId;
    var data = {
        "widgetId": item.id,
        "name": item.name,
        "robotId": robotId,
        "index" : item.properties.index
    };

    renderWidgetTemplate("joystickTemplate", data, item, containerId);

    var m = nipplejs.create({
        zone: document.getElementById(item.id + "_joystick"),
        mode: 'static',
        position: {left: '50%', right: '50%', top: '50%'},
        color: 'blue'
    });

    m.on('end', function(nipplejs, event) {
        console.log("End of movement");

        var command = {
            "controllerId": robotId,
            "itemId": "drive",
            "commandType":"stop"
        };
        sendCommand(command);
    });

    m.on('dir:up plain:up dir:left plain:left dir:down ' +
        'plain:down dir:right plain:right', function(nipplejs, event) {
        var x = event.direction.x;
        var y = event.direction.y;
        var angle = event.direction.angle;
        var direction = "stop";
        var standardY = y === 'up' ? 'forward' : 'backward';


        if(y === angle) {
            direction = standardY;
        } else if (x === angle) {
            direction = x;
        } else {

        }

        console.log("Joystick direction X: %s Y: %s angle: %s", x, y, angle);

        var command = {
            "controllerId": robotId,
            "itemId": "drive",
            "commandType":"direction",
            "properties": {
                "direction": direction,
                "speed": "1023"
            }
        };
        sendCommand(command);
    })
}

function sendCommand(command) {
    var commandSvcLocation = $("#commandsvc").attr('location');
    var jsonData = JSON.stringify(command);
    console.log("Sending command %s to service: %s", jsonData, commandSvcLocation)


    $.ajax({url: commandSvcLocation, type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
        console.log("Posted Command successfully");
    }})
}

function renderWidgetTemplate(templateName, data, item, containerId) {
    var rendered = renderTemplate(templateName, data);
    appendContainer(rendered, item.id, item.properties.column, item.properties.index, containerId)
}

function forceUpdateDeviceState(itemId) {
    $.get("/data/state(" + itemId + ")", function(data){
        if(!isEmpty(data)) {
            handleStateUpdate(data);
        }
    });
}

function appendContainer(widgetHtml, index, columnId, widgetId, containerId) {
    if ($("#" + widgetId).length > 0) {
        //widget already exists
    } else {
        var container = $("div[containerId=" + containerId + "]");
        var mode = container.attr("mode");
        if(mode == "list") {
            var list = $("ul[containerId=" + containerId + "]");

            console.log("Drawing a list widget");
            //list.append(widgetHtml);

            insertInColumn(widgetHtml, index, list);
        } else if(mode == "grid") {
            console.log("Drawing in a grid");

            //var currentColumn = container.attr("currentColumn");
            var column = $("ul[containerId=" + containerId + "][column=" + columnId + "]");
            //column.append(widgetHtml);

            insertInColumn(widgetHtml, index, column);

            var nextColumn = column.attr("next");
            container.attr("currentColumn", nextColumn);
        }
    }
}

function insertInColumn(widgetHtml, index, column) {
    //var childWidgets = column.children("li");
    column.append(widgetHtml);

    column.sort(function(a,b){
        return a.attr("index") > b.attr("index");
    })
}



function getCurrentColumn(containerId) {
    var container = $("div[containerId=" + containerId + "]");
    var mode = container.attr("mode");
    if(mode == "grid") {
        return container.attr("currentColumn");
    } else {
        return 0;
    }
}

function getCurrentWidgetSize(containerId, columnId) {
    var container = $("div[containerId=" + containerId + "]");
    var mode = container.attr("mode");
    if(mode == "grid") {
        var column = $("ul[containerId=" + containerId + "][column=" + columnId + "]");
        return column.children("li").length;
    } else {
        var list = $("ul[containerId=" + containerId + "]");
        return list.children("li").length;
    }
}

function renderTemplate(templateName, data) {
    var template = $('#' + templateName).html();
    Mustache.parse(template);
    return Mustache.render(template, data);
}

function renderAndAppend(templateName, data, elementId) {
    $("#" + elementId).append(renderTemplate(templateName, data));
}

function isEmpty(str) {
    return (!str || 0 === str.length);
}

$(document).ready(function() {
    renderDashboardsLinks();
    renderDefaultDashboard();

    // connect();
});