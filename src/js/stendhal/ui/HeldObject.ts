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

import { Point } from "../util/Point";


/**
 * Represents a held item.
 */
export interface HeldObject {
	[index: string]: any;
	zone: string,
	path: string,
	slot?: string,
	quantity?: number,
	origin?: Point
}

/**
 * Handles displaying held object image.
 *
 * Currently only works with touch events.
 */
export class HeldObjectManager {

	/** Image displayed when an object is "held". */
	private readonly image: HTMLImageElement;

	/** Singleton instance. */
	private static instance: HeldObjectManager;


	/**
	 * Retrieves singleton instance.
	 */
	public static get(): HeldObjectManager {
		if (!HeldObjectManager.instance) {
			HeldObjectManager.instance = new HeldObjectManager();
		}
		return HeldObjectManager.instance
	}

	/**
	 * Static properties & methods only.
	 */
	private constructor() {
		this.image = document.getElementById("held-object")! as HTMLImageElement;
	}

	/**
	 * Sets the image's visibility.
	 *
	 * @param visible
	 *   `true` if image should be visible.
	 */
	private setVisible(visible=true) {
		this.image.style.setProperty("display", visible ? "block" : "none");
	}

	/**
	 * Sets global held object & image to display.
	 *
	 * NOTE: currently this should only be used with touch events
	 *
	 * @param obj {ui.HeldObject.HeldObject}
	 *   Object considered to be "held".
	 * @param img {HTMLImageElement|string}
	 *   Image element or path to image.
	 * @param pos {util.Point.Point}
	 *   Initial position of displayed image.
	 */
	public set(obj: HeldObject, img: HTMLImageElement|string, pos?: Point) {
		stendhal.ui.heldObject = obj;
		this.setImage(img);
		this.onSet(pos);
	}

	/**
	 * Called when object is ready to be displayed.
	 *
	 * @param pos {util.Point.Point}
	 *   Initial position of displayed image.
	 */
	private onSet(pos?: Point) {
		if (pos) {
			this.setPosition(pos.x, pos.y);
		} else {
			const rect = stendhal.ui.gamewindow.getElement().getBoundingClientRect();
			this.setPosition(rect.left, rect.top);
		}
		this.setVisible(true);

		document.body.addEventListener("touchmove", this.onDragWhileHeld);
		document.body.addEventListener("touchend", this.onReleaseWhileHeld);
	}

	/**
	 * Hides object image.
	 */
	public onRelease() {
		// NOTE: should we unset `stendhal.ui.heldObject` here?
		this.setVisible(false);
	}

	/**
	 * Sets or unsets object image.
	 *
	 * @param img {HTMLImageElement|string}
	 *   Image element or path to image.
	 */
	private setImage(img?: HTMLImageElement|string) {
		if (!img) {
			this.image.src = "";
		} else if (typeof(img) === "string") {
			this.image.src = img;
		} else {
			this.image.src = img.src;
		}
	}

	/**
	 * Updates position of displayed image.
	 *
	 * @param x {number}
	 *   Absolute positioning on X axis.
	 * @param y {number}
	 *   Absolute positioning on Y axis.
	 */
	private setPosition(x: number, y: number) {
		this.image.style.left = x + "px";
		this.image.style.top = y + "px";
	}

	/**
	 * Handles event to update held object drawing position.
	 */
	private onDragWhileHeld(e: Event) {
		const pos = stendhal.ui.html.extractPosition(e);
		HeldObjectManager.get().setPosition(pos.pageX, pos.pageY);
	}

	/**
	 * Handles event to unset held object.
	 */
	private onReleaseWhileHeld(e: Event) {
		const hom = HeldObjectManager.get();
		hom.onRelease();

		document.body.removeEventListener("touchmove", hom.onDragWhileHeld);
		document.body.removeEventListener("touchend", hom.onReleaseWhileHeld);
	}
}
