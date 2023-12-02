$(document).ready(function() {
    $(document).on("click", ".addWidget", function () {
        console.log("Opening widget form");

        var containerId = $(this).data('id');
        $(".modal-body #containerId").val( containerId );

        showDevice();
        loadControllers();

        new bootstrap.Modal("#dataModal").show();
    });

    function showVirtual(labelText) {
        $("#virtual").removeClass("hide");
        $("#controller").addClass("hide");
        $("#virtualLabel").text(labelText);
        $("#plugin").addClass("hide");
        $("#device").addClass("hide");
    }

    function showDevice() {
        $("#widgetValueTypeDiv").addClass("hide");
        $("#widgetUnitTypeDiv").addClass("hide");

        $("#virtual").addClass("hide");
        $("#controller").removeClass("hide");
        $("#plugin").removeClass("hide");
        $("#device").removeClass("hide");
    }

    $(document).on("click", ".removeWidget", function (event) {
        event.preventDefault();
        var widgetId = this.getAttribute('widgetId');

        $.ajax({url: "/ui/items(" + widgetId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Removed Widget successfully");
        }});

        $("#" + widgetId).remove();
    });

    $(document).on("click", ".removeContainer", function (event) {
        event.preventDefault();
        var containerId = this.getAttribute('containerId');

        $.ajax({url: "/ui/containers(" + containerId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Removed container successfully");
        }});

        $("#" + containerId).remove();
    });

    $(document).on("click", ".removeDashboard", function (event) {
        event.preventDefault();
        var dashboardId = $("#dashboards").attr("dashboardId");

        $.ajax({url: "/dashboards/(" + dashboardId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Removed dashboard successfully");

            renderDashboardsLinks();
            renderDefaultDashboard();
        }});

    });


    function loadControllers() {
        $('#widgetList').attr('disabled', true);
        $.get("/api/controllers", function(data){
            if(!isEmpty(data)) {
                var list = $("#controllerList");
                list.empty();

                if(data.length > 1) {
                    list.append(new Option("", ""));
                }

                $.each(data, function (i, ci) {
                    list.append(new Option(ci.controllerId, ci.controllerId));
                })

                var selectedController = list.find('option:selected').val();
                loadThings(selectedController);
                // loadPlugins(selectedController);
            }
        });
    }

    $("#sourceItem").change(function() {
        var selectedSource = $("#sourceItem").find('option:selected').val();

        if(selectedSource == "device") {
            showDevice();
            loadControllers();
        } else if(selectedSource == "group") {
            showVirtual("Group");
            loadGroups();
        } else if(selectedSource == "virtual") {
            showVirtual("Virtual Item");
            loadVirtualItems();
        }
    });

    function loadGroups() {
        console.log("Retrieving Groups");
        $.get("/groups/", function(data){
            if(!isEmpty(data)) {
                var list = $("#virtualList");
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
                var list = $("#virtualList");
                list.empty();
                list.append(new Option("", ""));

                $.each(data, function (i, g) {
                    list.append(new Option(g.name, g.id));
                })
            }
        })

    }

    // $("#controllerList").change(function() {
    //     var selectedController = $("#controllerList").find('option:selected').val();
    //     loadPlugins(selectedController);
    // });

    // function loadPlugins(controllerId) {
    //     console.log("Retrieving plugins for controller: " + controllerId);
    //     $.get("/data/controllers(" + controllerId + ")/plugins", function(data){
    //         if(!isEmpty(data)) {
    //             var list = $("#pluginList");
    //             list.empty();
    //             list.append(new Option("", ""));
    //
    //             $.each(data, function (i, ci) {
    //                 list.append(new Option(ci.name, ci.pluginId));
    //             })
    //         }
    //     })
    // }

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
        $.get("/api/controllers(" + selectedController + ")/things", function(data){
            console.log("Received data: " + JSON.stringify(data))
            if(!isEmpty(data)) {
                var list = $("#thingList");
                list.empty();
                list.append(new Option("", ""));

                $.each(data, function (i, thing) {
                    list.append(new Option(thing.friendlyName, thing.thingId));
                })
            }
        })
    }

    $('#thingList').change(function() {
        loadLabels(getSelectedController(), getSelectedThing());
    });

    $("#virtualList").change(function() {
        var selectedVirtual = $("#virtualList").find('option:selected').val();
        loadLabels(selectedVirtual);
    });

    function loadLabels(controllerId, itemId) {
        $('#widgetList').attr('disabled', false);

        $.get("/data/controllers(" + controllerId + ")/things(" + itemId + ")", function(data){
            var list = $("#widgetLabel");
            list.empty();
            list.append(new Option("Custom", "custom"));

            if(!isEmpty(data)) {
                $.each(data.attributes, function (i, attribute) {
                    list.append(new Option(attribute, attribute));
                })
            }
        })

    }

    $("#widgetList").change(function () {
        var widgetType = this.value;
        if(widgetType == "label" || widgetType == "graph") {
            $("#widgetValueTypeDiv").removeClass("hide");
            $("#widgetUnitTypeDiv").removeClass("hide");
            $("#widgetCustomValueType").removeClass("hide");

            if(widgetType == "graph") {
                $("#graphTimeDiv").removeClass('hide');
                $("#graphGroupingDiv").removeClass('hide');
            }
        } else {
            $("#widgetValueTypeDiv").addClass("hide");
            $("#widgetUnitTypeDiv").addClass("hide");
        }
    });

    $("#widgetLabel").change(function() {
        var label = this.value;
        if(label == "custom") {
            $("#widgetCustomValueType").removeClass("hide");
        } else {
            $("#widgetCustomValueType").addClass("hide");
        }
    });

    $("#createUIItemForm").submit(function(event) {
        console.log("Creating UI Item")
        event.preventDefault();

        var name = $("#itemName").val();
        var container = $("#containerId").val();
        var widget = $("#widgetList").find('option:selected').val();
        var controllerId = getSelectedController();
        var thingId = getSelectedThing();
        var virtualItemId = $("#virtualList").find('option:selected').val();
        var selectedSource = $("#sourceItem").find('option:selected').val();

        var column = getCurrentColumn(container);
        var widgetIndex = getCurrentWidgetSize(container, column) + 1;

        if(selectedSource == "group" || selectedSource == "virtual") {
            thingId = virtualItemId;
        }

        console.log("Creating ui item name: " + name);
        console.log("Creating ui item container: " + container);
        console.log("Creating ui item widget: " + widget);

        var item = {
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
        if(widget === "label" || widget === "graph") {
            var label = $("#widgetLabel").find('option:selected').val();
            if(label === "custom") {
                label = $("#customLabel").val();
            }

            var unit = $("#widgetLabelUnitType").find('option:selected').text();
            console.log("We have a label: " + label + " and unit: " + unit);

            item.properties.label = label;
            item.properties.unit = unit;

            if(widget === "graph") {
                var period = $("#graphTime").find('option:selected').val();
                var aggregation = $("#graphGrouping").find('option:selected').val();

                item.properties.period = period;
                item.properties.aggregation = aggregation;
            }
        }

        var jsonData = JSON.stringify(item);

        console.log("Posting: " + jsonData)

        $.ajax({url: "/ui/items", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Posted UI Item successfully");

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
        $("#widgetList").val("switch");
        $("#widgetLabel").val("none");
        $("#widgetLabelUnitType").val("none");

        $("#deviceList").empty();
        $("#pluginList").empty();
        $("#controllerList").empty();
        $("#groupList").empty();
    }
});