function renderRobots() {
    $.get("/humanoid/", function(data) {
        console.log("Humanoid Robots: " + JSON.stringify(data));

        $.each(data, function(i, robot) {
            renderRobot(robot);
            renderJointPosition(robot);
        })
    });
}

function renderJointPosition(robot) {
    $.get("/humanoid/robot/" + robot.name + "/joints", function(data) {
        console.log("Joint Data: " + JSON.stringify(data));

        $.each(data, function(i, joint) {
            console.log("Joint data for: " + joint.id);

            $("#degrees-" + joint.id).val(joint.degrees);
            $("#position-" + joint.id).val(joint.position);
            $("#slider-" + joint.id).slider('setValue', joint.degrees);
        });
    });
}

function renderRobot(robot) {
    console.log("Rendering robot with name: " + robot.name);

    $.each(robot.chainSets, function(i, chain) {
        renderChain(chain);
    })
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
}

function renderJointChain(parentChainId, jointChain) {
    console.log("Rendering jointChain: " + jointChain.name)

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
        "sliderId" : "slider-" + joint.id
    };

    var rendered = renderTemplate("joint", data);
    $("#" + jointChainId).append(rendered);

    var slider = $("#slider-" + joint.id);
    slider.slider();
}

function renderTemplate(templateName, data) {
    var template = $('#' + templateName).html();
    Mustache.parse(template);
    return Mustache.render(template, data);
}

$(document).ready(function() {
    renderRobots();
});