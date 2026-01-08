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


/**
 * Adds event listeners to handle clicks & multiple touches.
 *
 * TODO: add support long & double click/touch
 */
export class ElementClickListener {

	/** Function executed when click is detected. */
	public onClick?: Function;
	/** Property denoting button was pressed with mouse click. */
	private clickEngaged = false;
	/** Property denoting button was pressed with tap/touch. */
	private touchEngaged = 0;


	constructor(element: HTMLElement) {
		// add supported listeners
		element.addEventListener("mousedown", (evt: MouseEvent) => {
			evt.preventDefault();
			if (evt.button == 0) {
				this.clickEngaged = true;
			}
		});
		element.addEventListener("touchstart", (evt: TouchEvent) => {
			evt.preventDefault();
			this.touchEngaged = evt.changedTouches.length;
		}, {passive: true});
		element.addEventListener("mouseup", (evt: MouseEvent) => {
			evt.preventDefault();
			if (this.clickEngaged && evt.button == 0 && this.onClick) {
				// FIXME: should veto if moved too much before release
				this.onClick(evt);
			}
			this.clickEngaged = false;
		});
		element.addEventListener("touchend", (evt: TouchEvent) => {
			evt.preventDefault();
			const target = stendhal.ui.html.extractTarget(evt);
			if (this.touchEngaged == evt.changedTouches.length && target == element && this.onClick) {
				// FIXME: should veto if moved too much before release
				this.onClick(evt);
			}
			this.touchEngaged = 0;
		});
	}
}
