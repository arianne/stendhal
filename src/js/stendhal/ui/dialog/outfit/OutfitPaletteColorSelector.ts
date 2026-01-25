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
import { OutfitColorSelector } from "./OutfitColorSelector";
import { ImageFilter } from "../../../sprite/image/ImageFilter";


export class OutfitPaletteColorSelector extends OutfitColorSelector {
	private _blockWidth: number;
	private _blockHeight: number;
	private _colorMap: any;
	private _hsMap: any;

	constructor(canvas: HTMLCanvasElement, gradientCanvas: HTMLCanvasElement, onColorChanged: Function) {
		super(canvas, gradientCanvas, onColorChanged);
		this._blockWidth = this.baseImage.width / 4;
		this._blockHeight = this.baseImage.height / 4;
		this._x = 0;
		this._y = 0;
		this.hX = this.baseImage.width / 2;
		this._createColorMap();
	}

	override get color() {
		if (this.enabled) {
			let filter = new ImageFilter();
			const hs = this._hsMap[Math.floor(this._x / this._blockWidth)][Math.floor(this._y / this._blockHeight)];
			const hsl = [hs[0], hs[1], this.hX / this.baseImage.width];
			const rgbArray = filter.hsl2rgb(hsl);
			return filter.mergergb(rgbArray);
		}
		return null;
	}

	override set color(rgb) {
		if (rgb != null) {
			this.enabled = true;
			let filter = new ImageFilter();
			const hsl = filter.rgb2hsl(filter.splitrgb(rgb));
			this.hX = hsl[2] * this.baseImage.width;
			let bestDelta = Number.MAX_VALUE;
			for (let i = 0; i < 4; i++) {
				for (let j = 0; j < 4; j++) {
					const hs = this._hsMap[i][j];
					const hueDelta = hs[0] - hsl[0];
					const satDelta = hs[1] - hsl[1];
					const delta = (hueDelta * hueDelta) + (satDelta * satDelta);
					if (delta < bestDelta) {
						bestDelta = delta;
						this._x = i * this._blockWidth;
						this._y = j * this._blockHeight;
					}
				}
			}
		} else {
			this.enabled = false;
		}
	}

	_createColorMap() {
		const hues = [0.05, 0.07, 0.09, 0.11];
		const saturations = [0.70, 0.55, 0.40, 0.25];
		const hsMap: any = [[],[],[],[]];
		const colors: any = [[],[],[],[]];
		let filter = new ImageFilter();
		for (let i = 0; i < 4; i++) {
			for (let j = 0; j < 4; j++) {
				const hue = hues[j];
				const sat = saturations[i];
				hsMap[i].push([hue, sat]);
				const color = filter.hsl2rgb([hue, sat, 0.5]);
				colors[i].push(color);
			}
		}
		this._hsMap = hsMap;
		this._colorMap = colors;
	}

	override _createBaseImage(width: number, height: number) {
		this._createColorMap();
		const img = document.createElement("canvas");
		img.width  = width;
		img.height = height;
		const ctx = img.getContext("2d")!;

		const blockWidth = width / 4;
		const blockHeight = height / 4;

		for (let x = 0; x < 4; x++) {
			for (let y = 0; y < 4; y++) {
				const rgb = this._colorMap[x][y];
				ctx.fillStyle = this._rgbToCssString(rgb);
				ctx.fillRect(x * blockWidth, y * blockHeight, blockWidth, blockHeight);
			}
		}
		return img;
	}

	override _calculateGradientStops() {
		let filter = new ImageFilter();
		let hs = this._hsMap[Math.floor(this._x / this._blockWidth)][Math.floor(this._y / this._blockHeight)];
		const hslLeft = [hs[0], hs[1], 0.08];
		const hslMiddle = [hs[0], hs[1], 0.5];
		const hslRight = [hs[0], hs[1], 0.92];
		const rgbLeft = filter.hsl2rgb(hslLeft);
		const rgbMiddle = filter.hsl2rgb(hslMiddle);
		const rgbRight = filter.hsl2rgb(hslRight);

		return [rgbLeft, rgbMiddle, rgbRight];
	}

	override _drawSelection() {
		let x = Math.floor(this._x / this._blockWidth);
		let y = Math.floor(this._y / this._blockHeight);
		this.ctx.strokeStyle = "white";
		this.ctx.strokeRect(x * this._blockWidth, y * this._blockHeight, this._blockWidth, this._blockHeight);
	}


}
