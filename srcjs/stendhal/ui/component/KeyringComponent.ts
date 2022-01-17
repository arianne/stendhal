/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ItemContainerImplementation } from "./ItemContainerImplementation";
import { ItemInventoryComponent } from "./ItemInventoryComponent";

declare var marauroa: any;
declare var stendhal: any;


export class KeyringComponent extends ItemInventoryComponent {

	private object: any;
	private slotName: string;
	private quickPickup: boolean;
	private defaultImage?: string;
	private isExtended: boolean = false;


	constructor(object: any, slot: string, sizeX: number, sizeY: number, quickPickup: boolean, defaultImage?: string) {
		super(object, slot, sizeX, sizeY, quickPickup, defaultImage);

		this.object = object;
		this.slotName = slot;
		this.quickPickup = quickPickup;
		this.defaultImage = defaultImage;
	}

	override update() {
		let features = null;
		if (marauroa.me != null) {
			features = marauroa.me["features"];
		}

		let keyringEnabled = false;
		let extendedKeyring = false;
		if (features != null) {
			keyringEnabled = features["keyring"] != null;
			extendedKeyring = features["keyring_ext"] != null;
		}

		if (keyringEnabled && !this.isVisible()) {
			this.setVisible(true);
		} else if (!keyringEnabled && this.isVisible()) {
			this.setVisible(false);
		}

		if (extendedKeyring && !this.isExtended) {
			this.addExtendedSlots();
		}

		super.update();
	}

	private addExtendedSlots() {
		// FIXME: keyring is 2x6 instead of 3x4
		let html = this.componentElement.innerHTML;
		for (let i = 8; i < 12; i++) {
			html += "<div id='" + this.slotName + this.suffix + i + "' class='itemSlot'></div>";
		}
		this.componentElement.innerHTML = html;

		this.isExtended = true;

		queueMicrotask(() => {
			this.itemContainerImplementation = new ItemContainerImplementation(this.slotName, 12, this.object, this.suffix, this.quickPickup, this.defaultImage);
			stendhal.ui.equip.inventory.push(this.itemContainerImplementation);
		});
	}
}
