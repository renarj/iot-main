var pixelSize = 20

interact('.rainbow-pixel-canvas')
    .draggable({
        max: Infinity,
        maxPerElement: Infinity,
        origin: 'self',
        modifiers: [
            interact.modifiers.snap({
                // snap to the corners of a grid
                targets: [
                    interact.snappers.grid({ x: pixelSize, y: pixelSize })
                ]
            })
        ],
        listeners: {
            // draw colored squares on move
            move: function (event) {
                var context = event.target.getContext('2d')
                // calculate the angle of the drag direction
                var dragAngle = 180 * Math.atan2(event.dx, event.dy) / Math.PI

                // set color based on drag angle and speed
                context.fillStyle = "red";
                    // 'hsl(' +
                    // dragAngle +
                    // ', 86%, ' +
                    // (30 + Math.min(event.speed / 1000, 1) * 50) +
                    // '%)'

                // draw squares
                context.fillRect(
                    event.pageX - pixelSize / 2,
                    event.pageY - pixelSize / 2,
                    pixelSize,
                    pixelSize
                )
            }
        }
    })
    // clear the canvas on doubletap
    .on('doubletap', function (event) {
        var context = event.target.getContext('2d')

        context.clearRect(0, 0, context.canvas.width, context.canvas.height)
        resizeCanvases()
    })

function resizeCanvases () {
    [].forEach.call(document.querySelectorAll('.rainbow-pixel-canvas'), function (
        canvas
    ) {
        delete canvas.width
        delete canvas.height

        var rect = canvas.getBoundingClientRect()

        canvas.width = rect.width
        canvas.height = rect.height
    })
}




$(document).ready(function() {
    resizeCanvases()

    // interact.js can also add DOM event listeners
    interact(window).on('resize', resizeCanvases)

    doDragMove();
});

function doDragMove() {
    var element = document.getElementById('grid-snap')
    var x = 0; var y = 0

    interact(element)
        .draggable({
            modifiers: [
                interact.modifiers.snap({
                    targets: [
                        interact.snappers.grid({ x: 30, y: 30 })
                    ],
                    range: Infinity,
                    relativePoints: [ { x: 0, y: 0 } ]
                }),
                interact.modifiers.restrict({
                    restriction: element.parentNode,
                    elementRect: { top: 0, left: 0, bottom: 1, right: 1 },
                    endOnly: true
                })
            ],
            inertia: true
        })
        .on('dragmove', function (event) {
            x += event.dx
            y += event.dy

            event.target.style.transform = 'translate(' + x + 'px, ' + y + 'px)'
        })
}



