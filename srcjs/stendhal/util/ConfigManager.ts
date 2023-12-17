/***************************************************************************
 *                    Copyright © 2022-2023 - Stendhal                     *
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

	private readonly defaults = {
		"client.pathfinding": "true",
		"ui.sound": "false",
		"ui.sound.master.volume": "100",
		"ui.sound.ambient.volume": "100",
		"ui.sound.creature.volume": "100",
		"ui.sound.gui.volume": "100",
		"ui.sound.music.volume": "100",
		"ui.sound.sfx.volume": "100",
		"ui.font.body": "Carlito",
		"ui.font.chat": "Carlito",
		"ui.font.tlog": "Black Chancery",
		"ui.stats.charname": "true",
		"ui.stats.hpbar": "true",
		"ui.joystick": "none",
		"ui.joystick.center.x": "224",
		"ui.joystick.center.y": "384",
		"ui.window.chest": "160,370",
		"ui.window.corpse": "160,370",
		"ui.window.menu": "150,20",
		"ui.window.outfit": "300,50",
		"ui.window.settings": "20,20",
		"ui.window.shortcuts": "20,20",
		"ui.window.trade": "200,100",
		"ui.window.travellog": "160,50",
		// FIXME: these should have been "gamewindow" to prevent confusion
		"gamescreen.blood": "true",
		"gamescreen.lighting": "true",
		"gamescreen.weather": "true",
		"gamescreen.nonude": "false",
		"gamescreen.shadows": "true",
		"gamescreen.speech.creature": "true",
		"input.click.indicator": "false",
		"input.movecont": "false",
		//"input.doubleclick": "false",
		"action.item.doubleclick": "false",
		"action.inventory.quickpickup": "true",
		"event.pvtmsg.sound": "ui/notify_up",
		"chat.custom_keywords": "",
	} as {[id: string]: string};

	private themes = {
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

	private fonts = {
		"sans-serif": "system default",
		"serif": "system default (serif)",
		"Amaranth": "",
		"Black Chancery": "",
		"Carlito": ""
	} as {[name: string]: string};

	private storage = window.localStorage;
	private windowstates: any = {};
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
		// do nothing
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
		this.storage.setItem(key, value);
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
		const ret = this.storage.getItem(key) || this.defaults[key];
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
	 * Removes a key & its value from storage.
	 *
	 * @param key
	 *     String identifier.
	 */
	remove(key: string) {
		this.storage.removeItem(key);
	}

	/**
	 * Removes all data from storage.
	 */
	clear() {
		this.storage.clear();
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
		this.set("ui.window." + id, x + "," + y);
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
			const tmp: string[] = (this.get("ui.window." + id) || "0,0").split(",");
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
		this.set("ui.theme", value);
	}

	/**
	 * Retrieves the identifier of current theme.
	 *
	 * @return
	 *     String identifier.
	 */
	getTheme(): string {
		return this.get("ui.theme") || "wood";
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
