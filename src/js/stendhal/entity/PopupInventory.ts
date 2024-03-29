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

import { Entity } from "./Entity";
import { RPObject } from "./RPObject";

declare var marauroa: any;


export class PopupInventory extends Entity {

	protected maxDistToView = 4;


	override draw(ctx: CanvasRenderingContext2D) {
		super.draw(ctx);
		if (this.inventory && this.inventory.isOpen() && !this.canViewContents()) {
			// player has moved too far away
			this.closeInventoryWindow();
		}
	}

	public closeInventoryWindow() {
		// inheriting classes can override
	}

	override destroy(_parent: RPObject) {
		this.closeInventoryWindow();
		super.destroy(_parent);
	}

	/**
	 * Checks if player is close enough to view contents.
	 */
	protected canViewContents(): boolean {
		if (!marauroa.me) {
			return false;
		}
		return this.getDistanceTo(marauroa.me) <= this.maxDistToView;
	}
}
