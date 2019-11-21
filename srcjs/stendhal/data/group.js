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

stendhal.data.group = {
	members: [],
	lootmode: "",
	leader: "",

	updateGroupStatus: function(members, leader, lootmode) {
		if (members) {
			var memberArray = members.substring(1, members.length - 1).split("\t");
			stendhal.data.group.members = {};
			for (var i = 0; i < memberArray.length; i++) {
				stendhal.data.group.members[memberArray[i]] = true;
			}
			stendhal.data.group.leader = leader;
			stendhal.data.group.lootmode = lootmode;
		} else {
			stendhal.data.group.members = [];
			stendhal.data.group.leader = "";
			stendhal.data.group.lootmode = "";
		}
	}
}
