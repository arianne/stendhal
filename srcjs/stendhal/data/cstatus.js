/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    * 
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.data = stendhal.data || {};
stendhal.data.cstatus = {

	send: function() {
		if (!marauroa.me || stendhal.data.cache.get("cid") === "noIndexedDB") {
			window.setTimeout(stendhal.data.cstatus.send, 1000);
			return;
		}

		var action = {
			"type": "cstatus",

			// the client version, the server will deactivate certain features that
			// are incompatible with old clients. E. g. changing of light and dark
			// in the current zone is implemented by retransmitting the tileset
			// information. an old client would mistake that for a zone change and
			// hang.
			"version": stendhal.data.build.version,

			// The build number is especially helpful for pre releases
			"build": stendhal.data.build.build,

			// so that we can ask bug reporters to try again with the official
			// client, if they are using an unofficial one.
			"dist": stendhal.data.build.dist,

			// a client id to help with the investigation of hacked accounts
			// especially in the common "angry sibling" case.
			"cid": stendhal.data.cache.get("cid")
		};
		marauroa.clientFramework.sendAction(action);
	}
}

window.setTimeout(stendhal.data.cstatus.send, 1000);