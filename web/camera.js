// Set constraints for the video stream
var constraints = { video: { facingMode: "user" }, audio: false };
// Define constants
const cameraView = document.querySelector("#camera--view"),
    cameraOutput = document.querySelector("#camera--output"),
    cameraSensor = document.querySelector("#camera--sensor"),
    cameraTrigger = document.querySelector("#camera--trigger");
// Access the device camera and stream to cameraView
function cameraStart() {
    navigator.mediaDevices
        .getUserMedia(constraints)
        .then(function (stream) {
            track = stream.getTracks()[0];
            cameraView.srcObject = stream;
        })
        .catch(function (error) {
            console.error("Oops. Something is broken.", error);
        });
}

function postData() {
    // /api/1.1/files
    var token = "wOVN53eAY3AAJmkGh77dp3CRgpagJeRL";
    const apiUrl =
        "https://b6i43d47.directus.app/files/?access_token=wOVN53eAY3AAJmkGh77dp3CRgpagJeRL";
    /*
    https://b6i43d47.directus.app/items/images/?access_token=wOVN53eAY3AAJmkGh77dp3CRgpagJeRL
    const options = {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            user_id: 4,
            image: cameraSensor.toDataURL("image/webp"),
        }),
    };*/
    console.log(cameraSensor.toDataURL("image/webp"));

    // Create a new FormData object to store the image file
    const formData = new FormData();
    formData.append("file", cameraSensor.toDataURL("image/webp"));
    console.log(formData);
    formData.entries[0];

    // Set up the POST request options
    const options = {
        method: "POST",
        headers: {
            "Content-Type": "image/webp",
        },
        body: cameraSensor.toDataURL("image/webp"),
    };

    // Send the POST request using fetch
    fetch(apiUrl, options)
        .then((response) => response.json())
        .then((data) => console.log(data))
        .catch((error) => console.error(error));
}
// Take a picture when cameraTrigger is tapped
cameraTrigger.onclick = function () {
    cameraSensor.width = cameraView.videoWidth;
    cameraSensor.height = cameraView.videoHeight;
    cameraSensor.getContext("2d").drawImage(cameraView, 0, 0);
    cameraOutput.src = cameraSensor.toDataURL("image/webp");
    cameraOutput.classList.add("taken");
    postData();
};
// Start the video stream when the window loads
window.addEventListener("load", cameraStart, false);
