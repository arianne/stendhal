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

declare var stendhal: any;

import { ActionSprite } from "./ActionSprite";
import { RPEntity } from "../../entity/RPEntity";
import { Direction } from "../../util/Direction";


export class MeleeAttackSprite extends ActionSprite {

	/** Direction entity is facing. */
	private readonly dir: Direction;
	/** Sprite image. */
	private readonly image: HTMLImageElement;


	/**
	 * Creates a new attack sprite.
	 *
	 * @param source {entity.RPEntity.RPEntity}
	 *   Entity from which event occurs.
	 * @param imagePath {string}
	 *   Path to sprite image.
	 */
	constructor(source: RPEntity, imagePath: string) {
		super();
		this.dir = Direction.VALUES[source["dir"]];
		const rot = 90 * (this.dir.val - 1);
		// TODO: rotate left & right 45 degrees & offset to center on entity
		this.image = stendhal.data.sprites.getRotated(imagePath, rot);
	}

	public override draw(ctx: CanvasRenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number) {
		if (!this.image || !this.image.height) {
			return;
		}

		//~ const dtime = Date.now() - this.initTime;
		//~ const frameIndex = Math.floor(Math.min(dtime / 60, 2));
		const drawWidth = this.image.width;
		const drawHeight = this.image.height;
		var centerX = x + (entityWidth - drawWidth) / 2;
		var centerY = y + (entityHeight - drawHeight) / 2;

		// offset sprite for facing direction
		let sx, sy;
		switch (this.dir) {
			case Direction.UP:
				sx = centerX + (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = y - (stendhal.ui.gamewindow.targetTileHeight * 1.5);
				break;
			case Direction.DOWN:
				sx = centerX;
				sy = y + entityHeight - drawHeight + (stendhal.ui.gamewindow.targetTileHeight / 2);
				break;
			case Direction.LEFT:
				sx = x - (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = centerY - (stendhal.ui.gamewindow.targetTileHeight / 2);
				break;
			case Direction.RIGHT:
				sx = x + entityWidth - drawWidth + (stendhal.ui.gamewindow.targetTileWidth / 2);
				sy = centerY; // - ICON_OFFSET; // ICON_OFFSET = 8 in Java client
				break;
			default:
				sx = centerX;
				sy = centerY;
		}

		ctx.drawImage(this.image, 0, 0, drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);
	}
}
