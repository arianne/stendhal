/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;

import { PerceptionListener } from "./PerceptionListener";
import { singletons } from "./SingletonRepo";

import { Paths } from "./data/Paths";

import { Ground } from "./entity/Ground";
import { RPObject } from "./entity/RPObject";
import { Zone } from "./entity/Zone";

import { ui } from "./ui/UI";
import { UIComponentEnum } from "./ui/UIComponentEnum";

import { BuddyListComponent } from "./ui/component/BuddyListComponent";
import { MiniMapComponent } from "./ui/component/MiniMapComponent";
import { PlayerEquipmentComponent } from "./ui/component/PlayerEquipmentComponent";
import { ZoneInfoComponent } from "./ui/component/ZoneInfoComponent";

import { ApplicationMenuDialog } from "./ui/dialog/ApplicationMenuDialog";
import { ChooseCharacterDialog } from "./ui/dialog/ChooseCharacterDialog";
import { LoginDialog } from "./ui/dialog/LoginDialog";

import { DesktopUserInterfaceFactory } from "./ui/factory/DesktopUserInterfaceFactory";

import { SingletonFloatingWindow } from "./ui/toolkit/SingletonFloatingWindow";

import { Chat } from "./util/Chat";
import { DialogHandler } from "./util/DialogHandler";


export class Client {

	private initialized = false;
	private errorCounter = 0;
	private unloading = false;
	public username?: string;

	private static click_indicator_id: number|undefined = undefined;

