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
import { Paths } from "../data/Paths";
import { TextSprite } from "../sprite/TextSprite";
import { Entity } from "./Entity";
import { ItemMap } from "./ItemMap";

import { htmlImageStore } from "data/HTMLImageStore";
import { marauroa } from "marauroa";
import { ImageSprite } from "sprite/image/ImageSprite";
import { images } from "sprite/image/ImageManager";

export class Item extends Entity {

	override minimapShow = false;
	override minimapStyle = "rgb(0,255,0)";
	override zIndex = 7000;
	private quantityTextSprite: TextSprite;

	// animation
	private frameTimeStamp = 0;
	private animated: boolean|null = null;


	constructor() {
		super();
		this.quantityTextSprite = new TextSprite("", "white", "10px sans-serif");
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);

		for (const mi of ItemMap.getActions(this)) {
			if (typeof(mi.index) === "number") {
				list.splice(mi.index, 0, mi);
			} else {
				list.push(mi);
			}
		}
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
			if (this["class"] && this["subclass"]) {
				this.imageSprite?.free();
				this.imageSprite = new ImageSprite(
					images.load(Paths.sprites + "/items/" + this["class"] + "/" + this["subclass"] + ".png"),
					0, (this["state"] || 0) * 32, 32, 32);
			}
		} else if (key === "state") {
			if (this.imageSprite) {
				this.imageSprite.offsetY = (this["state"] || 0) * 32
			}
	    } else if (key === "quantity") {
			this.quantityTextSprite = new TextSprite(this.formatQuantity(), "white", "10px sans-serif");
		}
	}

	override draw(ctx: RenderingContext2D) {
		this.stepAnimation();
		this.drawAt(ctx, this["x"] * 32, this["y"] * 32);
	}

	drawAt(ctx: RenderingContext2D, x: number, y: number) {
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}
		this.drawSpriteAt(ctx, x, y);
		let textMetrics = this.quantityTextSprite.getTextMetrics(ctx);
		if (!textMetrics) {
			throw new Error("textMetrics is undefined");
		}
		this.quantityTextSprite.draw(ctx, x + (32 - textMetrics.width), y + 6);
	}

	public stepAnimation() {
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}
		const currentTimeStamp = +new Date();
//		if (this.frameTimeStamp == 0) {
//			this.frameTimeStamp = currentTimeStamp;
//			this.imageSprite?.offsetX = 0;
//		}
		if (currentTimeStamp - this.frameTimeStamp >= 100) {
			// FIXME: need proper FPS limit
			this.setXFrameIndex(this.getXFrameIndex() + 1);
			this.frameTimeStamp = currentTimeStamp;
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
		let cursor;
		if (!this._parent) {
			cursor = "itempickupfromslot";
		} else {
			cursor = ItemMap.getCursor(this["class"], this["name"]);
		}
		return "url(" + Paths.sprites + "/cursor/" + cursor + ".png) 1 3, auto";
	}

	public getToolTip(): string {
		if (this["class"] === "scroll" && this["dest"]) {
			const dest = this["dest"].split(",");
			if (dest.length > 2) {
				return dest[0] + " " + dest[1] + "," + dest[2];
			}
		}
		return "";
	}

	public isAnimated(): boolean {
		if (this.animated === null) {
			let image = this.imageSprite?.imageRef?.image
			if (image) {
				this.animated = (image.width / 32) > 1;
			}
		}
		return this.animated || false;
	}

	private setXFrameIndex(idx: number) {
		let image = this.imageSprite?.imageRef.image
		if (!image) {
			return;
		}
		let frames = image.width / 32;
		if (idx >= frames) {
			// restart
			idx = 0;
		}

		this.imageSprite!.offsetX = idx * 32;
	}

	public getXFrameIndex(): number {
		return (this.imageSprite?.offsetX || 0) / 32;
	}

	public override isDraggable(): boolean {
		return true;
	}
}
