$(document).ready(function() {
    retrieveServiceUrls(pageInit);
});

function pageInit() {
    loadMotions();
    loadControllers();

    renderSelectedRobot();

    addListeners();

    // connect();
}

function loadControllers() {
    loadControllerTemplate("controllerTemplate", "controllerList", function() {
        let controllerId = getControllerId();
        if(controllerId !== undefined) {
            $(".controllerButton[controllerId=" + controllerId + "]").addClass('active');

            loadRobots();
        }
    })
}

function loadRobots() {
    let controllerId = getControllerId();
    $("#robotListCard").removeClass("hide");

    $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/plugins(RobotExtension)/things?type=robot", function(data) {
        $.each(data, function (i, thing) {
            let data = {
                "controllerId": thing.controllerId,
                "robotId" : thing.thingId
            };

            renderAndAppend("robotTemplate", data, "robotList");
        })

        let robotId = getRobotId();
        if(robotId !== undefined) {
            $(".robotButton[robotId=" + robotId + "]").addClass('active');
        }
    });
}

function getRobotId() {
    let root = $("#root")
    return root.attr("robotId");
}

function connect() {
    console.log("Connecting to websocket");
    let socket = new SockJS('/ws');
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
        let value = state.values[attr];
        let id = state.sensorId + "-" + attr;
        $("#sensor-value-" + id).html(value);
    });
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

function renderSelectedRobot() {
    let robotId = getRobotId();
    if(robotId !== undefined) {
        let controllerId = getControllerId();

        console.log("Rendering selected robot: " + robotId + " on controller: " + controllerId)
        renderRobot(controllerId, robotId);
    }
}

function addListeners() {
    $("#positionSlider").on("slideStop", handleSlideStop);

    $("#tEnable").click(function (e) {
        e.preventDefault();

        let jointId = $("#editor").attr("currentJoint");
        console.log("We had torgue enable on joint: " + jointId);
        setTorgueSelected(jointId, true);
    });

    $("#torgueAllOn").click(function(e) {
        e.preventDefault();
        setTorgueAll(true);
    });

    $("#torgueAllOff").click(function(e) {
        e.preventDefault();
        setTorgueAll(false);
    });

    $("#tDisable").click(function (e) {
        e.preventDefault();

        var jointId = $("#editor").attr("currentJoint");
        console.log("We had torgue disable on joint: " + jointId);
        setTorgueSelected(jointId, false);
    });

    $("#torgueOnSelected").click(function (e) {
        e.preventDefault();
        setTorgueSelected(getSelectedJoints().join(),true);
    });

    $("#torgueOffSelected").click(function (e) {
        e.preventDefault();

        setTorgueSelected(getSelectedJoints().join(), false);
    });

    $("#add").click(function (e) {
        e.preventDefault();

        let jointId = $("#editor").attr("currentJoint");

        if(isSyncMode()) {
            console.log("Sync mode addition, getting similar joints")
            let similarJoints = getSimilarJoints(jointId);

            $.each(similarJoints, function(i, jid) {
                addPosition(jid);
            });
        } else {
            console.log("Adding single position of joint and adding");
            addPosition(jointId);
        }
    });

    $("#addSelected").click(function (e) {
        e.preventDefault();

        let servos = getSelectedJoints();
        $.each(servos, function(i, joint) {
            addLivePosition(joint);
        })
    });

    $("#clearFrame").click(function (e) {
        e.preventDefault();

        $("#jointPositions").empty();
    });

    $("#saveFrame").click(function (e) {
        e.preventDefault();

        let frameName = $("#frameName").val();
        if(frameName !== undefined && frameName !== "") {
            storeFrame(frameName);
        } else {
            let frameId = $("#editor").attr("frameid");
            if(frameId !== undefined) {
                console.log("We already have a frame id: " + frameId);
            } else {
                frameId = findNextFrameId();
                console.log("Frame not set yet, generated frameId: " + frameId);
            }

            storeFrame(frameId);
        }
    });

    $("#goToPositions").click(function (e) {
        e.preventDefault();

        let frameData = getFrameData("tempFrame");
        let data = {
            "controllerId" : getControllerId(),
            "thingId" : getRobotId(),
            "commandType" : "VALUE",
            "attributes" : {
                "frame" : JSON.stringify(frameData)
            }
        };
        sendCommand(data, function() {
            console.log("Executed keyframe");
        })
        // $.ajax({url: "/editor/motion/run/" + robotId + "/keyFrame", data: JSON.stringify((data)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
        //         console.log("Executed keyframe");
        //     }, error: function(data) {
        //         showModal("KeyFrame error", "Could not run frame: " + JSON.stringify(data));
        //     }});
    });

    $("#newFrame").click(function (e) {
        e.preventDefault();

        $("#editor").attr("frameid", "");
        $("#jointPositions").empty();
        $("#time").val("");
        $("#frameName").val("");
    });

    $("#saveMotion").click(function (e) {
        let frames = $("#keyFrames").find("tr");

        let motionName = $("#motionName").val();
        let keyFrames = [];
        $.each(frames, function(i, frame) {
            let json = $(this).find(".action").attr("json");
            let parsedJson = JSON.parse(json);

            keyFrames.push(parsedJson);
        });

        if(motionName === undefined || motionName === "") {
            showModal("Motion Save Error", "Could not store motion without name");
        } else {
            let motion = {
                "thingId" :motionName,
                "controllerId" : getControllerId(),
                "parentId" : getRobotId(),
                "friendlyName" : motionName,
                "schemaId" : "Motion",
                "type" : "Motion",
                "pluginId" : "RobotExtension",
                "properties" : {
                    "motionData" : JSON.stringify(keyFrames)
                }
            };

            console.log("Posting: " + JSON.stringify(motion));
            $.ajax({url: thingSvcUrl + "/api/controllers(" + getControllerId() + ")/things", data: JSON.stringify((motion)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
                    console.log("Saved motion");
                    loadMotions();

                    showModal("Motion Saved", "Motion was saved successfully");
                }, error: function(data) {
                    showModal("Motion Save Error", "Could not save motion: " + JSON.stringify(data));
                }});
        }
    });

    $("#runMotion").click(function (e) {
        e.preventDefault();

        let motionId = $("#motionName").val();
        console.log("Running motion: " + motionId);

        let data = {
            "controllerId" : getControllerId(),
            "thingId" : getRobotId(),
            "commandType" : "VALUE",
            "attributes" : {
                "motion" : motionId
            }
        };
        sendCommand(data);
    });

    $("#newMotion").click(function (e) {
        e.preventDefault();

        console.log("Clearing motion");

        $("#jointPositions").empty();
        $("#editor").attr("frameid", "");
        $("#time").val("");
        $("#frameName").val("");

        $("#motionTool").attr("currentMotion", "");
        $("#motionLabel").html("");
        $("#motionName").val("");

        $("#keyFrames").empty();
    });

    $("#exportMotions").click(function (e) {
        e.preventDefault();

        $.get("/editor/motions", function(data) {

        });
    });
}
function setTorgueAll(targetState) {
    let data = {
        "controllerId" : getControllerId(),
        "thingId" : getRobotId(),
        "commandType" : "SWITCH",
        "attributes" : {
            "torgue" : targetState
        }
    };

    console.log("Torgue command: " + JSON.stringify(data));
    sendCommand(data, function() {
        console.log("Torgue for all servos set to: " + targetState);
    })
}

