function connect() {
    console.log("Connecting to websocket");
    var socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function(frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/joints', function(frame){
            handleStateUpdate(JSON.parse(frame.body));
        });
    });
    stompClient.debug = null
}

function handleStateUpdate(state) {
    var currentJointId = $("#editor").attr("currentJoint");

    if(currentJointId === state.id) {
        console.log("Update for current active joint");
        $("#degrees").val(state.degrees);
        $("#position").val(state.position);
    }

    // $("#slider-" + state.id).slider('setValue', state.degrees);
}

function renderRobots() {
    $.get("/humanoid/", function(data) {
        console.log("Humanoid Robots: " + JSON.stringify(data));

        $.each(data, function(i, robot) {
            renderRobot(robot);
        })
    });
}

function addListeners() {
    $("#positionSlider").on("slideStop", handleSlideStop);

    $("#tEnable").click(function (e) {
        e.preventDefault();

        var jointId = $("#editor").attr("currentJoint");
        console.log("We had torgue enable on joint: " + jointId);
        $.ajax({
            url: "/servos/enable/" + jointId + "/torgue",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Enable torgue for servo successfully");
            }
        });
    });

    $("#tDisable").click(function (e) {
        e.preventDefault();

        var jointId = $("#editor").attr("currentJoint");
        console.log("We had torgue disable on joint: " + jointId);
        $.ajax({
            url: "/servos/disable/" + jointId + "/torgue",
            type: "POST",
            contentType: "application/json; charset=utf-8",
            success: function (data) {
                console.log("Disable torgue for servo successfully");
            }
        });
    });

    $("#add").click(function (e) {
        e.preventDefault();

        var jointId = $("#editor").attr("currentJoint");

        if(isSyncMode()) {
            var similarJoints = getSimilarJoints(jointId);

            $.each(similarJoints, function(i, jid) {
                addPosition(jid);
            });
        } else {
            addPosition(jointId);
        }
    });


    $("#saveFrame").click(function (e) {
        e.preventDefault();

        var frameId = $("#editor").attr("frameid");
        if(frameId !== undefined) {
            console.log("We already have a frame id: " + frameId);
        } else {
            frameId = findNextFrameId();
            console.log("Frame not set yet, generated frameId: " + frameId);
        }

        storeFrame(frameId);
    });

    $("#saveMotion").click(function (e) {
        e.preventDefault();
        var frames = $("#keyFrames").find("tr");

        var motionName = $("#motionName").val();
        var keyFrames = [];
        $.each(frames, function(i, frame) {
            var json = $(this).find(".action").attr("json");
            var parsedJson = JSON.parse(json);

            keyFrames.push(parsedJson);
        });

        var motion = {
            "name" :motionName,
            "keyFrames" : keyFrames
        };

        console.log("Posting: " + JSON.stringify(motion));
        $.ajax({url: "/editor/motion/" + motionName, data: JSON.stringify((motion)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Saved motion")
        }});
    });

    $("#runMotion").click(function (e) {
        e.preventDefault();

        var motionId = $("#motionName").val();
        console.log("Running motion: " + motionId);

        $.ajax({url: "/editor/motion/run/maximus/" + motionId, type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
                console.log("Executed motion")
            }});
    });
}

function storeFrame(frameId) {
    var servoSteps = getPositionData();
    var timeInMs = $("#time").val();
    var json = {
        "jointTargets": servoSteps,
        "timeInMs": timeInMs,
        "keyFrameId" : frameId
    };


    renderFrame(frameId, timeInMs, servoSteps.length, json);
}

function renderFrame(frameId, time, nrSteps, json) {
    var data = {
        "id" : frameId,
        "time" : time,
        "frames" : nrSteps,
        "json" : JSON.stringify(json)
    };
    var rendered = renderTemplate("frame", data);

    var currentFrame = $("#frame-" + frameId);
    if(currentFrame.length > 0) {
        currentFrame.replaceWith(rendered);
    } else {
        $("#keyFrames").append(rendered);
    }

    $("#fedit-" + frameId).click(function (e){
        e.preventDefault();

        var frameJson = $(this).closest("tr").find(".action").attr("json");
        var parsedJson = JSON.parse(frameJson);

        $("#time").val(parsedJson.timeInMs);
        $("#editor").attr("frameid", parsedJson.keyFrameId);
        $("#jointPositions").empty();
        $.each(parsedJson.jointTargets, function(i, step){
            renderJointPosition(step.servoId, step.targetPosition, step.targetAngle);
        });
    });

    $("#fdelete-" + frameId).click(function (e) {
        e.preventDefault();

        $(this).closest("tr").remove();
    });
}

function getPositionData() {
    var servoSteps = [];

    var rows = $("#jointPositions").find("tr");
    $.each(rows, function(i, row) {
        var servoId = $(this).find(".jid").html();
        var pos = parseInt($(this).find(".pos").html());
        var deg = parseInt($(this).find(".deg").html());

        console.log("Found a servo: " + servoId + " with position: " + pos + " and degrees: " + deg);

        var json = {
            "servoId" : servoId,
            "targetPosition" : pos,
            "targetAngle" : deg
        };
        servoSteps.push(json);
    });

    return servoSteps;
}

