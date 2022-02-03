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

import { Entity } from "./Entity";

declare var marauroa: any;


export class Item extends Entity {

	override minimapShow = false;
	override minimapStyle = "rgb(0,255,0)";
	override zIndex = 7000;

	constructor() {
		super();
		this.sprite = {
			height: 32,
			width: 32
		};
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	// default action for items on the ground is to pick them up
	// do not require players to use drag & drop (as the classic client did)
	// because drag & drop is difficult on mobile (especially if the page is zoomed)
	override getDefaultAction() {
		return {
			type: "equip",
			"source_path": this.getIdPath(),
			"target_path": "[" + marauroa.me["id"] + "\tbag]",
			"clicked": "", // useful for changing default target in equip action
			"zone": marauroa.currentZoneName
		} as any;
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "class" || key === "subclass") {
			this.sprite.filename = "/data/sprites/items/"
				+ this["class"] + "/" + this["subclass"] + ".png";
		}
	}

	override draw(ctx: CanvasRenderingContext2D) {
		this.drawAt(ctx, this["x"] * 32, this["y"] * 32);
	}

	drawAt(ctx: CanvasRenderingContext2D, x: number, y: number) {
		if (this.sprite) {
			this.drawSpriteAt(ctx, x, y);
			var text = this.formatQuantity();
			var textMetrics = ctx.measureText(text);
			this.drawOutlineText(ctx, text, "white", x + (32 - textMetrics.width) / 2, y + 6);
		}
	}

	formatQuantity() {
		if (!this["quantity"] || this["quantity"] === "1") {
			return "";
		}
		if (this["quantity"] > 10000000) {
			return Math.floor(this["quantity"] / 1000000) + "m";
		}
		if (this["quantity"] > 10000) {
			return Math.floor(this["quantity"] / 1000) + "k";
		}
		return this["quantity"];
	}

	override getCursor(_x: number, _y: number) {
		return "url(/data/sprites/cursor/itempickupfromslot.png) 1 3, auto";
	}

}
