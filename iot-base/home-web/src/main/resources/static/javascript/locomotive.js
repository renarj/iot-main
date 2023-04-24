$(document).ready(function() {
    console.log("Hello world")

    $("#openModalTrain").click(function() {
        $.get("/api/controllers", function(data){
            if(!isEmpty(data)) {
                let list = $("#controllerList");
                list.empty();

                if(data.length >= 1) {
                    list.append(new Option("No Controller", "No Controller"));
                }

                $.each(data, function (i, ci) {
                    list.append(new Option(ci.controllerId, ci.controllerId, true, true));
                })
            }
        });
    })

    $("#addLocBtn").click(function() {
        let selectedController = $("#controllerList").find('option:selected').val();
        let locId = $("#locId").val()
        let locName = $("#locName").val()
        let uniqueId = $("#uniqueId").val()

        let locData = {
            "thingId" : uniqueId,
            "locAddress" : locId,
            "name" : locName,
            "controllerId": selectedController
        }
        let jsonData = JSON.stringify(locData);
        console.log("Posting data: " + jsonData)
        $.ajax({url: "/api/locomotives", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function() {
            console.log("Posted Locomotives successfully")

            $('#locomotiveForm').modal('hide');
            reloadLocs();
        }})
    })

    reloadLocs();
})

function reloadLocs() {
    let locList = $("#locList");
    locList.empty();

    $.get("/api/locomotives", function(data) {
        if (!isEmpty(data)) {

            $.each(data, function (i, loc) {
                var data = {
                    "name": loc.name,
                    "thingId": loc.thingId,
                    "controllerId": loc.controllerId
                };

                renderAndAppend("locTemplate", data, "locList");
            })

            $(".locButton").on('click', function(event) {
                event.preventDefault();

                let thingId = this.getAttribute('id');
                let controllerId = this.getAttribute('controllerId');
                console.log("Loc: " + thingId + " selected on controller: " + controllerId);

                let wasActive = $(this).hasClass("active");

                $("#locControl").remove();
                $(this).parent().children().removeClass('active');

                if(!wasActive) {
                    this.classList.add("active");

                    renderLocControl();

                    $("#locSpeedControl").on('change', function() {
                        let speedControl = $("#locSpeedControl");
                        let speedValue = speedControl.val();

                        console.log("Change value of loc speed control to " + speedValue);
                    })

                }
            })

        }
    });

    function renderLocControl(controllerId, thingId) {
        let data = {
            "thingId": thingId,
            "controllerId": controllerId
        }

        let rendered = renderTemplate("locControlTemplate", data);

        $("#locListPanel").after(rendered);

        let imgElement =document.getElementById('img' + thingId);
        Holder.run({
            images: imgElement
        });

        renderGauge();
    }

    function renderGauge() {
        var opts = {
            angle: -0.1, // The span of the gauge arc
            lineWidth: 0.15, // The line thickness
            radiusScale: 0.8, // Relative radius
            pointer: {
                length: 0.4, // // Relative to gauge radius
                strokeWidth: 0.035, // The thickness
                color: '#000000' // Fill color
            },
            limitMax: false,     // If false, max value increases automatically if value > maxValue
            limitMin: false,     // If true, the min value of the gauge will be fixed
            colorStart: '#6FADCF',   // Colors
            colorStop: '#8FC0DA',    // just experiment with them
            strokeColor: '#E0E0E0',  // to see which ones work best for you
            generateGradient: true,
            highDpiSupport: true,     // High resolution support
            percentColors: [[0.0, "#a9d70b" ], [0.50, "#f9c802"], [1.0, "#ff0000"]],
            staticZones: [
                {strokeStyle: "#30B32D", min: 0, max: 140}, // Green
                {strokeStyle: "#FFDD00", min: 140, max: 180}, // Yellow
                {strokeStyle: "#F03E3E", min: 180, max: 300}  // Red
            ],
            staticLabels: {
                font: "12px sans-serif",  // Specifies font
                labels: [0, 100, 140, 200, 300],  // Print labels at these values
                color: "#FFFFFF",  // Optional: Label text color
                fractionDigits: 0  // Optional: Numerical precision. 0=round off.
            }
        };
        var target = document.getElementById('gaugeControl'); // your canvas element
        var gauge = new Gauge(target).setOptions(opts); // create sexy gauge!

        gauge.maxValue = 300; // set max gauge value
        gauge.setMinValue(0);  // Prefer setter over gauge.minValue = 0
        gauge.animationSpeed = 32; // set animation speed (32 is default value)
        gauge.set(20); // set actual value
    }
}