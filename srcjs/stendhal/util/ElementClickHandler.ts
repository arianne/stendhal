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


/**
 * Adds event listeners to handle clicks & multiple touches.
 *
 * TODO: add support long & double click/touch
 */
export class ElementClickHandler {

	/** Property denoting button was pressed with mouse click. */
	private clickEngaged = false;
	/** Property denoting button was pressed with tap/touch. */
	private touchEngaged = 0;


	constructor(private element: HTMLElement) {}

	/**
	 * Listens for recognized click events.
	 *
	 * @param onClick {Function}
	 *   Function executed when click is detected.
	 */
	addClickListener(onClick: Function): ElementClickHandler {
		this.element.addEventListener("mousedown", (evt: MouseEvent) => {
			evt.preventDefault();
			if (evt.button == 0) {
				this.clickEngaged = true;
			}
		});
		this.element.addEventListener("touchstart", (evt: TouchEvent) => {
			evt.preventDefault();
			this.touchEngaged = evt.changedTouches.length;
		});
		this.element.addEventListener("mouseup", (evt: MouseEvent) => {
			evt.preventDefault();
			if (this.clickEngaged && evt.button == 0) {
				// FIXME: should veto if moved too much before release
				onClick(evt);
			}
			this.clickEngaged = false;
		});
		this.element.addEventListener("touchend", (evt: TouchEvent) => {
			evt.preventDefault();
			const target = stendhal.ui.html.extractTarget(evt, this.touchEngaged - 1);
			if (this.touchEngaged == evt.changedTouches.length && target == this.element) {
				// FIXME: should veto if moved too much before release
				onClick(evt);
			}
			this.touchEngaged = 0;
		});
		return this;
	}
}
