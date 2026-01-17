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

import { ItemInventoryComponent } from "./ItemInventoryComponent";

import { marauroa } from "marauroa"


export class KeyringComponent extends ItemInventoryComponent {

	private slotSize = "2 4";

	constructor(object: any, slot: string, sizeX: number, sizeY: number, quickPickup: boolean, defaultImage?: string) {
		super(object, slot, sizeX, sizeY, quickPickup, defaultImage);
	}

	override update() {
		let features = null;
		if (marauroa.me != null) {
			features = marauroa.me["features"];
		}
		if (features == null) {
			return;
		}

		let size = features["keyring"];

		if (size) {
			if (!this.isVisible()) {
				this.setVisible(true);
			}
			if (this.slotSize != size) {
				this.slotSize = size;
				let sizeArray = size.split(" ");
				let sizeX = parseInt(sizeArray[0], 10);
				let sizeY = parseInt(sizeArray[1], 10);
				this.resize(sizeX, sizeY);
			}
		} else if (this.isVisible()) {
			this.setVisible(false);
		}

		super.update();
	}


	private resize(sizeX: number, sizeY: number) {
		super.setSize(sizeX, sizeY);
		this.itemContainerImplementation.init(sizeX * sizeY);
	}
}
