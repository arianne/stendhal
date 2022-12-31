/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
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
		"ui.sound": "false",
		"ui.sound.master.volume": "100",
		"ui.font.body": "Carlito",
		"ui.font.chat": "Carlito",
		"ui.font.tlog": "Black Chancery",
		// FIXME: these should have been "gamewindow" to prevent confusion
		"gamescreen.blood": "true",
		"gamescreen.lighting": "true",
		"gamescreen.weather": "true",
		"gamescreen.nonude": "true",
		"gamescreen.shadows": "true",
		"gamescreen.speech.creature": "true",
		"input.movecont": "false",
		//"input.doubleclick": "false",
		"action.item.doubleclick": "false",
		"action.chest.quickpickup": "false",
		"event.pvtmsg.sound": "ui/notify_up"
	} as {[id: string]: string};

	private themes = {
		/**
		 * Theme backgrounds indexed by ID.
		 */
		map: {
			"aubergine": "panel_aubergine_001.png",
			"brick": "panel_brick_brown_001.png",
			"honeycomb": "panel_honeycomb_001.png",
			"metal": "panelmetal003.gif",
			"parquet": "panel_parquet_brown_001.png",
			"stone": "paneldrock048.jpg",
			"tile": "panel_tile_aqua_001.png",
			"wood": "panelwood003.jpg",
			"wood2": "panelwood006.jpg"
		},

		/**
		 * Themes that should use light text.
		 */
		dark: {
			"aubergine": true,
			"brick": true,
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

	private dialogstates: any = {};
	private character?: string;
	private storage = window.localStorage;

	private static instance: ConfigManager;


	static get(): ConfigManager {
		if (!ConfigManager.instance) {
			ConfigManager.instance = new ConfigManager();
		}
		return ConfigManager.instance;
	}

	private constructor() {
		// do nothing
	}

	init(args: any) {
		this.character = args.get("char") || args.get("character") || args.get("name");

		// store window information for this session
		// TODO: move this into "session" file
		this.dialogstates["menu"] = {x: 150, y: 20};
		this.dialogstates["settings"] = {x: 20, y: 20};
		this.dialogstates["travellog"] = {x: 160, y: 50};
		this.dialogstates["outfit"] = {x: 300, y: 50};
		this.dialogstates["chest"] = {x: 160, y: 370};
		this.dialogstates["corpse"] = {x: 160, y: 370};
	}

	set(key: string, value: any) {
		this.storage.setItem(key, value);
	}

	get(key: string): string|null {
		const ret = this.storage.getItem(key) || this.defaults[key];
		// allow null to be a value
		if (ret === "null") {
			return null;
		}
		return ret;
	}

	getInt(key: string): number|undefined {
		const value = this.getFloat(key);
		if (value) {
			return Math.trunc(value);
		}
	}

	getFloat(key: string): number|undefined {
		const value = Number(this.get(key));
		if (!Number.isNaN(value)) {
			return value;
		}
	}

	getBoolean(key: string): boolean {
		const value = this.get(key);
		if (value) {
			return value.toLowerCase() === "true";
		}
		return false;
	}

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

	remove(key: string) {
		this.storage.removeItem(key);
	}

	clear() {
		this.storage.clear();
	}

	setTheme(value: string) {
		this.set("ui.theme", value);
	}

	getTheme(): string {
		return this.get("ui.theme") || "wood";
	}

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

		// buddy list text color
		if (this.usingDarkTheme()) {
			document.documentElement.style.setProperty("--text-color-online", "#0a0");
			document.documentElement.style.setProperty("--text-color-offline", "#aaa");
		} else {
			document.documentElement.style.setProperty("--text-color-online", "#070");
			document.documentElement.style.setProperty("--text-color-offline", "#777");
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
