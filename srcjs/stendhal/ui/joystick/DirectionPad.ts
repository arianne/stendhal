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


export class DirectionPad extends JoystickBase {

	private up: HTMLImageElement;
	private down: HTMLImageElement;
	private left: HTMLImageElement;
	private right: HTMLImageElement;

	private readonly radius = 36;


	public constructor() {
		super();

		this.up = new Image();
		this.up.src = this.getResource("dpad_arrow_up");
		this.down = new Image();
		this.down.src = this.getResource("dpad_arrow_down");
		this.left = new Image();
		this.left.src = this.getResource("dpad_arrow_left");
		this.right = new Image();
		this.right.src = this.getResource("dpad_arrow_right");
	}

	public override draw(ctx: CanvasRenderingContext2D) {
		// wait for images to be loaded
		for (const img of [this.up, this.down, this.left, this.right]) {
			if (img.height == 0) {
				return;
			}
		}

		const center_x = stendhal.ui.gamewindow.offsetX + this.centerX - this.radius;
		const center_y = stendhal.ui.gamewindow.offsetY + ctx.canvas.height - this.centerX - this.radius;

		ctx.drawImage(this.up, center_x, center_y - this.radius);
		ctx.drawImage(this.down, center_x, center_y + this.radius + (this.down.height / 2))
		ctx.drawImage(this.left, center_x - this.radius, center_y);
		ctx.drawImage(this.right, center_x + this.radius + (this.right.width / 2), center_y);
	}
}
