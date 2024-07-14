$(document).ready(function() {
    retrieveServiceUrls(pageInit);
});

function pageInit() {
    let widgetList = $("#widgetList");
    let widgetLabel = $("#widgetLabel");

    $(document).on("click", ".addWidget", function () {
        console.log("Opening widget form");

        let containerId = $(this).data('id');
        $(".modal-body #containerId").val( containerId );

        showThing();
        loadControllers();

        new bootstrap.Modal("#dataModal").show();
    });

    function showVirtual(labelText) {
        $("#virtual").removeClass("fade");
        $("#controller").addClass("fade");
        $("#virtualLabel").text(labelText);
        $("#plugin").addClass("fade");
        $("#device").addClass("fade");
    }

    function showThing() {
        $("#widgetValueTypeDiv").addClass("fade");
        $("#widgetUnitTypeDiv").addClass("fade");

        $("#virtual").addClass("fade");
        $("#controller").removeClass("fade");
        $("#plugin").removeClass("fade");
        $("#device").removeClass("fade");
    }

    $(document).on("click", ".removeWidget", function (event) {
        event.preventDefault();
        let widgetId = this.getAttribute('widgetId');

        $.ajax({url: "/ui/items(" + widgetId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Removed Widget successfully");
        }});

        $("#" + widgetId).remove();
    });

    $(document).on("click", ".removeContainer", function (event) {
        event.preventDefault();
        let containerId = this.getAttribute('containerId');

        $.ajax({url: "/ui/containers(" + containerId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Removed container successfully");
        }});

        $("#" + containerId).remove();
    });

    $(document).on("click", ".removeDashboard", function (event) {
        event.preventDefault();
        let dashboardId = $("#dashboards").attr("dashboardId");

        $.ajax({url: "/dashboards/(" + dashboardId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Removed dashboard successfully");

            renderDashboardsLinks();
            renderDefaultDashboard();
        }});

    });


    function loadControllers() {
        widgetList.attr('disabled', true);
        $.get(thingSvcUrl + "/api/controllers", function(data){
            if(!isEmpty(data)) {
                let list = $("#controllerList");
                list.empty();

                if(data.length > 1) {
                    list.append(new Option("", ""));
                }

                $.each(data, function (i, ci) {
                    list.append(new Option(ci.controllerId, ci.controllerId));
                })

                let selectedController = list.find('option:selected').val();
                loadThings(selectedController);
                // loadPlugins(selectedController);
            }
        });
    }

    $("#sourceItem").change(function() {
        let selectedSource = $("#sourceItem").find('option:selected').val();

        if(selectedSource === "device") {
            loadControllers();
        } else if(selectedSource === "group") {
            showVirtual("Group");
            loadGroups();
        } else if(selectedSource === "virtual") {
            showVirtual("Virtual Item");
            loadVirtualItems();
        }
    });

    function loadGroups() {
        console.log("Retrieving Groups");
        $.get("/groups/", function(data){
            if(!isEmpty(data)) {
                let list = $("#virtualList");
                list.empty();
                list.append(new Option("", ""));

                $.each(data, function (i, g) {
                    list.append(new Option(g.name, g.id));
                })
            }
        })
    }

    function loadVirtualItems() {
        console.log("Retrieving Virtual Items");
        $.get("/virtualitems/", function(data){
            if(!isEmpty(data)) {
                let list = $("#virtualList");
                list.empty();
                list.append(new Option("", ""));

                $.each(data, function (i, g) {
                    list.append(new Option(g.name, g.id));
                })
            }
        })

    }

    function getSelectedController() {
        return $("#controllerList").find('option:selected').val();
    }

    function getSelectedThing() {
        return $("#thingList").find('option:selected').val();
    }

    $('#controllerList').change(function() {
        loadThings(getSelectedController());
    });

    function loadThings(selectedController) {
        console.log("Retrieving Things for controller: " + selectedController);
        $.get(thingSvcUrl + "/api/controllers(" + selectedController + ")/things", function(data){
            console.log("Received data: " + JSON.stringify(data))
            if(!isEmpty(data)) {
                let list = $("#thingList");
                list.empty();
                list.append(new Option("", ""));

                $.each(data, function (i, thing) {
                    list.append(new Option(thing.friendlyName + " (" + thing.thingId + ")", thing.thingId));
                })
            }
        })
    }

    $('#thingList').change(function() {
        loadLabels(getSelectedController(), getSelectedThing());
    });

    $("#virtualList").change(function() {
        let selectedVirtual = $("#virtualList").find('option:selected').val();
        loadLabels(selectedVirtual);
    });

    function loadLabels(controllerId, itemId) {
        widgetList.attr('disabled', false);

        $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/things(" + itemId + ")", function(data){
            widgetLabel.empty();
            widgetLabel.append(new Option("Custom", "custom"));

            if(!isEmpty(data)) {
                $.each(data.attributes, function (key, attribute) {
                    widgetLabel.append(new Option(key + "(" + attribute + ")", key));
                })
            }
        })
    }

    widgetList.change(function () {
        let widgetType = this.value;
        if(widgetType === "label" || widgetType === "graph") {
            $("#widgetValueTypeDiv").removeClass("fade");
            $("#widgetUnitTypeDiv").removeClass("fade");
            $("#widgetCustomValueType").removeClass("fade");
            widgetLabel.prop("disabled", false);

            if(widgetType === "graph") {
                $("#graphTimeDiv").removeClass('fade');
                $("#graphGroupingDiv").removeClass('fade');
            }
        } else if(widgetType === "switch") {
            // $("#widgetValueTypeDiv").addClass("fade");
            $("#widgetValueTypeDiv").removeClass("fade");
            $("#widgetUnitTypeDiv").addClass("fade");
            widgetLabel.prop("disabled", false);
            // $("#widgetCustomValueType").removeClass("fade");
        } else {
            $("#widgetValueTypeDiv").addClass("fade");
            $("#widgetUnitTypeDiv").addClass("fade");
        }
    });

    widgetLabel.change(function() {
        let label = this.value;
        if(label === "custom") {
            $("#widgetCustomValueType").removeClass("hide");
        } else {
            $("#widgetCustomValueType").addClass("hide");
        }
    });

    $("#createUIItemForm").submit(function(event) {
        console.log("Creating UI Item")
        event.preventDefault();

        let name = $("#itemName").val();
        let container = $("#containerId").val();
        let widget = widgetList.find('option:selected').val();
        let controllerId = getSelectedController();
        let thingId = getSelectedThing();
        let virtualItemId = $("#virtualList").find('option:selected').val();
        let selectedSource = $("#sourceItem").find('option:selected').val();

        let column = getCurrentColumn(container);
        let widgetIndex = getCurrentWidgetSize(container, column) + 1;

        if(selectedSource === "group" || selectedSource === "virtual") {
            thingId = virtualItemId;
        }

        console.log("Creating ui item name: " + name);
        console.log("Creating ui item container: " + container);
        console.log("Creating ui item widget: " + widget);

        let item = {
            "name" : name,
            "widgetType" : widget,
            "containerId" : container,
            "thingId" : thingId,
            "controllerId": controllerId,
            "properties" : {
                "column" : column,
                "index" : widgetIndex
            }
        };
        if(widget === "label" || widget === "switch") {
            let label = widgetLabel.find('option:selected').val();
            if(label === "custom") {
                label = $("#customLabel").val();
            }
            item.properties.label = label;

            if(widget === "label") {
                item.properties.unit = $("#widgetLabelUnitType").find('option:selected').text();
                console.debug("We have a label: " + item.properties.label + " and unit: " + item.properties.unit);
            }
        }

        let jsonData = JSON.stringify(item);
        console.debug("Posting: " + jsonData)

        $.ajax({url: "/ui/items", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            console.info("Posted UI Item successfully");

            $('#dataModal').modal('hide');

            resetForm();

            renderWidget(container, data);
        }})
    });

    function isEmpty(str) {
        return (!str || 0 === str.length);
    }

    function resetForm() {
        $("#itemName").val("");
        $("#containerId").val("");
        widgetList.val("switch");
        widgetLabel.val("none");
        $("#widgetLabelUnitType").val("none");

        $("#deviceList").empty();
        $("#pluginList").empty();
        $("#controllerList").empty();
        $("#groupList").empty();
    }
}