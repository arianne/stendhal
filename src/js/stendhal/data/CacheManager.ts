/***************************************************************************
 *                (C) Copyright 2017-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";


/**
 * setup a cache that is powered by IndexedDB, but allow synchronous access
 * for simplicity of use.
 */
export class CacheManager {

	/** Singleton instance. */
	private static instance: CacheManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): CacheManager {
		if (!CacheManager.instance) {
			CacheManager.instance = new CacheManager();
		}
		return CacheManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Initializes cache.
	 */
	init() {
		// https://dzone.com/articles/getting-all-stored-items
		function requestAllItems(storeName: string, callback: Function) {
			var tx = stendhal.data.cache.db.transaction(storeName, "readonly");
			var store = tx.objectStore(storeName);
			var items: any[] = [];

			tx.oncomplete = function(evt: Event) {
				callback(items);
			};
			var cursorRequest = store.openCursor();
			cursorRequest.onerror = function(error: any) {
				console.log(error);
			};
			cursorRequest.onsuccess = function(evt: any) {
				var cursor = evt.target.result;
				if (cursor) {
					items.push(cursor.value);
					cursor.continue();
				}
			};
		}

		let cacheId = null;
		try {
			cacheId = localStorage.getItem("cache.cid");
		} catch (e) {
			// ignore
		}
		stendhal.data.cache.sync = {
			"cid": cacheId
		};

		if (cacheId || !window.indexedDB) {
			return;
		}
		var open = indexedDB.open("stendhal", 1);

		open.onupgradeneeded = function() {
			var db = open.result;
			db.createObjectStore("cache", {keyPath: "key"});
		};

		open.onsuccess = function() {
			stendhal.data.cache.db = open.result;
			requestAllItems("cache", function(items: any[]) {
				var len = items.length;
				for (var i = 0; i < len; i += 1) {
					stendhal.data.cache.sync[items[i].key] = items[i].data;
					try {
						cacheId = localStorage.setItem("cache.cid", items[i].data);
					} catch (e) {
						// ignore
					}
				}
				if (!stendhal.data.cache.sync["cid"]) {
					stendhal.data.cache.put("cid", (Math.random()*1e48).toString(36));
				}
			});
		};
		open.onerror = function() {
			stendhal.data.cache.put("cid", (Math.random()*1e48).toString(36));
		};
	}

	get(key: string) {
		return stendhal.data.cache.sync[key];
	}

	put(key: string, value: any) {
		if (stendhal.data.cache.sync[key] === value) {
			return;
		}
		stendhal.data.cache.sync[key] = value;
		try {
			localStorage.setItem("cache." + key, value);
		} catch (e) {
			console.log(e);
		}
	}
}
