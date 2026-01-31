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

import { stendhal } from "../stendhal";

import { Point } from "./Point";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { BuddyListComponent } from "../ui/component/BuddyListComponent";
import { Paths } from "../data/Paths";


/**
 * Manages configuration settings that persist accross sessions.
 */
export class ConfigManager {

	/**
	 * Configuration keys & default values.
	 *
	 * NOTE: all keys are automatically prefixed with "client." from the set/get methods.
	 */
	private readonly defaults: {[id: string]: string} = {
		"activity-indicator": "true",
		"activity-indicator.animate": "false",
		// TODO: possible change key prefixes pertaining directly to chat panel to "panel.chat."
		"chat.autohide": "false",
		"chat.float": "false",
		"chat.history": "[]",
		"chat.history.index": "0",
		"chat.private.sound": "ui/notify_up",
		"chat.visible": "false",
		"chat-opts.custom": "",
		"click-indicator": "false",
		"effect.blood": "true",
		"effect.entity-overlay": "true",
		"effect.lighting": "true",
		"effect.weather": "true",
		"effect.no-nude": "true",
		"effect.parallax": "true",
		"effect.shadows": "true",
		"emojis.native": "false",
		"font.body": "Carlito",
		"font.chat": "Carlito",
		"font.travel-log": "Black Chancery",
		// NOTE: quick-pickup has precedence over double-click in chests & corpses
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
		"pathfinding.minimap": "true",
		"sound": "false",
		"sound.master.volume": "50",
		"sound.ambient.volume": "100",
		"sound.creature.volume": "100",
		"sound.gui.volume": "100",
		"sound.music.volume": "100",
		"sound.sfx.volume": "100",
		"speech.creature": "true",
		"theme": "wood",
		"window.about": "50,20",
		"window.chest": "160,370",
		"window.corpse": "160,370",
		"window.menu": "150,20",
		"window.outfit": "300,50",
		"window.settings": "20,20",
		"window.shortcuts": "20,20",
		"window.trade": "200,100",
		"window.travel-log": "160,50",
		"zoom.touch": "false"
	};

