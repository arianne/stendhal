/***************************************************************************
 *                 Copyright © 2023-2026 - Faiumoni e. V.                  *
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
import { RPEntity } from "../../entity/RPEntity";
import { AttackSprite } from "./AttackSprite";

import { images } from "sprite/image/ImageManager";
import { ImageRef } from "sprite/image/ImageRef";
import { ViewPort } from "ui/ViewPort";


export class BarehandAttackSprite extends AttackSprite {

	private readonly dir: number;
	private readonly imageRef: ImageRef;


	constructor(source: RPEntity, imagePath: string) {
		super();
		this.dir = source["dir"];
		this.imageRef = images.load(imagePath);
	}

	override draw(ctx: RenderingContext2D, x: number, y: number, 
			entityWidth: number, entityHeight: number): boolean {
		
		let image = this.imageRef.image;
		if (!image) {
			return this.expired();
		}
				
		let viewPort = ViewPort.get();
		const dtime = Date.now() - this.initTime;
		const frameIndex = Math.floor(Math.min(dtime / 60, 2));
		const drawWidth = image.width / 3;
		const drawHeight = image.height / 4;
		const centerX = x + (entityWidth - drawWidth) / 2;
		const centerY = y + (entityHeight - drawHeight) / 2;

		// offset sprite for facing direction
		let sx, sy;
		switch (this.dir+"") {
			case "1": // UP
				sx = centerX + (viewPort.targetTileWidth / 2);
				sy = y - (viewPort.targetTileHeight * 1.5);
				break;
			case "3": // DOWN
				sx = centerX;
				sy = y + entityHeight - drawHeight + (viewPort.targetTileHeight / 2);
				break;
			case "4": // LEFT
				sx = x - (viewPort.targetTileWidth / 2);
				sy = centerY - (viewPort.targetTileHeight / 2);
				break;
			case "2": // RIGHT
				sx = x + entityWidth - drawWidth + (viewPort.targetTileWidth / 2);
				sy = centerY; // - ICON_OFFSET; // ICON_OFFSET = 8 in Java client
				break;
			default:
				sx = centerX;
				sy = centerY;
		}

		ctx.drawImage(image, 
				frameIndex * drawWidth, (this.dir - 1) * drawHeight,
				drawWidth, drawHeight, 
				sx, sy, 
				drawWidth, drawHeight);
		return this.expired();
	}

	override free(): void {
		this.imageRef?.free();
	}

}
