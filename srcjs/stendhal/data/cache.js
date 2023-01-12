/***************************************************************************
 *                   (C) Copyright 2017 - Faiumoni e. V.                   *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var stendhal = window.stendhal = window.stendhal || {};
stendhal.data = stendhal.data || {};

/**
 * setup a cache that is powered by IndexedDB, but allow synchronous access
 * for simplicity of use.
 */
stendhal.data.cache = {
	init: function() {

		// https://dzone.com/articles/getting-all-stored-items
		function requestAllItems(storeName, callback) {
			var tx = stendhal.data.cache.db.transaction(storeName, IDBTransaction.READ_ONLY);
			var store = tx.objectStore(storeName);
			var items = [];

			tx.oncomplete = function(evt) {
				callback(items);
			};
			var cursorRequest = store.openCursor();
			cursorRequest.onerror = function(error) {
				console.log(error);
			};
			cursorRequest.onsuccess = function(evt) {
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
			requestAllItems("cache", function(items) {
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
	},

	get: function(key) {
		return stendhal.data.cache.sync[key];
	},

	put: function(key, value) {
		if (stendhal.data.cache.sync[key] === value) {
			return;
		}
		stendhal.data.cache.sync[key] = value;
		try {
			localStorage.setItem("cache." + key, items[i].data);
		} catch (e) {
			console.log(e);
		}
	}
}

stendhal.data.cache.init();
