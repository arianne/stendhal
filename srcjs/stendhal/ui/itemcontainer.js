/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/

"use strict";

window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};


/**
 * slot name, slot size, object (a corpse or chest) or null for marauroa.me,
 * which changes on zone change.
 */
stendhal.ui.ItemContainerWindow = function(name, size, object, suffix) {
	this.update = function() {
		render();
	};

	function render() {
		var myobject = object || marauroa.me;
		var cnt = 0;
		if (myobject[name]) {
			for (var i = 0; i < myobject[name].count(); i++) {
				var o = myobject[name].getByIndex(i);
				var e = document.getElementById(name + suffix + cnt);
				e.style.backgroundImage = "url(/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png " + ")";
				e.textContent = o.formatQuantity();
				e.dataItem = o;
				cnt++;
			}
		}
		for (var i = cnt; i < size; i++) {
			var e = document.getElementById(name + suffix + i);
			e.style.backgroundImage = "none";
			e.textContent = "";
			e.dataItem = null;
		}
	}

	function onDragStart(e) {
		var myobject = object || marauroa.me;
		var slotNumber = e.target.id.slice(name.length + suffix.length);
		var item = myobject[name].getByIndex(slotNumber);
		if (item) {
			var img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			e.dataTransfer.setDragImage(img, 0, 0);
			e.dataTransfer.setData("text/x-stendhal-item", JSON.stringify({
				"path": item.getIdPath(),
				"zone": marauroa.currentZoneName
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
		var datastr = e.dataTransfer.getData("text/x-stendhal-item");
		if (datastr) {
			var data = JSON.parse(datastr);
			var targetPath = "[" + myobject.id + "\t" + name + "]";
			var action = {
				"type": "equip",
				"source_path": data.path,
				"target_path": targetPath,
				"zone" : data.zone
			};
			marauroa.clientFramework.sendAction(action);
		}
		e.stopPropagation();
	}

	for (var i = 0; i < size; i++) {
		var e = document.getElementById(name + suffix + i);
		e.setAttribute("draggable", true);
		e.addEventListener("dragstart", onDragStart);
		e.addEventListener("dragover", onDragOver);
		e.addEventListener("drop", onDrop);
	}
}


stendhal.ui.equip = {
	slotNames: ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet", "bag", "keyring"],
	slotSizes: [1,    1,      1,       1,        1,       1,        1,     1,       12,     8   ],
	counter: 0,

	init: function() {
		this.inventory = [];
		for (var i in this.slotNames) {
			this.inventory.push(
				new stendhal.ui.ItemContainerWindow(
					this.slotNames[i], this.slotSizes[i], null, ""));
		}
	},

	update: function() {
		for (var i in this.inventory) {
			this.inventory[i].update();
		}
	},

	createInventoryWindow(name, sizeX, sizeY, object) {
		stendhal.ui.equip.counter++;
		var suffix = "." + stendhal.ui.equip.counter + ".";
		var html = "<div style='border: 1px solid black; width: 80px; padding: 2px; float: left'>";
		for (var i = 0; i < sizeX * sizeY; i++) {
			html += "<div id='" + name + suffix + i + "' class='itemSlot'></div>";
		}
		html += "</div>";
		new stendhal.ui.popup(html);
		this.inventory.push(
			new stendhal.ui.ItemContainerWindow(
				name, sizeX * sizeY, object, suffix));
	}
};

stendhal.ui.equip.init();
