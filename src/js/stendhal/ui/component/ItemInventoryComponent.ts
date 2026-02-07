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

import { stendhal } from "../../stendhal";

/**
 * handles an item inventory
 */
export class ItemInventoryComponent extends Component {

	private static counter = 0;
	protected itemContainerImplementation!: ItemContainerImplementation;
	protected suffix;
	private oldSizeX = 0;

	constructor(object: any, private slot: string, sizeX: number, sizeY: number, quickPickup: boolean, defaultImage?: string) {
		super("iteminventory-template", true);

		ItemInventoryComponent.counter++;
		this.suffix = "-" + ItemInventoryComponent.counter + "-";
		this.componentElement.classList.add("inventorypopup_" + sizeX);
		if (quickPickup) {
			this.componentElement.classList.add("quickPickup");
		}

		// TODO: rewrite ItemContainerImplementation not to depend on unique ids (aka suffix)
		this.setSize(sizeX, sizeY);

		this.itemContainerImplementation = new ItemContainerImplementation(
			this.componentElement, slot, sizeX * sizeY, object, this.suffix, quickPickup, defaultImage);
		stendhal.ui.equip.add(this.itemContainerImplementation);
	}

	setObject(object: any) {
		this.itemContainerImplementation.object = object;
	}

	setSize(sizeX: number, sizeY: number) {
		this.componentElement.classList.remove("inventorypopup_" + this.oldSizeX);
		this.componentElement.classList.add("inventorypopup_" + sizeX);
		this.oldSizeX = sizeX;

		let html = "";
		for (let i = 0; i < sizeX * sizeY; i++) {
			html += "<div id='" + this.slot + this.suffix + i + "' class='itemSlot'></div>";
		}
		this.componentElement.innerHTML = html;
	}

	update() {
		this.itemContainerImplementation.update();
	}

	public markDirty() {
		this.itemContainerImplementation.markDirty();
	}

	public override onParentClose() {
		let idx = stendhal.ui.equip.indexOf(this.itemContainerImplementation);
		if (idx < 0) {
			console.log("Cannot cleanup unknown itemContainerImplementation");
			return;
		}
		stendhal.ui.equip.removeIndex(idx);
	}

}
