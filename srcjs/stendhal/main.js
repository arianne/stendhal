/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};

stendhal.main = {
	errorCounter: 0,
	zoneFile: null,
	loaded: false,

	onDataMap: function(data) {
		var zoneinfo = {};
		var deserializer = marauroa.Deserializer.fromBase64(data);
		deserializer.readAttributes(zoneinfo);
		document.getElementById("zoneinfo").textContent = zoneinfo["readable_name"];
		stendhal.main.zoneFile = zoneinfo["file"];
		// Object { file: "Level 0/semos/city_easter.tmx", danger_level: "0.036429932929822995", zoneid: "", readable_name: "Semos city", id: "-1", color_method: "multiply" }
	},


	/**
	 * register marauroa event handlers.
	 */
	registerMarauroaEventHandlers: function() {

		marauroa.clientFramework.onDisconnect = function(reason, error){
			stendhal.ui.chatLog.addLine("error", "Disconnected: " + error);
		};

		marauroa.clientFramework.onLoginRequired = function() {
			window.location = "/index.php?id=content/account/login&url="
				+ escape(window.location.pathname + window.location.hash);
		};

		marauroa.clientFramework.onLoginFailed = function(reason, text) {
			alert("Login failed. Please login on the Stendhal website first and make sure you open the client on an https://-URL");
			marauroa.clientFramework.close();
			document.getElementById("chatinput").disabled = true;
			document.getElementById("chat").style.backgroundColor = "#AAA";
		};

		marauroa.clientFramework.onAvailableCharacterDetails = function(characters) {
			var name = null;
			if (window.location.hash) {
				name = window.location.hash.substring(1);
			} else {
				name = marauroa.util.first(characters)["a"]["name"];
				var admin = 0;
				for (var i in characters) {
					if (characters.hasOwnProperty(i)) {
						if (characters[i]["a"]["adminlevel"] > admin) {
							admin = characters[i]["a"]["adminlevel"];
							name = characters[i]["a"]["name"];
						}
					}
				}
			}
			marauroa.clientFramework.chooseCharacter(name);
			var body = document.getElementById("body")
			body.style.cursor = "auto";
			stendhal.ui.chatLog.addLine("client", "Loading world...");
		};


		marauroa.clientFramework.onTransferREQ = function(items) {
			for (var i in items) {
				if (typeof(items[i]["name"]) == "undefined") {
					continue;
				}
				items[i]["ack"] = true;
			}
		};

		marauroa.clientFramework.onTransfer = function(items) {
			var data = {};
			var zoneName = ""
			for (var i in items) {
				var name = items[i]["name"];
				zoneName = name.substring(0, name.indexOf("."));
				name = name.substring(name.indexOf(".") + 1);
				data[name] = items[i]["data"];
				if (name === "data_map") {
					stendhal.main.onDataMap(items[i]["data"]);
				}
			}
			stendhal.data.map.onTransfer(zoneName, data);
		};

		// update user interface on perceptions
		if (document.getElementById("gamewindow")) {
			marauroa.perceptionListener.onPerceptionEnd = function(type, timestamp) {
				stendhal.zone.sortEntities();
				stendhal.ui.minimap.draw();
				stendhal.ui.buddyList.update();
				stendhal.ui.equip.update();
				stendhal.ui.stats.update();
				if (!stendhal.main.loaded) {
					stendhal.main.loaded = true;
					// delay visibile change of client a little to allow for initialisation in the background for a smoother experience
					setTimeout(function() {
						document.getElementById("client").style.display = "block";
						document.getElementById("loginpopup").style.display = "none";
					}, 300);
				}
			}
		}
	},

	toggleSound: function() {
		stendhal.config.sound.play = !stendhal.config.sound.play;

		stendhal.main.onSoundToggled();
	},

	onSoundToggled: function() {
		var soundbutton = document.getElementById("soundbutton");
		if (stendhal.config.sound.play) {
			soundbutton.textContent = "🔊";
		} else {
			soundbutton.textContent = "🔇";
		}
	},

	/**
	 * registers global browser event handlers.
	 */
	registerBrowserEventHandlers: function() {
		document.addEventListener("keydown", stendhal.ui.keyhandler.onKeyDown);
		document.addEventListener("keyup", stendhal.ui.keyhandler.onKeyUp);
		document.addEventListener("contextmenu", stendhal.main.preventContextMenu);

		var gamewindow = document.getElementById("gamewindow");
		gamewindow.setAttribute("draggable", true);
		gamewindow.addEventListener("mousedown", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("touchstart", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dblclick", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dragstart", stendhal.ui.gamewindow.onDragStart);
		gamewindow.addEventListener("mousemove", stendhal.ui.gamewindow.onMouseMove);
		gamewindow.addEventListener("touchmove", stendhal.ui.gamewindow.onMouseMove);
		gamewindow.addEventListener("dragover", stendhal.ui.gamewindow.onDragOver);
		gamewindow.addEventListener("drop", stendhal.ui.gamewindow.onDrop);
		gamewindow.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);

		var minimap = document.getElementById("minimap");
		minimap.addEventListener("click", stendhal.ui.minimap.onClick);
		minimap.addEventListener("dblclick", stendhal.ui.minimap.onClick);

		var buddyList = document.getElementById("buddyList");
		buddyList.addEventListener("mouseup", stendhal.ui.buddyList.onMouseUp);
		buddyList.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);

		var menubutton = document.getElementById("menubutton");
		menubutton.addEventListener("click", stendhal.ui.menu.onOpenAppMenu);

		var soundbutton = document.getElementById("soundbutton");
		soundbutton.addEventListener("click", stendhal.main.toggleSound);
		// update button state
		stendhal.main.onSoundToggled();

		var chatinput = document.getElementById("chatinput");
		chatinput.addEventListener("keydown", stendhal.ui.chatinput.onKeyDown);
		chatinput.addEventListener("keypress", stendhal.ui.chatinput.onKeyPress);
	},

	/**
	 * starts the Stendhal web client and connects to the Stendhal server.
	 */
	startup: function() {
		stendhal.ui.chatLog.addLine("error", "This is an early stage of an experimental web-based client. Please use the official client at https://stendhalgame.org to play Stendhal.");
		stendhal.ui.chatLog.addLine("client", "Client loaded. Connecting...");

		stendhal.main.registerMarauroaEventHandlers();
		stendhal.main.registerBrowserEventHandlers();
		marauroa.clientFramework.connect(null, null);

		if (document.getElementById("gamewindow")) {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}
	},

	onerror: function(error) {
		stendhal.main.errorCounter++;
		if (stendhal.main.errorCounter > 5) {
			console.log("Too many errors, stopped reporting");
			return;
		}
		var text = error.message + "\r\n";
		text += error.filename + ":" + error.lineno;
		if (error.colno) {
			text += ":" + error.colno;
		}
		if (error.error) {
			text += "\r\n" + error.error.stack;
		}
		text += "\r\n" + window.navigator.userAgent;
		try {
			console.log(text);
			var action = {
				"type": "report_error",
				"text": text,
			};
			marauroa.clientFramework.sendAction(action);
		} catch (e) {
			// ignore
		}
		return true;
	},

	preventContextMenu: function(event) {
		event.preventDefault();
	}
}

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
