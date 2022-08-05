$(document).ready(function() {
    var m = nipplejs.create({
        zone: document.getElementById('test'),
        color: 'blue'
    });

    m.on('dir:up plain:up dir:left plain:left dir:down ' +
        'plain:down dir:right plain:right', function(nipple, event) {
        var x = event.direction.x;
        var y = event.direction.y;

        console.log("Joystick direction X: %s Y: %s", x, y);



    })
});
