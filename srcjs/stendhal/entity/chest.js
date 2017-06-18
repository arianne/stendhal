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

(function() {
	
var OPEN_SPRITE = {
	filename: "/data/sprites/chest.png",
	height: 32,
	width: 32,
	offsetY: 32
};

var CLOSED_SPRITE = {
	filename: "/data/sprites/chest.png",
	height: 32,
	width: 32
};

marauroa.rpobjectFactory["chest"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {
	zIndex: 5000,
	sprite: CLOSED_SPRITE,
	open: false,

	set: function(key, value) {
		marauroa.rpobjectFactory["entity"].set.apply(this, arguments);
		if (key === "open") {
			this.sprite = OPEN_SPRITE;
			this.open = true;
		}
		if (this.isNextTo(marauroa.me)) {
			this.openInventoryWindow();
		}
	},

	unset: function(key) {
		marauroa.rpobjectFactory["entity"].proto.unset.call(this, key);
		if (key === "open") {
			this.sprite = CLOSED_SPRITE;
			this.open = false;
			if (this.inventory && this.inventory.popupdiv.parentNode) {
				this.inventory.close();
			}
		}
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	onclick: function(x, y) {
		if (marauroa.me.isNextTo(this)) {
			// If we are next to the chest, open or close it.
			var action = {
				"type": "use",
				"target": "#" + this["id"],
				"zone": marauroa.currentZoneName
			};
			marauroa.clientFramework.sendAction(action);
		} else {
			// We are far away, but if the chest is open, we can take a look
			if (this.open) {
				this.openInventoryWindow();
			}
		}
	},

	openInventoryWindow: function() {
		if (!this.inventory || !this.inventory.popupdiv.parentNode) {
			this.inventory = stendhal.ui.equip.createInventoryWindow("content", 5, 6, this, "Chest");
		}
	}

});

}());