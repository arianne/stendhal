const SERVER_ORIGIN = "https://stendhalgame.org";
// const SERVER_ORIGIN = "http://localhost";
const SERVER_PATH = "/account/login.html";

const UPDATE_URL = "https://arianne-project.org/stendhal/updates/manifest.json";


let authenticated = false;

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
	authenticated = true;
	let ticketString = hex(event.detail);
	document.querySelector("form").action = SERVER_ORIGIN + SERVER_PATH + "?" + Date.now();
	document.querySelector("#steam_auth_ticket").value = ticketString;
	document.querySelector("form").submit();
}

async function update() {
	try {
		let manifest = await Neutralino.updater.checkForUpdates(UPDATE_URL);

		if(manifest.version > NL_APPVERSION) {
			console.log("Updating...");
			await Neutralino.updater.install();
		} else {
			console.log('Stendhal is up to date.');
		}
	} catch (e) {
		console.log("Update check failed", e);
	}
}

function onNoAuthToken(_event) {
	console.log("onNoAuthToken");
	update();
	window.location = SERVER_ORIGIN + SERVER_PATH + "?" + Date.now();
}

async function timeoutExtensionConnection() {
	if (authenticated) {
		return;
	}
	let stats = await Neutralino.extensions.getStats();
	let connected = stats.connected.includes("nativehelper");
	if (!connected) {
		console.log("timeoutExtensionConnection");
		onNoAuthToken();
		return;
	}
	setTimeout(() => timeoutExtensionAuthentication(), 10000);
}

async function timeoutExtensionAuthentication() {
	if (authenticated) {
		return;
	}
	console.log("timeoutExtensionAuthentication");
	onNoAuthToken();
}

Neutralino.init();

// https://github.com/neutralinojs/neutralinojs/issues/678
Neutralino.events.on("windowClose", () => {
	Neutralino.app.exit();
});

setTimeout(() => timeoutExtensionConnection(), 2000);

Neutralino.events.on("steamAuthToken", onSteamAuthToken);
Neutralino.events.on("noAuthToken", onNoAuthToken);
Neutralino.extensions.dispatch("nativehelper", "request_authentication");
