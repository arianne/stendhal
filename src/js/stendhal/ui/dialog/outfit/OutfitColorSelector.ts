/***************************************************************************
 *                (C) Copyright 2015-2026 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { singletons } from "../../../SingletonRepo";
import { Canvas, RenderingContext2D } from "util/Types";
import { ImageFilter } from "../../../sprite/image/ImageFilter";


export class OutfitColorSelector {
	protected ctx: RenderingContext2D;
	private gradCtx: RenderingContext2D;
	protected baseImage: Canvas;
	private onColorChanged: Function;
	private _enabled: boolean;
	protected _x: number;
	protected _y: number;
	protected hX: number;

	constructor(canvas: HTMLCanvasElement, gradientCanvas: HTMLCanvasElement, onColorChanged: Function) {
		this.ctx = canvas.getContext("2d")!;
		this.gradCtx = gradientCanvas.getContext("2d")!;
		this.baseImage = this._createBaseImage(canvas.width, canvas.height);
		this.onColorChanged = onColorChanged!;
		this._enabled = false;
		this._x = this.baseImage.width / 2;
		this._y = this.baseImage.height / 2;
		this.hX = this._x;
		canvas.addEventListener("mousedown", (e) => this._onMouseDown(e));
		canvas.addEventListener("mousemove", (e) => this._onMouseMove(e));

		gradientCanvas.addEventListener("mousedown", (e) => this._onMouseDownGrad(e));
		gradientCanvas.addEventListener("mousemove", (e) => this._onMouseMoveGrad(e));
		gradientCanvas.style.margin = "5px 0px 0px 0px";
	}

	set enabled(value) {
		this._enabled = value ? true : false;
		this.draw();
		this.onColorChanged(this.color);
	}

	get enabled() {
		return this._enabled;
	}

	get color() {
		if (this.enabled) {
			let filter = new ImageFilter();
			const hsl = [this._x / this.baseImage.width, 1 - this._y / this.baseImage.height,
				this.hX / this.baseImage.width];
			const rgbArray = filter.hsl2rgb(hsl);
			return filter.mergergb(rgbArray);
		}
		return null;
	}

	set color(rgb) {
		if (rgb != null) {
			let filter = new ImageFilter();
			const hsl = filter.rgb2hsl(filter.splitrgb(rgb));
			this._x = hsl[0] * this.baseImage.width;
			this._y = (1 - hsl[1]) * this.baseImage.height;
			this.hX = hsl[2] * this.baseImage.width;
			this.enabled = true;
		} else {
			this.enabled = false;
		}
	}

	_createBaseImage(width: number, height: number) {
		const img = document.createElement("canvas");
		img.width  = width;
		img.height = height;
		const ctx = img.getContext("2d")!;
		let filter = new ImageFilter();
		for (let x = 0; x < width; x++) {
			for (let y = 0; y < height; y++) {
				const hsl = [x / width, 1 - y / height, 0.5];
				const rgb = filter.hsl2rgb(hsl);
				ctx.fillStyle = this._rgbToCssString(rgb);
				ctx.fillRect(x, y, 1, 1);
			}
		}
		return img;
	}

	_rgbToCssString(rgb: string[]|number[]) {
		return "rgb(" + rgb[0] + "," + rgb[1] + "," + rgb[2] + ")";
	}

	draw() {
		if (this.enabled) {
			this.ctx.drawImage(this.baseImage, 0, 0);
			this._drawSelection();
		} else {
			this.ctx.fillStyle = "gray";
			this.ctx.fillRect(0, 0, this.baseImage.width, this.baseImage.height);
		}
		this._drawGradientPart();
	}

	_drawSelection() {
		this.ctx.strokeStyle = "black";
		this.ctx.beginPath();
		this.ctx.moveTo(this._x, 0);
		this.ctx.lineTo(this._x, this.baseImage.height);
		this.ctx.moveTo(0, this._y);
		this.ctx.lineTo(this.baseImage.width, this._y);
		this.ctx.stroke();
	}

	_drawGradientPart() {
		if (this.enabled) {
			const gradient = this.gradCtx.createLinearGradient(0, 0, this.baseImage.width, 0);
			const stops = this._calculateGradientStops();
			gradient.addColorStop(0, this._rgbToCssString(stops[0]));
			gradient.addColorStop(0.5, this._rgbToCssString(stops[1]));
			gradient.addColorStop(1, this._rgbToCssString(stops[2]));

			const ctx = this.gradCtx;
			ctx.fillStyle = gradient;
			ctx.fillRect(0, 0, this.baseImage.width, 10);

			ctx.fillStyle = "black";
			ctx.beginPath();
			ctx.moveTo(this.hX, 0);
			ctx.lineTo(this.hX, 10);
			ctx.stroke();
		} else {
			this.gradCtx.fillStyle = "gray";
			this.gradCtx.fillRect(0, 0, this.baseImage.width, 10);
		}
	}

	_calculateGradientStops() {
		let filter = new ImageFilter();
		const width = this.baseImage.width;
		const height = this.baseImage.height;
		const hslLeft = [this._x / width, 1 - this._y / height, 0.08];
		const hslMiddle = [this._x / width, 1 - this._y / height, 0.5];
		const hslRight = [this._x / width, 1 - this._y / height, 0.92];
		const rgbLeft = filter.hsl2rgb(hslLeft);
		const rgbMiddle = filter.hsl2rgb(hslMiddle);
		const rgbRight = filter.hsl2rgb(hslRight);

		return [rgbLeft, rgbMiddle, rgbRight];
	}

	_onMouseDown(event: MouseEvent) {
		if (!this.enabled) {
			return;
		}
		this._x = event.offsetX;
		this._y = event.offsetY;
		this.draw();
		this.onColorChanged(this.color);
	}

	_onMouseMove(event: MouseEvent) {
		if (event.buttons) {
			this._onMouseDown(event);
		}
	}

	_onMouseDownGrad(event: MouseEvent) {
		if (!this.enabled) {
			return;
		}
		this.hX = event.offsetX;
		this.draw();
		this.onColorChanged(this.color);
	}

	_onMouseMoveGrad(event: MouseEvent) {
		if (event.buttons) {
			this._onMouseDownGrad(event);
		}
	}
}
