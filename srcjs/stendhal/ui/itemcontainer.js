/***************************************************************************
 *                   (C) Copyright 2003-2014 - Stendhal                    *
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
 * windows for items: character, bag, keyring
 */
stendhal.ui.equip = {
	slots: ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet"],

	update: function() {
		for (var i in this.slots) {
			var s = marauroa.me[this.slots[i]];
			var e = document.getElementById(this.slots[i]);
			if (typeof(s) != "undefined") {
				var o = s.first();
				if (typeof(o) != "undefined") {
					e.style.backgroundImage = "url(/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png" + ")";
					e.textContent = o.formatQuantity();
					e.dataItem = o;
				} else {
					e.style.backgroundImage = "none";
					e.textContent = "";
					e.dataItem = null;
				}
			} else {
				e.style.backgroundImage = "none";
				e.textContent = "";
				e.dataItem = null;
			}
		}
	}
}

stendhal.ui.bag = {
	update: function() {
		stendhal.ui.itemContainerWindow.render("bag", 12);
	}
}

stendhal.ui.keyring = {
	update: function() {
		stendhal.ui.itemContainerWindow.render("keyring", 8);
	}
}

stendhal.ui.itemContainerWindow = {
	render: function(name, size) {
		var cnt = 0;
		for (var i in marauroa.me[name]) {
			if (!isNaN(i)) {
				var o = marauroa.me[name][i];
				var e = document.getElementById(name + cnt);
				e.style.backgroundImage = "url(/data/sprites/items/" + o['class'] + "/" + o.subclass + ".png " + ")";
				e.textContent = o.formatQuantity();
				e.dataItem = o;
				cnt++;
			}
		}
		for (var i = cnt; i < size; i++) {
			var e = document.getElementById(name + i);
			e.style.backgroundImage = "none";
			e.textContent = "";
			e.dataItem = null;
		}
	}
}

stendhal.ui.window = {};
stendhal.ui.window.container = {

	init: function(object, slotName, width, height)  {
		this.canvas = document.createElement('canvas');;
		this.canvas.height = height * 40 + 2;
		this.canvas.width = width * 40 + 2;
		this.canvas.addEventListener("click", this.onclick.bind(this));
		window.body.appendChild(this.canvas);
		this.object = object;
		this.slotName = slotName;
		this.width = width;
		this.height = height;
	},

	draw: function() {
		// draw outline
		var ctx = this.canvas.getContext("2d");
		ctx.fillStyle = "rgb(255,255,255)";
		ctx.fillRect(0, 0, this.width * 40, this.height * 40);
		ctx.strokeStyle = "rgb(0,0,0)";
		ctx.strokeRect(0, 0, this.width * 40, this.height * 40);
		ctx.fillStyle = "rgb(224,224,224)";
		for (var h = 0; h < this.height; h++) {
			for (var w = 0; w < this.width; w++) {
				ctx.fillRect(w * 40 + 2, h * 40 + 2,
						40 - 2, 40 - 2);
			}
		}

		// draw items
		var slot = this.object[this.slotName];
		var index = 0;
		for (var i in slot) {
			if (isNaN(i)) {
				continue;
			}

			var w = index % this.width;
			var h = Math.floor(index / this.width);
			var localX = w * 40 + 3;
			var localY = h * 40 + 3;

			var item = slot[i];
			item.drawAt(ctx, localX, localY);
			index++;
		}
	},

	
	onclick: function(e) {
		// which item?
		var x = Math.floor(e.offsetX / 40);
		var y = Math.floor(e.offsetY / 40);
		var idx = y * this.width + x;
		if (!this.object[this.slotName].hasOwnProperty(idx)) {
			return;
		}
		this.pickupItem(idx);
	},

	/**
	 * tries to move an item from a corpse to the players bag
	 */
	pickupItem: function(idx) {
		var item = this.object[this.slotName][idx];
		var action = {
			"type": "equip", 
			"source_path": item.getIdPath(),
			"target_path": "[" + marauroa.me.id + "\tbag]"
		};
		console.log(action);
		marauroa.clientFramework.sendAction(action);
	},

	close: function() {
		this.canvas.remove();
	}
}