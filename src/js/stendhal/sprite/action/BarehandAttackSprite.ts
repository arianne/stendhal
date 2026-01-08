/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { AttackSprite } from "./AttackSprite";
import { RPEntity } from "../../entity/RPEntity";
import { RenderingContext2D } from "util/Types";

import { stendhal } from "../../stendhal";


export class BarehandAttackSprite extends AttackSprite {

	private readonly dir: number;
	private readonly image: HTMLImageElement;


	constructor(source: RPEntity, image: HTMLImageElement) {
		super();
		this.dir = source["dir"];
		this.image = image;
	}

	override draw(ctx: RenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number): boolean {
		if (!this.image || !this.image.height) {
			return this.expired();
		}

		const dtime = Date.now() - this.initTime;
		const frameIndex = Math.floor(Math.min(dtime / 60, 2));
		const drawWidth = this.image.width / 3;
		const drawHeight = this.image.height / 4;
		const centerX = x + (entityWidth - drawWidth) / 2;
		const centerY = y + (entityHeight - drawHeight) / 2;

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

		ctx.drawImage(this.image, frameIndex * drawWidth, (this.dir - 1) * drawHeight,
				drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);
		return this.expired();
	}
}
