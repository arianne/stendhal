/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Color } from "../../util/Color";

// TODO: refactor into a Component
export class StatBar {

	readonly canvas: HTMLCanvasElement;
	protected readonly ctx: CanvasRenderingContext2D;


	constructor(id: string) {
		const element = document.getElementById(id);
		if (!element) {
			throw new Error("Cannot create status bar because there is no HTML element with id " + id);
		}
		if (!(element instanceof HTMLCanvasElement)) {
			throw new Error("Cannot create status bar because element with id " + id + " is not a canvas");
		}

		this.canvas = <HTMLCanvasElement> element;
		this.ctx = this.canvas.getContext("2d")!;
	}

	draw(ratio: number) {
		this.ctx.beginPath();
		this.ctx.fillStyle = "#808080"; // same as java.awt.Color.GRAY (rgb(128,128,128))
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
		this.ctx.fillStyle = Color.getStatBarColor(ratio);
		this.ctx.fillRect(0, 0, this.canvas.width * ratio, this.canvas.height);
	}

	setVisible(visible=true) {
		if (visible) {
			this.canvas.style.display = "";
		} else {
			this.canvas.style.display = "none";
		}
	}

	isVisible() {
		return this.canvas.style.display !== "none";
	}
}
