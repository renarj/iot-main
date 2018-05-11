$(document).ready(function() {
    $("#goToPosition").click(function (e) {
        e.preventDefault();

        console.log("Direction position set, sending command");

        var dirX = $("#inputDirX").val();
        var dirY = $("#inputDirY").val();
        var dirZ = $("#inputDirZ").val();

        var rotX = $("#rotateDirX").val();
        var rotY = $("#rotateDirY").val();
        var rotZ = $("#rotateDirZ").val();

        var pUrl = "/walker/" + dirX + "/" + dirY + "/" + dirZ + "/" + rotX + "/" + rotY + "/" + rotZ;

        var targetLeg = $("#targetLeg").val();
        if(targetLeg !== "All") {
            pUrl = "/walker/leg/" + targetLeg + "/" + dirX + "/" + dirY + "/" + dirZ + "/" + rotX + "/" + rotY + "/" + rotZ;
        }

        console.log("Posting to url: %s", pUrl);

        $.ajax({url: pUrl , type: "POST", data: {}, dataType: "json", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Posted walker data successfully");

            $("#feedback").val(JSON.stringify(data, null, 4));
        }})

    });
});