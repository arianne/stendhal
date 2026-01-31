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

import { Component } from "../toolkit/Component";

import { Outfit } from "../../data/Outfit";

import { Direction } from "../../util/Direction";
import { StringUtil } from "../../util/StringUtil";


/**
 * Component to preview an entity's outfit sprite.
 *
 * FIXME:
 *   - character select previews don't draw after page refresh
 */
export class OutfitPreviewComponent extends Component {

	private dir: Direction;
	private index: number;
	private image?: ImageBitmap;
	private bgColor?: string;


	constructor() {
		super("outfit-preview-template");
		this.dir = Direction.DOWN;
		this.index = 1;
	}

	/**
	 * Sets current outfit for this preview & updates drawing.
	 *
	 * @param {string} outfit
	 *   Outfit formatted string ("body=0,head=2,eyes=1,...).
	 * @param {string=} coloring
	 *   Outfit coloring.
	 */
	setOutfit(outfit: string, coloring?: string) {
		const otemp = Outfit.build(outfit, coloring);
		otemp.toImage().then((image) => {
			this.image?.close();
			this.image = image;
			this.update();
		});
	}

	/**
	 * Sets facing direction to draw.
	 *
	 * Default is down.
	 *
	 * @param {Direction} dir
	 *   Facing direction.
	 */
	setDirection(dir: Direction) {
		this.dir = dir.val < 1 ? Direction.VALUES[1] : dir.val > 4 ? Direction.VALUES[4] : dir;
		this.update();
	}

	/**
	 * Increments facing direction to draw clockwise.
	 */
	nextDirection() {
		let dir = Direction.VALUES[this.dir.val + 1];
		if (typeof(dir) === "undefined") {
			dir = Direction.VALUES[1];
		}
		this.setDirection(dir);
	}

	/**
	 * Increments facing direction to draw counter-clockwise.
	 */
	prevDirection() {
		let dir = Direction.VALUES[this.dir.val - 1];
		if (typeof(dir) === "undefined" || dir.val === 0) {
			dir = Direction.VALUES[4];
		}
		this.setDirection(dir);
	}

	/**
	 * Sets frame index to draw.
	 *
	 * Default is 1 (center frame).
	 *
	 * @param {number} index
	 *   Frame index.
	 */
	setFrame(index: number) {
		this.index = index < 0 ? 0 : index > 2 ? 2 : index;
		this.update();
	}

	/**
	 * Sets or unsets background color to fill before drawing outfit.
	 *
	 * @param {string|undefined|null} color
	 *   Background color or `undefined|null` or empty string to unset.
	 */
	setBGColor(color: string|undefined|null) {
		if (!color || StringUtil.isEmpty(color)) {
			color = undefined;
		}
		this.bgColor = color;
	}

	/**
	 * Draws outfit in preview area.
	 */
	update() {
		if (!this.image || !this.image.height) {
			console.warn("outfit preview not ready");
			return;
		}
		const w = this.image.width / 3;
		const h = this.image.height / 4;
		const ctx = (this.componentElement as HTMLCanvasElement).getContext("2d")!;
		if (w !== ctx.canvas.width || h !== ctx.canvas.height) {
			console.warn("Image dimensions do not match preview canvas: " + w + "x" + h + " != "
					+ ctx.canvas.width + "x" + ctx.canvas.height);
		}
		if (this.bgColor) {
			ctx.fillStyle = this.bgColor;
			ctx.fillRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		} else {
			ctx.clearRect(0, 0, ctx.canvas.width, ctx.canvas.height);
		}
		ctx.drawImage(this.image, this.index * w, (this.dir.val - 1) * h, w, h, 0, 0, w, h);
	}

	close() {
		this.image?.close();
	}
}
