/***************************************************************************
 *                 Copyright © 2022-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { BuddyListComponent } from "../ui/component/BuddyListComponent";

declare var stendhal: any;


export class ConfigManager {

	private readonly defaults: {[id: string]: string} = {
		"activity-indicator": "true",
		"chat.autohide": "false",
		"chat.float": "false",
		"chat.private.sound": "ui/notify_up",
		"chat.visible": "false",
		"chat-opts.custom": "",
		"click-indicator": "false",
		"effect.blood": "true",
		"effect.lighting": "true",
		"effect.weather": "true",
		"effect.no-nude": "true",
		"effect.shadows": "true",
		"emojis.native": "false",
		"font.body": "Carlito",
		"font.chat": "Carlito",
		"font.travel-log": "Black Chancery",
		"inventory.double-click": "false",
		"inventory.quick-pickup": "true",
		"joystick": "false",
		"joystick.center.x": "224",
		"joystick.center.y": "384",
		"joystick.style": "joystick",
		"menu.style": "traditional",
		"move.cont": "false",
		"panel.stats.charname": "true",
		"panel.stats.hpbar": "true",
		"pathfinding": "true",
		"sound": "false",
		"sound.master.volume": "100",
		"sound.ambient.volume": "100",
		"sound.creature.volume": "100",
		"sound.gui.volume": "100",
		"sound.music.volume": "100",
		"sound.sfx.volume": "100",
		"speech.creature": "true",
		// represents most recently used client version
		"version": document.documentElement.getAttribute("data-build-version") || "",
		"window.chest": "160,370",
		"window.corpse": "160,370",
		"window.menu": "150,20",
		"window.outfit": "300,50",
		"window.settings": "20,20",
		"window.shortcuts": "20,20",
		"window.trade": "200,100",
		"window.travel-log": "160,50"
	};

	private readonly opts: {[key: string]: {[id: string]: string}} = {
		"joystick.style": {
			"joystick": "",
			"dpad": "direction pad"
		},
		"menu.style": {
			"traditional": "",
			"floating": ""
		}
	};

	/**
	 * Old keys that should be replaced.
	 *
	 * NOTE: both old (if previously followed convention) & replacement keys must be prefixed with "client."
	 */
	private readonly deprecated: {[old: string]: string} = {
		"action.inventory.quickpickup": "client.inventory.quick-pickup",
		"action.item.doubleclick": "client.inventory.double-click",
		"chat.custom_keywords": "client.chat-opts.custom",
		"event.pvtmsg.sound": "client.chat.private.sound",
		"gamescreen.blood": "client.effect.blood",
		"gamescreen.lighting": "client.effect.lighting",
		"gamescreen.weather": "client.effect.weather",
		"gamescreen.nonude": "client.effect.no-nude",
		"gamescreen.shadows": "client.effect.shadows",
		"gamescreen.speech.creature": "client.speech.creature",
		"input.click.indicator": "client.click-indicator",
		"input.movecont": "client.move.cont",
		"ui.font.body": "client.font.body",
		"ui.font.chat": "client.font.chat",
		"ui.font.tlog": "client.font.travel-log",
		"ui.joystick": "client.joystick.style",
		"ui.joystick.center.x": "client.joystick.center.x",
		"ui.joystick.center.y": "client.joystick.center.y",
		"ui.sound": "client.sound",
		"ui.sound.master.volume": "client.sound.master.volume",
		"ui.sound.ambient.volume": "client.sound.ambient.volume",
		"ui.sound.creature.volume": "client.sound.creature.volume",
		"ui.sound.gui.volume": "client.sound.gui.volume",
		"ui.sound.music.volume": "client.sound.music.volume",
		"ui.sound.sfx.volume": "client.sound.sfx.volume",
		"ui.stats.charname": "client.panel.stats.charname",
		"ui.stats.hpbar": "client.panel.stats.hpbar",
		"ui.theme": "client.theme",
		"ui.window.chest": "client.window.chest",
		"ui.window.corpse": "client.window.corpse",
		"ui.window.menu": "client.window.menu",
		"ui.window.outfit": "client.window.outfit",
		"ui.window.settings": "client.window.settings",
		"ui.window.shortcuts": "client.window.shortcuts",
		"ui.window.trade": "client.window.trade",
		"ui.window.travellog": "client.window.travel-log"
	};

	private readonly themes = {
		/**
		 * Theme backgrounds indexed by ID.
		 */
		map: {
			"aubergine": "panel_aubergine.png",
			"brick": "panel_brick.png",
			"honeycomb": "panel_honeycomb.png",
			"leather": "panel_leather.png",
			"metal": "panel_metal.png",
			"parquet": "panel_wood_parquet.png",
			"stone": "panel_stone.png",
			"tile": "panel_aqua_tile.png",
			"wood": "panel_wood_v.png",
			"wood2": "panel_wood_h.png"
		},

		/**
		 * Themes that should use light text.
		 */
		dark: {
			"aubergine": true,
			"brick": true,
			"leather": true,
			"metal": true,
			"parquet": true,
			"stone": true,
			"tile": true,
			"wood": true,
			"wood2": true
		}
	} as any;

	private readonly fonts: {[name: string]: string} = {
		"sans-serif": "system default",
		"serif": "system default (serif)",
		"Amaranth": "",
		"Black Chancery": "",
		"Carlito": ""
	};

	private readonly storage = window.localStorage;
	private readonly windowstates: any = {};
	/** @deprecated */
	private initialized = false;

	/** Singleton instance. */
	private static instance: ConfigManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): ConfigManager {
		if (!ConfigManager.instance) {
			ConfigManager.instance = new ConfigManager();
		}
		return ConfigManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// convert old keys
		for (const keyOld in this.deprecated) {
			const keyNew = this.deprecated[keyOld];
			if (keyNew != null) {
				let valueOld = this.storage.getItem(keyOld);
				if (this.storage.getItem(keyNew) == null && valueOld != null) {
					// special cases
					if (keyOld === "ui.joystick") {
						if (valueOld !== "none" && this.storage.getItem("client.joystick") == null) {
							this.storage.setItem("client.joystick", "true");
						}
						if (valueOld === "dpad") {
							this.storage.setItem(keyNew, valueOld);
						}
					}
				}
			}
			// clean up old key
			this.storage.removeItem(keyOld);
		}
	}

	/**
	 * @deprecated
	 */
	init(args: any) {
		if (this.initialized) {
			console.warn("tried to re-initialize ConfigManager");
			return;
		}
		this.initialized = true;
	}

	/**
	 * Retrieves all available settings keys.
	 */
	public getKeys(): string[] {
		return Object.keys(this.defaults);
	}

	/**
	 * Stores a configuration setting.
	 *
	 * @param key
	 *     String identifier.
	 * @param value
	 *     Item to be stored.
	 */
	set(key: string, value: any) {
		if (typeof(value) === "object") {
			value = JSON.stringify(value);
		}
		// index in storage with "client." prefix
		this.storage.setItem("client." + key, value);
		stendhal.session.set(key, value);
	}

	/**
	 * Retrieves a configuration setting value.
	 *
	 * @param key
	 *     String identifier.
	 * @return
	 *     Stored value identified by key or undefined if key not found.
	 */
	get(key: string): string|null|undefined {
		const ret = stendhal.session.get(key) || this.storage.getItem("client." + key) || this.defaults[key];
		// allow null to be a value
		if (ret === "null") {
			return null;
		}
		return ret;
	}

	/**
	 * Retrieves an integer number value from storage.
	 *
	 * @param key
	 *     String identifier.
	 * @param dval
	 *     Default value if key is not found.
	 * @return
	 *     Integer or undefined.
	 */
	getInt(key: string, dval?: number): any {
		let value = this.getFloat(key);
		if (typeof(value) === "undefined") {
			if (typeof(dval) === "undefined") {
				return;
			}
			value = dval;
		}
		return Math.trunc(value);
	}

	/**
	 * Retrieves a float number value from storage.
	 *
	 * @param key
	 *     String identifier.
	 * @param dval
	 *     Default value if key is not found.
	 * @return
	 *     Float or undefined.
	 */
	getFloat(key: string, dval?: number): any {
		let value = Number(this.get(key));
		if (Number.isNaN(value)) {
			if (typeof(dval) === "undefined") {
				return;
			}
			value = dval;
		}
		return value;
	}

	/**
	 * Retrieves a boolean value from storage.
	 *
	 * @param key
	 *     String identifier.
	 * @return
	 *     Boolean value of key or false if key not found.
	 */
	getBoolean(key: string): boolean {
		const value = this.get(key);
		if (value) {
			return value.toLowerCase() === "true";
		}
		return false;
	}

	/**
	 * Retrieves a JSON type object or array from storage.
	 *
	 * @param key
	 *     String identifier.
	 * @return
	 *     Object, array, or undefined.
	 */
	getObject(key: string): any|undefined {
		let value = this.get(key);
		if (!value) {
			return;
		}
		value = JSON.parse(value);
		if (typeof(value) === "object") {
			return value;
		}
	}

	/**
	 * Retrieves a list of available options for a config id.
	 *
	 * @param key
	 *   String identifier.
	 * @return
	 *   String enumeration.
	 */
	public getOpts(key: string): {[id: string]: string} {
		return this.isOptsPairs(key) ? this.opts[key] : {};
	}

	/**
	 * Checks if a key represents a set of configuration key=value pairs.
	 *
	 * @param key
	 *   String identifier.
	 * @return
	 *   `true` if `key` has a value in `ConfigManager.opts`.
	 */
	private isOptsPairs(key: string): boolean {
		return Object.keys(this.opts).indexOf(key) > -1;
	}

	/**
	 * Removes a key & its value from storage.
	 *
	 * @param key
	 *     String identifier.
	 */
	remove(key: string) {
		this.storage.removeItem("client." + key);
		stendhal.session.remove(key);
	}

	/**
	 * Removes all data from storage.
	 */
	clear() {
		for (const key of Object.keys(this.defaults)) {
			this.remove(key);
		}
	}

	/**
	 * Sets attributes for a dialog window.
	 *
	 * TODO: move into session manager
	 *
	 * @param id
	 *   Dialog identifier.
	 * @param x
	 *   Horizontal position.
	 * @param y
	 *   Vertical position.
	 */
	setWindowState(id: string, x: number, y: number) {
		this.windowstates[id] = {x: x, y: y};
		this.set("window." + id, x + "," + y);
	}

	/**
	 * Retrieves attributes for a dialog window.
	 *
	 * TODO: move into session manager
	 *
	 * @param id
	 *   Dialog identifier.
	 * @return
	 *   Object containing X/Y positioning of dialog.
	 */
	getWindowState(id: string): {[index: string]: number} {
		let state: {[index: string]: number} = {};
		if (this.windowstates.hasOwnProperty(id)) {
			state = this.windowstates[id];
		} else {
			const tmp: string[] = (this.get("window." + id) || "0,0").split(",");
			state.x = parseInt(tmp[0], 10);
			state.y = parseInt(tmp[1], 10);
		}
		return state;
	}

	/**
	 * Sets the UI theme.
	 *
	 * @param value
	 *     Theme string identifier.
	 */
	setTheme(value: string) {
		this.set("theme", value);
	}

	/**
	 * Retrieves the identifier of current theme.
	 *
	 * @return
	 *     String identifier.
	 */
	getTheme(): string {
		return this.get("theme") || "wood";
	}

	/**
	 * Retrieves image filename of current theme.
	 *
	 * @return
	 *     String filename.
	 */
	getThemeBG(): string {
		return this.themes.map[this.getTheme()] || this.themes.map["wood"];
	}

	/**
	 * Applies current theme to an element.
	 *
	 * @param element
	 *     Element to be updated.
	 * @param children
	 *     If <code>true</code>, theme will be applied to children elements
	 *     (default: <code>false</code>).
	 * @param recurse
	 *     If <code>true</code>, applies to all children recursively (default:
	 *     <code>false</code>).
	 * @param updateBG
	 *     If <code>true</code>, applies white backgrounds for dark themes &
	 *     black backgrounds for light themes (default: <code>false</code>).
	 */
	applyTheme(element: HTMLElement, children=false, recurse=false, updateBG=false) {
		const current = this.getTheme();
		element.style.setProperty("background",
				"url(" + stendhal.paths.gui + "/" + this.themes.map[current] + ")");

		// make texts readable with dark & light themes
		let color = "#000000";
		let colorbg = "#ffffff";
		if (this.themes.dark[current]) {
			color = "#ffffff";
			colorbg = "#000000";
		}
		element.style.setProperty("color", color);
		if (updateBG) {
			element.style.setProperty("background-color", colorbg);
		}

		if (children && element.children) {
			for (let idx = 0; idx < element.children.length; idx++) {
				this.applyTheme(<HTMLElement> element.children[idx], recurse, recurse);
			}
		}
	}

	/**
	 * Refreshes theme for all applicable elements.
	 *
	 * @param updateBG
	 *     If <code>true</code>, applies white backgrounds for dark themes &
	 *     black backgrounds for light themes (default: <code>false</code>).
	 */
	refreshTheme(updateBG=false) {
		for (const elem of Array.from(document.querySelectorAll(".background"))) {
			this.applyTheme(<HTMLElement> elem, undefined, undefined, updateBG);
		}

		const current = this.getTheme();

		let rootStyle = document.documentElement.style;
		rootStyle.setProperty("--background-url",
			"url(" + stendhal.paths.gui + "/" + this.themes.map[current] + ")");
		if (this.usingDarkTheme()) {
			rootStyle.setProperty("--text-color", "#fff");
			rootStyle.setProperty("--text-color-inactive", "#aaa");
			rootStyle.setProperty("--text-color-online", "#0a0");
			rootStyle.setProperty("--text-color-offline", "#aaa");
		} else {
			rootStyle.setProperty("--text-color", "#000");
			rootStyle.setProperty("--text-color-inactive", "#555");
			rootStyle.setProperty("--text-color-online", "#070");
			rootStyle.setProperty("--text-color-offline", "#777");
		}

		const buddyList = <BuddyListComponent> ui.get(UIComponentEnum.BuddyList);
		if (buddyList) {
			buddyList.update();
		}
	}

	/**
	 * Checks if the current theme is defined as "dark".
	 */
	usingDarkTheme(): boolean {
		return this.themes.dark[this.getTheme()] == true;
	}
}
