$(document).ready(function() {
    $(document).on("click", ".addWidget", function () {
        var containerId = $(this).data('id');
        $(".modal-body #containerId").val( containerId );
    });


    // $("#widgetList").change(function () {
    //     var widgetType = this.value;
    //     if(widgetType == "label" || widgetType == "graph") {
    //         $("#widgetValueTypeDiv").removeClass("hide");
    //         $("#widgetUnitTypeDiv").removeClass("hide");
    //         $("#widgetCustomValueType").removeClass("hide");
    //
    //         if(widgetType == "graph") {
    //             $("#graphTimeDiv").removeClass('hide');
    //             $("#graphGroupingDiv").removeClass('hide');
    //         }
    //     } else {
    //         $("#widgetValueTypeDiv").addClass("hide");
    //         $("#widgetUnitTypeDiv").addClass("hide");
    //     }
    // });

    // $("#widgetLabel").change(function() {
    //     var label = this.value;
    //     if(label == "custom") {
    //         $("#widgetCustomValueType").removeClass("hide");
    //     } else {
    //         $("#widgetCustomValueType").addClass("hide");
    //     }
    // });

    $("#createUIItemForm").submit(function(event) {
        console.log("Creating UI Item")
        event.preventDefault();

        var name = $("#itemName").val();
        var container = $("#containerId").val();
        var widget = $("#widgetList").find('option:selected').val();
        var robotControllerId = $("#robotControllerId").val();

        var column = getCurrentColumn(container);
        var widgetIndex = getCurrentWidgetSize(container, column) + 1;

        console.log("Creating ui item name: " + name);
        console.log("Creating ui item container: " + container);
        console.log("Creating ui item widget: " + widget);

        var item = {
            "name" : name,
            "widgetType" : widget,
            "containerId" : container,
            "properties" : {
                "robotId" : robotControllerId
            }
        };
        // if(widget == "label" || widget == "graph") {
        //     var label = $("#widgetLabel").find('option:selected').val();
        //     if(label == "custom") {
        //         label = $("#customLabel").val();
        //     }
        //
        //     var unit = $("#widgetLabelUnitType").find('option:selected').text();
        //     console.log("We have a label: " + label + " and unit: " + unit);
        //
        //     item.properties.label = label;
        //     item.properties.unit = unit;
        //
        //     if(widget == "graph") {
        //         var period = $("#graphTime").find('option:selected').val();
        //         var aggregation = $("#graphGrouping").find('option:selected').val();
        //
        //         item.properties.period = period;
        //         item.properties.aggregation = aggregation;
        //     }
        // }

        var jsonData = JSON.stringify(item);

        console.log("Posting: " + jsonData)

        $.ajax({url: "/ui/widgets", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
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
        $("#robotControllerId").val("");
        $("#itemName").val("");
        $("#containerId").val("");
        $("#widgetList").val("switch");
        // $("#widgetLabel").val("none");
        // $("#widgetLabelUnitType").val("none");

        // $("#deviceList").empty();
        // $("#pluginList").empty();
        // $("#controllerList").empty();
        // $("#groupList").empty();
    }
});