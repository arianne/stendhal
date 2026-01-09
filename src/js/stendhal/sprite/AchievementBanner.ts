/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
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
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";
import { stendhal } from "../stendhal";

export class AchievementBanner extends TextBubble {

	private title: string;
	private banner: BackgroundPainter;
	private icon: HTMLImageElement;

	private font = "normal 14px " + stendhal.config.get("font.travel-log");
	private fontT = "normal 20px " + stendhal.config.get("font.travel-log");

	private innerWidth = 0;
	private innerHeight = 0;
	private readonly padding = 32;


	constructor(cat: string, title: string, desc: string) {
		super(desc);
		this.title = title;
		const bg = singletons.getSpriteStore().get(Paths.gui + "/banner_background.png");
		this.banner = new BackgroundPainter(bg);
		this.icon = singletons.getSpriteStore().get(Paths.achievements
				+ "/" + cat.toLowerCase() + ".png");

		/* keep achievements on the screen a bit longer since they
		 * don't leave a line in the chat log
		 */
		this.duration = TextBubble.STANDARD_DUR * 4;

		const gamewindow = document.getElementById("viewport")! as HTMLCanvasElement;

		const td = this.getTextDimensions(gamewindow.getContext("2d")!);
		this.innerWidth = td.width + this.padding; // add padding between icon & text
		this.innerHeight = td.height;
		this.width = this.innerWidth + (this.padding * 2); // add left & right padding
		this.height = bg.height || this.innerHeight;

		this.x = (gamewindow.width / 2) - (this.width / 2);
		this.y = gamewindow.height - this.height;
	}

	override draw(ctx: RenderingContext2D): boolean {
		const targetX = stendhal.ui.gamewindow.offsetX + this.x;
		const targetY = stendhal.ui.gamewindow.offsetY + this.y;

		const iconX = targetX + (this.width / 2) - (this.innerWidth / 2);
		const iconY = targetY + (this.height / 2) - (this.icon.height * 0.75);
		const textX = iconX + this.icon.width + this.padding;
		// TODO: user inner height (text height) for centering vertically
		const textY = targetY + (this.height / 2) + 10;

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

	private getTextDimensions(ctx: RenderingContext2D): any {
		const ret = {} as any;
		ctx.font = this.font;
		let m = ctx.measureText(this.text);
		ret.width = m.width;
		ctx.font = this.fontT;
		m = ctx.measureText(this.title);
		ret.width = Math.max(ret.width, m.width);
		// FIXME: how to find text height
		ret.height = 96;
		return ret;
	}

	override getX(): number {
		return stendhal.ui.gamewindow.offsetX + this.x;
	}

	override getY(): number {
		return stendhal.ui.gamewindow.offsetY + this.y;
	}
}
