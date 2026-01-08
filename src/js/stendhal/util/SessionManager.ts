/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "../data/Paths";

import { stendhal } from "../stendhal";


/**
 * Manages current client session independent of clients open in other browser tabs.
 */
export class SessionManager {

	/** Name of user's character for this client session. */
	private charname?: string;
	/** Attribute set to prevent re-initialization. */
	private initialized = false;
	/** Attribute determining connection to default server counterpart (test client only). */
	private server_default = true;

	/** Session configuration values. */
	private states: {[id: string]: string} = {};

	/** Singleton instance. */
	private static instance: SessionManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): SessionManager {
		if (!SessionManager.instance) {
			SessionManager.instance = new SessionManager();
		}
		return SessionManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Initializes session values.
	 */
	init(args: any) {
		if (this.initialized) {
			console.warn("tried to re-initialize SessionManager");
			return;
		}
		this.initialized = true;

		const charname = args.get("char") || args.get("character") || args.get("name");
		if (charname) {
			this.setCharName(charname);
		}
		// server selection from query string (test client only)
		const server = args.get("server");
		if (server && this.isTestClient()) {
			this.server_default = server !== "main";
		}
		// store configuration in active memory
		for (let key of stendhal.config.getKeys()) {
			let value = stendhal.config.get(key);
			if (value == null) {
				value = "null";
			}
			this.set(key, value);
		}

		this.update();
	}

	/**
	 * Retrieves a value from session memory.
	 *
	 * NOTE: Unless specifically needed do not call this directly. Instead `util.ConfigManager.get(key) should be called.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @return {string|undefined}
	 *   `string` value indexed by "key" or `undefined` if key not found.
	 * @fixme
	 *   Should be allowed to return `null`.
	 */
	get(key: string): string|null|undefined {
		return this.states[key];
	}

	/**
	 * Stores a value in session memory.
	 *
	 * NOTE: Unless specifically needed do not call this directly. Instead `util.ConfigManager.set(key, value) should be
	 *       called.
	 *
	 * @param key {string}
	 *   `string` identifier.
	 * @param value {any}
	 *   Value to be stored.
	 */
	set(key: string, value: any) {
		value = this.toString(value);
		if (value == undefined) {
			this.remove(key);
			return;
		}
		this.states[key] = value;
	}

	/**
	 * Removes a key & its value from storage.
	 *
	 * NOTE: Unless specifically needed do not call this directly. Instead `util.ConfigManager.remove(key) should be
	 *       called.
	 *
	 * @param key {string}
	 *   String identifier.
	 * @todo
	 *   Return `true` if successfully removed.
	 */
	remove(key: string) {
		delete this.states[key];
	}

	/**
	 * Converts an object to string equivalent.
	 *
	 * @param value {any}
	 *   Object to be converted.
	 * @return {string|undefined}
	 *   String representation of "value".
	 */
	private toString(value: any): string|undefined {
		if (value == null) {
			return "null";
		}
		const vtype = typeof(value);
		if (vtype === "undefined") {
			return undefined;
		} else if (vtype === "string") {
			return value;
		} else if (vtype === "object") {
			return JSON.stringify(value);
		}
		return ""+value;
	}

	/**
	 * Sets active character name for this session.
	 *
	 * @param charname {string}
	 *   Player's character name.
	 */
	setCharName(charname: string) {
		this.charname = charname;
		// display character name in browser title/tab
		document.title = "Stendhal - " + this.charname;
		// display in stats panel
		document.getElementById("charname")!.innerText = this.charname;
	}

	/**
	 * Retrieves the active character name for this session.
	 *
	 * @return {string}
	 *   Player's character name or empty string if not set.
	 */
	getCharName(): string {
		return this.charname || "";
	}

	/**
	 * Syncs inteface with updated settings.
	 */
	update() {
		if (stendhal.config.getBoolean("zoom.touch")) {
			document.documentElement.style.removeProperty("touch-action");
		} else {
			// disable double-tap zoom in browsers that support it
			// NOTE: "manipulation" does not work in Chrome
			document.documentElement.style.setProperty("touch-action", "pan-x pan-y");
		}
	}

	/**
	 * Detects if test client is being used based on data path.
	 *
	 * @return {boolean}
	 *   `true` if data path root is set to "/testdata".
	 */
	isTestClient(): boolean {
		return Paths.data === "/testdata";
	}

	/**
	 * Checks if the client should connect to its default server counterpart.
	 *
	 * Test client only. Ignored by production client.
	 *
	 * @return {boolean}
	 *   `true` if test client should connect to test server. `false` if it should connect to main production server.
	 */
	isServerDefault(): boolean {
		return this.server_default;
	}

	/**
	 * Checks if joystick/direction pad should be visible.
	 *
	 * @return {boolean}
	 *   `true` if joystick property has been enabled manually or if no pointer device is detected.
	 */
	joystickEnabled(): boolean {
		if (!stendhal.config.isSet("joystick") && this.touchOnly()) {
			// display by default if a pointer device is not detected from the system
			return true;
		}
		return stendhal.config.getBoolean("joystick");
	}

	/**
	 * Detectes if system is touch enabled without fine pointing device (mouse).
	 *
	 * @return {boolean}
	 *   `true` if fine pointing device not detected.
	 */
	touchOnly(): boolean {
		return !window.matchMedia("(pointer: fine)").matches;
	}
}
