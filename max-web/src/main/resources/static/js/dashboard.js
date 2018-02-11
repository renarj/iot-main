$(document).ready(function() {
    $(document).on("click", ".addContainer", function () {
        $("#addContainerLabel").text("Add a container");
        $("#createContainerForm").attr("mode", "container");

        var parentContainerId = $(this).data('id');
        $(".modal-body #parentContainerId").val( parentContainerId );
    });

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

            // renderDashboardsLinks();
            // renderDefaultDashboard();
        }});

    });

    $("#createContainerForm").submit(function(event){
        event.preventDefault();

        var mode = $("#createContainerForm").attr("mode");
        if(mode == "container") {
            createContainer();
        } else if (mode == "dashboard") {
            createDashboard();
        } else {
            console.log("Unsupported container creation: " + mode);
        }


    });

    function createDashboard() {
        var name = $("#containerName").val();
        var weight = determineMaxWeight() + 1;

        var dashboard = {
            "name" : name,
            "weight" : weight
        };

        var jsonData = JSON.stringify(dashboard);

        console.log("Posting dash: " + jsonData);

        $.ajax({url: "/dashboards/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            var dashboardId = data.id;
            console.log("Posted Dashboard successfully, id: " + dashboardId);

            $('#containerModal').modal('hide');

            $("#containerName").val("");
            $("#parentContainerId").val("");

            renderDashboardsLinks();
        }})

    }

    function determineMaxWeight() {
        return $("#dashboards").find("a").size();
    }

    function createContainer() {
        var name = $("#containerName").val();
        var parentContainer = $("#parentContainerId").val();
        var dashboardId = $("#dashboards").attr("dashboardId");
        var layout = $("#containerLayout").val();
        console.log("Creating container for dashboard: " + dashboardId);

        var container = {
            "name" : name,
            "dashboardId" : dashboardId,
            "parentContainerId" : parentContainer,
            "properties" : {
                "layout" : layout
            }
        };
        var jsonData = JSON.stringify(container);

        $.ajax({url: "/ui/containers", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            var containerId = data.id;
            console.log("Posted Container successfully, id: " + containerId);

            $('#containerModal').modal('hide');

            $("#containerName").val("");
            $("#parentContainerId").val("");

            renderContainerById(containerId);
        }})
    }
});