function setTorgueSelected(servos, targetState) {
    if(servos !== undefined && servos.length > 0) {
        let sD = servos;
        if(servos.isArray) {
            sD = servos.join();
        }
        let targetId = servos.isArray ? getRobotId() : sD;
        let data = {
            "controllerId" : getControllerId(),
            "thingId" : targetId,
            "commandType" : "VALUE",
            "attributes" : {
                "torgue" : targetState,
                "servos" : sD
            }
        };

        console.log("Torgue command: " + JSON.stringify(data));
        sendCommand(data, function() {
            console.log("Torgue for all selected servos: " + servos + " set to: " + targetState);
        })
    }
}

function getSelectedJoints() {
    let servos = [];
    $(".jointCheckBox:checked").each(function (i) {
        var jId = $(this).attr("jointId");
        servos.push(jId);
    });
    return servos;
}

function storeFrame(frameId) {
    let frameData = getFrameData(frameId);

    if(frameData.timeInMs === undefined || frameData.timeInMs === "") {
        showModal("KeyFrame error", "No time specified");
    } else {

        $("#editor").attr("frameid", frameId);
        renderFrame(frameId, frameData.timeInMs, frameData.jointTargets.length, frameData);
    }
}

function getFrameData(frameId) {
    let jointSteps = getPositionData();
    let timeInMs = $("#time").val();

    return {
        "jointTargets": jointSteps,
        "timeInMs": timeInMs,
        "keyFrameId": frameId
    };
}

