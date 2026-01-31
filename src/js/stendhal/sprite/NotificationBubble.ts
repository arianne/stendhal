/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { TextBubble } from "./TextBubble";

import { Color } from "../data/color/Color";

import { NotificationType } from "../util/NotificationType";
import { Speech } from "../util/Speech";
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";
import { singletons } from "../SingletonRepo";
import { HTMLImageElementUtil } from "./image/HTMLImageElementUtil";


export class NotificationBubble extends TextBubble {

	private mtype: string;
	private lines: string[];
	private profile?: HTMLImageElement;
	private profileName?: string;
	private lmargin = 4;


	constructor(mtype: string, text: string, profile?: string) {
		super(text);
		this.mtype = mtype;
		this.profileName = profile;

		this.duration = Math.max(
				TextBubble.STANDARD_DUR,
				this.text.length * TextBubble.STANDARD_DUR / 50);

		const linewrap = 30;
		const wordbreak = 60;
		this.lines = [];

		let words = text.split("\t").join(" ").split(" ");
		let nextline = "";
		for (const w of words) {
			if (nextline) {
				nextline += " ";
			}
			nextline += w;

			if (nextline.length > wordbreak) {
				this.lines.push(nextline.substr(0, wordbreak) + "-");
				nextline = "-" + nextline.substr(wordbreak);
			} else if (nextline.length >= linewrap) {
				this.lines.push(nextline);
				nextline = "";
			}
		}
		if (nextline) {
			this.lines.push(nextline);
		}

		if (profile) {
			// FIXME: first drawing of profile may still be delayed on slower systems
			// cache profile image at construction
			this.profile = new Image();
			this.loadProfileSprite();
		}
	}

	override draw(ctx: RenderingContext2D): boolean {
		const screenTop = stendhal.ui.gamewindow.offsetY;
		const screenBottom = screenTop + ctx.canvas.height;
		const screenLeft = stendhal.ui.gamewindow.offsetX;
		const screenCenterX = screenLeft + (ctx.canvas.width / 2);


		// get width & height of text
		const fontsize = 14;
		const lheight = fontsize + 6;
		ctx.lineWidth = 2;
		ctx.font = fontsize + "px sans-serif";
		ctx.fillStyle = "#ffffff";
		ctx.strokeStyle = "#000000";

		const lcount = this.lines.length;
		if (this.width < 0 || this.height < 0) {
			let longest = 0;
			for (let li = 0; li < lcount; li++) {
				let measurement = ctx.measureText(this.lines[li]);
				if (measurement.width > longest) {
					longest = measurement.width;
				}
			}

			this.width = longest + (this.lmargin * 2);
			this.height = lcount * lheight;
		}
		this.x = screenCenterX - (this.width / 2);
		// Note: border is 1 pixel
		this.y = screenBottom - this.height + TextBubble.adjustY - 1;


		if (this.profile) {
			if (!this.profile.complete || !this.profile.height) {
				this.loadProfileSprite();
			}
			if (this.profile.complete && this.profile.height) {
				ctx.drawImage(this.profile, this.x - 48, this.y - 16);
			}
			Speech.drawBubbleRounded(ctx, this.x, this.y - 15,
					this.width, this.height);
		} else {
			Speech.drawBubble(ctx, this.x, this.y, this.width,
					this.height);
		}

		ctx.fillStyle = NotificationType[this.mtype] || Color.BLACK;

		let sy = this.y;
		for (let li = 0; li < lcount; li++) {
			ctx.fillText(this.lines[li], this.x + this.lmargin, sy);
			sy += lheight;
		}

		return this.expired();
	}

	/**
	 * Loads a profile image to be drawn with text.
	 */
	private loadProfileSprite() {
		const img = singletons.getSpriteStore().get(Paths.sprites
				+ "/npc/" + this.profileName + ".png");
		if (img.complete && img.height) {
			this.profile = HTMLImageElementUtil.getAreaOf(img, 48, 48, 48, 128);
		}
	}
}
