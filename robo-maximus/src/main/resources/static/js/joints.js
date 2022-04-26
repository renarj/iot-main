var stompClient = null;

function connect() {
    console.log("Connecting to websocket");
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/joints', function(frame){
            handleStateUpdate(JSON.parse(frame.body));
        });

        stompClient.subscribe('/topic/sensors', function(frame) {
            handleSensorUpdate(JSON.parse(frame.body));
        });
    });
    stompClient.debug = null
}

function handleSensorUpdate(state) {
    // console.log("sensor update: " + JSON.stringify(state));

    $.each(state.attributes, function(i, attr) {
        var value = state.values[attr];
        var id = state.sensorId + "-" + attr;
        $("#sensor-value-" + id).html(value);
    });
}

function handleStateUpdate(state) {
    var jointState = $("#" + state.id).attr("state")
    if(jointState === "ready") {
        if(state.values.degrees) {
            $("#degrees-" + state.id).val(state.values.degrees);
            $("#slider-" + state.id).slider('setValue', state.values.degrees);
        }
        if(state.values.position) {
            $("#position-" + state.id).val(state.values.position);
        }

        if(state.values.torgue !== undefined) {
            var tState = $("#tToggle-" + state.id);
            console.log("Torgue state: " + state.values.torgue + " for joint: " + state.id);
            if(state.values.torgue === 1) {
                tState.prop('checked', true);
            } else {
                tState.prop('checked', false);
            }
        }
    }
}

function renderRobots() {
    $.get("/humanoid/", function(data) {
        console.log("Humanoid Robots: " + JSON.stringify(data));

        $.each(data, function(i, robot) {
            renderRobot(robot);
            renderJointPosition(robot);
        })
    });

    addListeners();
}

function addListeners() {
    $("#torgueAllOn").click(function(e) {
        e.preventDefault();

        $.ajax({
            url: "/servos/enable/torgue",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Enable torgue for all servos successfully");
            }
        });
    });

    $("#torgueAllOff").click(function(e) {
        e.preventDefault();

        $.ajax({
            url: "/servos/disable/torgue",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Disable torgue for all servos successfully");
            }
        });
    });
}

function renderJointPosition(robot) {
    $.get("/humanoid/robot/" + robot.name + "/joints", function(data) {
        console.log("Joint Data: " + JSON.stringify(data));

        $.each(data, function(i, joint) {
            $("#degrees-" + joint.id).val(joint.values.degrees);
            $("#position-" + joint.id).val(joint.values.position);
            $("#slider-" + joint.id).slider('setValue', joint.values.degrees);

            var tState = $("#tToggle-" + joint.id);
            if(joint.values.torgue === 1) {
                tState.prop('checked', true);
            } else {
                tState.prop('checked', false);
            }
        });
    });
}

function renderRobot(robot) {
    console.log("Rendering robot with name: " + robot.name);

    $.each(robot.chainSets, function(i, chain) {
        renderChain(chain);
    })

    $.each(robot.sensors, function(i, sensor) {
        renderSensor(sensor);
    });
}

function renderSensor(sensor) {
    $.each(sensor.values, function(i, value) {
        var data = {
            "id" : sensor.name + "-" + i,
            "value": value.raw
        };
        var rendered = renderTemplate("sensor", data);
        $("#sensors").append(rendered);

    });
}

function renderChain(chain) {
    console.log("Rendering chain: " + chain.name);

    var chainId = "chain-" + chain.name;
    var data = {
        "name": chain.name,
        "id" : chainId
    };

    var rendered = renderTemplate("chain", data);
    $("#chainContainer").append(rendered);

    $.each(chain.jointChains, function(i, jointChain) {
        renderJointChain(chainId, jointChain);
    });

    if(chain.joints.length > 0) {
        console.log("We have joints here to load");
        renderJointChain(chainId, chain);
    }
}

function renderJointChain(parentChainId, jointChain) {
    console.log("Rendering jointChain: " + jointChain.name);

    var jointChainId = "jointChain-" + jointChain.name;
    var data = {
        "name": jointChain.name,
        "id" : jointChainId
    };

    var rendered = renderTemplate("jointChain", data);
    $("#" + parentChainId).append(rendered);

    $.each(jointChain.joints, function(i, joint) {
        renderJoint(jointChainId, joint);
    })
}

