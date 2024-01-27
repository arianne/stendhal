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

declare var stendhal: any;


export class SessionManager {

	private charname?: string;
	private initialized = false;
	private server_default = true;

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
		for (const id of stendhal.config.getKeys()) {
			const value = window.localStorage.getItem(id);
			if (value != null) {
				this.states[id] = value;
			}
		}
	}

	/**
	 * Retrieves a value from session memory.
	 *
	 * @param key
	 *     String identifier.
	 * @return
	 *     Value indexed by `key` or `null` if key does not exist.
	 */
	get(key: string): string|undefined {
		return this.states[key];
	}

	/**
	 * Stores a value in session memory.
	 *
	 * @param key
	 *     String identifier.
	 * @param value
	 *     Value to be stored.
	 */
	set(key: string, value: any) {
		value = this.toString(value);
		if (value == undefined) {
			this.remove(key);
			return;
		}
		this.states[key] = value;
	}

	remove(key: string) {
		delete this.states[key];
	}

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
	 * @param charname
	 *     Player's character name.
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
	 * @return
	 *     Player's character name.
	 */
	getCharName(): string {
		return this.charname || "";
	}

	/**
	 * Detects if test client is being used based on data path.
	 */
	isTestClient(): boolean {
		return stendhal.paths.data === "/testdata";
	}

	/**
	 * Checks if the client should connect to its default server counterpart.
	 *
	 * Used for test client only.
	 *
	 * @return
	 *   `true` if test client should connect to test server. `false` if it should connect to main
	 *   server.
	 */
	isServerDefault(): boolean {
		return this.server_default;
	}
}
