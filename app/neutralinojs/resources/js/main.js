function showInfo() {
    console.log(`
        ${NL_APPID} is running on port ${NL_PORT}  inside ${NL_OS}
        <br/><br/>
        <span>server: v${NL_VERSION} . client: v${NL_CVERSION}</span>
        `);
}


function onWindowClose() {
    Neutralino.app.exit();
}

Neutralino.init();

function onSteamAuthToken(event) {
    console.log("onSteamAuthToken", event);
}

Neutralino.events.on("windowClose", onWindowClose);
Neutralino.events.on("steamAuthToken", onSteamAuthToken);

showInfo();

async function showStats() {
    console.log("Show stats");
    let stats = await Neutralino.extensions.getStats();
    console.log('stats: ', stats);
}


showStats();
