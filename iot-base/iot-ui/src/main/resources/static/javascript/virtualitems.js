$(document).ready(function() {

    $(document).on("click", ".removeItem", function (event) {
        event.preventDefault();
        var itemId = this.getAttribute('itemId');
        console.log("Removing item: " + itemId);

        $.ajax({url: "/virtualitems/items(" + itemId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Removed item successfully");

            location.reload(true);
        }, error: function(data) {
            console.log("Failed: " + data);
        }});
    });


    $("#itemForm").submit(function(event) {
        event.preventDefault();

        console.log("Item form submitted");

        var name = $("#itemName").val();
        var controllerId = $("#controllerId").val();

        var itemJson = {
            "name" : name,
            "controllerId" : controllerId,
        };

        var jsonData = JSON.stringify(itemJson);

        $.ajax({url: "/virtualitems/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Posted item successfully");

            $('#dataModal').modal('hide');

            location.reload(true);
        }})
    });

    $.get("/data/controllers", function(data){
        if(!isEmpty(data)) {
            var controllerDiv = $("#controllers");

            $.each(data, function (i, ci) {
                controllerDiv.append("<a  class=\"list-group-item select\" href=\"#\" controllerId=" + ci.controllerId + ">" + ci.controllerId + "</a>");
            })
        }
    });

    $(document).on("click", ".select", function (event) {
        event.preventDefault();

        var controllerId = this.getAttribute('controllerId');
        $(".active").removeClass("active");
        $(this).addClass("active");
        $("#itemInfo").empty();

        $.get("/virtualitems/items/controller(" + controllerId + ")", function(data){

        });

        ///items/controller({controllerId})
    });
});