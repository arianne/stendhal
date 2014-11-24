
	marauroa.clientFramework.onDisconnect = function(reason, error){
		stendhal.ui.chatLog.addLine("error", "Disconnected: " + error);
	}
	
	marauroa.clientFramework.onLoginRequired = function() {
		window.location = "/index.php?id=content/account/login&url="
			+ escape(window.location.pathname + window.location.hash);
	}

	marauroa.clientFramework.onLoginFailed = function(reason, text) {
		alert("Login failed. Please login on the Stendhal website first and make sure you open the client on an https://-URL");
		marauroa.clientFramework.close();
		document.getElementById("chatbar").disabled = true;
		document.getElementById("chat").style.backgroundColor = "#AAA";
	}

	marauroa.clientFramework.onAvailableCharacterDetails = function(characters) {
		if (window.location.hash) {
			marauroa.clientFramework.chooseCharacter(window.location.hash.substring(1));
		} else {
			marauroa.clientFramework.chooseCharacter(marauroa.util.first(characters).a.name);
		}

		var body = document.getElementById("body")
		body.style.cursor = "auto";
		stendhal.ui.chatLog.addLine("client", "Loading world...");
	}


	marauroa.clientFramework.onTransferREQ = function(items) {
		for (var i in items) {
			if (typeof(items[i].name) != "undefined" && items[i].name.match(".collision$")) {
				items[i].ack = true;
			}
		}
	}

	document.onkeydown=stendhal.ui.chatBar.keydown;
	document.onkeyup=stendhal.ui.chatBar.keyup;


function stendhalStartClient() {
	stendhal.ui.chatLog.addLine("client", "Client loaded. Connecting...");
	var body = document.getElementById("body")
	body.style.cursor = "wait";
	marauroa.clientFramework.connect(null, null);
}