	/**
	 * Enumerated options for multiple choice settings.
	 */
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
	 * Format is "old-key": "new-key". If "new-key" is `null` then the old key value is simply removed from storage
	 * without updating any new key.
	 *
	 * NOTE: "new-key" must abide the naming convention by including the "client." prefix. "old-key" must include
	 *       prefix only if added after the naming convention was implemented.
	 */
	private readonly deprecated: {[old: string]: string} = {
		"action.inventory.quickpickup": "client.inventory.quick-pickup",
		"action.item.doubleclick": "client.inventory.double-click",
		"chat.custom_keywords": "client.chat-opts.custom",
		"chat.history": "client.chat.history",
		"chat.history.index": "client.chat.history.index",
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

	/**
	 * Themes to apply to panel backgrounds, borders & fonts coloring.
	 */
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

	/**
	 * Available font faces the user can select for different areas of the user interface.
	 */
	private readonly fonts: {[name: string]: string} = {
		"sans-serif": "system default",
		"serif": "system default (serif)",
		"Amaranth": "",
		"Black Chancery": "",
		"Carlito": ""
	};

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
				let valueOld = window.localStorage.getItem(keyOld);
				if (window.localStorage.getItem(keyNew) == null && valueOld != null) {
					// special cases
					if (keyOld === "ui.joystick") {
						if (valueOld !== "none" && window.localStorage.getItem("client.joystick") == null) {
							window.localStorage.setItem("client.joystick", "true");
						}
						if (valueOld === "dpad") {
							window.localStorage.setItem(keyNew, valueOld);
						}
					} else {
						window.localStorage.setItem(keyNew, valueOld);
					}
				}
			}
			// clean up old key
			window.localStorage.removeItem(keyOld);
		}
	}

	/**
	 * @deprecated
	 *   Not needed anymore as this is now a singleton class & is initialized using the `ConfigManager.get()` method.
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
	 *
	 * NOTE: Keys in the returned list are not prefixed with "client.". Normally it is considered hidden & of no use to
	 *       other parts of the client. Thus this method also exludes it. So in cases where methods do need to know of
	 *       the prefix, such as the method `ui.SessionManager.init` which manages copies of the keys for the current
	 *       session must prepend it itself.
	 */
	public getKeys(): string[] {
		return Object.keys(this.defaults);
	}

	/**
	 * Checks if a key has been changed from the default value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {boolean}
	 *   `true` if key is found in local storage.
	 */
	isSet(key: string): boolean {
		return window.localStorage.getItem("client." + key) != null;
	}

	/**
	 * Stores a configuration setting.
	 *
	 * @param key
	 *   `string` identifier.
	 * @param value
	 *   Item to be stored.
	 */
	set(key: string, value: any) {
		if (typeof(value) === "object") {
			value = JSON.stringify(value);
		}
		// index in storage with "client." prefix
		window.localStorage.setItem("client." + key, value);
		stendhal.session.set(key, value);
	}

	/**
	 * Retrieves a configuration setting value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {string|null|undefined}
	 *   Stored value identified by key as a `string` or `undefined` if key not found. If value is the string "null" it
	 *   is converted to a `null` object.
	 */
	get(key: string): string|null|undefined {
		const ret = stendhal.session.get(key) || window.localStorage.getItem("client." + key) || this.defaults[key];
		// allow null to be a value
		if (ret === "null") {
			return null;
		}
		return ret;
	}

	/**
	 * Retrieves an integer number configuration value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @param dval {number}
	 *   Default value in case key is not found.
	 * @return {number|undefined}
	 *   Integer `number` or `undefined`.
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
	 * Retrieves a float number configuration value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @param dval {number}
	 *   Default value in case key is not found.
	 * @return {number|undefined}
	 *   Float `number` or `undefined`.
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
	 * Retrieves a boolean configuration value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {boolean}
	 *   `true` if value is (case-insensitive) string "true", otherwise `false` for any other string value or if key not
	 *   found.
	 */
	getBoolean(key: string): boolean {
		const value = this.get(key);
		if (value) {
			return value.toLowerCase() === "true";
		}
		return false;
	}

	/**
	 * Retrieves a JSON type object or array configuration value.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {object|string[]}
	 *   JavaScript `object`, `string[]` array, or `undefined`.
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
	 * Retrieves an enumarable object of available options for a configuration key.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {object}
	 *   `object` that can be enumerated into a set of key=value pairs in the format of `"id": "label"` where "label" is
	 *   displayed in multiple choice settings. If "label" is an empty `string` then "id" is displayed instead.
	 */
	public getOpts(key: string): {[id: string]: string} {
		return this.isOptsPairs(key) ? this.opts[key] : {};
	}

	/**
	 * Checks if a key represents a set of enumerable configuration key=value pairs.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {boolean}
	 *   `true` if "key" has a value in the `ConfigManager.opts` object.
	 */
	private isOptsPairs(key: string): boolean {
		return Object.keys(this.opts).indexOf(key) > -1;
	}

	/**
	 * Removes a key & its value from storage.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @todo
	 *   Return `true` if successfully removed.
	 */
	remove(key: string) {
		window.localStorage.removeItem("client." + key);
		stendhal.session.remove(key);
	}

	/**
	 * Removes all stored configuration values.
	 *
	 * @todo
	 *   Return `true` if all values successfully removed.
	 */
	clear() {
		for (const key of Object.keys(this.defaults)) {
			this.remove(key);
		}
	}

	/**
	 * Stores attributes for a dialog window.
	 *
	 * TODO: support resizable windows?
	 *
	 * @param id {string}
	 *   Dialog `string` identifier (excluding "client.window." prefix).
	 * @param x {number}
	 *   Horizontal position.
	 * @param y {number}
	 *   Vertical position.
	 */
	setWindowState(id: string, x: number, y: number) {
		this.set("window." + id, x + "," + y);
	}

	/**
	 * Retrieves stored attributes for a dialog window.
	 *
	 * @param id {string}
	 *   Dialog `string` identifier (excluding "client.window." prefix).
	 * @return {util.Point.Point}
	 *   Point containing X/Y positioning of window.
	 */
	getWindowState(id: string): Point {
		let val = this.get("window." + id) || "0,0";
		let tmp: string[] = val.split(",");
		let x = parseInt(tmp[0]);
		let y = parseInt(tmp[1]);
		return new Point(x >= 0 ? x : 0, y >= 0 ? y : 0);
	}

	/**
	 * Sets the UI theme.
	 *
	 * @param value {string}
	 *   Theme `string` name/identifier.
	 */
	setTheme(value: string) {
		this.set("theme", value);
	}

	/**
	 * Retrieves the identifier of current theme.
	 *
	 * @return {string}
	 *   Theme `string` name/identifier.
	 */
	getTheme(): string {
		return this.get("theme") || "wood";
	}

	/**
	 * Retrieves image filename of current theme.
	 *
	 * @return {string}
	 *   `string` filename path.
	 */
	getThemeBG(): string {
		return this.themes.map[this.getTheme()] || this.themes.map["wood"];
	}

	/**
	 * Applies current theme to an element.
	 *
	 * @param element {HTMLElement}
	 *   Element to be updated.
	 * @param children {boolean}
	 *   If `true`, theme will be applied to child elements of "element" (default: `false`).
	 * @param recurse {boolean}
	 *   If `true`, applies to all child elements recursively (default: `false`).
	 * @param updateBG {boolean}
	 *   If `true`, applies white backgrounds for dark themes & black backgrounds for light themes (default: `false`).
	 * @todo
	 *   Check that parameters "children", "recurse", & "updateBG" are still necessary as all elements may now have set
	 *   the appropriate attribute to denote themable.
	 */
	applyTheme(element: HTMLElement, children=false, recurse=false, updateBG=false) {
		const current = this.getTheme();
		element.style.setProperty("background",
				"url(" + Paths.gui + "/" + this.themes.map[current] + ")");

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
	 * Refreshes & applies theme for all applicable elements.
	 *
	 * @param updateBG {boolean}
	 *   If `true`, applies white backgrounds for dark themes & black backgrounds for light themes (default: `false`).
	 * @todo
	 *   Check that parameter "updateBG" is still necessary as all elements may now have set the appropriate attribute
	 *   to denote themable.
	 */
	refreshTheme(updateBG=false) {
		for (const elem of Array.from(document.querySelectorAll(".background"))) {
			this.applyTheme(<HTMLElement> elem, undefined, undefined, updateBG);
		}

		const current = this.getTheme();

		let rootStyle = document.documentElement.style;
		rootStyle.setProperty("--background-url",
			"url(" + Paths.gui + "/" + this.themes.map[current] + ")");
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
	 * Checks if the current theme is configured as "dark".
	 *
	 * @return {boolean}
	 *   `true` if theme is considered dark & should employ a light text foreground color.
	 */
	usingDarkTheme(): boolean {
		return this.themes.dark[this.getTheme()] == true;
	}

	/**
	 * Retreives themes mappings.
	 */
	getThemesMap(): {[name: string]: string} {
		return this.themes.map;
	}

	/**
	 * Retrieves fonts mappings.
	 */
	getFontsMap(): {[name: string]: string} {
		return this.fonts;
	}
}
