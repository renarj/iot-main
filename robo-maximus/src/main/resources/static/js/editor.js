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
        renderChain(chain);
    })
}

function renderChain(chain) {
    console.log("Rendering chain: " + chain.name);

    $.each(chain.jointChains, function(i, jointChain) {
        renderJointChain(jointChain);
    });
}

function renderJointChain(jointChain) {
    console.log("Rendering jointChain: " + jointChain.name)

    $.each(jointChain.joints, function(i, joint) {
        renderJoint(joint);
    })
}

function renderJoint(joint) {
    console.log("Rendering joint: " + joint.name);
}

$(document).ready(function() {
    renderRobots();
});