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

import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";
import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { PopupInventory } from "./PopupInventory";

declare var marauroa: any;
declare var stendhal: any;

export class Corpse extends PopupInventory {

	override minimapShow = false;
	override zIndex = 5500;
	autoOpenedAlready = false;

	override set(key: string, value: any) {
		super.set(key, value);

		this.sprite = this.sprite || {};
		const bloodEnabled = stendhal.config.getBoolean("gamescreen.blood");

		if (bloodEnabled && (key === "image")) {
			this.sprite.filename = "/data/sprites/corpse/" + value + ".png";
		} else if (!bloodEnabled && (key === "harmless_image")) {
			this.sprite.filename = "/data/sprites/corpse/" + value + ".png";
		}
	}

	override createSlot(name: string) {
		var slot = marauroa.util.fromProto(marauroa.rpslotFactory["_default"], {
			add: function(object: any) {
				marauroa.rpslotFactory["_default"].add.apply(this, arguments);
				if (this._objects.length > 0) {
					this._parent.autoOpenIfDesired();
				}
			},

			del: function(key: any) {
				marauroa.rpslotFactory["_default"].del.apply(this, arguments);
				if (this._objects.length == 0) {
					this._parent.closeCorpseInventory();
				}
			}
		});
		slot._name = name;
		slot._objects = [];
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

			const dstate = stendhal.config.dialogstates["corpse"];
			const invComponent = new ItemInventoryComponent(this,
					"content", content_row, content_col, true, undefined);
			invComponent.setConfigId("corpse");

			this.inventory = new FloatingWindow("Corpse", invComponent,
					dstate.x, dstate.y);
		}
	}

	autoOpenIfDesired() {
		if (!this.autoOpenedAlready) {
			this.autoOpenedAlready = true;
			if (marauroa.me && (this["corpse_owner"] == marauroa.me["_name"])) {

				// TODO: for unknown reason, /data/sprites/items/undefined/undefined.png is requested without this delay
				var that = this;
				window.setTimeout(function() {
					that.openCorpseInventory();
				}, 1);
			}
		}
	}

	override closeInventoryWindow() {
		this.closeCorpseInventory();
	}

	override onclick(_x: number, _y: number) {
		this.openCorpseInventory();
	}

	override getCursor(_x: number, _y: number) {
		if (!this["content"] || this["content"]._objects.length === 0) {
			return "url(/data/sprites/cursor/emptybag.png) 1 3, auto";
		}

		// owner
		if (!this["corpse_owner"] || (this["corpse_owner"] == marauroa.me["_name"])) {
			return "url(/data/sprites/cursor/bag.png) 1 3, auto";
		}

		if ((stendhal.data.group.lootmode === "shared") && (stendhal.data.group.members[this["corpse_owner"]])) {
			return "url(/data/sprites/cursor/bag.png) 1 3, auto";
		}
		return "url(/data/sprites/cursor/lockedbag.png) 1 3, auto";
	}

	public override isDraggable(): boolean {
		return true;
	}
}
