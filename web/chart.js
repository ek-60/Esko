// Using quickchart.io because hololens doesn't support canvas-html tag
const quickchart = "https://quickchart.io/chart?c=";
const read_only = "bW-wll4QD6d84BLHnl9XbLx8VREoX_pH";
const directus_url = "https://test.koskiniemi.fi";
const currentUser = {
	id: undefined,
	name: undefined,
	age: undefined,
	data: undefined,
};

lookNewUser();
// Main loop that checks if user has changed
const start = setInterval(async function () {
	lookNewUser();
}, 5000);

// Check if user has changed
async function lookNewUser() {
	const response = await fetch(
		`${directus_url}/items/currentUser/1?access_token=${read_only}`
	);

	const json = await response.json();

	// Jos käyttäjä vaihtuu, hae uudet tiedot
	if (json.data.active_user != currentUser.id) {
		// Update current user to new id (user)
		currentUser.id = json.data.active_user;
		// Get new data from database (directus)
		getNewData();
	}

	return currentUser.id;
}

// Object that is used to call functions from other javascript files (webqr.js)
var test = {};

// Function that is called when QR-code is scanned
test.alert = async function (id) {
	const data = { pid: 1, active_user: id };
	const xhr = new XMLHttpRequest();
	xhr.open(
		"PATCH",
		`${directus_url}/items/currentUser/1?access_token=${read_only}`
	);
	xhr.setRequestHeader("Content-Type", "application/json");
	xhr.onreadystatechange = function () {
		if (xhr.readyState === XMLHttpRequest.DONE) {
			if (xhr.status === 200) {
				const response = JSON.parse(xhr.responseText);
				console.log(response);
			} else {
				console.error(xhr.status);
			}
		}
	};
	// Update current user to database (directus) with new id (user)
	xhr.send(JSON.stringify(data));

	currentUser.id = id;
	getNewData();
};

// Get new data from database (directus)
async function getNewData() {
	const response = await fetch(
		`${directus_url}/items/user/${currentUser.id}?access_token=${read_only}`
	);
	const json = await response.json();

	currentUser.name = json.data.name;
	currentUser.age = json.data.age;

	// Get data from database (directus)
	const response2 = await fetch(
		`${directus_url}/items/potilastiedot?access_token=${read_only}&filter[user_id][_eq]=${currentUser.id}&sort=timestamp`
	);
	const json2 = await response2.json();
	currentUser.data = json2.data;

	console.log(currentUser);

	// Update html elements (top-left info bar) with new data
	currentUser.data.forEach((e) => {
		document.getElementById(e.tyyppi).innerHTML = e.arvo;
	});
	const infoDiv = document.getElementsByClassName("personal-info");
	infoDiv[0].children[0].innerText = `${currentUser.name}, ${currentUser.age}, ${currentUser.id}`;

	// Draw chart with new data
	drawChart(
		"Weight",
		currentUser.data
			.filter((e) => e.tyyppi === "weight")
			.map((item) => item.arvo)
	);
}

// Draw chart with new data
async function drawChart(graphName, data) {
	const json = {
		type: "line",
		data: {
			labels: [
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
			],
			datasets: [
				{
					label: graphName,
					data: data,
					backgroundColor: "red",
					borderColor: "red",
					fill: false,
				},
			],
		},
		options: {
			legend: { position: "bottom", labels: { fontColor: "white" } },
			responsive: true,
			maintainAspectRatio: false,
			scales: {
				xAxes: [
					{
						gridLines: {
							color: "white",
						},
						ticks: {
							fontColor: "white",
						},
					},
				],
				yAxes: [
					{
						gridLines: {
							color: "white",
						},
						ticks: {
							fontColor: "white",
						},
					},
				],
			},
		},
	};
	const bloodpressure = document.getElementById("chart");
	// Load chart from 3rd party API (quickchart.io) because canvas-html tag is not supported by hololens
	bloodpressure.src = quickchart + encodeURIComponent(JSON.stringify(json));

	// Just in case hide camera and show chart so they don't overlap
	hide("none", "inline");
}

// Function to make hiding and showing elements easier
function hide(cam_val, chart_val) {
	const cam = document.getElementById("camera");
	cam.style.display = cam_val;

	const chart = document.getElementById("chart");
	chart.style.display = chart_val;
}

// To make elements draggable (jquery-ui)
$(function () {
	$("#chart").draggable();
	$("#camera").draggable();
});

// Function that is used to call from sidebar menu
function WeightButton() {
	drawChart(
		"Weight",
		currentUser.data
			.filter((e) => e.tyyppi === "weight")
			.map((item) => item.arvo)
	);
}
// Function that is used to call from sidebar menu
function OxygenButton() {
	drawChart(
		"Oxygen",
		currentUser.data
			.filter((e) => e.tyyppi === "oxygen")
			.map((item) => item.arvo)
	);
}
// Function that is used to call from sidebar menu
function CameraButton() {
	// Show camera div and hide graph div
	hide("inline", "none");
}

/* Function that is used to call from sidebar menu
 * This is more complicated because blood pressure needs 2 lines in chart
 * and it needs to be split from string to 2 numbers
 */
function BPButton() {
	const json = {
		type: "line",
		data: {
			labels: [
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
			],
			datasets: [
				{
					label: "Systolic",
					data: currentUser.data
						.filter((e) => e.tyyppi === "bloodPressure")
						.map((item) => item.arvo.split("/")[0]),
					backgroundColor: "red",
					borderColor: "red",
					fill: false,
				},
				{
					label: "Diastolic",
					data: currentUser.data
						.filter((e) => e.tyyppi === "bloodPressure")
						.map((item) => item.arvo.split("/")[1]),
					backgroundColor: "green",
					borderColor: "green",
					fill: false,
				},
			],
		},
		options: {
			legend: { position: "bottom", labels: { fontColor: "white" } },
			responsive: true,
			maintainAspectRatio: false,
			scales: {
				xAxes: [
					{
						gridLines: {
							color: "white",
						},
						ticks: {
							fontColor: "white",
						},
					},
				],
				yAxes: [
					{
						gridLines: {
							color: "white",
						},
						ticks: {
							fontColor: "white",
						},
					},
				],
			},
		},
	};
	const bloodpressure = document.getElementById("chart");
	bloodpressure.src = quickchart + encodeURIComponent(JSON.stringify(json));
	hide("none", "inline");
}
