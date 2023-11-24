/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { JoystickBase } from "./JoystickBase";

declare var stendhal: any;


export class Joystick extends JoystickBase {

	private outer: HTMLImageElement;
	private inner: HTMLImageElement;

	public constructor() {
		super();

		// TODO: make smaller joystick image
		this.outer = new Image();
		this.outer.src = this.getResource("joystick_outer");
		this.inner = new Image();
		this.inner.src = this.getResource("joystick_inner");
	}

	public override draw(ctx: CanvasRenderingContext2D) {
		// wait for images to be loaded
		if (this.outer.height == 0 || this.inner.height == 0) {
			return;
		}

		const draw_left = stendhal.ui.gamewindow.offsetX + this.centerX;
		const draw_top = stendhal.ui.gamewindow.offsetY + ctx.canvas.height - this.centerX;

		ctx.drawImage(this.outer, draw_left - (this.outer.width / 2), draw_top - (this.outer.height / 2));
		ctx.drawImage(this.inner, draw_left - (this.inner.width / 2), draw_top - (this.inner.height / 2));
	}
}
