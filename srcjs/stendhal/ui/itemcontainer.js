/***************************************************************************
 *                   (C) Copyright 2003-2015 - Stendhal                    *
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
};

stendhal.ui.ItemContainerWindow = function(name, size) {
	var initialized = false;
	
	this.update = function() {
		if (!initialized) {
			for (var i = 0; i < size; i++) {
				var e = document.getElementById(name + i);
				e.setAttribute("draggable", true);
				e.addEventListener("dragstart", onDragStart);
				e.addEventListener("dragover", onDragOver);
				e.addEventListener("drop", onDrop);
			}
			initialized = true;
		}
		render(name, size);
	};
	
	function render(name, size) {
		var cnt = 0;
		if (marauroa.me[name]) {
			for (var i = 0; i < marauroa.me[name].count(); i++) {
				var o = marauroa.me[name].getByIndex(i);
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
	
	function onDragStart(e) {
		var slotNumber = e.target.id.slice(name.length);
		var item = marauroa.me[name].getByIndex(slotNumber);
		if (item) {
			var img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			e.dataTransfer.setDragImage(img, 0, 0);
			e.dataTransfer.setData("text/x-stendhal-item", item.getIdPath());
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
		var data = e.dataTransfer.getData("text/x-stendhal-item");
		if (data) {
			var targetPath = "[" + marauroa.me.id + "\t" + name + "]";
			var action = {
				"type": "equip",
				"source_path": data,
				"target_path": targetPath,
				// FIXME: This is not necessarily true. What to do when the drag
				// started on previous zone?
				// "zone" : marauroa.currentZoneName
			};
			marauroa.clientFramework.sendAction(action);
		}
		e.stopPropagation();
	}
}

stendhal.ui.bag = new stendhal.ui.ItemContainerWindow("bag", 12);
stendhal.ui.keyring = new stendhal.ui.ItemContainerWindow("keyring", 8);

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
		this.canvas.setAttribute("draggable", true);
		this.canvas.addEventListener("dragstart", this.onDragStart.bind(this));
		this.canvas.addEventListener("dragover", this.onDragOver.bind(this));
		this.canvas.addEventListener("drop", this.onDrop.bind(this));
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
		for (var i = 0; i < slot.count(); i++) {
			var w = index % this.width;
			var h = Math.floor(index / this.width);
			var localX = w * 40 + 3;
			var localY = h * 40 + 3;

			var item = slot.getByIndex(i);
			item.drawAt(ctx, localX, localY);
			index++;
		}
	},

	getItem: function(xOffset, yOffset) {
		var x = Math.floor(xOffset / 40);
		var y = Math.floor(yOffset / 40);
		var idx = y * this.width + x;
		if (this.object.hasOwnProperty(this.slotName)) {
			return this.object[this.slotName].getByIndex(idx);
		}
		return null;
	},
	
	onclick: function(e) {
		// which item?
		var item = this.getItem(e.offsetX, e.offsetY);
		if (item) {
			this.pickupItem(item);
		}
	},

	/**
	 * tries to move an item from a corpse to the players bag
	 */
	pickupItem: function(item) {
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
	},
	
	onDragStart: function(e) {
		var item = this.getItem(e.offsetX, e.offsetY);
		if (item) {
			var img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			e.dataTransfer.setDragImage(img, 0, 0);
			e.dataTransfer.setData("text/x-stendhal-item", item.getIdPath());
		} else {
			e.preventDefault();
		}
	},
	
	onDragOver: function(e) {
		e.preventDefault(); // Necessary. Allows us to drop.
		e.dataTransfer.dropEffect = "move";
		return false;
	},
	
	onDrop: function(e) {
		var data = e.dataTransfer.getData("text/x-stendhal-item");
		if (data) {
			var targetPath = this.object.getIdPath();
			// add the slot name to the path
			targetPath = targetPath.substr(0, targetPath.length - 1) + "\t" +
					this.slotName + "]";
			var action = {
				"type": "equip",
				"source_path": data,
				"target_path": targetPath,
				// FIXME: This is not necessarily true. What to do when the drag
				// started on previous zone?
				// "zone" : marauroa.currentZoneName
			};
			marauroa.clientFramework.sendAction(action);
		}
		e.stopPropagation();
	}
};