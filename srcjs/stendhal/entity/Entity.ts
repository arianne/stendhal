/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { MenuItem } from "../action/MenuItem";
import { Chat } from "../util/Chat";
import { RPObject } from "./RPObject";

declare var marauroa: any;
declare var stendhal: any;

/**
 * General entity
 */

export class Entity extends RPObject {

	minimapShow = false;
	minimapStyle = "rgb(200,255,200)";
	zIndex = 10000;

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

	/**
	 *  Ensure that the drawing code can rely on _x and _y
	 */
	updatePosition(time: number) {
		// The position of non active entities can change too, so always copy
		// the position
		this["_y"] = this["y"];
		this["_x"] = this["x"];
	}

	draw(ctx: CanvasRenderingContext2D) {
		if (this.sprite) {
			this.drawSprite(ctx);
		}
	}

	/**
	 * draws a standard sprite
	 */
	drawSprite(ctx: CanvasRenderingContext2D) {
		this.drawSpriteAt(ctx, this["x"] * 32, this["y"] * 32);
	}

	drawSpriteAt(ctx: CanvasRenderingContext2D, x: number, y: number) {
		var image = stendhal.data.sprites.get(this.sprite.filename);
		if (image.height) {
			var offsetX = this.sprite.offsetX || 0;
			var offsetY = this.sprite.offsetY || 0;
			var width = this.sprite.width || image.width;
			var height = this.sprite.height || image.height;
			ctx.drawImage(image, offsetX, offsetY, width, height, x, y, width, height);
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
	 */
	say(text: string) {
		if (marauroa.me && marauroa.me.isInHearingRange(this)) {
			Chat.log("normal", text);
		}
	}

	getCursor(x: number, y: number) {
		var cursor = "unknown";
		if (this["cursor"]) {
			cursor = this["cursor"];
		}

		return "url(/data/sprites/cursor/" + cursor.toLowerCase().replace("_", "") + ".png) 1 3, auto";
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
		marauroa.clientFramework.sendAction(this.getDefaultAction());
	}
}
