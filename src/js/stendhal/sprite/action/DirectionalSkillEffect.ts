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

import { SkillEffect } from "./SkillEffect";

import { Entity } from "../../entity/Entity";

import { Direction } from "../../util/Direction";
import { RenderingContext2D } from "util/Types";


/**
 * Represents an entity animation overlay with variations for facing direction.
 */
export class DirectionalSkillEffect extends SkillEffect {

	private readonly entity: Entity;


	/**
	 * Creates a new skill effect sprite.
	 *
	 * @param {Entity} entity
	 *   Entity associated with this effect.
	 * @param {string} name
	 *   Image filename (excluding .png suffix).
	 * @param {number} [delay=100]
	 *   Time, in milliseconds, for which each frame should be displayed. Any value less than 1
	 *   results in using a default of 100ms.
	 * @param {number} [duration=0]
	 *   Time, in milliseconds, sprite drawing should persist. Any value less than 1 signifies that
	 *   sprite should not expire.
	 */
	constructor(entity: Entity, name: string, delay=100, duration=0) {
		super(name, delay, duration);
		this.entity = entity;
	}

	protected override drawInternal(ctx: RenderingContext2D, colIdx: number, x: number,
			y: number, drawWidth: number, drawHeight: number) {
		const dir = Direction.VALUES[parseInt(this.entity["dir"], 10)];
		let rowIdx = dir.val - 1;
		if (rowIdx < 0) {
			rowIdx = Direction.DOWN.val - 1; // default is third row (down)
		}
		ctx.drawImage(this.image, colIdx*drawWidth, rowIdx*drawHeight, drawWidth, drawHeight, x, y,
				drawWidth, drawHeight);
	}
}
