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

import { RenderingContext2D } from "util/Types";
import { OverlaySpriteImpl } from "../OverlaySpriteImpl";


/**
 * Sprite representing an attack animation.
 */
export abstract class AttackSprite implements OverlaySpriteImpl {

	protected readonly initTime: number;


	constructor() {
		this.initTime = Date.now();
	}

	abstract draw(ctx: RenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number): boolean;

	expired(): boolean {
		return Date.now() - this.initTime > 180;
	}
}
