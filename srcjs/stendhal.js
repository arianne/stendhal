	marauroa.rpobjectFactory.entity.prototype.onEvent = function(e) {
		if (e.c == "private_text") {
			stendhal.ui.chatLog.addLine(e.a.texttype.toLowerCase(), e.a.text);
		} else if (e.c == "text") {
			stendhal.ui.chatLog.addLine("normal", this.title + ": " + e.a.text);
		}
	}

	marauroa.rpobjectFactory.entity.prototype.set = function(key, value) {
		if (key == 'name') {
			if (typeof(this['title']) == "undefined") {
				this['title'] = value;
			}
		} else {
			this[key] = value;
		}
	}

	marauroa.clientFramework.onDisconnect = function(reason, error){
		stendhal.ui.chatLog.addLine("error", "Disconnected: " + error);
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
	}


	marauroa.clientFramework.onTransferREQ = function(items) {
		for (var i in items) {
			if (typeof(items[i].name) != "undefined" && items[i].name.match(".collision$")) {
				items[i].ack = true;
			}
		}
	}


	document.onkeydown=stendhal.ui.chatBar.keydown;


	marauroa.clientFramework.connect(null, null);
