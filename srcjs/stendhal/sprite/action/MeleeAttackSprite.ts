/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ActionSprite } from "./ActionSprite";
import { RPEntity } from "../../entity/RPEntity";

declare var stendhal: any;


export class MeleeAttackSprite extends ActionSprite {

	private readonly dir: number;
	private readonly image: HTMLImageElement;
	private readonly barehand: boolean;


	constructor(source: RPEntity, image: HTMLImageElement, barehand: boolean) {
		super();
		this.dir = source["dir"];
		this.image = image;
		this.barehand = barehand;
	}

	public override draw(ctx: CanvasRenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number) {
		if (!this.image || !this.image.height) {
			return;
		}

		const dtime = Date.now() - this.initTime;
		const frameIndex = Math.floor(Math.min(dtime / 60, 3));

		let yRow, frame, drawWidth, drawHeight;
		if (this.barehand) {
			yRow = this.dir - 1;
			frame = frameIndex;
			drawWidth = this.image.width / 3;
			drawHeight = this.image.height / 4;
		} else {
			yRow = 0;
			frame = 0;
			drawWidth = this.image.width;
			drawHeight = this.image.height;
		}

		var centerX = x + (entityWidth - drawWidth) / 2;
		var centerY = y + (entityHeight - drawHeight) / 2;

		// offset sprite for facing direction
		let sx, sy;
		switch (this.dir+"") {
			case "1": // UP
				sx = centerX + (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = y - (stendhal.ui.gamewindow.targetTileHeight * 1.5);
				break;
			case "3": // DOWN
				sx = centerX;
				sy = y + entityHeight - drawHeight + (stendhal.ui.gamewindow.targetTileHeight / 2);
				break;
			case "4": // LEFT
				sx = x - (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = centerY - (stendhal.ui.gamewindow.targetTileHeight / 2);
				break;
			case "2": // RIGHT
				sx = x + entityWidth - drawWidth + (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = centerY; // - ICON_OFFSET; // ICON_OFFSET = 8 in Java client
				break;
			default:
				sx = centerX;
				sy = centerY;
		}

		ctx.drawImage(this.image, frame * drawWidth, yRow * drawHeight,
				drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);
	}
}
