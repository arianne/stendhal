/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { MenuItem } from "../action/MenuItem";
import { Chat } from "../util/Chat";
import { RPObject, RPZone } from "marauroa";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";
import { ImageSprite } from "../sprite/image/ImageSprite";

/**
 * General entity
 */

export class Entity extends RPObject {

	minimapShow = false;
	minimapStyle = "rgb(200,255,200)";
	zIndex = 10000;
	imageSprite?: ImageSprite;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === 'name') {
			if (typeof(this['title']) === "undefined") {
				this['title'] = value;
			}
			this["_name"] = value;
		} else if (['x', 'y', 'height', 'width'].indexOf(key) > -1) {
			this[key] = parseInt(value, 10);
		} else {
			this[key] = value;
		}
	}

	/**
	 * is the other entity next to this entity?
	 *
	 * @return true, if the other entity is right next to us; false otherwise
	 */
	isNextTo(other: Entity) {
		if (!other || !this["x"] || !this["y"] || !other["x"] || !other["y"]) {
			return false;
		}

		var nextX = ((this["x"] + this["width"] >= other["x"]) && this["x"] <= other["x"])
				|| ((other["x"] + other["width"] >= this["x"]) && other["x"] <= this["x"]);
		if (!nextX) {
			return false;
		}

		var nextY = ((this["y"] + this["height"] >= other["y"]) && this["y"] <= other["y"])
			|| ((other["y"] + other["height"] >= this["y"]) && other["y"] <= this["y"]);
		return nextY;
	}

	/**
	 * Finds distance to another object on the X axis.
	 *
	 * @param other
	 *     The object to measure against.
	 * @return
	 *     Distance in steps or -1.
	 */
	private getXDistanceTo(other: RPObject): number {
		if (other && this["x"] && other["x"]) {
			const tx_right = this["x"] + (this["width"] || 1) - 1;
			const ox_right = other["x"] + (other["width"] || 1) - 1;

			if (this["x"] > ox_right) {
				return Math.abs(this["x"] - ox_right);
			} else if (other["x"] > tx_right) {
				return Math.abs(other["x"] - tx_right);
			}

			return 0;
		}

		return -1;
	}

	/**
	 * Finds distance to another object on the Y axis.
	 *
	 * @param other
	 *     The object to measure against.
	 * @return
	 *     Distance in steps or -1.
	 */
	private getYDistanceTo(other: RPObject): number {
		if (other && this["y"] && other["y"]) {
			const ty_bottom = this["y"] + (this["height"] || 1) - 1;
			const oy_bottom = other["y"] + (other["height"] || 1) -1;

			if (this["y"] > oy_bottom) {
				return Math.abs(this["y"] - oy_bottom);
			} else if (other["y"] > ty_bottom) {
				return Math.abs(other["y"] - ty_bottom);
			}

			return 0;
		}

		return -1;
	}

	/**
	 * Finds the combined distance on the X & Y axi to
	 * another object.
	 *
	 * @param other
	 *     The object to measure against.
	 * @return
	 *     Distance in steps or -1.
	 */
	public getDistanceTo(other: RPObject): number {
		let x_dist = this.getXDistanceTo(other);
		let y_dist = this.getYDistanceTo(other);

		if (x_dist < 0 && y_dist < 0) {
			return -1;
		}

		x_dist = x_dist > -1 ? x_dist : 0;
		y_dist = y_dist > -1 ? y_dist : 0;

		return x_dist + y_dist;
	}

	/**
	 * is this entity visible to a specific action
	 *
	 * @param filter 0: short left click
	 * @return true, if the entity is visible, false otherwise
	 */
	isVisibleToAction(filter: boolean) {
		return false;
	}

	/**
 	 * Map descriptive command names to the real commands
	 */
	actionAliasToAction(actionAlias: string) {
		var actionAliases: {
			[key: string]: any;
		}  = {
			"look_closely" : "use",
			"read" : "look"
		};

		var actionCommand = "look";
		if (typeof(actionAlias) === "string") {
			if (actionAliases.hasOwnProperty(actionAlias)) {
				actionCommand = actionAliases[actionAlias];
			} else {
				actionCommand = actionAlias;
			}
		}
		return actionCommand;
	}

	buildActions(list: MenuItem[]) {
		// menu is an alias for "Use" command
		if (this["menu"]) {
			var pos = this["menu"].indexOf("|");
			list.push({
				title: this["menu"].substring(0, pos),
				type: this["menu"].substring(pos + 1).toLowerCase()
			});
		}

		// action replaces "Look" command (e. g. "look closely" on signs)
		if (this["action"]) {
			list.push({
				title: stendhal.ui.html.niceName(this["action"]),
				type: this.actionAliasToAction(this["action"])
			});
		} else {
			list.push({
				title: "Look",
				type: "look"
			});
		}
	}

	public onMiniMapDraw() {
		// do nothing
	}

	/**
	 *  Ensure that the drawing code can rely on _x and _y
	 */
	updatePosition(time: number) {
		// The position of non active entities can change too, so always copy
		// the position
		this["_y"] = this["y"];
		this["_x"] = this["x"];
	}

	draw(ctx: RenderingContext2D) {
		if (this.sprite || this.imageSprite) {
			this.drawSprite(ctx);
		}
	}

	/**
	 * draws a standard sprite
	 */
	drawSprite(ctx: RenderingContext2D) {
		this.drawSpriteAt(ctx, this["x"] * 32, this["y"] * 32);
	}

	drawSpriteAt(ctx: RenderingContext2D, x: number, y: number) {
		if (this.imageSprite) {
			this.imageSprite.drawOnto(ctx, x, y, this.getWidth() * 32, this.getHeight() * 32);
		} else {
			let image = singletons.getSpriteStore().get(this.sprite.filename);
			if (image.height) {
				let offsetX = this.sprite.offsetX || 0;
				let offsetY = this.sprite.offsetY || 0;
				let width = this.sprite.width || image.width;
				let height = this.sprite.height || image.height;

				// use entity dimensions to center sprite
				x += Math.floor((this.getWidth() * 32 - width) / 2);
				y += Math.floor((this.getHeight() * 32 - height) / 2);
	
				ctx.drawImage(image, offsetX, offsetY, width, height, x, y, width, height);
			}
		}
	}

	/**
	 * gets the container path identifying the item
	 *
	 * @returns
	 */
	getIdPath() {
		var object = this;
		var res = "";
		while (object) {
			res = object["id"] + "\t" + res;
			var slot = object._parent;
			if (!slot) {
				break;
			}
			res = slot._name + "\t" + res;
			object = slot._parent;
		}
		return "[" + res.substr(0, res.length - 1) + "]";
	}

	/**
	 * says a text
	 *
	 * @param text
	 *     Message contents.
	 * @param rangeSquared
	 *     Distance squared within which the entity can be heard (-1
	 *     represents entire map).
	 */
	say(text: string, rangeSquared?: number) {
		if (marauroa.me && marauroa.me.isInHearingRange(this, rangeSquared)) {
			Chat.log("normal", text);
		}
	}

	getCursor(x: number, y: number) {
		var cursor = "unknown";
		if (this["cursor"]) {
			cursor = this["cursor"];
		}

		return "url(" + Paths.sprites + "/cursor/" + cursor.toLowerCase().replace("_", "") + ".png) 1 3, auto";
	}

	/**
	 * Create the default action for this entity. If the entity specifies a
	 * default action description, interpret it as an action command.
	 */
	getDefaultAction() {
		return {
			"type": this.actionAliasToAction(this["action"]),
			"target": "#" + this["id"],
			"zone": marauroa.currentZoneName
		};
	}

	getResistance() {
		return this["resistance"];
	}

	isObstacle(entity: Entity) {
		return ((entity != this)
			&& (this.getResistance() * (entity.getResistance() / 100) > 95));
	}

	onclick(x: number, y: number) {
		const action = this.getDefaultAction();
		if (action.type === "moveto") {
			// call `User.moveTo` so viewport can be unfrozen
			marauroa.me.moveTo(action);
			return;
		}
		marauroa.clientFramework.sendAction(action);
	}

	/**
	 * Checks if this is an entity that can be moved via
	 * drag-and-drop, such as a corpse or an item.
	 */
	public isDraggable(): boolean {
		return false;
	}

	/**
	 * Checks if within vieport area.
	 */
	public inView(): boolean {
		const view_rect: any = {};
		view_rect.left = stendhal.ui.gamewindow.offsetX;
		view_rect.right = view_rect.left + stendhal.ui.gamewindow.width;
		view_rect.top = stendhal.ui.gamewindow.offsetY;
		view_rect.bottom = view_rect.top + stendhal.ui.gamewindow.height;

		const pixelX = this["x"] * stendhal.ui.gamewindow.targetTileWidth
				+ Math.floor(stendhal.ui.gamewindow.targetTileWidth / 2);
		const pixelY = this["y"] * stendhal.ui.gamewindow.targetTileHeight
				+ Math.floor(stendhal.ui.gamewindow.targetTileHeight / 2);

		const ent_rect: any = {};
		// horizontal orientation is centered
		ent_rect.left = pixelX - (this["drawWidth"] / 2);
		ent_rect.right = pixelX + (this["drawWidth"] / 2);
		// FIXME: vertical orientation should be offset using entity height & sprite height?
		ent_rect.bottom = pixelY;
		ent_rect.top = ent_rect.bottom - this["drawHeight"];

		return ent_rect.right > view_rect.left && ent_rect.left < view_rect.right
				&& ent_rect.bottom > view_rect.top && ent_rect.top < view_rect.bottom
	}

	override destroy(_parent: RPObject|RPZone) {
		this.imageSprite?.free();
	}
}
