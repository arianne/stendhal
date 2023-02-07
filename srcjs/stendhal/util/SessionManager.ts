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


export class SessionManager {

	private storage = window.sessionStorage;
	private charname?: string;
	private initialized = false;

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

		const charname = args.get("char") || args.get("character") || args.get("name");
		if (charname) {
			this.setCharName(charname);
		}
		this.initialized = true;
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
}
