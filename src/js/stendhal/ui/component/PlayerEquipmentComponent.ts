/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";
import { ItemContainerImplementation } from "./ItemContainerImplementation";

import { marauroa } from "marauroa"

/**
 * manage the equipment which is used by the player
 */
export class PlayerEquipmentComponent extends Component {

	private slotNames = ["head", "lhand", "rhand", "finger", "armor", "cloak", "legs", "feet", "pouch"];
	private slotSizes = [   1,       1,      1,       1,        1,       1,       1,     1,       1];
	private slotImages = ["slot-helmet.png", "slot-shield.png", "slot-weapon.png", "slot-ring.png", "slot-armor.png", "slot-cloak.png",
		"slot-legs.png", "slot-boots.png", "slot-pouch.png"];
	private inventory: ItemContainerImplementation[] = [];

	private pouchVisible = false;

	constructor() {
		super("equipment");
		this.inventory = [];
		for (var i in this.slotNames) {
			this.inventory.push(
				new ItemContainerImplementation(
					document, this.slotNames[i], this.slotSizes[i], null, "", false, this.slotImages[i]));
		}

		// hide pouch by default
		this.showPouch(false);
	}

	public update() {
		for (var i in this.inventory) {
			this.inventory[i].update();
		}

		if (!this.pouchVisible) {
			var features = null
			if (marauroa.me != null) {
				features = marauroa.me["features"];
			}

			if (features != null) {
				if (features["pouch"] != null) {
					this.showPouch(true);
				}
			}
		}
	}

	public markDirty() {
		for (const inv of this.inventory) {
			inv.markDirty();
		}
	}

	private showSlot(id: string, show: boolean) {
		var slot = document.getElementById(id)!;
		var prevState = slot.style.display;

		if (show === true) {
			slot.style.display = "block";
		} else {
			slot.style.display = "none";
		}

		return prevState != slot.style.display;
	}

	private showPouch(show: boolean) {
		if (this.showSlot("pouch0", show)) {
			// resize the inventory window
			var equip = document.getElementById("equipment")!;
			if (show) {
				equip.style.height = "200px";
			} else {
				equip.style.height = "160px";
			}
			this.pouchVisible = show;
		}
	}

}
