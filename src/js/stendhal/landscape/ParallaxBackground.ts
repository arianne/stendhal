/***************************************************************************
 *                  Copyright © 2024-2026 - Faiumoni e. V.                 *
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
import { ImageRef } from "sprite/image/ImageRef";
import { images } from "sprite/image/ImageManager";

export class ParallaxBackground {

	/** Default scrol rate of parallax background (1/4). */
	public static readonly SCROLL = 0.25;

	/** Tiled image to be drawn. */
	private imageRef?: ImageRef;

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
		let fullPath = singletons.getPaths().parallax + "/" + name + ".png";
		this.imageRef = images.load(fullPath);

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
		this.imageRef?.free();
		this.imageRef = undefined;
	}

	draw(ctx: RenderingContext2D, offsetX: number, offsetY: number) {
		if (!this.imageRef?.image) {
			return;
		}
		let image = this.imageRef.image;

		// FIXME: seams are visible when walking
		let dy = offsetY - ((offsetY / 4) % image.height);
		for (dy; dy < image.height * 100; dy += image.height) {
			let dx = offsetX - ((offsetX / 4) % image.width);
			for (dx; dx < image.width * 100; dx += image.width) {
				ctx.drawImage(image, dx, dy);
			}
		}

		/* TODO: use the following when `setImage` fixed
		const tileLeft = offsetX - (offsetX * ParallaxBackground.SCROLL);
		const tileTop = offsetY - (offsetY * ParallaxBackground.SCROLL);
		ctx.drawImage(this.image, tileLeft, tileTop);
		*/
	}
}
