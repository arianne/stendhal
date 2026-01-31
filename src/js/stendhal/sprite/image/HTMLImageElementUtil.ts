/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { store } from "../../data/SpriteStore";

export class HTMLImageElementUtil {

	/**
	 * Get an image element whose image data is an area of a specified image.
	 * If the area matches the original image, the image itself is returned.
	 * Otherwise <em>a copy</em> of the image data is returned. This is meant
	 * to be used for obtaining the drag image for drag and drop.
	 *
	 * @param image original image
	 * @param width width of the area
	 * @param height height of the area
	 * @param {number=} offsetX optional. left x coordinate of the area
	 * @param {number=} offsetY optional. top y coordinate of the area
	 */
	static getAreaOf(image: HTMLImageElement, width: number, height: number,
		offsetX?: number, offsetY?: number): any {
		try {
			offsetX = offsetX || 0;
			offsetY = offsetY || 0;
			if ((image.width === width) && (image.height === height)
				&& (offsetX === 0) && (offsetY === 0)) {
				return image;
			}
			var canvas = document.createElement("canvas") as HTMLCanvasElement;
			canvas.width = width;
			canvas.height = height;
			var ctx = canvas.getContext("2d")!;
			ctx.drawImage(image, offsetX, offsetY, width, height, 0, 0, width, height);
			// Firefox would be able to use the canvas directly as a drag image, but
			// Chrome does not. This should work in any standards compliant browser.
			// TODO: Check if that is still true
			var newImage = new Image();
			newImage.src = canvas.toDataURL("image/png");
			return newImage;
		} catch (err) {
			if (err instanceof DOMException) {
				return store.getFailsafe();
			} else {
				// don't ignore other errors
				throw err;
			}
		}
	}
}
