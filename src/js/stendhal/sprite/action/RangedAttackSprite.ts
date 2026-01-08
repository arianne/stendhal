/***************************************************************************
 *                    Copyright © 2023-2024 - Stendhal                     *
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
import { Paths } from "../../data/Paths";
import { singletons } from "../../SingletonRepo";

import { stendhal } from "../../stendhal";


export class RangedAttackSprite extends AttackSprite {

	private readonly dir: number;
	private readonly targetX: number;
	private readonly targetY: number;
	private readonly color: string;
	private readonly image = singletons.getSpriteStore().get(Paths.sprites + "/combat/ranged.png");
	private readonly weapon?: string;


	constructor(source: RPEntity, target: RPEntity, color: string, weapon?: string) {
		super();
		this.dir = source["dir"];
		this.targetX = (target.x + target.width / 2) * 32;
		this.targetY = (target.y + target.height / 2) * 32;
		this.color = color;
		this.weapon = weapon;
	}

	override draw(ctx: RenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number): boolean {
		// FIXME: alignment with entity is not correct

		var dtime = Date.now() - this.initTime;
		// We can use fractional "frame" for the lines. Just
		// draw the arrow where it should be at the moment.
		var frame = Math.min(dtime / 60, 4);

		var startX = x + entityWidth / 4;
		var startY = y + entityHeight / 4;

		var yLength = (this.targetY - startY) / 4;
		var xLength = (this.targetX - startX) / 4;

		startY += frame * yLength;
		var endY = startY + yLength;
		startX += frame * xLength;
		var endX = startX + xLength;

		ctx.strokeStyle = this.color;
		ctx.lineWidth = 2;
		ctx.moveTo(startX, startY);
		ctx.lineTo(endX, endY);
		ctx.stroke();

		// draw bow
		if (this.weapon === "ranged" && this.image.height) {
			frame = Math.floor(Math.min(dtime / 60, 3));
			const yRow = this.dir - 1;
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

			ctx.drawImage(this.image, frame * drawWidth, yRow * drawHeight,
					drawWidth, drawHeight, sx, sy, drawWidth, drawHeight);
		}
		return this.expired();
	}
}
