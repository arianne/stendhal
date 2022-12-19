/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

export class OutfitPartSelector {
		private _part: string;
		private _onPartChanged: Function;
		private _index: number;
		private _maxindex: number;
		private _ctx: CanvasRenderingContext2D;
		private _width: number;
		private _height: number;
		private _color?: any;
		private _image?: Promise<CanvasImageSource>;

	constructor(part: string, initialIndex: any, maxindex: number, onPartChanged: Function) {
		this._part = part;
		this._onPartChanged = onPartChanged;
		this._index = parseInt(initialIndex, 10);
		this._maxindex = maxindex;

		const canvas = document.getElementById('setoutfit' + part + 'canvas') as HTMLCanvasElement;
		canvas.style.margin = "5px";
		this._ctx = canvas.getContext("2d")!;
		this._width = canvas.width;
		this._height = canvas.height;
		this._color = undefined;
		this._image = undefined;
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


	draw() {
		const image = this._getPartSprite(this._part, this._index, this._color);
		this._image = image;
		this._ctx.fillStyle = "white";
		this._ctx.fillRect(0, 0, this._width, this._height);

		image.then((img: CanvasImageSource) => {
			this._ctx.drawImage(img, -48, -128);
			this._onPartChanged();
		});
	}

	get image() {
		return this._image;
	}

	get index() {
		return this.indexString(this._index);
	}

	set index(newIndex: any) {
		this._index = parseInt(newIndex, 10);
		this.draw();
	}

	set color(newColor: any) {
		this._color = newColor;
		this.draw();
	}

	previous() {
		const numOutfits = this._maxindex + 1;
		this._index += this._maxindex;
		this._index %= numOutfits;
		this.draw();
		this._onPartChanged();
	}

	next() {
		const numOutfits = this._maxindex + 1;
		this._index++;
		this._index %= numOutfits;
		this.draw();
		this._onPartChanged();
	}

	_getPartSprite(part: string, index: number, color = null): Promise<CanvasImageSource> {
		let suffix = ".png";
		if (this._part === "body" && stendhal.config.getBoolean("gamescreen.nonude")) {
			suffix = "-nonude.png";
		}

		// FIXME: need to draw "busty" dress variants for body index 1

		const fname = stendhal.paths.sprites + "/outfit/" + part + "/" + this.indexString(index) + suffix;
		if (color != null) {
			return stendhal.data.sprites.getFilteredWithPromise(fname, "trueColor", color);
		}
		return stendhal.data.sprites.getWithPromise(fname);
	}
}
