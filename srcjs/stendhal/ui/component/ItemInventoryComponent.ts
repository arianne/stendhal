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

import { Component } from "../toolkit/Component";
import { ItemContainerImplementation } from "./ItemContainerImplementation";

declare var stendhal: any;

/**
 * handles an item inventory
 */
export class ItemInventoryComponent extends Component {

	private static counter = 0;
	protected itemContainerImplementation!: ItemContainerImplementation;
	protected suffix;

	constructor(object: any, slot: string, sizeX: number, sizeY: number, quickPickup: boolean, defaultImage?: string) {
		super("iteminventory-template");

		ItemInventoryComponent.counter++;
		this.suffix = "." + ItemInventoryComponent.counter + ".";
		this.componentElement.classList.add("inventorypopup_" + sizeX);
		if (quickPickup) {
			this.componentElement.classList.add("quickPickup");
		}

		// TODO: rewrite ItemContainerImplementation not to depend on unique ids (aka suffix)
		let html = "";
		for (let i = 0; i < sizeX * sizeY; i++) {
			html += "<div id='" + slot + this.suffix + i + "' class='itemSlot'></div>";
		}
		this.componentElement.innerHTML = html;

		// ItemContainerImplementation uses document.getElementById, so our parent windows must be added to the DOM first.
		queueMicrotask(() => {
			this.itemContainerImplementation = new ItemContainerImplementation(slot, sizeX * sizeY, object, this.suffix, quickPickup, defaultImage);
			stendhal.ui.equip.inventory.push(this.itemContainerImplementation);
		});
	}


	update() {
		this.itemContainerImplementation.update();
	}

	public setVisible(visible: boolean) {
		if (visible) {
			this.componentElement.style.display = "block";
		} else {
			this.componentElement.style.display = "none";
		}
	}

	public isVisible(): boolean {
		return this.componentElement.style.display !== "none";
	}

	public override onParentClose() {
		let idx = stendhal.ui.equip.inventory.indexOf(this.itemContainerImplementation);
		console.log(stendhal.ui.equip.inventory, stendhal.ui.equip.inventory.indexOf(this.itemContainerImplementation));
		if (idx < 0) {
			console.log("Cannot cleanup unknown itemContainerImplementation");
			return;
		}
		stendhal.ui.equip.inventory.splice(idx, 1);
	}

}
