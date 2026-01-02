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
import { Direction } from "../../util/Direction";
import { RenderingContext2D } from "util/Types";
import { singletons } from "../../SingletonRepo";


export class MeleeAttackSprite extends AttackSprite {

	/** Direction entity is facing. */
	private readonly dir: Direction;
	/** Set of 3 frame images displayed in sequence. */
	private readonly frames: HTMLImageElement[];
	/** Pixel offsets for each frame on X axis. */
	private readonly offsetX: number[];
	/** Pixel offsets for each frame on Y axis. */
	private readonly offsetY: number[];


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
		this.offsetX = [];
		this.offsetY = [];
		const rot = 90 * (this.dir.val - 1);
		imagePath = imagePath.replace(/\.png$/, "");
		this.frames = [
			//~ singletons.getSpriteStore().getRotated(imagePath + "_rot45.png", rot-90),
			undefined,
			singletons.getSpriteStore().getRotated(imagePath + ".png", rot),
			//~ singletons.getSpriteStore().getRotated(imagePath + "_rot45.png", rot)
			undefined
		];
		if (this.frames[1].complete) {
			this.onFrameReady(this.frames[1]);
		} else {
			this.frames[1].onload = () => {
				this.frames[1].onload = null;
				this.onFrameReady(this.frames[1]);
			};
		}
	}

	/**
	 * Configures frame offsets when at least one frame image is ready.
	 *
	 * @param frame {HTMLImageElement}
	 *   Image used to calculate offsets.
	 */
	private onFrameReady(frame: HTMLImageElement) {
		// NOTE: all frame images should be same dimensions
		const halfW = Math.floor(frame.width / 2);
		const halfH = Math.floor(frame.height / 2);
		// offset for facing direction
		// FIXME: alignment needs improvement
		switch (this.dir) {
			case Direction.UP:
				this.offsetX[0] = -frame.width;
				this.offsetX[1] = -halfW;
				this.offsetX[2] = 0;
				this.offsetY[0] = -frame.height;
				this.offsetY[1] = -frame.height;
				this.offsetY[2] = -frame.height;
				break;
			case Direction.DOWN:
				this.offsetX[0] = 0;
				this.offsetX[1] = -halfW;
				this.offsetX[2] = -frame.width;
				this.offsetY[0] = 0;
				this.offsetY[1] = 0;
				this.offsetY[2] = 0;
				break;
			case Direction.LEFT:
				this.offsetX[0] = -frame.width;
				this.offsetX[1] = -frame.width;
				this.offsetX[2] = -frame.width;
				this.offsetY[0] = 0;
				this.offsetY[1] = -halfH;
				this.offsetY[2] = -frame.height;
				break;
			default: // RIGHT
				this.offsetX[0] = 0;
				this.offsetX[1] = 0;
				this.offsetX[2] = 0;
				this.offsetY[0] = -frame.height;
				this.offsetY[1] = -halfH;
				this.offsetY[2] = 0;
		}
	}

	override draw(ctx: RenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number): boolean {
		const dtime = Date.now() - this.initTime;
		const frameIndex = Math.floor(Math.min(dtime / 60, 2));
		// TODO: add rotated images for each attack type
		//~ const frame = this.frames[frameIndex];
		const frame = this.frames[1];
		if (!frame || !frame.height) {
			return this.expired();
		}

		const cx = x + Math.floor(entityWidth / 2);
		const cy = y + Math.floor(entityHeight / 2);
		// FIXME: offset should compensate for entity width/height
		//~ const dx = cx + this.offsetX[frameIndex];
		//~ const dy = cy + this.offsetY[frameIndex];
		const dx = cx + this.offsetX[1];
		const dy = cy + this.offsetY[1];

		ctx.drawImage(frame, 0, 0, frame.width, frame.height, dx, dy, frame.width, frame.height);
		return this.expired();
	}
}
