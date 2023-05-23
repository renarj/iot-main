var gauge = null;

function connect() {
    console.log("Connecting to websocket");
    var socket = new SockJS('http://localhost:9999/ws');
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

    $.each(state.stateItems, function (i, stateItem) {
        let attribute = stateItem.attribute;

        if(attribute === "speed") {
            updateSpeedState(state.controllerId, state.itemId, stateItem);
        } else if(attribute === "direction") {
            updateDirection(state.controllerId, state.itemId, stateItem);
        } else {
            checkFunctionState(state.controllerId, state.itemId, stateItem);
        }
    });
}

function checkFunctionState(controllerId, thingId, state) {
    let button = $("button[thingid='" + thingId + "'][controllerId='" + controllerId + "'][functionId='" + state.attribute + "']");
    if(button.length > 0) {
        if(state.value.value === true) {
            button.addClass("active");
        } else {
            button.removeClass("active");
        }
    }
}

function checkActiveLoc(controllerId, thingId) {
    let locControl = $("#locControl");
    if(locControl) {
        let currentThingId = locControl.attr("thingId");
        let currentController = locControl.attr("controllerId");

        return currentController === controllerId && currentThingId === thingId;
    }

    return false;
}

function updateSpeedState(controllerId, thingId, stateItem) {
    if(checkActiveLoc(controllerId, thingId)) {
        console.log("Updating speed")
        updateSpeed(stateItem.value.value);
    }
}

function updateDirection(controllerId, thingId, stateItem) {
    if(checkActiveLoc(controllerId, thingId)) {
        let revBtn = $("#rev-button");
        let fwdBtn = $("#fwd-button");

        if(stateItem.value.value === "FORWARD") {
            fwdBtn.addClass("active")
            revBtn.removeClass("active");
        } else {
            revBtn.addClass("active")
            fwdBtn.removeClass("active");
        }
        $("#direction").html(stateItem.value.value.toLowerCase());
    }
}

function updateSpeed(speedValue) {
    console.log("Setting speed to " + speedValue);
    $("#speed").html(speedValue);
    gauge.set(speedValue);
    $("#locSpeedControl").val(speedValue);
}

function renderFunctionsList() {
    let functions = 26;
    for(let i = 0; i <= functions; i++) {
        let data = {
            "functionNr" : i
        }
        renderAndAppend("functionTemplate", data, "functionList");
    }
}

