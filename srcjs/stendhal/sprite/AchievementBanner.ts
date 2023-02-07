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

import { TextBubble } from "./TextBubble";
import { BackgroundPainter } from "../util/BackgroundPainter";

declare const stendhal: any;


export class AchievementBanner extends TextBubble {

	private title: string;
	private banner: BackgroundPainter;
	private icon: HTMLImageElement;

	private font = "normal 14px " + stendhal.config.get("ui.font.tlog");
	private fontT = "normal 20px " + stendhal.config.get("ui.font.tlog");


	constructor(cat: string, title: string, desc: string) {
		super(desc);
		this.title = title;
		this.banner = new BackgroundPainter(stendhal.paths.gui
				+ "/banner_background.png");
		this.icon = stendhal.data.sprites.get(stendhal.paths.achievements
				+ "/" + cat.toLowerCase() + ".png");

		/* keep achievements on the screen a bit longer since they
		 * don't leave a line in the chat log
		 */
		this.duration = TextBubble.STANDARD_DUR * 4;

		const gamewindow =
				<HTMLCanvasElement> document.getElementById("gamewindow")!;

		// FIXME: set banner dimensions based on text length
		this.width = 480;
		this.height = 96;

		this.x = (gamewindow.width / 2) - (this.width / 2);
		this.y = gamewindow.height - this.height;
	}

	override draw(ctx: CanvasRenderingContext2D): boolean {
		const targetX = stendhal.ui.gamewindow.offsetX + this.x;
		const targetY = stendhal.ui.gamewindow.offsetY + this.y;

		// FIXME:
		const textX = targetX + (this.width / 2) + 16;
		const textY = targetY + (this.height / 2) + 10;
		const iconX = targetX + (this.width / 2) - this.icon.width - 16;
		const iconY = targetY + (this.height / 2) - (this.icon.height * 0.75);

		this.banner.paint(ctx, targetX, targetY, this.width,
				this.height);
		if (this.icon.height && this.icon.complete) {
			ctx.drawImage(this.icon, 0, 0, this.icon.width,
					this.icon.height, iconX, iconY, this.icon.width,
					this.icon.height);
		}
		ctx.fillStyle = "#000000";
		ctx.font = this.fontT;
		ctx.fillText(this.title, textX, textY-25);
		ctx.font = this.font;
		ctx.fillText(this.text, textX, textY);

		return this.expired();
	}

	override getX(): number {
		return stendhal.ui.gamewindow.offsetX + this.x;
	}

	override getY(): number {
		return stendhal.ui.gamewindow.offsetY + this.y;
	}
}
