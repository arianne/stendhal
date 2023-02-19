/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ActiveEntity } from "./ActiveEntity";
import { Entity } from "./Entity";
import { singletons } from "../SingletonRepo";
import { MenuItem } from "../action/MenuItem";
import { Chat } from "../util/Chat";
import { Color } from "../util/Color";
import { Nature } from "../util/Nature";
import { Floater } from "../sprite/Floater";
import { EmojiSprite } from "../sprite/EmojiSprite";
import { SpeechBubble } from "../sprite/SpeechBubble";
import { TextSprite } from "../sprite/TextSprite";
import { SoundManager } from "../ui/SoundManager";

declare var marauroa: any;
declare var stendhal: any;

const emojiStore = singletons.getEmojiStore();

var HEALTH_BAR_HEIGHT = 6;


export class RPEntity extends ActiveEntity {

	private static readonly soundManager = SoundManager.get();

	override zIndex = 8000;
	drawY = 0;
	spritePath = "";
	titleStyle = "#FFFFFF";
	_target?: RPEntity;
	attackSprite: any = undefined; // TODO
	attackResult: any = undefined; // TODO
	dir = 3;
	titleTextSprite?: TextSprite;
	floaters: any[] = [];
	// for keeping hp bar & title on screen
	protected statusBarYOffset: number = 0;
	// for adjusting entity hp bar & title vertical position
	protected titleDrawYOffset: number = 0;
	// canvas for merging outfit layers to be drawn
	private octx?: CanvasRenderingContext2D;

	private attackers: {[key: string]: any} = { size: 0 };


	override set(key: string, value: any) {
		// Ugly hack to detect changes. The old value is no
		// longer available after super.set()
		let oldValue = this[key];

		super.set(key, value);
		if (key == "text") {
			this.say(value);
		} else if (["hp", "base_hp"].indexOf(key) !== -1) {
			this[key] = parseInt(value, 10);
			if (key === "hp" && oldValue != undefined) {
				this.onHPChanged(this[key] - oldValue);
			}
		} else if (key === "id" && !oldValue && this._target) {
			// update list of attackers since id was not set when entity was targeted
			this._target.onTargeted(this);
		} else if (key === "target" && this["id"]) {
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
		} else if (key === "xp" && typeof(oldValue) !== "undefined") {
			this.onXPChanged(this[key] - oldValue);
		} else if (["level", "atk", "ratk", "def"].indexOf(key) > -1
				&& typeof(oldValue) !== "undefined") {
			// FIXME: xp change should be printed before level
			this.onLevelChanged(key, value, oldValue);
		} else if (["title", "name", "class", "type"].indexOf(key) >-1) {
			this.createTitleTextSprite();
		}
	}

	override unset(key: string) {
		if (key === "target" && this._target) {
			this._target.onAttackStopped(this);
			this._target = undefined;
		} else if (key === "away") {
			this.addFloater("Back", "#ffff00");
		} else if (key === "grumpy") {
			this.addFloater("Receptive", "#ffff00");
		}
		super.unset(key);
	}

