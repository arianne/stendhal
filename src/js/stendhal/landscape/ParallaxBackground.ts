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

import { RenderingContext2D } from "util/Types";
import { singletons } from "../SingletonRepo";


export class ParallaxBackground {

	/** Default scrol rate of parallax background (1/4). */
	public static readonly SCROLL = 0.25;

	/** Tiled image to be drawn. */
	private image?: HTMLImageElement;

	/** Singleton instance. */
	private static instance: ParallaxBackground;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): ParallaxBackground {
		if (!ParallaxBackground.instance) {
			ParallaxBackground.instance = new ParallaxBackground();
		}
		return ParallaxBackground.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Sets image to be drawn.
	 *
	 * @param {string} name
	 *   Relative path (exluding .png filename suffix) to image inside "data/maps/parallax" directory
	 *   or `undefined` to unset.
	 * @param {number} width
	 *   Map pixel width.
	 * @param {number} height
	 *   Map pixel height.
	 */
	setImage(name: string, width: number, height: number) {
		const fullPath = singletons.getPaths().parallax + "/" + name + ".png";
		this.image = singletons.getSpriteStore().get(fullPath);

		/* FIXME:
		//this.image = singletons.getTileStore().getParallax(name, ParallaxBackground.SCROLL, width, height);
		singletons.getTileStore().getParallaxPromise(name, ParallaxBackground.SCROLL, width, height)
				.then(image => {
					this.image = image;
				}).catch(error => {
					console.error("Error setting parallax background \"" + name + "\"\n", error);
				});
		*/
	}

	/**
	 * Unsets parallax background image.
	 */
	reset() {
		this.image = undefined;
	}

	draw(ctx: RenderingContext2D, offsetX: number, offsetY: number) {
		if (!this.image || !this.image.height) {
			return;
		}

		// FIXME: seams are visible when walking
		let dy = offsetY - ((offsetY / 4) % this.image.height);
		for (dy; dy < this.image.height * 100; dy += this.image.height) {
			let dx = offsetX - ((offsetX / 4) % this.image.width);
			for (dx; dx < this.image.width * 100; dx += this.image.width) {
				ctx.drawImage(this.image, dx, dy);
			}
		}

		/* TODO: use the following when `setImage` fixed
		const tileLeft = offsetX - (offsetX * ParallaxBackground.SCROLL);
		const tileTop = offsetY - (offsetY * ParallaxBackground.SCROLL);
		ctx.drawImage(this.image, tileLeft, tileTop);
		*/
	}
}
