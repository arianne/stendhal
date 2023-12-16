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

declare var stendhal: any;


export class SessionManager {

	private storage = window.sessionStorage;
	private charname?: string;
	private initialized = false;
	private server_default = true;

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
	}

	/**
	 * Retrieves a value from session storage.
	 *
	 * @param key
	 *     String identifier.
	 * @return
	 *     Value indexed by `key` or `null` if key does not exist.
	 */
	get(key: string): string|null {
		return this.storage.getItem(key);
	}

	/**
	 * Stores a value in session storage.
	 *
	 * @param key
	 *     String identifier.
	 * @param value
	 *     Value to be stored.
	 */
	set(key: string, value: any) {
		this.storage.setItem(key, value);
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
	 *   `true` is test client should connect to test server. `false` if it should connect to main
	 *   server.
	 */
	isServerDefault(): boolean {
		return this.server_default;
	}
}
