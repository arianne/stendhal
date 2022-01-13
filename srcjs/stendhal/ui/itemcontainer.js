/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var ItemContainerComponent = require("../../../build/ts/ui/component/ItemContainerComponent").ItemContainerComponent;


var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


stendhal.ui.equip = {
	slotNames: ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet", "pouch", "bag", "keyring", "portfolio"],
	slotSizes: [   1,       1,      1,       1,        1,       1,       1,     1,       1,      12,       8,         9     ],
	slotImages: ["slot-helmet.png", "slot-shield.png", "slot-weapon.png", "slot-ring.png", "slot-armor.png", "slot-cloak.png",
		"slot-legs.png", "slot-boots.png", "slot-pouch.png", null, "slot-key.png", "slot-portfolio.png"],
	counter: 0,

	pouchVisible: false,

	init: function() {
		stendhal.ui.equip.inventory = [];
		for (var i in this.slotNames) {
			stendhal.ui.equip.inventory.push(
				new ItemContainerComponent(
					this.slotNames[i], this.slotSizes[i], null, "", false, this.slotImages[i]));
		}

		// hide pouch by default
		stendhal.ui.showPouch(false);
	},

	update: function() {
		for (var i in this.inventory) {
			stendhal.ui.equip.inventory[i].update();
		}

		if (!this.pouchVisible) {
			var features = null
			if (marauroa.me != null) {
				features = marauroa.me["features"];
			}

			if (features != null) {
				if (features["pouch"] != null) {
					stendhal.ui.showPouch(true);
				}
			}
		}
	},

	createInventoryWindow: function(slot, sizeX, sizeY, object, title, quickPickup) {
		stendhal.ui.equip.counter++;
		var suffix = "." + stendhal.ui.equip.counter + ".";
		var html = "<div class=\"inventorypopup inventorypopup_" + sizeX;
		if (quickPickup) {
			html += " quickPickup";
		}
		html += "\">";
		for (var i = 0; i < sizeX * sizeY; i++) {
			html += "<div id='" + slot + suffix + i + "' class='itemSlot'></div>";
		}
		html += "</div>";

		var popup = new stendhal.ui.Popup(title, html, 160, 370);
		var itemContainer = new ItemContainerComponent(slot, sizeX * sizeY, object, suffix, quickPickup, null);
		stendhal.ui.equip.inventory.push(itemContainer);
		itemContainer.update();
		popup.onClose = function() {
			stendhal.ui.equip.inventory.splice(stendhal.ui.equip.inventory.indexOf(itemContainer), 1);
		}
		return popup;
	}
};

stendhal.ui.showSlot = function(id, show) {
	var slot = document.getElementById(id);
	var prevState = slot.style.display;

	if (show === true) {
		slot.style.display = "block";
	} else {
		slot.style.display = "none";
	}

	return prevState != slot.style.display;
};

stendhal.ui.showPouch = function(show) {
	if (stendhal.ui.showSlot("pouch0", show)) {
		// resize the inventory window
		var equip = document.getElementById("equipment");
		if (show) {
			equip.style.height = "200px";
		} else {
			equip.style.height = "160px";
		}
		pouchVisible = show;
	}
}

stendhal.ui.equip.init();
