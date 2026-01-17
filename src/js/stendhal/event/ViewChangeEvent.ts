/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { Canvas } from "util/Types";
import { RPEvent } from "marauroa"


/**
 * View center changing event.
 */
export class ViewChangeEvent extends RPEvent {

	public x!: number;
	public y!: number;


	execute(_entity: any) {
		const canvas = stendhal.ui.viewport.getElement() as Canvas;
		queueMicrotask(() => {
			stendhal.ui.viewport.freeze = true;
			stendhal.ui.viewport.offsetX = this.x * stendhal.ui.viewport.targetTileWidth
					- Math.round(canvas.width / 2);
			stendhal.ui.viewport.offsetY = this.y * stendhal.ui.viewport.targetTileHeight
					- Math.round(canvas.height / 2);
		});
	}
}
