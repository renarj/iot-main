function renderRobots() {
    $.get("/humanoid/", function(data) {
        console.log("Humanoid Robots: " + JSON.stringify(data));

        $.each(data, function(i, robot) {
            renderRobot(robot);
        })
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
    // var data = {
    //     name : chain.name,
    //     id : chainId
    // };
    // var rendered = renderTemplate("chainContainer", data);
    // $("#joints").append(rendered);

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
    })
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

        var url = "/humanoid/robot/" + robotId + "/joints/" + jointId;
        console.log("Requesting joint information: " + url);

        $.get(url, function(data) {
            console.log("Received joint data: " + JSON.stringify(data));

            $("#position").val(data.position);
            $("#degrees").val(data.degrees);
            $("#positionSlider").slider('setValue', data.degrees);
        });
    });
}

function renderTemplate(templateName, data) {
    var template = $('#' + templateName).html();
    Mustache.parse(template);
    return Mustache.render(template, data);
}


$(document).ready(function() {
    $("#positionSlider").slider();

    renderRobots();
});