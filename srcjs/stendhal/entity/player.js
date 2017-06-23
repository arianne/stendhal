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
		var playerName = this["_name"];
		var hasBuddy = playerName in marauroa.me["buddies"];
		if (!hasBuddy) {
			list.push({
				title: "Add to buddies",
				action: function(entity) {
					var action = {
						"type": "addbuddy",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}

		var temp = marauroa.me["!ignore"]._objects;
		var isIgnored = temp.length > 0 && ("_" + playerName) in temp[0];
		if (isIgnored) {
			list.push({
				title: "Remove ignore",
				action: function(entity) {
					var action = {
						"type": "unignore",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		} else if (!hasBuddy) {
			list.push({
				title: "Ignore",
				action: function(entity) {
					var action = {
						"type": "ignore",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		
		}
	/*

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

