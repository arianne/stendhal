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

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";
import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";

import { Entity } from "./Entity";

declare var marauroa: any;
declare var stendhal: any;


let OPEN_SPRITE = {
	filename: "/data/sprites/chest.png",
	height: 32,
	width: 32,
	offsetY: 32
};

let CLOSED_SPRITE = {
	filename: "/data/sprites/chest.png",
	height: 32,
	width: 32
};

export class Chest extends Entity {
	override zIndex = 5000;
	sprite = CLOSED_SPRITE;
	open = false;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "open") {
			this.sprite = OPEN_SPRITE;
			this.open = true;
		}
		if (this.isNextTo(marauroa.me)) {
			this.openInventoryWindow();
		}
	}

	override unset(key: string) {
		super.unset(key);
		if (key === "open") {
			this.sprite = CLOSED_SPRITE;
			this.open = false;
			if (this.inventory && this.inventory.isOpen()) {
				this.inventory.close();
				this.inventory = undefined;
			}
		}
	}

	override isVisibleToAction(_filter: boolean) {
		return true;
	}

	override onclick(_x: number, _y: number) {
		if (marauroa.me.isNextTo(this)) {
			// If we are next to the chest, open or close it.
			var action = {
				"type": "use",
				"target": "#" + this["id"],
				"zone": marauroa.currentZoneName
			};
			marauroa.clientFramework.sendAction(action);
		} else {
			// We are far away, but if the chest is open, we can take a look
			if (this.open) {
				this.openInventoryWindow();
			}
		}
	}

	openInventoryWindow() {
		if (!this.inventory || !this.inventory.isOpen()) {
			const dstate = stendhal.config.dialogstates["chest"];
			const invComponent = new ItemInventoryComponent(this,
					"content", 5, 6, false, undefined);
			invComponent.setConfigId("chest");

			this.inventory = new FloatingWindow("Chest", invComponent,
					dstate.x, dstate.y);
		}
	}

	closeInventoryWindow() {
		if (this.inventory && this.inventory.isOpen()) {
			this.inventory.close();
			this.inventory = undefined;
		}
	}

	override draw(ctx: CanvasRenderingContext2D) {
		super.draw(ctx);

		this.checkDistance();
	}

	private checkDistance() {
		if (marauroa.me) {
			const xDist = Math.abs(this["x"] - marauroa.me["x"]);
			const yDist = Math.abs(this["y"] - marauroa.me["y"]);

			if (xDist > 4 || yDist > 4) {
				this.closeInventoryWindow();
			}
		}
	}

	override getCursor(_x: number, _y: number) {
		return "url(/data/sprites/cursor/bag.png) 1 3, auto";
	}

}
