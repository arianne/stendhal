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

	onDataMap: function(data) {
		var zoneinfo = {};
		var deserializer = marauroa.Deserializer.fromBase64(data);
		deserializer.readAttributes(zoneinfo);
		document.getElementById("zoneinfo").textContent = zoneinfo["readable_name"];
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
				if (items[i]["name"].match(".collision$")
					|| items[i]["name"].match(".data_map$")) {
					items[i]["ack"] = true;
				}
			}
		};
		
		marauroa.clientFramework.onTransfer = function(items) {
			for (var i in items) {
				if (items[i]["name"].match(".data_map$")) {
					stendhal.main.onDataMap(items[i]["data"]);
				}
			}
		};

		// update user interface on perceptions
		if (document.getElementById("gamewindow")) {
			marauroa.perceptionListener.onPerceptionEnd = function(type, timestamp) {
				stendhal.zone.sortEntities();
				stendhal.ui.minimap.draw();
				stendhal.ui.buddyList.update();
				stendhal.ui.equip.update();
				stendhal.ui.stats.update();
				stendhal.data.map.load(marauroa.currentZoneName);
			}
		}
	},


	/**
	 * registers global browser event handlers.
	 */
	registerBrowserEventHandlers: function() {
		var gamewindow = document.getElementById("gamewindow");
		gamewindow.setAttribute("draggable", true);
		gamewindow.addEventListener("mousedown", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dragstart", stendhal.ui.gamewindow.onDragStart);
		gamewindow.addEventListener("dragover", stendhal.ui.gamewindow.onDragOver);
		gamewindow.addEventListener("drop", stendhal.ui.gamewindow.onDrop);
		gamewindow.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);

		var minimap = document.getElementById("minimap");
		minimap.addEventListener("click", stendhal.ui.minimap.onClick);

		var buddyList = document.getElementById("buddyList");
		buddyList.addEventListener("mouseup", stendhal.ui.buddyList.onMouseUp);
		buddyList.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);

		var chatinput = document.getElementById("chatinput");
		chatinput.addEventListener("keydown", stendhal.ui.chatinput.onKeyDown);
		chatinput.addEventListener("keyup", stendhal.ui.chatinput.onKeyUp);
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
			
			document.addEventListener("click", function(e) {
				if (e.target.dataItem) {
					marauroa.clientFramework.sendAction({
						type: "use", 
						"target_path": e.target.dataItem.getIdPath(),
						"zone": marauroa.currentZoneName
					});
				}
			});
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
	}
}

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
