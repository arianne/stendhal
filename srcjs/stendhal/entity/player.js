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

/**
 * Player
 */
marauroa.rpobjectFactory["player"] = marauroa.util.fromProto(marauroa.rpobjectFactory["rpentity"], {

	minimapShow: true,
	minimapStyle: "rgb(255, 255, 255)",
	dir: 3,
	
	set: function(key, value) {
		marauroa.rpobjectFactory["rpentity"].proto.set.apply(this, arguments);
		if (key === "text") {
			this.say(value);
		} else if (key === "ghostmode") {
			this.minimapShow = false;
		}
		
		// stats
		if (marauroa.me !== this) {
			return;
		}
		if (stendhal.ui.stats.keys.indexOf(key) > -1) {
			stendhal.ui.stats.dirty = true;
		}
	},

	/**
	 * Is this player an admin?
	 */
	isAdmin: function() {
		return (typeof(this["adminlevel"]) !== "undefined" && this["adminlevel"] > 600);
	},

	buildActions: function(list) {
		marauroa.rpobjectFactory["rpentity"].proto.buildActions.apply(this, arguments);
	/*
		boolean hasBuddy = User.hasBuddy(entity.getName());
		if (!hasBuddy) {
			list.add(ActionType.ADD_BUDDY.getRepresentation());
		}
	
		if (User.isIgnoring(entity.getName())) {
			list.add(ActionType.UNIGNORE.getRepresentation());
		} else if (!hasBuddy)  {
			list.add(ActionType.IGNORE.getRepresentation());
		}

		list.push({
			title: "Trade",
			type: "trade"
		})
		list.add(ActionType.INVITE.getRepresentation());
		*/
	},


	/** 
	 * Can the player hear this chat message?
	 */
	isInHearingRange: function(entity) {
		return (this.isAdmin() 
			|| ((Math.abs(this["x"] - entity["x"]) < 15) 
				&& (Math.abs(this["y"] - entity["y"]) < 15)));
	}
});

