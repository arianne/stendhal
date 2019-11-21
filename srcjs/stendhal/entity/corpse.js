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

marauroa.rpobjectFactory["corpse"] = marauroa.util.fromProto(marauroa.rpobjectFactory["entity"], {

	minimapShow: false,
	zIndex: 5500,
	autoOpenedAlready: false,

	set: function(key, value) {
		marauroa.rpobjectFactory["corpse"].proto.set.apply(this, arguments);

		this.sprite = this.sprite || {};
		if (stendhal.config.gamescreen.blood && (key === "image")) {
			this.sprite.filename = "/data/sprites/corpse/" + value + ".png";
		} else if (!stendhal.config.gamescreen.blood && (key === "harmless_image")) {
			this.sprite.fFilename = "/data/sprites/corpse/" + value + ".png";
		}
	},

	createSlot: function(name) {
		var slot = marauroa.util.fromProto(marauroa.rpslotFactory["_default"], {
			add: function(object) {
				marauroa.rpslotFactory["_default"].add.apply(this, arguments);
				if (this._objects.length > 0) {
					this._parent.autoOpenIfDesired();
				}
			},

			del: function(key) {
				marauroa.rpslotFactory["_default"].del.apply(this, arguments);
				if (this._objects.length == 0) {
					this._parent.closeCorpseInventory();
				}
			}
		});
		slot._name = name;
		slot._objects = [];
		slot._parent = this;
		return slot;
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	closeCorpseInventory: function() {
		if (this.inventory) {
			this.inventory.close();
		}
	},

	openCorpseInventory: function() {
		if (!this.inventory || !this.inventory.popupdiv.parentNode) {
			this.inventory = stendhal.ui.equip.createInventoryWindow("content", 2, 2, this, "Corpse", true);
		}
	},

	autoOpenIfDesired: function() {
		if (!this.autoOpenedAlready) {
			this.autoOpenedAlready = true;
			if (marauroa.me && (this["corpse_owner"] == marauroa.me["_name"])) {

				// TODO: for unknown reason, /data/sprites/items/undefined/undefined.png is requested without this delay
				var that = this;
				window.setTimeout(function() {
					that.openCorpseInventory();
				}, 1);
			}
		}
	},

	destroy: function() {
		this.closeCorpseInventory();
	},

	onclick: function(x, y) {
		this.openCorpseInventory();
	},

	getCursor: function(x, y) {
		if (!this["content"] || this["content"]._objects.length === 0) {
			return "url(/data/sprites/cursor/emptybag.png) 1 3, auto";
		}

		// owner
		if (!this["corpse_owner"] || (this["corpse_owner"] == marauroa.me["_name"])) {
			return "url(/data/sprites/cursor/bag.png) 1 3, auto";
		}

		if ((stendhal.data.group.lootmode === "shared") && (stendhal.data.group.members[this["corpse_owner"]])) {
			return "url(/data/sprites/cursor/bag.png) 1 3, auto";
		}
		return "url(/data/sprites/cursor/lockedbag.png) 1 3, auto";
	}

});