function findNextFrameId() {
    var frameIds = $("#keyFrames").find("tr").find("td:first");
    var highest = 0;
    $.each(frameIds, function(i, f) {
        var fid = f.getAttribute("frameId");
        if(fid > highest) {
            highest = fid;
        }
    });

    return ++highest;
}

function addPosition(jointId) {
    var position = $("#position").val();
    var degrees = $("#degrees").val();

    $("#jointPosition-" + jointId).remove();

    renderJointPosition(jointId, position, degrees);
}

function renderJointPosition(jointId, position, degrees) {
    var data = {
        "id": jointId,
        "position": position,
        "degrees": degrees
    };
    var rendered = renderTemplate("jointPosition", data);
    $("#jointPositions").append(rendered);

    $("#jdelete-" + jointId).click(function (e) {
        e.preventDefault();

        $(this).closest("tr").remove();
    });

    $("#jedit-" + jointId).click(function (e) {
        e.preventDefault();

        var jointId = $(this).closest("tr").attr("jointId");
        var elementId = "joint-" + jointId;
        $("#" + elementId).trigger("click");
    });
}

function renderRobot(robot) {
    console.log("Rendering robot with name: " + robot.name);

    $.each(robot.chainSets, function(i, chain) {
        renderChain(chain, robot.robotId);
    });

    $("#joints").attr("robotId", robot.robotId);
}

function renderChain(chain) {
    console.log("Rendering chain: " + chain.name);
    var chainId = "chain-" + chain.name;

    $.each(chain.jointChains, function(i, jointChain) {
        renderJointChain(chain.name, chainId, jointChain);
    });
}

function renderJointChain(chainName, parentChainId, jointChain) {
    console.log("Rendering jointChain: " + jointChain.name);

    var jointChainId = "jointChain-" + jointChain.name;
    var data = {
        "chainName" : chainName,
        "name" : jointChain.name,
        "id" : jointChainId
    };
    var rendered = renderTemplate("jointContainer", data);
    $("#joints").append(rendered);

    $.each(jointChain.joints, function(i, joint) {
        renderJoint(jointChainId, joint);
    });
}

function renderJoint(jointChainId, joint) {
    console.log("Rendering joint: " + joint.name);

    var elementId = "joint-" + joint.id;
    var data = {
        "name" : joint.name,
        "id" : elementId,
        "jointId": joint.id,
        "jointType": joint.jointType
    };
    var rendered = renderTemplate("jointsTemplate", data);
    $("#" + jointChainId).append(rendered);

    $( "#" +elementId).click(function(e) {
        e.preventDefault();
        var jointId = $(this).attr("jointId");
        var robotId = $("#joints").attr("robotId");

        $(".list-group-item").removeClass("active");
        $(this).addClass("active");

        var url = "/humanoid/robot/" + robotId + "/joints/" + jointId;
        console.log("Requesting joint information: " + url);

        $.get(url, function(data) {
            console.log("Received joint data: " + JSON.stringify(data));
            $("#editor").attr("currentJoint", data.id);


            $("#servoTitle").text(data.id);
            $("#position").val(data.position);
            $("#degrees").val(data.degrees);
            $("#positionSlider").slider('setValue', data.degrees);
        });
    });
}

function handleSlideStop(slideEvt) {
    var val = slideEvt.value;
    var jointId = $("#editor").attr("currentJoint");

    if(isSyncMode()) {
        console.log("Sync moving mode");

        var similarJoints = getSimilarJoints(jointId);
        if(similarJoints.length > 1) {
            console.log("We have multiple joints");

            var joints = [];
            $.each(similarJoints, function(i, jointId) {
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
        setServoProperty(jointId, val);
    }
}

function getSimilarJoints(jointId) {
    var jointElem = $("#joint-" + jointId);
    var jointType = jointElem.attr("jointType");

    var nrTypes = $("a[jointType=" + jointType + "]");
    var joints = [];

    $.each(nrTypes, function(i, joint) {
        joints.push(joint.getAttribute("jointId"));
    });
    return joints;
}

function isSyncMode() {
    var syncCheckBox = $("#sync");
    return syncCheckBox.first().prop("checked");
}

function setServoProperty(jointId, degrees) {
    var json = {
        id: jointId,
        degrees: degrees,
        position: 0
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

function loadMotions() {
    $.get("/editor/motions", function(data) {
        console.log("Motions available: " + JSON.stringify(data));

        $.each(data, function(i, motion) {
            console.log("Motion available: " + motion.name);

            var data = {
                "id" : motion.name,
                "frames" : motion.frames.length
            };
            var rendered = renderTemplate("motion", data);
            $("#motionList").append(rendered);

            $("#medit-" + motion.name).click(function (e) {
                e.preventDefault();

                var motionId = $(this).closest("tr").attr("motionId");
                loadMotionInEditor(motionId);
            });
        })
    });
}

function loadMotionInEditor(motionId) {
    console.log("Loading motion into editor: " + motionId);

    $.get("/editor/motions/" + motionId, function(data) {
        $("#motionTool").attr("currentMotion", data.name);
        $("#motionLabel").html(data.name);
        $("#motionName").val(data.name);

        $("#keyFrames").empty();

        $.each(data.frames, function(i, frame) {
            renderFrame(frame.keyFrameId, frame.timeInMs, frame.jointTargets.length, frame);
        });
    });
}


$(document).ready(function() {
    $("#positionSlider").slider();

    loadMotions();
    renderRobots();

    addListeners();

    connect();
});