var canvas = document.getElementById("canvas");
var ctx = canvas.getContext("2d");
ctx.strokeStyle = "lightgray";

var canvasOffset = $("#canvas").offset();
var offsetX = canvasOffset.left;
var offsetY = canvasOffset.top;

var mouseIsDown = false;
var lastX = 0;
var lastY = 0;

var ships = [];
/*
function random(max, add) {
  let res = [];
  for (let i = 0; i < 12; i++) {
    res.push(Math.floor(Math.random() * max) + add);
  }
  return res;
}

var xValues = [
  "Jan",
  "Feb",
  "Mar",
  "Apr",
  "May",
  "Jun",
  "Jul",
  "Aug",
  "Sep",
  "Oct",
  "Nov",
  "Dec",
];

new Chart("canvas", {
  type: "line",
  data: {
    labels: xValues,
    datasets: [
      {
        data: random(9, 130),
        borderColor: "red",
        label: "Yläpaine",
        fill: false,
      },
      {
        data: random(5, 85),
        borderColor: "blue",
        label: "Alapaine",
        fill: true,
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    legend: { display: false },
    scales: {
      y: {
        grid: {
          color: "white",
        },
      },
      x: {
        grid: {
          color: "white",
        },
      },
    },
    layout: {
      padding: {
        left: 100,
        top: 5,
      },
    },
  },
});*/

// make some ship
makeShip(20, 30, 50, 25, "skyblue");
makeShip(20, 100, 30, 25, "skyblue");
makeShip(20, 170, 50, 25, "salmon");
makeShip(20, 240, 30, 25, "salmon");

function makeShip(x, y, width, height, fill) {
  var ship = {
    x: x,
    y: y,
    width: width,
    height: height,
    right: x + width,
    bottom: y + height,
    fill: fill,
  };
  ships.push(ship);
  return ship;
}

drawAllShips();

function drawAllShips() {
  ctx.clearRect(0, 0, canvas.width, canvas.height);
  for (var i = 0; i < ships.length; i++) {
    var ship = ships[i];
    drawShip(ship);
    ctx.fillStyle = ship.fill;
    ctx.fill();
    ctx.stroke();
  }
}

function drawShip(ship) {
  ctx.beginPath();
  ctx.moveTo(ship.x, ship.y);
  ctx.lineTo(ship.right, ship.y);
  ctx.lineTo(ship.right + 10, ship.y + ship.height / 2);
  ctx.lineTo(ship.right, ship.bottom);
  ctx.lineTo(ship.x, ship.bottom);
  ctx.closePath();
}

function handleMouseDown(e) {
  mouseX = parseInt(e.clientX - offsetX);
  mouseY = parseInt(e.clientY - offsetY);

  // mousedown stuff here
  lastX = mouseX;
  lastY = mouseY;
  mouseIsDown = true;
}

function handleMouseUp(e) {
  mouseX = parseInt(e.clientX - offsetX);
  mouseY = parseInt(e.clientY - offsetY);

  // mouseup stuff here
  mouseIsDown = false;
}

function handleMouseMove(e) {
  if (!mouseIsDown) {
    return;
  }

  mouseX = parseInt(e.clientX - offsetX);
  mouseY = parseInt(e.clientY - offsetY);

  // mousemove stuff here
  for (var i = 0; i < ships.length; i++) {
    var ship = ships[i];
    drawShip(ship);
    if (ctx.isPointInPath(lastX, lastY)) {
      ship.x += mouseX - lastX;
      ship.y += mouseY - lastY;
      ship.right = ship.x + ship.width;
      ship.bottom = ship.y + ship.height;
    }
  }
  lastX = mouseX;
  lastY = mouseY;
  drawAllShips();
}

$("#canvas").mousedown(function (e) {
  handleMouseDown(e);
});
$("#canvas").mousemove(function (e) {
  handleMouseMove(e);
});
$("#canvas").mouseup(function (e) {
  handleMouseUp(e);
});