function renderFrame(frameId, time, nrSteps, json) {
    let data = {
        "id" : frameId,
        "time" : time,
        "frames" : nrSteps,
        "json" : JSON.stringify(json)
    };
    let rendered = renderTemplate("frame", data);

    let currentFrame = $("#frame-" + frameId);
    if(currentFrame.length > 0) {
        currentFrame.replaceWith(rendered);
    } else {
        $("#keyFrames").append(rendered);
    }

    $("#fedit-" + frameId).click(function (e){
        e.preventDefault();

        let frameJson = $(this).closest("tr").find(".action").attr("json");
        let parsedJson = JSON.parse(frameJson);

        $("#time").val(parsedJson.timeInMs);
        $("#frameName").val(parsedJson.keyFrameId);
        $("#editor").attr("frameid", parsedJson.keyFrameId);
        $("#jointPositions").empty();
        $.each(parsedJson.jointTargets, function(i, step){
            renderJointPosition(step.jointId, step.targetPosition, step.targetAngle);
        });
    });

    $("#fdelete-" + frameId).click(function (e) {
        e.preventDefault();

        $(this).closest("tr").remove();
    });
}

function getPositionData() {
    let servoSteps = [];

    let rows = $("#jointPositions").find("tr");
    $.each(rows, function(i, row) {
        let jointId = $(this).find(".jid").attr("jointId");
        let pos = parseInt($(this).find(".pos").html());
        let deg = parseInt($(this).find(".deg").val());

        console.log("Found a Joint: " + jointId + " with position: " + pos + " and degrees: " + deg);

        let json = {
            "jointId" : jointId,
            "targetPosition" : pos,
            "targetAngle" : deg
        };
        servoSteps.push(json);
    });

    return servoSteps;
}

function findNextFrameId() {
    let frameIds = $("#keyFrames").find("tr").find("td:first");
    let highest = 0;
    $.each(frameIds, function(i, f) {
        let fid = f.getAttribute("frameId");
        if(fid > highest) {
            highest = fid;
        }
    });

    return ++highest;
}

function addLivePosition(jointId) {
    let robotId = $("#joints").attr("robotId");
    let url = "/humanoid/robot/" + robotId + "/joints/" + jointId;
    console.log("Requesting joint information: " + url);

    $.get(url, function(data) {
        console.log("Received joint data: " + JSON.stringify(data));
        $("#jointPosition-" + jointId).remove();
        renderJointPosition(jointId, data.values.position, data.values.degrees);
    });
}

function addPosition(jointId) {
    let position = $("#position").val();
    let degrees = $("#degrees").val();

    $("#jointPosition-" + jointId).remove();

    renderJointPosition(jointId, position, degrees);
}

function findName(jointId) {
    let elementId = "joint-" + jointId;
    let n = $("#" + elementId).attr("friendlyName");
    console.log("Found friendly name: " + n + " for joint: " + jointId);
    return n;
}

function renderJointPosition(jointId, position, degrees) {
    var data = {
        "id": jointId,
        "name" : findName(jointId),
        "position": position,
        "degrees": degrees
    };
    let rendered = renderTemplate("jointPosition", data);
    $("#jointPositions").append(rendered);

    $("#jdelete-" + jointId).click(function (e) {
        e.preventDefault();

        $(this).closest("tr").remove();
    });

    $("#jedit-" + jointId).click(function (e) {
        e.preventDefault();

        let jointId = $(this).closest("tr").attr("jointId");
        let elementId = "joint-" + jointId;
        $("#" + elementId).trigger("click");
    });
}

function renderRobot(controllerId, robotId) {
    console.log("Rendering robot with name: " + robotId);

    $("#groupListCard").removeClass("hide");
    $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/things(" + robotId + ")/children?type=bodyGroup", function(data) {
        if(data.length > 0) {
            $.each(data, function(i, child) {
                if(child.type === "bodyGroup") {
                    renderBodyGroup(child);
                }
            });
        } else {
            $.get(thingSvcUrl + "/api/controllers(" + controllerId + ")/things(" + robotId + ")/children?type=Joint", function(data) {
                $.each(data, function(i, child) {
                    renderJoint("groupList", child);
                });
            })
        }
    })
}

function renderSensor(sensor) {
    $.each(sensor.values, function(i, value) {
        let data = {
            "id" : sensor.name + "-" + i,
            "value": value.raw
        };
        let rendered = renderTemplate("sensor", data);
        $("#sensors").append(rendered);

    });
}

function renderBodyGroup(group) {
    console.log("Rendering chain: " + group.thingId);
    let groupId = "group-" + group.thingId;
    let data = {
        "name" : group.thingId,
        "id" : groupId
    };
    renderAndAppend("groupContainer", data, "groupList")

    $.get(thingSvcUrl + "/api/controllers(" + group.controllerId + ")/things(" + group.thingId + ")/children", function(data) {
        $.each(data, function(i, robotPart) {
            if(robotPart.type === "Joint") {
                renderJoint(groupId, robotPart)
            } else {
                renderLinkedJoints(group, groupId, robotPart)
            }
        });
    })

}

