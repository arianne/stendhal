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

import { Component } from "../toolkit/Component";
import { Color } from "../../util/Color";

export class StatBarComponent extends Component {

	readonly canvas: HTMLCanvasElement;
	protected readonly ctx: CanvasRenderingContext2D;


	constructor(id?: string) {
		if (!id) {
			id = "statbar-template";
		}
		super(id);
		this.canvas = <HTMLCanvasElement> this.componentElement;
		this.ctx = this.canvas.getContext("2d")!;
	}

	draw(ratio: number) {
		this.ctx.beginPath();
		this.ctx.fillStyle = "#808080"; // same as java.awt.Color.GRAY (rgb(128,128,128))
		this.ctx.fillRect(0, 0, this.canvas.width, this.canvas.height);
		this.ctx.fillStyle = Color.getStatBarColor(ratio);
		this.ctx.fillRect(0, 0, this.canvas.width * ratio, this.canvas.height);
	}

}
