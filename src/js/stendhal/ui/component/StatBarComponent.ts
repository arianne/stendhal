/***************************************************************************
 *                    Copyright © 2003-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";
import { Color } from "../../data/color/Color";
import { RenderingContext2D } from "util/Types";

/**
 * TODO: move this to `ui.toolkit`.
 */
export class StatBarComponent extends Component {

	readonly canvas: HTMLCanvasElement;
	protected readonly ctx: RenderingContext2D;


	constructor(id?: string) {
		if (!id) {
			id = "statbar-template";
		}
		super(id);
		this.canvas = this.componentElement as HTMLCanvasElement;
		this.ctx = this.canvas.getContext("2d")!;
	}

	drawBase() {
		this.ctx.beginPath();
		this.ctx.fillStyle = Color.GRAY;
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
	}

	draw(ratio: number) {
		this.ctx.beginPath();
		this.ctx.fillStyle = Color.BLACK;
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
		this.ctx.fillStyle = Color.getStatBarColor(ratio);
		this.ctx.fillRect(0, 0, this.canvas.width * ratio, this.canvas.height);
	}

}
