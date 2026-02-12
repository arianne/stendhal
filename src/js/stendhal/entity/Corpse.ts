/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { CorpseSlot } from "./CorpseSlot";
import { RenderingContext2D } from "util/Types";
import { ActivityIndicatorSprite } from "../sprite/ActivityIndicatorSprite";

import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";
import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { Chat } from "../util/Chat";

import { PopupInventory } from "./PopupInventory";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";

import { marauroa, RPObject, RPZone } from "marauroa"
import { stendhal } from "../stendhal";
import { images } from "sprite/image/ImageManager";
import { ImageSprite } from "sprite/image/ImageSprite";
import { ViewPort } from "ui/ViewPort";

export class Corpse extends PopupInventory {

	override minimapShow = false;
	override zIndex = 5500;
	autoOpenedAlready = false;

	private readonly indicator?: ActivityIndicatorSprite;


	constructor() {
		super();
		if (stendhal.config.getBoolean("activity-indicator")) {
			this.indicator = new ActivityIndicatorSprite();
		}
	}

	override set(key: string, value: any) {
		super.set(key, value);

		let bloodEnabled = stendhal.config.getBoolean("effect.blood");
		if (bloodEnabled && (key === "image")) {
			this.imageSprite?.free();
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/corpse/" + value + ".png"));
		} else if (!bloodEnabled && (key === "harmless_image")) {
			this.imageSprite?.free();
			this.imageSprite = new ImageSprite(
				images.load(Paths.sprites + "/corpse/" + value + ".png"));
		}
	}

	override draw(ctx: RenderingContext2D) {
		let image = this.imageSprite?.imageRef?.image;
		if (!image) {
			return;
		}
		super.draw(ctx);

		if (this.indicator && !this.isEmpty()) {
			let viewPort = ViewPort.get();
			const tileW = viewPort.targetTileWidth;
			const tileH = viewPort.targetTileHeight;
			let width = image.width < tileW ? tileW : image.width;
			let height = image.height < tileH ? tileH : image.height;
			let offsetX = Math.floor((this["width"] * tileW - width) / 2);
			let offsetY = Math.floor((this["height"] * tileH - height) / 2);
			let dx = this["x"] * tileW + offsetX;
			let dy = this["y"] * tileH + offsetY;
			this.indicator.draw(ctx, dx, dy, width);
		}
	}

	override createSlot(name: string) {
		let slot = new CorpseSlot();
		slot._name = name;
		slot._parent = this;
		return slot;
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	closeCorpseInventory() {
		if (this.inventory && this.inventory.isOpen()) {
			this.inventory.close();
			this.inventory = undefined;
		}
	}

	openCorpseInventory() {
		if (!this.inventory || !this.inventory.isOpen()) {
			let content_row = 2;
			const content_col = 2;
			if (this["content"]) {
				if (this["content"]._objects.length > 4) {
					content_row = 3;
				}
			}

			const invComponent = new ItemInventoryComponent(this,
					"content", content_row, content_col,
					stendhal.config.getBoolean("inventory.quick-pickup"), undefined);
			// TODO: remove, deprecated
			invComponent.setConfigId("corpse");

			const dstate = stendhal.config.getWindowState("corpse");
			this.inventory = new FloatingWindow("Corpse", invComponent,
					dstate.x, dstate.y);
			this.inventory.setId("corpse");
		}
	}

	/**
	 * Opens inventory window if player is within range.
	 */
	private checkOpenCorpseInventory() {
		if (this.canViewContents()) {
			this.openCorpseInventory();
		} else {
			Chat.log("client", "The corpse is too far away.");
		}
	}

	autoOpenIfDesired() {
		if (!this.autoOpenedAlready) {
			this.autoOpenedAlready = true;
			if (marauroa.me && (this["corpse_owner"] == marauroa.me["_name"])) {

				// TODO: for unknown reason, /data/sprites/items/undefined/undefined.png is requested without this delay
				var that = this;
				window.setTimeout(function() {
					that.checkOpenCorpseInventory();
				}, 1);
			}
		}
	}

	override closeInventoryWindow() {
		this.closeCorpseInventory();
	}

	override onclick(_x: number, _y: number) {
		this.checkOpenCorpseInventory();
	}

	override getCursor(_x: number, _y: number) {
		if (this.isEmpty()) {
			return "url(" + Paths.sprites + "/cursor/emptybag.png) 1 3, auto";
		}

		// owner
		if (!this["corpse_owner"] || (this["corpse_owner"] == marauroa.me["_name"])) {
			return "url(" + Paths.sprites + "/cursor/bag.png) 1 3, auto";
		}

		if ((stendhal.data.group.lootmode === "shared") && (stendhal.data.group.members[this["corpse_owner"]])) {
			return "url(" + Paths.sprites + "/cursor/bag.png) 1 3, auto";
		}
		return "url(" + Paths.sprites + "/cursor/lockedbag.png) 1 3, auto";
	}

	public override isDraggable(): boolean {
		return true;
	}

	private isEmpty() {
		return !this["content"] || this["content"]._objects.length === 0;
	}

	override destroy(parent: RPObject|RPZone): void {
		this.indicator?.free();
		super.destroy(parent);
	}
}
