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

import { Paths } from "../../../data/Paths";
import { RenderingContext2D } from "util/Types";

import { stendhal } from "../../../stendhal";
import { ImageRef } from "sprite/image/ImageRef";
import { images } from "sprite/image/ImageManager";

export class OutfitPartSelector {
	private part: string;
	private onPartChanged: Function;
	private _index: number;
	private minIndex: number;
	private maxIndex: number;
	private ctx: RenderingContext2D;
	private width: number;
	private height: number;
	private _color?: any;
	private imageRef!: ImageRef;

	constructor(part: string, initialIndex: any, minIndex: number, maxIndex: number, onPartChanged: Function) {
		this.part = part;
		this.onPartChanged = onPartChanged;
		this._index = parseInt(initialIndex, 10);
		this.minIndex = minIndex;
		this.maxIndex = maxIndex;

		const canvas = document.getElementById('setoutfit' + part + 'canvas') as HTMLCanvasElement;
		canvas.style.margin = "5px";
		this.ctx = canvas.getContext("2d")!;
		this.width = canvas.width;
		this.height = canvas.height;
		this._color = undefined;
		this.loadPartSprite();
	}


	private indexString(index: number) {
		if (index > -1 && index < 100) {
			if (index < 10) {
				return "00" + index;
			} else {
				return "0" + index;
			}
		}
		return "" + index;
	}


	async draw() {
		this.ctx.fillStyle = "white";
		this.ctx.fillRect(0, 0, this.width, this.height);

		let currentImage = this.imageRef;
		await currentImage.waitFor();
		if (currentImage === this.imageRef && currentImage.image) {
			this.ctx.drawImage(currentImage.image, -48, -128);
		}
	}

	get index() {
		return this.indexString(this._index);
	}

	set index(newIndex: any) {
		this._index = parseInt(newIndex, 10);
		this.loadPartSprite();
		this.draw();
	}

	set color(newColor: any) {
		this._color = newColor;
		this.loadPartSprite();
		this.onPartChanged();
		this.draw();
	}

	previous() {
		this._index--
		if (this._index < this.minIndex) {
			this._index = this.maxIndex;
		}
		this.loadPartSprite();
		this.onPartChanged();
		this.draw();
	}

	next() {
		this._index++;
		if (this._index > this.maxIndex) {
			this._index = this.minIndex;
		}
		this.loadPartSprite();
		this.onPartChanged();
		this.draw();
	}

	private loadPartSprite() {
		this.imageRef?.free();
		let suffix = ".png";
		if (this.part === "body" && stendhal.config.getBoolean("effect.no-nude")) {
			suffix = "-nonude.png";
		}

		// FIXME: need to draw "busty" dress variants for body index 1

		let fname = Paths.sprites + "/outfit/" + this.part + "/" + this.indexString(this._index) + suffix;
		if (this._color != null) {
			this.imageRef = images.load(fname, "trueColor", this._color);
		} else {
			this.imageRef = images.load(fname);
		}
	}

	close() {
		this.imageRef?.free();
	}
}
