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

import { ImageWithDimensions } from "data/ImageWithDimensions";

export class HTMLImageElementUtil {

	/**
	 * Get an image element whose image data is an area of a specified image.
	 * If the area matches the original image, the image itself is returned.
	 * Otherwise <em>a copy</em> of the image data is returned. This is meant
	 * to be used for obtaining the drag image for drag and drop.
	 *
	 * This method intentionally returns an HTMLImageElement because Chrome is
	 * not able to use an HTMLCanvasElement as drag image. Firefox does support
	 * this. But neither browser is able to use an ImageBitmap.
	 *
	 * @param image original image
	 * @param width width of the area
	 * @param height height of the area
	 * @param offsetX optional. left x coordinate of the area
	 * @param offsetY optional. top y coordinate of the area
	 */
	static getAreaOf(image: CanvasImageSource & ImageWithDimensions, width: number, height: number,
		offsetX?: number, offsetY?: number): any {

		offsetX = offsetX || 0;
		offsetY = offsetY || 0;
		if (image instanceof HTMLImageElement
			&& (image.width === width) && (image.height === height)
			&& (offsetX === 0) && (offsetY === 0)) {
			return image;
		}
		let canvas = document.createElement("canvas") as HTMLCanvasElement;
		canvas.width = width;
		canvas.height = height;
		let ctx = canvas.getContext("2d", {willReadFrequently: true})!;
		try {
			ctx.drawImage(image, offsetX, offsetY, width, height, 0, 0, width, height);
		} catch (err) {
			console.log(err);
		}
		let newImage = new Image();
		newImage.src = canvas.toDataURL("image/png");
		return newImage;
	}
}
