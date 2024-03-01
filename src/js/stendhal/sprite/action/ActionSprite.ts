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


export abstract class ActionSprite {

	protected readonly initTime: number;


	constructor() {
		this.initTime = Date.now();
	}

	public abstract draw(ctx: CanvasRenderingContext2D, x: number, y: number, entityWidth: number,
			entityHeight: number): void;

	public expired(): boolean {
		return Date.now() - this.initTime > 180;
	}
}
