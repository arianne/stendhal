/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { TextBubbleSprite } from "./TextBubbleSprite";
import { RPEntity } from "../entity/RPEntity";

declare var stendhal: any;


export class SpeechBubbleSprite extends TextBubbleSprite {

	private entity: RPEntity;


	constructor(text: string, entity: RPEntity) {
		super((text.length > 30) ? (text.substring(0, 30) + "...") : text);
		this.entity = entity;
	}

	override draw(ctx: CanvasRenderingContext2D): boolean {
		this.x = this.entity["_x"] * 32 + (32 * this.entity["width"]);
		this.y = this.entity["_y"] * 32 - 16 - (32 * (this.entity["height"]
			- 1));

		ctx.lineWidth = 2;
		ctx.font = "14px Arial";
		ctx.fillStyle = '#ffffff';
		ctx.strokeStyle = "#000000";

		if (this.width < 0 || this.height < 0) {
			// get width of text
			this.width = ctx.measureText(this.text).width + 8;
			this.height = 20;
		}

		this.entity.drawSpeechBubbleRounded(ctx, this.x, this.y - 15,
				this.width, this.height);

		ctx.fillStyle = "#000000";
		ctx.fillText(this.text, this.x + 4, this.y);

		// prevent new listener being added for every redraw
		if (typeof(this.onRemovedAction) === "undefined") {
			// add click listener to remove chat bubble
			const listener = (e: MouseEvent) => {
				this.onClick(e);
			}
			ctx.canvas.addEventListener("click", listener);
			this.onRemovedAction = function() {
				ctx.canvas.removeEventListener("click", listener);
			};
		}

		return Date.now() > this.timeStamp + 2000 + 20 * this.text.length;
	}
}