	/** Singleton instance. */
	private static instance: Client;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): Client {
		if (!Client.instance) {
			Client.instance = new Client();
		}
		return Client.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Initializations to be called before startup.
	 */
	init() {
		if (this.initialized) {
			console.warn("tried to re-initialize client");
			return;
		}
		this.initialized = true;

		stendhal.paths = singletons.getPaths();
		stendhal.config = singletons.getConfigManager();
		stendhal.session = singletons.getSessionManager();

		this.initData();
		this.initUI();
		this.initZone();
	}

	private initData() {
		// build info is stored in build/js/build.js
		stendhal.data = stendhal.data || {};
		stendhal.data.cache = singletons.getCacheManager();
		stendhal.data.cache.init();
		stendhal.data.cstatus = singletons.getCStatus();
		stendhal.data.cstatus.init();
		stendhal.data.group = singletons.getGroupManager();
		stendhal.data.outfit = singletons.getOutfitStore();
		stendhal.data.sprites = singletons.getSpriteStore();
		stendhal.data.map = singletons.getMap();
	}

	private initUI() {
		stendhal.ui = stendhal.ui || {};
		stendhal.ui.equip = singletons.getInventory();
		stendhal.ui.html = singletons.getHTMLManager();
		stendhal.ui.touch = singletons.getTouchHandler();
		stendhal.ui.soundMan = singletons.getSoundManager();
		stendhal.ui.gamewindow = singletons.getViewPort();
	}

	private initZone() {
		stendhal.zone = new Zone();
		stendhal.zone.ground = new Ground();
	}

	startup() {
		this.devWarning();

		// initialize configuration & session managers
		const sparams = new URL(document.URL).searchParams;
		stendhal.config.init(sparams);
		stendhal.session.init(sparams);

		// update user interface after config is loaded
		stendhal.config.refreshTheme();
		document.getElementById("body")!.style.setProperty("font-family", stendhal.config.get("ui.font.body"));

		// initialize events
		singletons.getEventRegistry().init();

		// initialize tileset animation data
		singletons.getTileStore().init();
		// initialize emoji data
		singletons.getEmojiStore().init();
		// initialize outfit data
		stendhal.data.outfit.init();

		new DesktopUserInterfaceFactory().create();

		Chat.log("client", "Client loaded. Connecting...");

		this.registerMarauroaEventHandlers();
		this.registerBrowserEventHandlers();

		let ws = Paths.ws.substring(1);
		if (stendhal.session.isTestClient() && !stendhal.session.isServerDefault()) {
			ws = ws.replace(/t/, "s");
			// disclaimer for using the test client on the main server
			Chat.log("warning", "WARNING: You are connecting to the production server with a development"
					+ " build of the test client which may contain bugs or not function as intented. Proceeed"
					+ " with caution.");
		}
		marauroa.clientFramework.connect(null, null, ws);

		stendhal.ui.actionContextMenu = new DialogHandler();
		stendhal.ui.globalInternalWindow = new DialogHandler();

		// pre-cache images & sounds
		stendhal.data.sprites.startupCache();
		singletons.getSoundManager().startupCache();

		if (document.getElementById("gamewindow")) {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
			// initialize on-screen joystick
			stendhal.ui.gamewindow.updateJoystick();
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
	}

	devWarning() {
		console.log("%c ", "padding: 30px; background: url(" + window.location.protocol + "://" + window.location.host + "/images/buttons/devtools-warning.png) no-repeat; color: #AF0");
		console.log("%cIf someone told you, to copy and paste something here, it's a scam and will give them access to your account.", "color:#A00; background-color:#FFF; font-size:150%");
		console.log("If you are a developer and curious about Stendhal, have a look at https://stendhalgame.org/development/introduction.html to get the source code. And perhaps, contribute a feature or a bugfix. ");
		console.log(" ");
		console.log(" ");
		window["eval"] = function() {};
	}

	onError(error: ErrorEvent): boolean|undefined {
		this.errorCounter++;
		if (this.errorCounter > 5) {
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

	/**
	 * register marauroa event handlers.
	 */
	registerMarauroaEventHandlers() {
		marauroa.clientFramework.onDisconnect = function(_reason: string, _error: string) {
			if (!Client.instance.unloading) {
				Chat.logH("error", "Disconnected from server.");
			}
		};

		marauroa.clientFramework.onLoginRequired = function(config: Record<string, string>) {
			if (config["client_login_url"]) {
				Client.instance.unloading = true;
				let currentUrl = encodeURI(window.location.pathname + window.location.hash);
				let url = config["client_login_url"].replace("[url]", currentUrl);
				window.location.href = url;
				return;
			}

			let body = document.getElementById("body")!;
			body.style.cursor = "auto";
			document.getElementById("loginpopup")!.style.display = "none";
			ui.createSingletonFloatingWindow(
				"Login",
				new LoginDialog(),
				100, 50);
		};

		marauroa.clientFramework.onCreateAccountAck = function(username: string) {
			// TODO: We should login automatically
			alert("Account succesfully created, please login.");
			window.location.reload();
		};

		marauroa.clientFramework.onCreateCharacterAck = function(charname: string, _template: any) {
			// Client.get().chooseCharacter(charname);
		};

		marauroa.clientFramework.onLoginFailed = function(_reason: string, _text: string) {
			alert("Login failed. " + _text);
			// TODO: Server closes the connection, so we need to open a new one
			window.location.reload();
		};

		marauroa.clientFramework.onAvailableCharacterDetails = function(characters: {[key: string]: RPObject}) {
			SingletonFloatingWindow.closeAll();
			if (!Object.keys(characters).length && this.username) {
				marauroa.clientFramework.createCharacter(this.username, {});
				return;
			}
			if (window.location.hash) {
				let name = window.location.hash.substring(1);
				stendhal.session.setCharName(name);
			}

			let name = stendhal.session.getCharName();
			if (name) {
				Client.get().chooseCharacter(name);
				return;
			}
			let body = document.getElementById("body")!;
			body.style.cursor = "auto";
			document.getElementById("loginpopup")!.style.display = "none";
			ui.createSingletonFloatingWindow(
				"Choose Character",
				new ChooseCharacterDialog(characters),
				100, 50);
		};

		marauroa.clientFramework.onTransferREQ = function(items: any) {
			for (var i in items) {
				if (typeof(items[i]["name"]) == "undefined") {
					continue;
				}
				items[i]["ack"] = true;
			}
		};

		marauroa.clientFramework.onTransfer = function(items: any) {
			var data = {} as any;
			var zoneName = ""
			for (var i in items) {
				var name = items[i]["name"];
				zoneName = name.substring(0, name.indexOf("."));
				name = name.substring(name.indexOf(".") + 1);
				data[name] = items[i]["data"];
				if (name === "data_map") {
					this.onDataMap(items[i]["data"]);
				}
			}
			stendhal.data.map.onTransfer(zoneName, data);
		};

		// update user interface on perceptions
		if (document.getElementById("gamewindow")) {
			// override perception listener
			marauroa.perceptionListener = new PerceptionListener(marauroa.perceptionListener);
			marauroa.perceptionListener.onPerceptionEnd = function(_type: Int8Array, _timestamp: number) {
				stendhal.zone.sortEntities();
				(ui.get(UIComponentEnum.MiniMap) as MiniMapComponent).draw();
				(ui.get(UIComponentEnum.BuddyList) as BuddyListComponent).update();
				stendhal.ui.equip.update();
				(ui.get(UIComponentEnum.PlayerEquipment) as PlayerEquipmentComponent).update();
				if (!this.loaded) {
					this.loaded = true;
					// delay visibile change of client a little to allow for initialisation in the background for a smoother experience
					setTimeout(function() {
						let body = document.getElementById("body")!;
						body.style.cursor = "auto";
						document.getElementById("client")!.style.display = "block";
						document.getElementById("loginpopup")!.style.display = "none";
					}, 300);
				}
			}
		}
	}

	chooseCharacter(name: string) {
		stendhal.session.setCharName(name);
		marauroa.clientFramework.chooseCharacter(name);
		Chat.log("client", "Loading world...");

		// play login sound for this user
		singletons.getSoundManager().playGlobalizedEffect("ui/login");
	}

	onBeforeUnload() {
		Client.instance.unloading = true;
	}

	/**
	 * registers global browser event handlers.
	 */
	registerBrowserEventHandlers() {
		const keyHandler = singletons.getKeyHandler();
		document.addEventListener("keydown", keyHandler.onKeyDown);
		document.addEventListener("keyup", keyHandler.onKeyUp);
		document.addEventListener("contextmenu", stendhal.main.preventContextMenu);

		// handles closing the context menu
		// FIXME: does not work for "touchstart" as it prevents actions on the context menu
		document.addEventListener("mousedown", function(e) {
			if (stendhal.ui.actionContextMenu.isOpen()) {
				stendhal.ui.actionContextMenu.close(true);
				e.preventDefault();
				e.stopPropagation();
			}
		});

		window.addEventListener("beforeunload", () => {
			this.onBeforeUnload();
		})

		document.getElementById("body")!.addEventListener("mouseenter", stendhal.main.onMouseEnter);

		var gamewindow = document.getElementById("gamewindow")!;
		gamewindow.setAttribute("draggable", "true");
		gamewindow.addEventListener("mousedown", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dblclick", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("dragstart", stendhal.ui.gamewindow.onDragStart);
		gamewindow.addEventListener("mousemove", stendhal.ui.gamewindow.onMouseMove);
		gamewindow.addEventListener("touchstart", stendhal.ui.gamewindow.onMouseDown);
		gamewindow.addEventListener("touchend", stendhal.ui.gamewindow.onTouchEnd);
		gamewindow.addEventListener("dragover", stendhal.ui.gamewindow.onDragOver);
		gamewindow.addEventListener("drop", stendhal.ui.gamewindow.onDrop);
		gamewindow.addEventListener("contextmenu", stendhal.ui.gamewindow.onContentMenu);
		gamewindow.addEventListener("wheel", stendhal.ui.gamewindow.onMouseWheel);

		// handle disengaging joystick when mouse button released outside joystick area
		document.body.addEventListener("mouseup", (e: MouseEvent) => {
			if (e.button == 0) {
				stendhal.ui.gamewindow.joystick.reset();
			}
		});

		var menubutton = document.getElementById("menubutton")!;
		menubutton.addEventListener("click", () => {
			const dialogState = stendhal.config.getWindowState("menu");
			const menuContent = new ApplicationMenuDialog();
			const menuFrame = ui.createSingletonFloatingWindow(
					"Menu", menuContent, dialogState.x, dialogState.y);
			menuFrame.setId("menu");
			menuContent.setFrame(menuFrame);
		});

		var soundbutton = document.getElementById("soundbutton")!;
		soundbutton.addEventListener("click", stendhal.main.toggleSound);
		// update button state
		this.onSoundToggled();

		// click/touch indicator
		// TODO:
		//   - animate
		//   - would work better if displayed upon mousedown/touchstart then position updated & timer
		//     started upon release
		const click_indicator = document.getElementById("click-indicator")! as HTMLImageElement;
		click_indicator.onload = () => {
			click_indicator.onload = null;
			// FIXME: some event handlers cancel propagation
			document.addEventListener("click", Client.handleClickIndicator);
			document.addEventListener("touchend", Client.handleClickIndicator);
		};
		click_indicator.src = stendhal.paths.gui + "/click_indicator.png";
	}

	toggleSound() {
		stendhal.config.set("ui.sound", !stendhal.config.getBoolean("ui.sound"));
		this.onSoundToggled();
	}

	onSoundToggled() {
		const soundMan = singletons.getSoundManager();
		var soundbutton = document.getElementById("soundbutton")!;
		if (stendhal.config.getBoolean("ui.sound")) {
			soundbutton.textContent = "🔊";

			if (!soundMan.unmuteAll()) {
				let errmsg = "Failed to unmute sounds:";
				for (const snd of soundMan.getActive()) {
					if (snd && snd.src && snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		} else {
			soundbutton.textContent = "🔇";

			if (!soundMan.muteAll()) {
				let errmsg = "Failed to mute sounds:";
				for (const snd of soundMan.getActive()) {
					if (snd && snd.src && !snd.muted) {
						errmsg += "\n- " + snd.src;
					}
				}
				console.warn(errmsg);
			}
		}
	}

	onDataMap(data: any) {
		var zoneinfo = {} as {[key: string]: string};
		var deserializer = marauroa.Deserializer.fromBase64(data);
		deserializer.readAttributes(zoneinfo);
		(ui.get(UIComponentEnum.ZoneInfo) as ZoneInfoComponent).zoneChange(zoneinfo);
		// Object { file: "Level 0/semos/city_easter.tmx", danger_level: "0.036429932929822995", zoneid: "", readable_name: "Semos city", id: "-1", color_method: "multiply" }
		singletons.getWeatherRenderer().update(zoneinfo["weather"]);
	}

	preventContextMenu(e: Event) {
		e.preventDefault();
	}

	onMouseEnter(e: MouseEvent) {
		// use Stendhal's built-in cursor for entire page
		(e.target as HTMLElement).style.cursor = "url(" + stendhal.paths.sprites + "/cursor/normal.png) 1 3, auto";
	}

	static handleClickIndicator(e: Event) {
		if (!stendhal.config.getBoolean("input.click.indicator")) {
			return;
		}
		if (Client.click_indicator_id !== undefined) {
			clearTimeout(Client.click_indicator_id);
			Client.click_indicator_id = undefined;
		}
		const pos = stendhal.data.html.extractPosition(e);
		const click_indicator = document.getElementById("click-indicator")! as HTMLImageElement;
		click_indicator.style["left"] = (pos.pageX - (click_indicator.width / 2)) + "px";
		click_indicator.style["top"] = (pos.pageY - (click_indicator.height / 2)) + "px";
		click_indicator.style["display"] = "inline";
		Client.click_indicator_id = setTimeout(function() {
			click_indicator.style["display"] = "none";
		}, 300);
	}
}
