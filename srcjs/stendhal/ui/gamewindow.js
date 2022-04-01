/***************************************************************************
 *                   (C) Copyright 2003-2019 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

"use strict";

var ui = require("../../../build/ts/ui/UI").ui;
var UIComponentEnum = require("../../../build/ts/ui/UIComponentEnum").UIComponentEnum;

var marauroa = window.marauroa = window.marauroa || {};
var stendhal = window.stendhal = window.stendhal || {};
stendhal.ui = stendhal.ui || {};

/** Represents an item being transferred from one container slot to another. */
stendhal.ui.heldItem = undefined;


/**
 * game window aka world view
 */
stendhal.ui.gamewindow = {
	/** screen offsets in pixels. */
	offsetX: 0,
	offsetY: 0,
	timeStamp: Date.now(),
	textSprites: [],
	notifSprites: [],
	emojiSprites: {},

	draw: function() {
		var startTime = new Date().getTime();

		if (marauroa.me && document.visibilityState === "visible") {
			if (marauroa.currentZoneName === stendhal.data.map.currentZoneName
				|| stendhal.data.map.currentZoneName === "int_vault"
				|| stendhal.data.map.currentZoneName === "int_adventure_island"
				|| stendhal.data.map.currentZoneName === "tutorial_island") {
				var canvas = document.getElementById("gamewindow");
				this.targetTileWidth = 32;
				this.targetTileHeight = 32;
				this.drawingError = false;

				this.ctx = canvas.getContext("2d");
				this.ctx.globalAlpha = 1.0;
				this.adjustView(canvas);
				this.ctx.fillStyle = "black";
				this.ctx.fillRect(0, 0, 10000, 10000);

				var tileOffsetX = Math.floor(this.offsetX / this.targetTileWidth);
				var tileOffsetY = Math.floor(this.offsetY / this.targetTileHeight);

				stendhal.data.map.strategy.render(canvas, this, tileOffsetX, tileOffsetY, this.targetTileWidth, this.targetTileHeight);

				this.drawEntitiesTop();
				this.drawEmojiSprites();
				this.drawTextSprites();
				this.drawTextSprites(this.notifSprites);
				// redraw inventory sprites
				// FIXME: animations don't begin until zone change or item moved
				stendhal.ui.equip.update();
				ui.get(UIComponentEnum.PlayerEquipment).update();
			}
		}
		setTimeout(function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}, Math.max((1000/20) - (new Date().getTime()-startTime), 1));

	},


	drawEntities: function() {
		var currentTime = new Date().getTime();
		var time = currentTime - this.timeStamp;
		this.timeStamp = currentTime;
		for (var i in stendhal.zone.entities) {
			var entity = stendhal.zone.entities[i];
			if (typeof(entity.draw) != "undefined") {
				entity.updatePosition(time);
				entity.draw(this.ctx);
			}
		}
	},

	drawEntitiesTop: function() {
		var i;
		for (i in stendhal.zone.entities) {
			const entity = stendhal.zone.entities[i];
			if (typeof(entity.setStatusBarOffset) !== "undefined") {
				entity.setStatusBarOffset();
			}
			if (typeof(entity.drawTop) != "undefined") {
				entity.drawTop(this.ctx);
			}
		}
	},

	drawTextSprites: function(sgroup=this.textSprites) {
		for (var i = 0; i < sgroup.length; i++) {
			var sprite = sgroup[i];
			var remove = sprite.draw(this.ctx);
			if (remove) {
				sgroup.splice(i, 1);
				i--;
			}
		}
	},

	/**
	 * Adds a sprite to be drawn on screen.
	 *
	 * @param owner
	 *     Entity the sprite is attached to.
	 * @param sprite
	 *     Sprite definition.
	 */
	addEmojiSprite: function(owner, sprite) {
		this.emojiSprites[owner] = sprite;
	},

	drawEmojiSprites: function() {
		for (const owner of Object.keys(this.emojiSprites)) {
			const sprite = this.emojiSprites[owner];
			const remove = sprite.draw(this.ctx);
			if (remove) {
				delete this.emojiSprites[owner];
			}
		}
	},

	adjustView: function(canvas) {
		// IE does not support ctx.resetTransform(), so use the following workaround:
		this.ctx.setTransform(1, 0, 0, 1, 0, 0);

		// Coordinates for a screen centered on player
		var centerX = marauroa.me["_x"] * this.targetTileWidth + this.targetTileWidth / 2 - canvas.width / 2;
		var centerY = marauroa.me["_y"] * this.targetTileHeight + this.targetTileHeight / 2 - canvas.height / 2;

		// Keep the world within the screen view
		centerX = Math.min(centerX, stendhal.data.map.zoneSizeX * this.targetTileWidth - canvas.width);
		centerX = Math.max(centerX, 0);

		centerY = Math.min(centerY, stendhal.data.map.zoneSizeY * this.targetTileHeight - canvas.height);
		centerY = Math.max(centerY, 0);

		this.offsetX = Math.round(centerX);
		this.offsetY = Math.round(centerY);
		this.ctx.translate(-this.offsetX, -this.offsetY);
	},

	addTextSprite: function(sprite) {
		this.textSprites.push(sprite);
	},

	addNotifSprite: function(sprite) {
		this.notifSprites.push(sprite);
	},

	removeNotifSprite: function(sprite) {
		const idx = this.notifSprites.indexOf(sprite);
		if (idx > -1) {
			this.notifSprites.splice(idx, 1);
		}
	},

	/**
	 * Checks if a notification sprite is drawn on top of all others.
	 *
	 * @param sprite
	 *     Sprite to be checked.
	 * @return
	 *     <code>true</code> if sprite is most recently added to client.
	 */
	isTopNotification: function(sprite) {
		return this.notifSprites.indexOf(sprite) + 1 == this.notifSprites.length;
	},

	/**
	 * Adds a notification bubble to window.
	 *
	 * @param cat
	 *     Achievement categroy.
	 * @param title
	 *     Achievement title.
	 * @param desc
	 *     Achievement description.
	 */
	addAchievementNotif: function(cat, title, desc) {
		const msg = "Achievement: " + title + ": \"" + desc + "\"";

		// for now we will just create a notification bubble & add line to chat log
		if (marauroa.me) {
			marauroa.me.addNotificationBubble("server", msg);
		}
		ui.get(UIComponentEnum.ChatLog).addLine("server", msg);
	},

	// Mouse click handling
	onMouseDown: (function() {
		var entity;
		var startX;
		var startY;

		function _onMouseDown(e) {
			// close action menu if open
			if (stendhal.ui.actionContextMenu.isOpen() && !this.isRightClick(e)) {
				stendhal.ui.actionContextMenu.close(true);
				return;
			}

			var pos = stendhal.ui.html.extractPosition(e);
			if (stendhal.ui.globalpopup) {
				stendhal.ui.globalpopup.close();
			}

			startX = pos.offsetX;
			startY = pos.offsetY;

			var x = pos.offsetX + stendhal.ui.gamewindow.offsetX;
			var y = pos.offsetY + stendhal.ui.gamewindow.offsetY;
			entity = stendhal.zone.entityAt(x, y);
			stendhal.ui.timestampMouseDown = +new Date();

			if (e.type !== "dblclick") {
				e.target.addEventListener("mousemove", onDrag);
				e.target.addEventListener("mouseup", onMouseUp);
				e.target.addEventListener("touchmove", onDrag);
				e.target.addEventListener("touchend", onMouseUp);
			} else if (entity == stendhal.zone.ground) {
				entity.onclick(pos.offsetX, pos.offsetY, true);
			}
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

		function onMouseUp(e) {
			var pos = stendhal.ui.html.extractPosition(e);
			if (isRightClick(e)) {
				if (entity != stendhal.zone.ground) {
					stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
						new ActionContextMenu(entity), pos.pageX - 50, pos.pageY - 5));
				}
			} else {
				entity.onclick(pos.offsetX, pos.offsetY);
			}
			cleanUp(pos);
			pos.target.focus();
			e.preventDefault();
		}

		function onDrag(e) {
			var pos = stendhal.ui.html.extractPosition(e);
			var xDiff = startX - pos.offsetX;
			var yDiff = startY - pos.offsetY;
			// It's not really a click if the mouse has moved too much.
			if (xDiff * xDiff + yDiff * yDiff > 5) {
				cleanUp(e);
			}
		}

		function cleanUp(e) {
			entity = null;
			e.target.removeEventListener("mouseup", onMouseUp);
			e.target.removeEventListener("mousemove", onDrag);
			e.target.removeEventListener("touchend", onMouseUp);
			e.target.removeEventListener("touchmove", onDrag);
		}

		return _onMouseDown;
	})(),

	onMouseMove: function(e) {
		var pos = stendhal.ui.html.extractPosition(e);
		var x = pos.offsetX + stendhal.ui.gamewindow.offsetX;
		var y = pos.offsetY + stendhal.ui.gamewindow.offsetY;
		var entity = stendhal.zone.entityAt(x, y);
		document.getElementById("gamewindow").style.cursor = entity.getCursor(x, y);
	},

	/**
	 * Changes character facing direction dependent on direction
	 * of wheel scroll.
	 */
	onMouseWheel: function(e) {
		if (marauroa.me) {
			e.preventDefault();

			// previous event may have changed type to string
			const currentDir = parseInt(marauroa.me["dir"], 10);
			let newDir = null;

			if (typeof(currentDir) === "number") {
				if (e.deltaY >= 100) {
					// clockwise
					newDir = currentDir + 1;
					if (newDir > 4) {
						newDir = 1;
					}
				} else if (e.deltaY <= -100) {
					// counter-clockwise
					newDir = currentDir - 1;
					if (newDir < 1) {
						newDir = 4;
					}
				}
			}

			if (newDir != null) {
				marauroa.clientFramework.sendAction({"type": "face", "dir": ""+newDir});
			}
		}
	},

	// ***************** Drag and drop ******************
	onDragStart: function(e) {
		var pos = stendhal.ui.html.extractPosition(e);
		var draggedEntity = stendhal.zone.entityAt(pos.offsetX + stendhal.ui.gamewindow.offsetX,
				pos.offsetY + stendhal.ui.gamewindow.offsetY);

		var img = undefined;
		if (draggedEntity.type === "item") {
			img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(draggedEntity.sprite.filename), 32, 32);
			stendhal.ui.heldItem = {
				path: draggedEntity.getIdPath(),
				zone: marauroa.currentZoneName
			}
		} else if (draggedEntity.type === "corpse") {
			img = stendhal.data.sprites.get(draggedEntity.sprite.filename);
		} else {
			e.preventDefault();
			return;
		}

		if (e.dataTransfer) {
			window.event = e; // required by setDragImage polyfil
			e.dataTransfer.setDragImage(img, 0, 0);
		}
	},

	onDragOver: function(e) {
		e.preventDefault(); // Necessary. Allows us to drop.
		if (e.dataTransfer) {
			e.dataTransfer.dropEffect = "move";
		}
		return false;
	},

	onDrop: function(e) {
		var pos = stendhal.ui.html.extractPosition(e);
		if (stendhal.ui.heldItem) {
			var action = {
				"x": Math.floor((pos.offsetX + stendhal.ui.gamewindow.offsetX) / 32).toString(),
				"y": Math.floor((pos.offsetY + stendhal.ui.gamewindow.offsetY) / 32).toString(),
				"zone": stendhal.ui.heldItem.zone
			}

			var id = stendhal.ui.heldItem.path.substr(1, stendhal.ui.heldItem.path.length - 2);
			var drop = /\t/.test(id);
			if (drop) {
				action["type"] = "drop";
				action["source_path"] = stendhal.ui.heldItem.path;
			} else {
				action["type"] = "displace";
				action["baseitem"] = id;
			}

			// item was dropped
			stendhal.ui.heldItem = undefined;

			// if ctrl is pressed, we ask for the quantity
			if (e.ctrlKey) {
				ui.createSingletonFloatingWindow("Quantity", new DropQuantitySelectorDialog(action), pos.pageX - 50, pos.pageY - 25);
			} else {
				marauroa.clientFramework.sendAction(action);
			}
		}
		e.stopPropagation();
		e.preventDefault();
	},

	onTouchStart: function(e) {
		e.preventDefault();
		stendhal.ui.touch.timestampTouchStart = +new Date();
	},

	onTouchEnd: function(e) {
		e.preventDefault();
		stendhal.ui.touch.timestampTouchEnd = +new Date();
		if (stendhal.ui.heldItem) {
			this.onDrop(e);
		} else {
			this.onMouseUp(e);
		}
	},

	onTouchMove: function(e) {
		if (stendhal.ui.heldItem) {
			this.onDragOver(e);
		} else {
			this.onDragStart(e);
		}
	},

	onContentMenu: function(e) {
		e.preventDefault();
	},

	/**
	 * Creates a screenshot of game screen to download.
	 */
	createScreenshot: function() {
		Chat.log("client", "creating screenshot ...");
		const uri = this.ctx.canvas.toDataURL("image/png");

		const d = new Date();
		const ts = {
			yyyy: d.getFullYear() + "",
			mm: (d.getMonth() + 1) + "",
			dd: d.getDate() + "",
			HH: d.getHours() + "",
			MM: d.getMinutes() + "",
			SS: d.getSeconds() + "",
			ms: d.getMilliseconds() + ""
		};

		for (let o of [ts.mm, ts.dd, ts.HH, ts.MM, ts.SS]) {
			if (o.length < 2) {
				o = "0" + o;
			}
		}
		while (ts.ms.length < 3) {
			ts.ms = "0" + ts.ms;
		}

		const filename = "screenshot_" + ts.yyyy + ts.mm
				+ ts.dd + "." + ts.HH + "." + ts.MM + "."
				+ ts.SS + "." + ts.ms + ".png";

		const anchor = document.createElement("a");
		anchor.download = filename;
		anchor.target = "_blank";
		anchor.href = uri;
		anchor.click();
	}
};


