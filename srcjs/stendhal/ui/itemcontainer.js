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
stendhal.ui = stendhal.ui || {};


/**
 * slot name, slot size, object (a corpse or chest) or null for marauroa.me,
 * which changes on zone change.
 *
 * @constructor
 */
stendhal.ui.ItemContainerWindow = function(slot, size, object, suffix, quickPickup, defaultImage) {
	this.update = function() {
		render();
	};

	function render() {
		var myobject = object || marauroa.me;
		var cnt = 0;
		if (myobject[slot]) {
			for (var i = 0; i < myobject[slot].count(); i++) {
				var o = myobject[slot].getByIndex(i);
				var e = document.getElementById(slot + suffix + cnt);
				e.style.backgroundImage = "url(/data/sprites/items/" + o["class"] + "/" + o["subclass"] + ".png " + ")";
				e.textContent = o.formatQuantity();
				e.dataItem = o;
				cnt++;
			}
		}
		for (var i = cnt; i < size; i++) {
			var e = document.getElementById(slot + suffix + i);
			if (defaultImage) {
				e.style.backgroundImage = "url(/data/gui/" + defaultImage + ")";
			} else {
				e.style.backgroundImage = "none";
			}
			e.textContent = "";
			e.dataItem = null;
		}
	}

	function onDragStart(e) {
		var myobject = object || marauroa.me;
		var slotNumber = e.target.id.slice(slot.length + suffix.length);
		var item = myobject[slot].getByIndex(slotNumber);
		if (item) {
			var img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			window.event = e; // required by setDragImage polyfil
			e.dataTransfer.setDragImage(img, 0, 0);
			e.dataTransfer.setData("Text", JSON.stringify({
				path: item.getIdPath(),
				zone: marauroa.currentZoneName
			}));
		} else {
			e.preventDefault();
		}
	}

	function onDragOver(e) {
		e.preventDefault();
		e.dataTransfer.dropEffect = "move";
		return false;
	}

	function onDrop(e) {
		var myobject = object || marauroa.me;
		var datastr = e.dataTransfer.getData("Text") || e.dataTransfer.getData("text/x-stendhal");
		if (datastr) {
			var data = JSON.parse(datastr);
			var targetPath = "[" + myobject["id"] + "\t" + slot + "]";
			var action = {
				"type": "equip",
				"source_path": data.path,
				"target_path": targetPath,
				"zone" : data.zone
			};
			// if ctrl is pressed, we ask for the quantity
			if (e.ctrlKey) {
				new stendhal.ui.DropNumberDialog(action, e.pageX - 50, e.pageY - 25);
			} else {
				marauroa.clientFramework.sendAction(action);
			}
		}
		e.stopPropagation();
		e.preventDefault();
	}

	function onContextMenu(e) {
		e.preventDefault();
	}

	function isRightClick(e) {
		if (+new Date() - stendhal.ui.timestampMouseDown > 300) {
			return true;
		}
		if (e.which) {
			return (e.which === 3);
		} else {
			return (e.button === 2);
		}
	}

	function onMouseDown(e) {
		stendhal.ui.timestampMouseDown = +new Date();
	}

	function onMouseUp(e) {
		e.preventDefault();
		let event = stendhal.ui.html.extractPosition(e);
		if (event.target.dataItem) {
			if (quickPickup) {
				marauroa.clientFramework.sendAction({
					type: "equip",
					"source_path": event.target.dataItem.getIdPath(),
					"target_path": "[" + marauroa.me["id"] + "\tbag]",
					"clicked": "", // useful for changing default target in equip action
					"zone": marauroa.currentZoneName
				});
				return;
			}

			if (isRightClick(e)) {
				new stendhal.ui.Menu(event.target.dataItem, event.pageX - 50, event.pageY - 5);
			} else {
				marauroa.clientFramework.sendAction({
					type: "use",
					"target_path": event.target.dataItem.getIdPath(),
					"zone": marauroa.currentZoneName
				});
			}
		}
		document.getElementById("gamewindow").focus();
	}

	for (var i = 0; i < size; i++) {
		var e = document.getElementById(slot + suffix + i);
		e.setAttribute("draggable", true);
		e.addEventListener("dragstart", onDragStart);
		e.addEventListener("dragover", onDragOver);
		e.addEventListener("drop", onDrop);
		e.addEventListener("mousedown", onMouseDown);
		e.addEventListener("mouseup", onMouseUp);
		e.addEventListener("touchstart", onMouseDown);
		e.addEventListener("touchend", onMouseUp);
		e.addEventListener("contextmenu", onContextMenu);
	}
};


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
				new stendhal.ui.ItemContainerWindow(
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
		var itemContainer = new stendhal.ui.ItemContainerWindow(slot, sizeX * sizeY, object, suffix, quickPickup, null);
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
