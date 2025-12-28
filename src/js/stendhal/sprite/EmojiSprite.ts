/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { RPEntity } from "../entity/RPEntity";


export class EmojiSprite {

	private timeStamp: number;
	private owner: RPEntity;
	private sprite: HTMLImageElement;

	constructor(sprite: HTMLImageElement, owner: RPEntity) {
		this.timeStamp = Date.now();
		this.owner = owner;
		this.sprite = sprite;
	}

	draw(ctx: RenderingContext2D): boolean {
		let x = this.owner["_x"] * 32 - 16;
		let y = this.owner["_y"] * 32 - 32;
		if (x < 0) {
			x = 0;
		}
		if (y < 0) {
			y = 0;
		}

		if (this.sprite.height) {
			ctx.drawImage(this.sprite, x, y);
		}

		// 5 seconds
		return Date.now() > this.timeStamp + 5000;
	}
}