function renderJoint(jointChainId, joint) {
    console.log("Rendering joint: " + joint.name);

    var data = {
        "name": joint.name,
        "id" : joint.id,
        "jointType" : joint.jointType,
        "sliderId" : "slider-" + joint.id
    };

    var rendered = renderTemplate("joint", data);
    $("#" + jointChainId).append(rendered);

    var slider = $("#slider-" + joint.id);
    slider.slider({
        min: joint.minDegrees,
        max: joint.maxDegrees
    });

    slider.on("slideStop", handleSlideStop);

    $("#tToggle-" + joint.id).change(function() {
        var jointId = this.getAttribute('jointid');
        console.log("Toggled the torgue button to: " + this.checked + " for joint: " + jointId);

        var state = "disable";
        if(this.checked) {
            state = "enable";
        }

        var url = "/servos/" + state + "/" + jointId + "/torgue";

        $.ajax({
            url: url,
            type: "POST",
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Torgue for servo " + jointId + " set successfully to: " + state);
            }
        });

    });

    $("#angle-edit-" + joint.id).click(function(e) {
       e.preventDefault();
       var jointId = this.getAttribute('jointid');
       var j = $("#" + jointId);
       if(j.attr("state") === "ready") {
           j.attr("state", "editing");
           $("#icon-" + jointId).text("check");
           $("#stop-icon-" + jointId).text("delete")

           $("#degrees-" + jointId).prop("disabled", false);
       } else {
           var degreeInput = $("#degrees-" + jointId);
           var angle = degreeInput.val();
           setServoAngle(jointId, angle);

           degreeInput.prop("disabled", true);

           stopEditJoint(jointId);
       }
    });
    $("#angle-edit-stop-" + joint.id).click(function(e) {
        e.preventDefault();

        var jointId = this.getAttribute('jointid');
        stopEditJoint(jointId);
    })
}

function stopEditJoint(jointId) {
    console.log("Stop Angle Edit on joint: " + jointId);

    $("#" + jointId).attr("state", "ready");
    $("#icon-" + jointId).text("edit");
    $("#stop-icon-" + jointId).text("");
    $("#degrees-" + jointId).prop("disabled", true);
}

function handleSlideStop(slideEvt) {
    var val = slideEvt.value;
    var jointId = this.getAttribute('jointid');

    var syncCheckBox = $("#sync-" + jointId);
    if(syncCheckBox.first().prop("checked")) {
        console.log("Sync moving mode");

        var jointDiv = $("#" + jointId);
        var jointType = jointDiv.attr("jointType");

        var nrTypes = $("div[jointType=" + jointType + "]");
        if(nrTypes.length > 1) {
            console.log("We have multiple joints");

            var joints = [];
            $.each(nrTypes, function(i, joint) {
                var jointId = joint.getAttribute("id");

                $("#slider-" + jointId).slider('setValue', val);

                var json = {
                    id: jointId,
                    degrees: val,
                    position: 0
                };
                console.log("JSON for ID " + jointId + " val: " + JSON.stringify(json));
                joints.push(json);
            });
            console.log("Full struct: " + JSON.stringify(joints));
            $.ajax({url: "/humanoid/robot/maximus/joints", data: JSON.stringify((joints)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
                    console.log("Set joints succesfully");
                }});
        }

    } else {
        console.log("Non sync mode");
        setServoAngle(jointId, val);
    }
}

function setServoAngle(jointId, degrees) {
    var json = {
        servoId: jointId,
        targetAngle: degrees,
        targetPosition: 0
    };

    console.log("Posting: " + JSON.stringify(json));

    $.ajax({url: "/humanoid/robot/maximus/joint", data: JSON.stringify((json)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Set joint succesfully");
        }});
}

function renderTemplate(templateName, data) {
    var template = $('#' + templateName).html();
    Mustache.parse(template);
    return Mustache.render(template, data);
}

$(document).ready(function() {
    renderRobots();

    connect();
});