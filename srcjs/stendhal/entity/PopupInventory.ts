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

import { Entity } from "./Entity";
import { RPObject } from "./RPObject";

declare var marauroa: any;


export class PopupInventory extends Entity {

	override draw(ctx: CanvasRenderingContext2D) {
		super.draw(ctx);
		if (this.inventory && this.inventory.isOpen()) {
			this.checkPlayerDistance(5);
		}
	}

	/**
	 * Closes floating window if player is not within range.
	 *
	 * @param distToClose
	 *     If player is standing at least this distance away
	 *     the window will be closed.
	 */
	private checkPlayerDistance(distToClose: number) {
		if (marauroa.me) {
			const dist = this.getDistanceTo(marauroa.me);
			if (dist >= distToClose || dist < 0) {
				this.closeInventoryWindow();
			}
		}
	}

	public closeInventoryWindow() {
		// inheriting classes can override
	}

	override destroy(_parent: RPObject) {
		this.closeInventoryWindow();
		super.destroy(_parent);
	}
}
