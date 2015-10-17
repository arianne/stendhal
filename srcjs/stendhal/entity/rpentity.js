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

(function() {

	var HEALTH_BAR_HEIGHT = 6;



/**
 * RPEntity
 */
marauroa.rpobjectFactory.rpentity = marauroa.util.fromProto(marauroa.rpobjectFactory.activeEntity, {
	zIndex: 8000,
	drawY: 0,
	spritePath: "",
	titleStyle: "#FFFFFF",
	_target: null,
	attackSprite: null,
	attackResult: null,
	dir: 3,

	set: function(key, value) {
		// Ugly hack to detect changes. The old value is no
		// longer available after .apply()
		var oldValue = this[key];

		marauroa.rpobjectFactory.rpentity.proto.set.apply(this, arguments);
		if (key == "text") {
			this.say(value);
		} else if (["hp", "base_hp"].indexOf(key) !== -1) {
			this[key] = parseInt(value);
			if (key === "hp" && oldValue != undefined) {
				this.onHPChanged(this[key] - oldValue);
			}
		} else if (key == "target") {
			if (this._target) {
				this._target.onAttackStopped(this);
			}
			this._target = marauroa.currentZone[value];
			if (this._target) {
				this._target.onTargeted(this);
			}
		} else if (key === "away") {
			this.addFloater("Away", "#ffff00");
		} else if (key === "grumpy") {
			this.addFloater("Grumpy", "#ffff00");
		} else if (key === "xp" && oldValue != undefined) {
			this.onXPChanged(this[key] - oldValue);
		}
	},
	
	unset: function(key) {
		if (key == "target" && this._target) {
			this._target.onAttackStopped(this);
			this._target = null;
		} else if (key === "away") {
			this.addFloater("Back", "#ffff00");
		} else if (key === "grumpy") {
			this.addFloater("Receptive", "#ffff00");
		}
		delete this[key];
	},

	isVisibleToAction: function(filter) {
		return true;
	},

	/** 
	 * says a text
	 */
	say: function (text) {
		this.addSpeechBubble(text);
		if (!marauroa.me) {
			return;
		}
		if (marauroa.me.isInHearingRange(this)) {
			if (text.match("^!me") == "!me") {
				stendhal.ui.chatLog.addLine("emote", text.replace(/^!me/, this.title));
			} else {
				stendhal.ui.chatLog.addLine("normal", this.title + ": " + text);
			}
		}
	},
	
	addSpeechBubble: function(text) {
		var x = this._x * 32 + 32;
		var y = this._y * 32 - 16;
		stendhal.ui.gamewindow.addTextSprite({
			realText: (text.length > 30) ? (text.substring(0, 30) + "...") : text,
			timeStamp: Date.now(),
			draw: function(ctx) {
				ctx.lineWidth = 2;
				ctx.font = "14px Arial";
				ctx.fillStyle = '#ffffff';
				// get width of text
				var width = ctx.measureText(this.realText).width + 8;
				ctx.strokeStyle = "#000000";
				ctx.strokeRect(x, y - 15, width, 20);
				ctx.fillRect(x, y - 15, width, 20);
				
				ctx.beginPath();
				ctx.moveTo(x, y);
				ctx.lineTo(x - 5, y + 8);
				ctx.lineTo(x + 1, y + 5);
				ctx.stroke();
				ctx.closePath();
				ctx.fill();

				ctx.fillStyle = "#000000";
				ctx.fillText(this.realText, x + 4, y);
				return Date.now() > this.timeStamp + 2000 + 20 * this.realText.length;
			}
		});
	},

	/**
	 * draw an outfix part
	 *
	 * @param ctx   ctx
	 * @param part  part
	 * @param index index
	 */
	drawOutfitPart: function(ctx, part, index) {
		var n = index;
		if (index < 10) {
			n = "00" + index;
		} else if(index < 100) {
			n = "0" + index;
		}
		var filename = "/data/sprites/outfit/" + part + "/" + part + "_" + n + ".png";
		this.drawSprite(ctx, filename);
	},

	/** 
	 * draw RPEntities
	 */
	draw: function(ctx) {
		this.drawCombat(ctx);
		var filename;
		if (typeof(this.outfit) != "undefined") {
			this.drawOutfitPart(ctx, "body", (this.outfit % 100));
			this.drawOutfitPart(ctx, "dress", (Math.floor(this.outfit/100) % 100));
			this.drawOutfitPart(ctx, "head", (Math.floor(this.outfit/10000) % 100));
			this.drawOutfitPart(ctx, "hair", (Math.floor(this.outfit/1000000) % 100));
		} else {
			filename = "/data/sprites/" + this.spritePath + "/" + this["class"];
			if (typeof(this.subclass) != "undefined") {
				filename = filename + "/" + this["subclass"];
			}
			filename = filename + ".png";
			this.drawSprite(ctx, filename);
		}
		this.drawAttack(ctx);
		this.drawFloaters(ctx);
		this.drawStatusIcons(ctx);
	},
	
	drawStatusIcons: function(ctx) {
		function _drawAnimatedIcon(icon, delay, nFrames, xdim, ydim, x, y) {
			var frame = Math.floor(Date.now() / delay) % nFrames;
			ctx.drawImage(icon, frame * xdim, 0, xdim, ydim, x, y, xdim, ydim);
		}
		function drawAnimatedIcon(iconPath, delay, x, y) {
			var icon = stendhal.data.sprites.get(iconPath);
			var dim = icon.height;
			var nFrames = icon.width / dim;
			_drawAnimatedIcon(icon, delay, nFrames, dim, dim, x, y);
		}
		function drawAnimatedIconWithFrames(iconPath, nFrames, delay, x, y) {
			var icon = stendhal.data.sprites.get(iconPath);
			var ydim = icon.height;
			var xdim = icon.width / nFrames;
			_drawAnimatedIcon(icon, delay, nFrames, xdim, ydim, x, y);
		}
		
		var x = this._x * 32 - 10;
		var y = (this._y + 1) * 32;
		if (this.hasOwnProperty("choking")) {
			ctx.drawImage(stendhal.data.sprites.get("/data/sprites/ideas/choking.png"), x, y - 10);
		} else if (this.hasOwnProperty("eating")) {
			ctx.drawImage(stendhal.data.sprites.get("/data/sprites/ideas/eat.png"), x, y - 10);
		}
		// NPC and pet idea icons
		if (this.hasOwnProperty("idea")) {
			ctx.drawImage(stendhal.data.sprites.get("/data/sprites/ideas/" + this.idea + ".png"), x + 32 * this.width, y - this.drawHeight);
		}
		if (this.hasOwnProperty("away")) {
			drawAnimatedIcon("/data/sprites/ideas/away.png", 1500, x + 32 * this.width, y - this.drawHeight);
		}
		if (this.hasOwnProperty("grumpy")) {
			drawAnimatedIcon("/data/sprites/ideas/grumpy.png", 1000, x + 5, y - this.drawHeight);
		}
		if (this.hasOwnProperty("last_player_kill_time")) {
			drawAnimatedIconWithFrames("/data/sprites/ideas/pk.png", 12, 300, x, y - this.drawHeight);
		}
		if (this.hasOwnProperty("poisoned")) {
			drawAnimatedIcon("/data/sprites/status/poison.png", 100, x + 32 * this.width - 10, y - this.drawHeight);
		}
		// NPC job icons
		if (this.hasOwnProperty("job_healer")) {
			ctx.drawImage(stendhal.data.sprites.get("/data/sprites/status/healer.png"), x, y - 10);
		}
		if (this.hasOwnProperty("job_merchant")) {
			ctx.drawImage(stendhal.data.sprites.get("/data/sprites/status/merchant.png"), x + 12, y - 10);
		}
	},
	
	/**
	 * Draw colored ellipses (or rectangles on browsers that do not support
	 * ellipses) when the entity is being attacked, or is attacking the user.
	 */
	drawCombat: function(ctx) {
		if (this.attackers && this.attackers.size > 0) {
			ctx.lineWidth = 1;
			/*
			 * As of 2015-9-15 CanvasRenderingContext2D.ellipse() is not
			 * supported in most browsers. Fall back to rectangles on these.
			 * Also on Chrome 45.0.2454.85 ellipse() does not seem to support
			 * the begin angle parameter correctly, nor does the stroke 
			 * direction work as it should so it can't be used as a workaround.
			 * Currently the second ellipse part is drawn as a full ellipse, but
			 * the code below should eventually draw the right thing once
			 * browsers catch up. Probably.
			 */
			if (ctx.ellipse instanceof Function) {
				var xRad = this.width * 16;
				var yRad = this.height * 16 / Math.SQRT2;
				var centerX = this._x * 32 + xRad;
				var centerY = (this._y + this.height) * 32 - yRad;
				ctx.strokeStyle = "#4a0000";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, Math.PI, false);
				ctx.stroke();
				ctx.strokeStyle = "#e60a0a";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, Math.PI, 2 * Math.PI, false);
				ctx.stroke();
			} else {
				ctx.strokeStyle = "#e60a0a";
				ctx.strokeRect(32 * this._x, 32 * this._y, 32 * this.width, 32 * this.height);
			}
		}
		if (this.getAttackTarget() === marauroa.me) {
			ctx.lineWidth = 1;
			// See above about ellipses.
			if (ctx.ellipse instanceof Function) {
				var xRad = this.width * 16 - 1;
				var yRad = this.height * 16 / Math.SQRT2 - 1;
				var centerX = this._x * 32 + xRad + 1;
				var centerY = (this._y + this.height) * 32 - yRad - 1;
				ctx.strokeStyle = "#ffc800";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, Math.PI, false);
				ctx.stroke();
				ctx.strokeStyle = "#ffdd0a";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, Math.PI, 2 * Math.PI, false);
				ctx.stroke();
			} else {
				ctx.strokeStyle = "#ffdd0a";
				ctx.strokeRect(32 * this._x + 1, 32 * this._y + 1, 32 * this.width - 2, 32 * this.height - 2);
			}
		}
		if (this.attackResult) {
			if (this.attackResult.draw(ctx, (this._x + this.width) * 32 - 10, (this._y + this.height) * 32 - 10)) {
				this.attackResult = null;
			}
		} 
	},
	
	/**
	 * Draw entities in this.floaters array. Each floater should have
	 * draw(ctx, x, y) method, where ctx is the canvas context, and x, y are
	 * the coordinates where to start floating. The method should return true
	 * when the floater should be removed.
	 */
	drawFloaters: function(ctx) {
		if (this.hasOwnProperty("floaters")) {
			var centerX = (this._x + this.width / 2) * 32;
			var topY = (this._y + 1) * 32 - this.drawHeight;
			// Grab an unchanging copy
			var currentFloaters = this.floaters;
			for (var i = 0; i < currentFloaters.length; i++) {
				var floater = currentFloaters[i];
				if (floater.draw(ctx, centerX, topY)) {
					// copy the array and remove the specific element from the copy
					this.floaters = this.floaters.slice();
					this.floaters.splice(this.floaters.indexOf(floater), 1);
				}
			}
		}
	},
	
	drawSprite: function(ctx, filename) {
		var localX = this._x * 32;
		var localY = this._y * 32;
		var image = stendhal.data.sprites.get(filename);
		if (image.complete) {
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this.dir - 1;
			// Ents are a hack in Java client too
			if (this["class"] == "ent") {
				nFrames = 1;
				nDirections = 2;
				yRow = Math.floor((this.dir - 1) / 2);
			}
			this.drawHeight = image.height / nDirections;
			this.drawWidth = image.width / nFrames;
			var drawX = ((this.width * 32) - this.drawWidth) / 2;
			var frame = 0;
			if (this.speed > 0 && nFrames != 1) {
				// % Works normally with *floats* (just whose bright idea was
				// that?), so use floor() as a workaround
				frame = Math.floor(Date.now() / 100) % nFrames;
			}
			var drawY = (this.height * 32) - this.drawHeight;
			ctx.drawImage(image, frame * this.drawWidth, yRow * this.drawHeight, this.drawWidth, this.drawHeight, localX + drawX, localY + drawY, this.drawWidth, this.drawHeight);
		}
	},

	drawTop: function(ctx) {
		var localX = this._x * 32;
		var localY = this._y * 32;

		this.drawHealthBar(ctx, localX, localY);
		this.drawTitle(ctx, localX, localY);
	},

	drawHealthBar: function(ctx, x, y) {
		var drawX = x + ((this.width * 32) - this.drawWidth) / 2;
		var drawY = y + (this.height * 32) - this.drawHeight - HEALTH_BAR_HEIGHT;
		
		ctx.strokeStyle = "#000000";
		ctx.lineWidth = 2;
		ctx.beginPath();
		ctx.rect(drawX, drawY, this.drawWidth, HEALTH_BAR_HEIGHT - 2);
		ctx.stroke();
		
		ctx.fillStyle = "#E0E0E0";
		ctx.fillRect(drawX, drawY, this.drawWidth, HEALTH_BAR_HEIGHT - 2);

		// Bar color
		var hpRatio = this.hp / this.base_hp;
		var red = Math.floor(Math.min((1 - hpRatio) * 2, 1) * 255);
		var green = Math.floor(Math.min(hpRatio * 2, 1) * 255);
		ctx.fillStyle = "rgb(".concat(red, ",", green, ",0)");
		ctx.fillRect(drawX, drawY, this.drawWidth * hpRatio, HEALTH_BAR_HEIGHT - 2);
	},

	drawTitle: function(ctx, x, y) {
		var title = this.title;
		if (title == undefined) {
			title = this.name;
			if (title == undefined || title == "") {
				title = this["class"];
				if (title == undefined) {
					title = this.type;
				}
			}
		}
		
		if (typeof(title) != "undefined") {
			ctx.font = "14px Arial";
			var textMetrics = ctx.measureText(title);
			var drawY = y + (this.height * 32) - this.drawHeight - HEALTH_BAR_HEIGHT;
			this.drawOutlineText(ctx, title, this.titleStyle, x + (this.width * 32 - textMetrics.width) / 2, drawY - 5 - HEALTH_BAR_HEIGHT);
		}
	},
	
	drawAttack: function(ctx) {
		if (this.attackSprite == null) {
			return;
		}
		if (this.attackSprite.expired()) {
			this.attackSprite = null;
			return;
		}
		var localX = this._x * 32;
		var localY = this._y * 32;
		this.attackSprite.draw(ctx, localX, localY, this.drawWidth, this.drawHeight);
	},

	// attack handling
	getAttackTarget: function() {
		// If the attack target id was read before the target was available,
		// _target does not point to the correct entity. Look up the target
		// again, if _target does not exist, but it should.
		if (!this._target && this.target) {
			this._target = marauroa.currentZone[this.target];
			if (this._target) {
				this._target.onTargeted(this);
			}
		}
		return this._target;
	},
	
	onDamaged: function(source, damage) {
		this.attackResult = this.createResultIcon("/data/sprites/combat/hitted.png");
	},

	onBlocked: function(source) {
		this.attackResult = this.createResultIcon("/data/sprites/combat/blocked.png");
	},

	onMissed: function(source) {
		this.attackResult = this.createResultIcon("/data/sprites/combat/missed.png");
	},
	
	onHPChanged: function(change) {
		if (change > 0) {
			this.addFloater("+" + change, "#00ff00");
		} else if (change < 0) {
			this.addFloater(change.toString(), "#ff0000");
		}
	},
	
	onXPChanged: function(change) {
		if (change > 0) {
			this.addFloater("+" + change, "#4169e1");
			stendhal.ui.chatLog.addLine("significant_positive", this.title + " earns " + change + " experience points.");
		} else if (change < 0) {
			this.addFloater(change.toString(), "#ff8f8f");
			stendhal.ui.chatLog.addLine("significant_negative", this.title + " loses " + Math.abs(change) + " experience points.");
		}
	},
	
	addFloater: function(message, color) {
		if (!this.hasOwnProperty("floaters")) {
			this.floaters = [];
		}
		var self = this;
		this.floaters.push({
			initTime: Date.now(),
			textOffset: null,
			draw: function(ctx, x, y) {
				ctx.font = "14px Arial";
				if (!this.textOffset) {
					this.textOffset = ctx.measureText(message).width / 2;
				}
				var timeDiff = Date.now() - this.initTime;
				self.drawOutlineText(ctx, message, color, x - this.textOffset, y - timeDiff / 50);
				return (timeDiff > 2000);
			}
		});
	},
	
	/**
	 * Create a closure for drawing attack result icons. The resulting object
	 * has a method draw(context, x, y) which returns true when the attack
	 * result has expired and draw() should no longer be called.
	 * 
	 * @param imagePath path to the result icon
	 * @return object for drawing the icon
	 */
	createResultIcon: function(imagePath) {
		return {
			initTime: Date.now(),
			image: stendhal.data.sprites.get(imagePath),
			draw: function(ctx, x, y) {
				ctx.drawImage(this.image, x, y);
				return (Date.now() - this.initTime > 1200);
			}
		};
	},

	onAttackPerformed: function(nature, ranged) {
		if (ranged) {
			var color;
			switch (nature) {
			case "0":
			default:
				color = "#c0c0c0";
			break;
			case "1":
				color = "#ff6400";
				break;
			case "2":
				color = "#8c8cff";
				break;
			case "3":
				color = "#fff08c";
				break;
			case "4":
				color = "#404040";
			}
			var tgt = this.getAttackTarget();
			this.attackSprite = (function(color, targetX, targetY) {
				return {
					initTime: Date.now(),
					expired: function() {
						return Date.now() - this.initTime > 180;
					},
					draw: function(ctx, x, y, entityWidth, entityHeight) {			
						var dtime = Date.now() - this.initTime;
						// We can use fractional "frame" for the lines. Just
						// draw the arrow where it should be at the moment.
						var frame = Math.min(dtime / 60, 4);
						
						var startX = x + entityWidth / 4;
						var startY = y + entityHeight / 4;
						
						var yLength = (targetY - startY) / 4;
						var xLength = (targetX - startX) / 4;

						startY += frame * yLength;
						var endY = startY + yLength;
						startX += frame * xLength;
						var endX = startX + xLength;
						
						ctx.strokeStyle = color;
						ctx.lineWidth = 2;
						ctx.moveTo(startX, startY);
						ctx.lineTo(endX, endY);
						ctx.stroke();
					}
				};
			})(color, (tgt.x + tgt.width / 2) * 32, (tgt.y + tgt.height / 2) * 32);
		} else {
			var imagePath;
			switch (nature) {
			case "0":
			default:
				imagePath = "/data/sprites/combat/blade_strike_cut.png";
			break;
			case "1":
				imagePath = "/data/sprites/combat/blade_strike_fire.png";
				break;
			case "2":
				imagePath = "/data/sprites/combat/blade_strike_ice.png";
				break;
			case "3":
				imagePath = "/data/sprites/combat/blade_strike_light.png";
				break;
			case "4":
				imagePath = "/data/sprites/combat/blade_strike_dark.png";
			}
			this.attackSprite = (function(imagePath, ranged, dir, width, height) {
				return {
					initTime: Date.now(),
					image: stendhal.data.sprites.get(imagePath),
					frame: 0,
					expired: function() {
						return Date.now() - this.initTime > 180;
					},
					draw: function(ctx, x, y, entityWidth, entityHeight) {
						if (!this.image.complete) {
							return;
						}
						var yRow = dir - 1;
						var drawHeight = this.image.height / 4;
						var drawWidth = this.image.width / 3;
						var dtime = Date.now() - this.initTime;
						var frame = Math.floor(Math.min(dtime / 60, 3));
						var centerX = x + (entityWidth - drawWidth) / 2;
						var centerY = y + (entityHeight - drawHeight) / 2;
						ctx.drawImage(this.image, frame * drawWidth, yRow * drawHeight,
								drawWidth, drawHeight, centerX, centerY, drawWidth, drawHeight);
					}
				};
			})(imagePath, ranged, this.dir);
		}
	},
	
	/**
	 * Called when this entity is selected as the attack target.
	 * 
	 * @param attacked The entity that selected this as the target
	 */
	onTargeted: function(attacker) {
		if (!this.attackers) {
			this.attackers = { size: 0 };
		}
		if (!(attacker.id in this.attackers)) {
			this.attackers[attacker.id] = true;
			this.attackers.size += 1;
		}
	},
	
	/**
	 * Called when an entity deselects this as its attack target.
	 * 
	 * @param attacker The entity that had this as the attack target, but
	 * 	stopped attacking
	 */
	onAttackStopped: function(attacker) {
		if (attacker.id in this.attackers) {
			delete this.attackers[attacker.id];
			this.attackers.size -= 1;
		}
	},
	
	destroy: function(obj) {
		if (this._target) {
			this._target.onAttackStopped(this);
		}
	}
});

})();