/**
 * Representation of an internal dialog window.
 */
stendhal.ui.dialogHandler = {
	content: null,

	/**
	 * Makes a copy.
	 *
	 * @return
	 *     New stendhal.ui.dialogHandler.
	 */
	copy: function() {
		let newObject = Object.assign({}, stendhal.ui.dialogHandler);
		// remove copying functionality
		delete newObject.copy;

		return newObject;
	},

	/**
	 * Sets the dialog window content.
	 *
	 * @param c
	 *     The FloatingWindow instance to be set.
	 */
	set: function(c) {
		if (!(c instanceof FloatingWindow)) {
			console.error("cannot set dialogHandler content to \""
				+ typeof(c) + "\", must be FloatingWindow instance");
			return;
		}

		// make sure this is closed before opening again
		if (this.isOpen()) {
			this.close();
		}

		this.content = c;
	},

	/**
	 * Sets the content to <code>null</code>.
	 */
	unset: function() {
		this.content = null;
	},

	/**
	 * Retrieves the window.
	 *
	 * @return
	 *     FloatingWindow instance or <code>null</code> if not set.
	 */
	get: function() {
		return this.content;
	},

	/**
	 * Checks if the action context menu is open.
	 *
	 * @return
	 *     The FloatingWindow open state.
	 */
	isOpen: function() {
		return this.content != null && this.content.isOpen();
	},

	/**
	 * Closes the dialog window.
	 *
	 * @param unset
	 *     If <code>true</code>, resets content to <code>null</code>.
	 */
	close: function(unset=false) {
		this.content.close();
		if (unset) {
			this.content = null;
		}
	}
};