$(document).ready(function() {
    $("#openModalTrain").click(function() {
        loadControllerList();

        $("#controllerList").change(function() {
            let selectedController = $("#controllerList").find('option:selected').val();

            loadCommandStationList(selectedController);
        })

        renderFunctionsList();
    })

    $("#addLocBtn").click(function() {
        let selectedController = $("#controllerList").find('option:selected').val();
        let locId = $("#locId").val()
        let locName = $("#locName").val()
        let uniqueId = $("#uniqueId").val()
        let dccMode = $("#dccMode").val();
        let commandStation = $("#commandStationList").find('option:selected').val();

        let functions = [];
        $("#functionList").children("tr").each(function(index) {
            let functionId = $(this).attr("functionId");

            let isEnabled = $("#" + functionId + "_enabled").prop('checked');
            let functionType = $("#" + functionId + "_type").val();
            let functionDescription = $("#" + functionId + "_description").val();
            let isToggle = $("#" + functionId + "_toggle").prop('checked');
            let f = {
                "functionNumber" : functionId,
                "functionType": functionType,
                "description": functionDescription,
                "toggle": isToggle,
                "enabled": isEnabled
            }
            functions.push(f)
        });
        let locData = {
            "thingId" : uniqueId,
            "locAddress" : locId,
            "name" : locName,
            "commandStation": commandStation,
            "stepMode" : dccMode,
            "controllerId": selectedController,
            "functions" : functions
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

    connect();

    console.log("Document has finished loading");
})

function loadControllerList(controllerId) {
    $.get("/api/controllers", function(data){
        if(!isEmpty(data)) {
            let list = $("#controllerList");
            list.empty();

            if(data.length >= 1) {
                list.append(new Option("No Controller", "No Controller"));
            }

            $.each(data, function (i, ci) {
                let selected = false;
                if(controllerId === ci.controllerId) {
                    selected = true;
                }

                list.append(new Option(ci.controllerId, ci.controllerId, selected, selected));
            })
        }
    });
}

function loadCommandStationList(controllerId, commandStation) {
    $.get("/api/controllers(" + controllerId + ")/plugins(trainAutomationExtension)/things?type=commandstation", function(data){
        let list = $("#commandStationList");
        list.empty();
        list.append(new Option("No Station", "No Station", true, true));

        $.each(data, function (i, ci) {
            let selected = false;
            if(ci.thingId === commandStation) {
                selected = true;
            }

            list.append(new Option(ci.thingId, ci.thingId, selected, selected));
        })
    });
}

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

                let currentElement = $(this);
                renderLoc(controllerId, thingId, currentElement);
            })
        }
    });

    function renderLoc(controllerId, thingId, currentElement) {
        $.get("/api/controllers(" + controllerId + ")/locomotives(" + thingId+")", function(data) {
            let wasActive = currentElement.hasClass("active");

            unrenderLoc();

            if(!wasActive) {
                currentElement.addClass("active");

                renderLocControl(controllerId, thingId, data.name, data.functions);

                $("#locSpeedControl").on('change', function() {
                    let speedControl = $("#locSpeedControl");
                    let speedValue = speedControl.val();

                    console.log("Change value of loc speed control to " + speedValue);
                    updateSpeed(speedValue);

                    setSpeedAndDirection();
                })

                $(".removeLoc").on('click', function(event) {
                    event.preventDefault();

                    let thingId = this.getAttribute('thingId');
                    let controllerId = this.getAttribute('controllerId');
                    console.log("Loc: " + thingId + " to be removed on controller: " + controllerId);

                    $.ajax({url: "/api/controllers(" + controllerId + ")/locomotives(" + thingId + ")", type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                            console.log("Removed locomotive successfully");
                            unrenderLoc();
                    }});
                })

                $(".editLoc").on('click', function(event) {
                    let thingId = this.getAttribute('thingId');
                    let controllerId = this.getAttribute('controllerId');

                    console.log("Loc: " + thingId + " to be edited on controller: " + controllerId);
                    loadLocFormData(controllerId, thingId);
                })
            }
        });
    }

    function loadLocFormData(controllerId, thingId) {
        $.get("/api/controllers(" + controllerId + ")/locomotives(" + thingId+")", function(data) {
            loadControllerList(controllerId);
            loadCommandStationList(controllerId, data.commandStation);
            renderFunctionsList();

            // $("#controllerList").val(data.controllerId);
            $("#locId").val(data.locAddress)
            $("#locName").val(data.name)
            $("#uniqueId").val(data.thingId)
            $("#dccMode").val(data.stepMode);

            $.each(data.functions, function (i, fun) {
                let functionId = fun.functionNumber;

                if(fun.enabled === true) {
                    $("#" + functionId + "_enabled").prop('checked', true);
                }
                $("#" + functionId + "_type").val(fun.functionType);
                $("#" + functionId + "_description").val(fun.description);
                if(fun.toggle) {
                    $("#" + functionId + "_toggle").prop('checked', true);
                }
            })

            $('#locomotiveForm').modal('show');

            //hack to get the active tab to display
            var triggerEl = document.querySelector('#tabList a[href="#details"]')
            bootstrap.Tab.getOrCreateInstance(triggerEl).show()

            reloadLocs();
        });
    }

    function setSpeedAndDirection() {
        let locControl = $("#locControl");
        let thingId = locControl.attr("thingId");
        let controllerId = locControl.attr("controllerId");
        let speedControl = $("#locSpeedControl");
        let speedValue = speedControl.val();
        let direction = "forward";
        if($("#rev-button").hasClass("active")) {
            direction = "reverse";
        }

        let data = {
            "controllerId" : controllerId,
            "thingId": thingId,
            "commandType" : "value",
            "properties" : {
                "speed" : speedValue,
                "direction": direction
            }
        };
        let jsonData = JSON.stringify(data);
        console.log("Sending train command: " + jsonData);

        $.ajax({url: "/api/command/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function (data) {
                console.log("Command posted succesfully");
        }});
    }

    function unrenderLoc() {
        $("#locControl").remove();

        $(".locButton, .active").removeClass('active');
    }



    function renderLocControl(controllerId, thingId, name, functions) {
        let data = {
            "thingId": thingId,
            "controllerId": controllerId,
            "name": name
        }

        let rendered = renderTemplate("locControlTemplate", data);

        $("#locListPanel").after(rendered);

        let imgElement = document.getElementById('img' + thingId);
        Holder.run({
            images: imgElement
        });

        renderGauge();

        renderFunctions(controllerId, thingId,      functions);
    }

    function renderFunctions(controllerId, thingId, functions) {
        $.each(functions, function (i, fun) {
            console.log("Rendering function: " + fun.functionNumber)
            if(fun.enabled === true) {
                let data = {
                    "functionId" : fun.functionNumber,
                    "description" : fun.description,
                    "controllerId" : controllerId,
                    "thingId" : thingId,
                    "mode" : fun.toggle === true ? "TOGGLE" : "ON"
                }

                renderAndAppend(fun.functionType, data, "functionButtons")
            }
        })

        $(".fun-button").click(function(event){
            event.preventDefault();

            let thingId = this.getAttribute('thingId');
            let controllerId = this.getAttribute('controllerId');
            let functionNr = this.getAttribute('functionId');

            let functionType = "ON";
            if(this.classList.contains("active")) {
                functionType = "OFF";
                this.classList.add("active");
            } else {
                let mode = this.getAttribute("mode");

                if(mode === "TOGGLE") {
                    functionType = "TOGGLE";
                } else {
                    this.classList.add("active");
                }
            }

            let data = {
                "controllerId" : controllerId,
                "thingId": thingId,
                "commandType" : "value",
                "properties" : {
                    "function" : functionNr,
                    "FUNCTION_STATE": functionType
                }
            };
            let jsonData = JSON.stringify(data);
            console.log("Sending train function command: " + jsonData);

            $.ajax({url: "/api/command/", type: "POST", data: jsonData, dataType: "json", contentType: "application/json; charset=utf-8", success: function (data) {
                    console.log("Command posted succesfully");
                }});
        });
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
        gauge = new Gauge(target).setOptions(opts); // create sexy gauge!

        gauge.maxValue = 300; // set max gauge value
        gauge.setMinValue(0);  // Prefer setter over gauge.minValue = 0
        gauge.animationSpeed = 32; // set animation speed (32 is default value)
        gauge.set(0); // set actual value
    }
}