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

import { stendhal } from "../stendhal";

import { ItemContainerImplementation } from "./component/ItemContainerImplementation";


export class Inventory {

	private inventory: ItemContainerImplementation[] = [];

	/** Singleton instance. */
	private static instance: Inventory;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): Inventory {
		if (!Inventory.instance) {
			Inventory.instance = new Inventory();
		}
		return Inventory.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	update() {
		for (var i in this.inventory) {
			this.inventory[i].update();
		}
	}

	getInventory(): ItemContainerImplementation[] {
		return this.inventory;
	}

	/**
	 * Retrieves an inventory item container.
	 *
	 * @param element {string}
	 *   HTML element associated with container.
	 * @return {ItemContainerImplementation}
	 *   Container with matching element or `undefined`.
	 */
	getByElement(element: HTMLElement): ItemContainerImplementation|undefined {
		for (const container of this.inventory) {
			if (element === container.getParentElement()) {
				return container;
			}
		}
	}

	add(comp: ItemContainerImplementation) {
		this.inventory.push(comp);
	}

	remove(comp: ItemContainerImplementation) {
		const idx = this.inventory.indexOf(comp);
		if (idx > -1) {
			this.inventory.splice(idx, 1);
		}
	}

	removeIndex(idx: number) {
		if (idx > -1 && idx < this.inventory.length) {
			this.inventory.splice(idx, 1);
		}
	}

	indexOf(comp: ItemContainerImplementation): number {
		return this.inventory.indexOf(comp);
	}
}
