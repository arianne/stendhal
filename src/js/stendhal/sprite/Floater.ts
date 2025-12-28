/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RenderingContext2D } from "util/Types";
import { TextSprite } from "./TextSprite";

/**
 * a text floating up from an Entity
 */
export class Floater {
	private initTime: number;
	private textSprite: TextSprite;

	constructor(private readonly message: string, private readonly color: string) {
		this.initTime = Date.now();
		this.textSprite = new TextSprite(this.message, this.color, "12px sans-serif")
	}

	draw(ctx: RenderingContext2D, x: number, y: number): boolean {
		let textOffset = this.textSprite.getTextMetrics(ctx).width / 2;
		var timeDiff = Date.now() - this.initTime;
		this.textSprite.draw(ctx, x - textOffset, y - timeDiff / 50);
		return (timeDiff > 2000);
	}

}