	override isVisibleToAction(_filter: boolean) {
		return typeof(this["ghostmode"]) == "undefined" || marauroa.me && marauroa.me.isAdmin();
	}

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);
		/*
		 * Menu is used to provide an alternate action for some entities (like
		 * puppies - and they should not be attackable).
		 *
		 * For now normally attackable entities get a menu only in Capture The
		 * Flag, and then they don't need attack. If that changes, this code
		 * will need to be adjusted.
		 */
		if (!this["menu"]) {
			if (marauroa.me._target === this) {
				list.push({
					title: "Stop attack",
					action: function(_entity: Entity) {
						var action = {
							"type": "stop",
							"zone": marauroa.currentZoneName,
							"attack": ""
						};
						marauroa.clientFramework.sendAction(action);
					}
				});
			} else if (this !== marauroa.me) {
				list.push({
					title: "Attack",
					type: "attack"
				});
			}
		}
		if (this != marauroa.me) {
			list.push({
				title: "Push",
				type: "push"
			});
		}
	}

	/**
	 * retrieves the entity's visible title
	 */
	getTitle() {
		return this["title"];
	}

	/**
	 * says a text
	 *
	 * @param text
	 *     Message contents.
	 * @param rangeSquared
	 *     Distance at which message can be heard (-1 represents
	 *     entire map).
	 */
	override say(text: string, rangeSquared?: number) {
		if (!marauroa.me) {
			return;
		}

		if (marauroa.me.isInHearingRange(this, rangeSquared)) {
			let emoji = emojiStore.create(text);
			if (emoji) {
				this.addEmoji(emoji);
				Chat.log("emoji", emoji, this.getTitle());
			} else if (text.startsWith("!me")) {
				Chat.log("emote", text.replace(/^!me/, this.getTitle()));
			} else {
				this.addSpeechBubble(text);
				Chat.log("normal", text, this.getTitle());
			}
		}
	}

	/**
	 * Displays a speech bubble attached to an entity.
	 *
	 * @param text
	 *     Text to display.
	 */
	addSpeechBubble(text: string) {
		stendhal.ui.gamewindow.addTextSprite(new SpeechBubble(text, this));
	}

	addEmoji(emoji: HTMLImageElement) {
		stendhal.ui.gamewindow.addEmojiSprite(new EmojiSprite(emoji, this));
	}

	/**
	 * Checks if the entity should cast a shadow.
	 */
	public castsShadow(): boolean {
		return typeof(this["no_shadow"]) === "undefined";
	}

	drawMultipartOutfit(ctx: CanvasRenderingContext2D) {
		// layers in draw order
		var layers: string[] = [];

		var outfit: {[key: string]: number} = {};
		if ("outfit_ext" in this) {
			layers = ["body", "dress", "head", "mouth", "eyes", "mask", "hair", "hat", "detail"];

			for (const part of this["outfit_ext"].split(",")) {
				if (part.includes("=")) {
					var tmp = part.split("=");
					outfit[tmp[0]] = parseInt(tmp[1], 10);
				}
			}
		} else {
			layers = ["body", "dress", "head", "hair", "detail"];

			outfit["body"] = this["outfit"] % 100;
			outfit["dress"] = Math.floor(this["outfit"]/100) % 100;
			outfit["head"] = Math.floor(this["outfit"]/10000) % 100;
			outfit["hair"] = Math.floor(this["outfit"]/1000000) % 100;
			outfit["detail"] = Math.floor(this["outfit"]/100000000) % 100;
		}

		if (stendhal.config.getBoolean("gamescreen.shadows") && this.castsShadow()) {
			// dressed entities should use 48x64 sprites
			// FIXME: this will not display correctly for horse outfit
			const shadow = stendhal.data.sprites.getShadow("48x64");

			if (shadow && shadow.complete && shadow.height) {
				// draw shadow below other layers
				this.drawSpriteImage(ctx, shadow);
			}
		}

		if (this.octx) {
			this.octx.clearRect(0, 0, this.octx.canvas.width, this.octx.canvas.height);
		}
		if (stendhal.data.outfit.detailHasRearLayer(outfit["detail"])) {
			layers.splice(0, 0, "detail-rear");
			outfit["detail-rear"] = outfit["detail"];
		}
		for (const layer of layers) {
			// hair is not drawn under certain hats/helmets
			if (layer == "hair" && !stendhal.data.outfit.drawHair(outfit["hat"])) {
				continue;
			}

			const lsprite = this.getOutfitPart(layer, outfit[layer], outfit["body"]);
			if (lsprite && lsprite.complete && lsprite.height) {
				if (!this.octx) {
					let ocanvas = document.createElement("canvas");
					this.octx = ocanvas.getContext("2d")!;
					ocanvas.width = lsprite.width;
					ocanvas.height = lsprite.height;
				}
				this.octx!.drawImage(lsprite, 0, 0);
			}
		}

		if (this.octx) {
			this.drawSpriteImage(ctx, this.octx.canvas);
		}
	}

	/**
	 * Get an outfit part (Image or a Promise)
	 *
	 * @param {string}  part
	 * @param {number} index
	 * @param {number} body
	 */
	getOutfitPart(part: string, index: number, body: number) {
		if (typeof(index) === "undefined" || index < 0) {
			return null;
		}

		let n = "" + index;
		if (index < 10) {
			n = "00" + index;
		} else if(index < 100) {
			n = "0" + index;
		}

		if (part === "body" && index < 3 && stendhal.config.getBoolean("gamescreen.nonude")) {
			n += "-nonude";
		} else if (part === "dress" && stendhal.data.outfit.drawBustyDress(index, body)) {
			n += "b";
		} else if (part.endsWith("-rear")) {
			n += "-rear";
			part = part.replace(/-rear$/, "");
		}

		const filename = stendhal.paths.sprites + "/outfit/" + part + "/" + n + ".png";
		const colors = this["outfit_colors"];
		let colorname;
		if (part === "body" || part === "head") {
			colorname = "skin";
		} else {
			colorname = part;
		}
		if (typeof(colors) !== "undefined" && (typeof(colors[colorname]) !== "undefined")) {
			return stendhal.data.sprites.getFiltered(filename, "trueColor", colors[colorname]);
		} else {
			return stendhal.data.sprites.get(filename);
		}
	}

	/**
	 * Sets the offset to keep text & health bar on screen when sprite
	 * extends past top edge.
	 */
	public setStatusBarOffset() {
		const screenOffsetY = stendhal.ui.gamewindow.offsetY;
		const entityBottom = (this["_y"] * 32) + (this["height"] * 32);
		// FIXME: how to get text height dynamically?
		const entityTop = entityBottom - this["drawHeight"]
				- HEALTH_BAR_HEIGHT - 26;

		if (screenOffsetY > entityTop && screenOffsetY < entityBottom) {
			this.statusBarYOffset = screenOffsetY - entityTop;
		} else {
			this.statusBarYOffset = 0;
		}
	}

	/**
	 * draw RPEntities
	 */
	override draw(ctx: CanvasRenderingContext2D) {
		if (typeof(this["ghostmode"]) != "undefined" && marauroa.me && !marauroa.me.isAdmin()) {
			return;
		}
		this.drawCombat(ctx);
		this.drawMain(ctx);
		this.drawAttack(ctx);
		this.drawStatusIcons(ctx);
	}

	drawMain(ctx: CanvasRenderingContext2D) {
		let filename;
		if (typeof(this["outfit"]) != "undefined" || typeof(this["outfit_ext"]) != "undefined") {
			this.drawMultipartOutfit(ctx);
		} else {
			filename = stendhal.paths.sprites + "/" + this.spritePath + "/" + this["class"];
			if (typeof(this["subclass"]) != "undefined") {
				filename = filename + "/" + this["subclass"];
			}

			// check for safe image
			if (!stendhal.config.getBoolean("gamescreen.blood") && stendhal.data.sprites.hasSafeImage(filename)) {
				filename = filename + "-safe.png";
			} else {
				filename = filename + ".png";
			}

			let image = stendhal.data.sprites.get(filename);

			if (stendhal.config.getBoolean("gamescreen.shadows") && this.castsShadow()) {
				// check for configured shadow style
				let shadow_style = this["shadow_style"];
				if (typeof(shadow_style) === "undefined") {
					// default to sprite dimensions
					shadow_style = (image.width / 3) + "x" + (image.height / 4);
				}

				const shadow = stendhal.data.sprites.getShadow(shadow_style);

				// draw shadow first
				if (typeof(shadow) !== "undefined") {
					this.drawSpriteImage(ctx, shadow);
				}
			}

			this.drawSpriteImage(ctx, image);
		}
	}

	drawStatusIcons(ctx: CanvasRenderingContext2D) {

		function _drawAnimatedIcon(icon: CanvasImageSource, delay: number, nFrames: number, xdim: number, ydim: number, x: number, y: number) {
			var frame = Math.floor(Date.now() / delay) % nFrames;
			ctx.drawImage(icon, frame * xdim, 0, xdim, ydim, x, y, xdim, ydim);
		}
		function drawAnimatedIcon(iconPath: string, delay: number, x: number, y: number) {
			var icon = stendhal.data.sprites.get(iconPath);
			var dim = icon.height;
			var nFrames = icon.width / dim;
			_drawAnimatedIcon(icon, delay, nFrames, dim, dim, x, y);
		}
		function drawAnimatedIconWithFrames(iconPath: string, nFrames: number, delay: number, x: number, y: number) {
			var icon = stendhal.data.sprites.get(iconPath);
			var ydim = icon.height;
			var xdim = icon.width / nFrames;
			_drawAnimatedIcon(icon, delay, nFrames, xdim, ydim, x, y);
		}

		var x = this["_x"] * 32 - 10;
		var y = (this["_y"] + 1) * 32;
		if (this.hasOwnProperty("choking")) {
			ctx.drawImage(stendhal.data.sprites.get(stendhal.paths.sprites + "/ideas/choking.png"), x, y - 10);
		} else if (this.hasOwnProperty("eating")) {
			ctx.drawImage(stendhal.data.sprites.get(stendhal.paths.sprites + "/ideas/eat.png"), x, y - 10);
		}
		// NPC and pet idea icons
		if (this.hasOwnProperty("idea")) {
			const idea = stendhal.paths.sprites + "/ideas/" + this["idea"] + ".png";
			const ani = stendhal.data.sprites.animations.idea[this["idea"]];
			if (ani) {
				drawAnimatedIcon(idea, ani.delay, x + ani.offsetX * this["width"],
						y - this["drawHeight"] + ani.offsetY);
			} else {
				ctx.drawImage(stendhal.data.sprites.get(idea), x + 32 * this["width"],
						y - this["drawHeight"]);
			}
		}
		if (this.hasOwnProperty("away")) {
			drawAnimatedIcon(stendhal.paths.sprites + "/ideas/away.png", 1500, x + 32 * this["width"], y - this["drawHeight"]);
		}
		if (this.hasOwnProperty("grumpy")) {
			drawAnimatedIcon(stendhal.paths.sprites + "/ideas/grumpy.png", 1000, x + 5, y - this["drawHeight"]);
		}
		if (this.hasOwnProperty("last_player_kill_time")) {
			drawAnimatedIconWithFrames(stendhal.paths.sprites + "/ideas/pk.png", 12, 300, x, y - this["drawHeight"]);
		}
		if (this.hasOwnProperty("poisoned")) {
			drawAnimatedIcon(stendhal.paths.sprites + "/status/poison.png", 100, x + 32 * this["width"] - 10, y - this["drawHeight"]);
		}
		// NPC job icons
		let nextX = x;
		if (this.hasOwnProperty("job_healer")) {
			ctx.drawImage(stendhal.data.sprites.get(stendhal.paths.sprites + "/status/healer.png"), nextX, y - 10);
			nextX += 12;
		}
		if (this.hasOwnProperty("job_merchant")) {
			ctx.drawImage(stendhal.data.sprites.get(stendhal.paths.sprites + "/status/merchant.png"), nextX, y - 10);
			nextX += 12;
		}
		if (this.hasOwnProperty("job_producer")) {
			ctx.drawImage(stendhal.data.sprites.get(stendhal.paths.sprites + "/status/producer.png"), nextX, y - 16);
			nextX += 16;
		}
	}

	/**
	 * Draw colored ellipses (or rectangles on browsers that do not support
	 * ellipses) when the entity is being attacked, or is attacking the user.
	 */
	drawCombat(ctx: CanvasRenderingContext2D) {
		if (this.attackers.size > 0) {
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
				var xRad = this["width"] * 16;
				var yRad = this["height"] * 16 / Math.SQRT2;
				var centerX = this["_x"] * 32 + xRad;
				var centerY = (this["_y"] + this["height"]) * 32 - yRad;
				ctx.strokeStyle = "#4a0000";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, 0, Math.PI, false);
				ctx.stroke();
				ctx.strokeStyle = "#e60a0a";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, Math.PI, 2 * Math.PI, false);
				ctx.stroke();
			} else {
				ctx.strokeStyle = "#e60a0a";
				ctx.strokeRect(32 * this["_x"], 32 * this["_y"], 32 * this["width"], 32 * this["height"]);
			}
		}
		if (this.getAttackTarget() === marauroa.me) {
			ctx.lineWidth = 1;
			// See above about ellipses.
			if (ctx.ellipse instanceof Function) {
				var xRad = this["width"] * 16 - 1;
				var yRad = this["height"] * 16 / Math.SQRT2 - 1;
				var centerX = this["_x"] * 32 + xRad + 1;
				var centerY = (this["_y"] + this["height"]) * 32 - yRad - 1;
				ctx.strokeStyle = "#ffc800";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, 0, Math.PI, false);
				ctx.stroke();
				ctx.strokeStyle = "#ffdd0a";
				ctx.beginPath();
				ctx.ellipse(centerX, centerY, xRad, yRad, 0, Math.PI, 2 * Math.PI, false);
				ctx.stroke();
			} else {
				ctx.strokeStyle = "#ffdd0a";
				ctx.strokeRect(32 * this["_x"] + 1, 32 * this["_y"] + 1, 32 * this["width"] - 2, 32 * this["height"] - 2);
			}
		}
		if (this.attackResult) {
			if (this.attackResult.draw(ctx, (this["_x"] + this["width"]) * 32 - 10, (this["_y"] + this["height"]) * 32 - 10)) {
				this.attackResult = undefined;
			}
		}
	}

	/**
	 * Draw entities in this.floaters array. Each floater should have
	 * draw(ctx, x, y) method, where ctx is the canvas context, and x, y are
	 * the coordinates where to start floating. The method should return true
	 * when the floater should be removed.
	 */
	drawFloaters(ctx: CanvasRenderingContext2D) {
		var centerX = (this["_x"] + this["width"] / 2) * 32;
		var topY = (this["_y"] + 1) * 32 - this["drawHeight"];
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

	/**
	 * @param {CanvasRenderingContext2D} ctx
	 * @param {Image} image
	 */
	drawSpriteImage(ctx: CanvasRenderingContext2D, image: CanvasImageSource) {
		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		if (image.height) { // image.complete is true on missing image files
			var nFrames = 3;
			var nDirections = 4;
			var yRow = this["dir"] - 1;
			var frame = 1; // draw center column when idle
			// Ents are a hack in Java client too
			if (this["class"] == "ent") {
				nFrames = 1;
				nDirections = 2;
				yRow = Math.floor((this["dir"] - 1) / 2);
				frame = 0;
			}
			this["drawHeight"] = image.height as number / nDirections;
			this["drawWidth"] = image.width as number / nFrames;
			var drawX = ((this["width"] * 32) - this["drawWidth"]) / 2;
			if ((this["speed"] > 0 || this.hasOwnProperty("active_idle")) && nFrames != 1) {
				var animLength = nFrames * 2 - 2;
				// % Works normally with *floats* (just whose bright idea was
				// that?), so use floor() as a workaround
				frame = Math.floor(Date.now() / 100) % animLength;
				if (frame >= nFrames) {
					frame = animLength - frame;
				}
			}
			var drawY = (this["height"] * 32) - this["drawHeight"];

			let opacity = parseInt(this["visibility"], 10);
			opacity = isNaN(opacity) ? 100 : opacity;
			if (this.hasOwnProperty("ghostmode") && this === marauroa.me && opacity > 50) {
				opacity = 50;
			}
			const opacity_orig = ctx.globalAlpha;
			if (opacity < 100) {
				ctx.globalAlpha = opacity * 0.01;
			}

			ctx.drawImage(image, frame * this["drawWidth"], yRow * this["drawHeight"], this["drawWidth"], this["drawHeight"], localX + drawX, localY + drawY, this["drawWidth"], this["drawHeight"]);
			// restore opacity
			ctx.globalAlpha = opacity_orig;
		}
	}

	drawTop(ctx: CanvasRenderingContext2D) {
		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		this.drawFloaters(ctx);
		this.drawHealthBar(ctx, localX, localY + this.statusBarYOffset);
		this.drawTitle(ctx, localX, localY + this.statusBarYOffset);
	}

	drawHealthBar(ctx: CanvasRenderingContext2D, x: number, y: number) {
		var drawX = x + ((this["width"] * 32) - this["drawWidth"]) / 2;
		var drawY = y + (this["height"] * 32) - this["drawHeight"]
				- HEALTH_BAR_HEIGHT + this.titleDrawYOffset;

		ctx.strokeStyle = "#000000";
		ctx.lineWidth = 2;
		ctx.beginPath();
		ctx.rect(drawX, drawY, this["drawWidth"], HEALTH_BAR_HEIGHT - 2);
		ctx.stroke();

		ctx.fillStyle = "#808080"; // same as java.awt.Color.GRAY (rgb(128,128,128))
		ctx.fillRect(drawX, drawY, this["drawWidth"], HEALTH_BAR_HEIGHT - 2);

		// Bar color
		var hpRatio = this["hp"] / this["base_hp"];
		ctx.fillStyle = Color.getStatBarColor(hpRatio);
		ctx.fillRect(drawX, drawY, this["drawWidth"] * hpRatio, HEALTH_BAR_HEIGHT - 2);
	}

	createTitleTextSprite() {
		let title = this.getTitle();
		if (!title) {
			title = this["_name"];
			if (!title) {
				title = this["class"];
				if (!title) {
					title = this["type"];
				}
			}
		}

		if (title) {
			this.titleTextSprite = new TextSprite(title, this.titleStyle, "14px sans-serif");
		}
	}

	drawTitle(ctx: CanvasRenderingContext2D, x: number, y: number) {
		if (this.titleTextSprite) {
			let textMetrics = this.titleTextSprite.getTextMetrics(ctx);
			var drawY = y + (this["height"] * 32) - this["drawHeight"]
					- HEALTH_BAR_HEIGHT + this.titleDrawYOffset;
			this.titleTextSprite.draw(ctx, x + (this["width"] * 32 - textMetrics.width) / 2, drawY - 5 - HEALTH_BAR_HEIGHT);
		}
	}

	drawAttack(ctx: CanvasRenderingContext2D) {
		if (this.attackSprite == null) {
			return;
		}
		if (this.attackSprite.expired()) {
			this.attackSprite = null;
			return;
		}
		var localX = this["_x"] * 32;
		var localY = this["_y"] * 32;
		var localW = this["width"] * stendhal.ui.gamewindow.targetTileWidth;
		var localH = this["height"] * stendhal.ui.gamewindow.targetTileHeight;
		this.attackSprite.draw(ctx, localX, localY, localW, localH);
	}

	// attack handling
	getAttackTarget() {
		// If the attack target id was read before the target was available,
		// _target does not point to the correct entity. Look up the target
		// again, if _target does not exist, but it should.
		if (!this._target && this["target"]) {
			this._target = marauroa.currentZone[this["target"]];
			if (this._target) {
				this._target.onTargeted(this);
			}
		}
		return this._target;
	}

	onDamaged(_source: Entity, damage: number) {
		this.attackResult = this.createResultIcon(stendhal.paths.sprites + "/combat/hitted.png");
		var sounds = ["attack-melee-01", "attack-melee-02", "attack-melee-03", "attack-melee-04", "attack-melee-05", "attack-melee-06", "attack-melee-07"];
		var index = Math.floor(Math.random() * Math.floor(sounds.length));
		RPEntity.soundManager.playLocalizedEffect(this["_x"], this["_y"], 20, 3, sounds[index], 1);
	}

	onBlocked(_source: Entity) {
		this.attackResult = this.createResultIcon(stendhal.paths.sprites + "/combat/blocked.png");
		var sounds = ["clang-metallic-1", "clang-dull-1"];
		var index = Math.floor(Math.random() * Math.floor(sounds.length));
		RPEntity.soundManager.playLocalizedEffect(this["_x"], this["_y"], 20, 3, sounds[index], 1);
	}

	onMissed(_source: Entity) {
		this.attackResult = this.createResultIcon(stendhal.paths.sprites + "/combat/missed.png");
	}

	protected onHPChanged(change: number) {
		if (change > 0) {
			this.addFloater("+" + change, "#00ff00");
		} else if (change < 0) {
			this.addFloater(change.toString(), "#ff0000");
		}
	}

	protected onXPChanged(change: number) {
		if (change > 0) {
			this.addFloater("+" + change, "#4169e1");
			Chat.log("significant_positive", this.getTitle() + " earns " + change + " experience points.");
		} else if (change < 0) {
			this.addFloater(change.toString(), "#ff8f8f");
			Chat.log("significant_negative", this.getTitle() + " loses " + Math.abs(change) + " experience points.");
		}
	}

	protected onLevelChanged(stat: string, newlevel: number, oldlevel: number) {
		if (!marauroa.me || newlevel === oldlevel) {
			return;
		}

		if (marauroa.me.isInHearingRange(this)) {
			let msg = this.getTitle();
			let msgtype = "significant_positive";
			if (newlevel > oldlevel ) {
				msg += " reaches ";
			} else if (newlevel < oldlevel) {
				msg += " drops to ";
				msgtype = "significant_negative";
			}
			msg += stat + " " + newlevel;
			Chat.logH(msgtype, msg);
		}
	}

	addFloater(message: string, color: string) {
		this.floaters.push(new Floater(message, color));
	}

	/**
	 * Create a closure for drawing attack result icons. The resulting object
	 * has a method draw(context, x, y) which returns true when the attack
	 * result has expired and draw() should no longer be called.
	 *
	 * @param imagePath path to the result icon
	 * @return object for drawing the icon
	 */
	createResultIcon(imagePath: string) {
		return {
			initTime: Date.now(),
			image: stendhal.data.sprites.get(imagePath),
			draw: function(ctx: CanvasRenderingContext2D, x: number, y: number) {
				ctx.drawImage(this.image, x, y);
				return (Date.now() - this.initTime > 1200);
			}
		};
	}

	onAttackPerformed(nature: number, ranged: boolean, weapon?: string) {
		const tileW = stendhal.ui.gamewindow.targetTileWidth;
		const tileH = stendhal.ui.gamewindow.targetTileHeight;

		if (ranged) {
			let color = Nature.VALUES[nature].color;
			var tgt = this.getAttackTarget()!;
			this.attackSprite = (function(color, targetX, targetY, dir) {
				return {
					initTime: Date.now(),
					image: stendhal.data.sprites.get(stendhal.paths.sprites + "/combat/ranged.png"),
					expired: function() {
						return Date.now() - this.initTime > 180;
					},
					draw: function(ctx: CanvasRenderingContext2D, x: number, y: number, entityWidth: number, entityHeight: number) {
						// FIXME: alignment with entity is not correct

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

						// draw bow
						if (ranged && weapon === "ranged" && this.image.height) {
							frame = Math.floor(Math.min(dtime / 60, 3));
							const yRow = dir - 1;
							const drawWidth = this.image.width / 3;
							const drawHeight = this.image.height / 4;

							const centerX = x + (entityWidth - drawWidth) / 2;
							const centerY = y + (entityHeight - drawHeight) / 2;

							// offset sprite for facing direction
							let sx, sy;
							switch (dir+"") {
								case "1": // UP
									sx = centerX + (tileW / 2);
									sy = y - (tileH * 1.5);
									break;
								case "3": // DOWN
									sx = centerX;
									sy = y + entityHeight - drawHeight + (tileH / 2);
									break;
								case "4": // LEFT
									sx = x - (tileW / 2);
									sy = centerY - (tileH / 2);
									break;
								case "2": // RIGHT
									sx = x + entityWidth - drawWidth + (tileW / 2);
									sy = centerY; // - ICON_OFFSET; // ICON_OFFSET = 8 in Java client
									break;
								default:
									sx = centerX;
									sy = centerY;
							}

							ctx.drawImage(this.image, frame * drawWidth, yRow * drawHeight,
									drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);
						}
					}
				};
			})(color, (tgt.x + tgt.width / 2) * 32, (tgt.y + tgt.height / 2) * 32, this["dir"]);
		} else {
			if (typeof(weapon) === "undefined") {
				weapon = "blade_strike";
			}
			if (weapon === "blade_strike" && nature == 0) {
				weapon += "_cut";
			}
			const imagePath = Nature.VALUES[nature].getWeaponPath(weapon);

			this.attackSprite = (function(imagePath, _ranged, dir) {
				return {
					initTime: Date.now(),
					image: stendhal.data.sprites.get(imagePath),
					frame: 0,
					barehand: weapon.startsWith("blade_strike"),
					expired: function() {
						return Date.now() - this.initTime > 180;
					},
					draw: function(ctx: CanvasRenderingContext2D, x: number, y: number, entityWidth: number, entityHeight: number) {
						if (!this.image.height) {
							return;
						}

						const dtime = Date.now() - this.initTime;
						const frameIndex = Math.floor(Math.min(dtime / 60, 3));
						let rotation = 0;

						let yRow, frame, drawWidth, drawHeight;
						if (this.barehand) {
							yRow = dir - 1;
							frame = frameIndex;
							drawWidth = this.image.width / 3;
							drawHeight = this.image.height / 4;
						} else {
							yRow = 0;
							frame = 0;
							drawWidth = this.image.width;
							drawHeight = this.image.height;
						}

						var centerX = x + (entityWidth - drawWidth) / 2;
						var centerY = y + (entityHeight - drawHeight) / 2;

						// offset sprite for facing direction
						let sx, sy;
						switch (dir+"") {
							case "1": // UP
								sx = centerX + (tileW / 2);
								sy = y - (tileH * 1.5);
								break;
							case "3": // DOWN
								sx = centerX;
								sy = y + entityHeight - drawHeight + (tileH / 2);
								rotation = 180;
								break;
							case "4": // LEFT
								sx = x - (tileW / 2);
								sy = centerY - (tileH / 2);
								rotation = -90;
								break;
							case "2": // RIGHT
								sx = x + entityWidth - drawWidth + (tileW / 2);
								sy = centerY; // - ICON_OFFSET; // ICON_OFFSET = 8 in Java client
								rotation = 90;
								break;
							default:
								sx = centerX;
								sy = centerY;
						}

						const rotated = !this.barehand && rotation != 0;
						if (rotated) {
							ctx.save();
							// FIXME: rotate correctly for direction & frame
							/*
							ctx.translate(sx + (drawWidth / 2) - stendhal.ui.gamewindow.offsetX,
									sy + (drawHeight / 2) - stendhal.ui.gamewindow.offsetY);
							ctx.rotate(rotation * Math.PI / 180);
							*/
						}

						ctx.drawImage(this.image, frame * drawWidth, yRow * drawHeight,
								drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);

						if (rotated) {
							ctx.restore();
						}
					}
				};
			})(imagePath, ranged, this["dir"]);
		}
	}

	/**
	 * Called when this entity is selected as the attack target.
	 *
	 * @param attacked The entity that selected this as the target
	 */
	onTargeted(attacker: Entity) {
		if (!(attacker["id"] in this.attackers)) {
			this.attackers[attacker["id"]] = true;
			this.attackers.size += 1;
		}
	}

	/**
	 * Called when an entity deselects this as its attack target.
	 *
	 * @param attacker The entity that had this as the attack target, but
	 * 	stopped attacking
	 */
	onAttackStopped(attacker: Entity) {
		if (attacker["id"] in this.attackers) {
			delete this.attackers[attacker["id"]];
			this.attackers.size -= 1;
		}
	}

	override destroy(_obj: Entity) {
		if (this._target) {
			this._target.onAttackStopped(this);
		}
	}
}
