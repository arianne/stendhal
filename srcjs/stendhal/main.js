/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
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

var Chat = require("../../build/ts/util/Chat").Chat;

var ui = require("../../build/ts/ui/UI").ui;
var UIComponentEnum = require("../../build/ts/ui/UIComponentEnum").UIComponentEnum;
var DesktopUserInterfaceFactory = require("../../build/ts/ui/factory/DesktopUserInterfaceFactory").DesktopUserInterfaceFactory;

var FloatingWindow = require("../../build/ts/ui/toolkit/FloatingWindow").FloatingWindow;

var ChatLogComponent = require("../../build/ts/ui/component/ChatLogComponent").ChatLogComponent;
var ItemInventoryComponent = require("../../build/ts/ui/component/ItemInventoryComponent").ItemInventoryComponent;

var ActionContextMenu = require("../../build/ts/ui/dialog/ActionContextMenu").ActionContextMenu;
var ApplicationMenuDialog = require("../../build/ts/ui/dialog/ApplicationMenuDialog").ApplicationMenuDialog;
var DropQuantitySelectorDialog = require("../../build/ts/ui/dialog/DropQuantitySelectorDialog").DropQuantitySelectorDialog;
var ImageViewerDialog = require("../../build/ts/ui/dialog/ImageViewerDialog").ImageViewerDialog;
var OutfitDialog = require("../../build/ts/ui/dialog/outfit/OutfitDialog").OutfitDialog;


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
			Chat.log("error", "Disconnected: " + error);
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
			let name = null;
			if (window.location.hash) {
				name = window.location.hash.substring(1);
				stendhal.config.character = name;
			} else {
				name = stendhal.config.character;

				if (name == null || typeof(name) === "undefined" || name === "") {
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
			}
			marauroa.clientFramework.chooseCharacter(name);
			var body = document.getElementById("body");
			body.style.cursor = "auto";
			Chat.log("client", "Loading world...");
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
				ui.get(UIComponentEnum.MiniMap).draw();
				ui.get(UIComponentEnum.BuddyList).update();
				stendhal.ui.equip.update();
				ui.get(UIComponentEnum.PlayerEquipment).update();
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
		stendhal.config.set("ui.sound", !stendhal.config.getBoolean("ui.sound"));
		stendhal.main.onSoundToggled();
	},

	onSoundToggled: function() {
		var soundbutton = document.getElementById("soundbutton");
		if (stendhal.config.getBoolean("ui.sound")) {
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

		document.getElementById("body").addEventListener("mouseenter", stendhal.main.onMouseEnter);

		var gamewindow = document.getElementById("gamewindow");
		gamewindow.setAttribute("draggable", true);
		gamewindow.addEventListener("mousedown", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dblclick", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dragstart", stendhal.ui.gamewindow.onDragStart);
		gamewindow.addEventListener("mousemove", stendhal.ui.gamewindow.onMouseMove);
		gamewindow.addEventListener("touchend", stendhal.ui.gamewindow.onTouchEnd);
		gamewindow.addEventListener("dragover", stendhal.ui.gamewindow.onDragOver);
		gamewindow.addEventListener("drop", stendhal.ui.gamewindow.onDrop);
		gamewindow.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);
		gamewindow.addEventListener("wheel", stendhal.ui.gamewindow.onMouseWheel);

		var menubutton = document.getElementById("menubutton");
		menubutton.addEventListener("click", (event) => {
			const dialogState = stendhal.config.dialogstates["menu"];
			const menuContent = new ApplicationMenuDialog();
			const menuFrame = ui.createSingletonFloatingWindow(
					"Menu", menuContent, dialogState.x, dialogState.y);
			menuContent.setFrame(menuFrame);
		});

		var soundbutton = document.getElementById("soundbutton");
		soundbutton.addEventListener("click", stendhal.main.toggleSound);
		// update button state
		stendhal.main.onSoundToggled();
	},

	devWarning: function() {
		console.log("%c ", "padding: 30px; background: url(" + window.location.protocol + "://" + window.location.host + "/images/buttons/devtools-warning.png) no-repeat; color: #AF0");
		console.log("%cIf someone told you, to copy and paste something here, it's a scam and will give them access to your account.", "color:#A00; background-color:#FFF; font-size:150%");
		console.log("If you are a developer and curious about Stendhal, have a look at https://stendhalgame.org/development/introduction.html to get the source code. And perhaps, contribute a feature or a bugfix. ");
		console.log(" ");
		console.log(" ");
		window["eval"] = undefined;
	},

	/**
	 * starts the Stendhal web client and connects to the Stendhal server.
	 */
	startup: function() {
		stendhal.main.devWarning();

		stendhal.config.init(new URL(document.location).searchParams);

		// update user interface after config is loaded
		stendhal.config.refreshTheme();
		document.getElementById("body").style.setProperty("font-family", stendhal.config.get("ui.font.body"));

		// cache tileset animations
		// FIXME: how to wait for animations to finish loading?
		stendhal.data.tileset.loadAnimations();

		new DesktopUserInterfaceFactory().create();

		Chat.log("error", "This is an early stage of an experimental web-based client. Please use the official client at https://stendhalgame.org to play Stendhal.");
		Chat.log("client", "Client loaded. Connecting...");

		stendhal.main.registerMarauroaEventHandlers();
		stendhal.main.registerBrowserEventHandlers();
		marauroa.clientFramework.connect(null, null);

		if (stendhal.ui.dialogHandler) {
			stendhal.ui.actionContextMenu = stendhal.ui.dialogHandler.copy();
			stendhal.ui.globalInternalWindow = stendhal.ui.dialogHandler.copy();
		} else {
			console.error("stendhal.ui.dialogHandler not found, some dialogs may not function");
		}

		if (document.getElementById("gamewindow")) {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}

		// attributes to set after connection made
		if (stendhal.config.getBoolean("input.movecont")) {
			const socket = marauroa.clientFramework.socket;
			let tries = 0;

			function checkConnection() {
				setTimeout(function() {
					tries++;
					if (socket.readyState === WebSocket.OPEN) {
						marauroa.clientFramework.sendAction({
							"type": "move.continuous",
							"move.continuous": ""
						});
						return;
					}

					if (tries > 5) {
						console.warn("could not set \"move.continuous\" attribute,"
								+ " gave up after " + tries + " tries");
						return;
					}

					checkConnection();
				}, 3000);
			}

			checkConnection();
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
	},

	onMouseEnter: function(e) {
		// use Stendhal's built-in cursor for entire page
		e.target.style.cursor = "url(" + stendhal.paths.sprites + "/cursor/normal.png) 1 3, auto";
	}
}

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
