const SERVER_ORIGIN = "https://stendhalgame.org";
// const SERVER_ORIGIN = "http://localhost";
const SERVER_PATH = "/account/login.html";

const byteToHex = [];
for (let n = 0; n <= 0xff; ++n) {
	const hexOctet = n.toString(16).padStart(2, "0");
	byteToHex.push(hexOctet);
}

function hex(arrayBuffer) {
	const buff = new Uint8Array(arrayBuffer);
	const hexOctets = [];
	for (let i = 0; i < buff.length; ++i) {
		hexOctets.push(byteToHex[buff[i]]);
	}
	return hexOctets.join("");
}

function onSteamAuthToken(event) {
	console.log("onSteamAuthToken");
	let ticketString = hex(event.detail);
	document.querySelector("form").action = SERVER_ORIGIN + SERVER_PATH + "?" + Date.now();
	document.querySelector("#steam_auth_ticket").value = ticketString;
	document.querySelector("form").submit();
}

function onNoAuthToken(_event) {
	console.log("onNoAuthToken");
	window.location = SERVER_ORIGIN + SERVER_PATH + "?" + Date.now();
}

Neutralino.init();
Neutralino.events.on("steamAuthToken", onSteamAuthToken);
Neutralino.events.on("noAuthToken", onNoAuthToken);
Neutralino.extensions.dispatch('nativehelper', 'request_authentication');
console.log("init complete");

