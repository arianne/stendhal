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

declare var stendhal: any;

import { RPEvent } from "./RPEvent";


/**
 * View center changing event.
 */
export class ViewChangeEvent extends RPEvent {

	public x!: number;
	public y!: number;


	execute(entity: any) {
		const canvas = stendhal.ui.viewport.getElement() as HTMLCanvasElement;
		queueMicrotask(() => {
			stendhal.ui.viewport.freeze = true;
			stendhal.ui.viewport.offsetX = this.x * stendhal.ui.viewport.targetTileWidth
					- Math.round(canvas.width / 2);
			stendhal.ui.viewport.offsetY = this.y * stendhal.ui.viewport.targetTileHeight
					- Math.round(canvas.height / 2);
		});
	}
}