function renderLinkedJoints(group, groupId, robotPart) {
    console.log("Rendering Joints for group: " + group.thingId + " and robot Part: " + robotPart.thingId);

    $.get(thingSvcUrl + "/api/controllers(" + group.controllerId + ")/things(" + robotPart.thingId + ")/linked?type=Joint", function(data) {
        $.each(data, function(i, joint) {
            renderJoint(groupId, joint)
        });
    })


    // $("#cb-" + jointChainId).change(function() {
    //     if ($(this).is(':checked')) {
    //         $("input[jointChainId=" + jointChainId + "]").prop("checked", true);
    //     } else {
    //         $("input[jointChainId=" + jointChainId + "]").prop("checked", false);
    //     }
    // });
}

function renderJoint(groupId, joint) {
    console.log("Rendering joint: " + joint.thingId);

    let elementId = "joint-" + joint.thingId;
    let data = {
        "name" : joint.thingId,
        "id" : elementId,
        "groupId" : groupId,
        "jointId": joint.thingId,
        "jointType": joint.type
    };
    renderAndAppend("jointsTemplate", data, groupId)

    $( "#" +elementId).click(function(e) {
        let jointId = $(this).attr("jointId");
        let controllerId = getControllerId();

        $(".list-group-item").removeClass("active");
        $(this).addClass("active");

        console.log("Selected joint: " + jointId + " on controller: " + controllerId);
        $.get(stateSvcUrl + "/api/state/controllers(" + controllerId + ")/things(" + jointId + ")", function(data) {
            let map = [];
            $.each(data.stateItems, function(i, si) {
                map[si.attribute] = si.value;
            });
            console.log("Received joint data: " + JSON.stringify(data));
            $("#editor").attr("currentJoint", data.itemId);

            $("#servoTitle").text(data.itemId);
            $("#position").val(map["position"].value);
            $("#degrees").val(map["degrees"].value);
            $("#positionSlider").val(map["degrees"].value);
        })
    });
}

function handleSlideStop(slideEvt) {
    let val = slideEvt.value;
    let jointId = $("#editor").attr("currentJoint");

    if(isSyncMode()) {
        console.log("Sync moving mode");

        let similarJoints = getSimilarJoints(jointId);
        if(similarJoints.length > 1) {
            console.log("We have multiple joints");

            let joints = [];
            $.each(similarJoints, function(i, jointId) {
                let json = {
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
    let json = {
        id: jointId,
        degrees: degrees,
        position: 0
    };

    console.log("Posting: " + JSON.stringify(json));

    $.ajax({url: "/humanoid/robot/maximus/joint", data: JSON.stringify((json)), type: "POST", contentType: "application/json; charset=utf-8", success: function(data) {
            console.log("Set joint succesfully");
        }});
}

function loadMotions() {
    let ml = $("#motionList");
    ml.empty();

    $.get(thingSvcUrl + "/api/controllers(" + getControllerId() + ")/things(" + getRobotId() + ")/children?type=Motion", function(data) {
        console.log("Motions available: " + JSON.stringify(data));

        $.each(data, function(i, motion) {
            console.log("Motion available: " + motion.thingId);
            let data = {
                "id" : motion.thingId,
                "frames" : JSON.parse(motion.properties.motionData).length
            };
            let rendered = renderTemplate("motion", data);
            ml.append(rendered);

            $("#medit-" + motion.thingId).click(function (e) {
                e.preventDefault();

                let motionId = $(this).closest("tr").attr("motionId");
                loadMotionInEditor(motionId);
            });

            $("#mdelete-" + motion.name).click(function (e) {
                e.preventDefault();

                // $.ajax({url: "/editor/motion/" + motion.name, type: "DELETE", contentType: "application/json; charset=utf-8", success: function(data) {
                //         console.log("Motion deleted succesfully");
                //         loadMotions();
                //
                //         showModal("Motion Deleted", "Motion " + motion.name + " was deleted");
                //     }});
            }) ;
        })
    });
}

function loadMotionInEditor(motionId) {
    console.log("Loading motion into editor: " + motionId);

    $.get(thingSvcUrl + "/api/controllers(" + getControllerId() + ")/things(" + motionId + ")", function(data) {
        $("#motionTool").attr("currentMotion", data.thingId);
        $("#motionLabel").html(data.thingId);
        $("#motionName").val(data.thingId);

        $("#keyFrames").empty();

        let frames = JSON.parse(data.properties.motionData);
        $.each(frames, function(i, frame) {
            renderFrame(frame.keyFrameId, frame.timeInMs, frame.jointTargets.length, frame);
        });
    });
}

function showModal(title, body) {
    let modal = $("#feedbackModal");

    $("#modalTitle").text(title);
    $("#modalText").text(body);

    modal.modal({
        show: true
    })
}


