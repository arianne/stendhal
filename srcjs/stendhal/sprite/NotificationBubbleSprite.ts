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
import { NotificationType } from "../util/NotificationType";

declare var stendhal: any;


export class NotificationBubbleSprite extends TextBubbleSprite {

	private mtype: string;
	private lines: string[];
	private entity: RPEntity;
	private profile?: HTMLImageElement;
	private lmargin = 4;


	constructor(mtype: string, text: string, entity: RPEntity, profile?: string) {
		super(text);
		this.mtype = mtype;
		this.entity = entity;

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
			// TODO: The image might not have been loaded here, so
			// so we need to retry getAreaOf in the drawing code.
			// But we still want to cache it once loading successed
			let img = stendhal.data.sprites.get("data/sprites/npc/" + profile + ".png");
			if (img.complete && img.height) {
				this.profile = stendhal.data.sprites.getAreaOf(img, 48, 48, 48, 128);
			}
		}
	}

	override draw(ctx: CanvasRenderingContext2D): boolean {
		const screenArea = document.getElementById("gamewindow")!.getBoundingClientRect();
		const screenTop = stendhal.ui.gamewindow.offsetY;
		const screenBottom = screenTop + screenArea.height;
		const screenLeft = stendhal.ui.gamewindow.offsetX;
		const screenCenterX = screenLeft + (screenArea.width / 2);

		const lcount = this.lines.length;

		let longest = "";
		for (let li = 0; li < lcount; li++) {
			if (this.lines[li].length > longest.length) {
				longest = this.lines[li];
			}
		}

		// get width & height of text
		const fontsize = 14;
		const lheight = fontsize + 6;
		const meas = ctx.measureText(longest);
		const width = meas.width + (this.lmargin * 2);
		const height = lcount * lheight;

		const x = screenCenterX - (width / 2);
		// FIXME: doesn't reach bottom of game window
		const y = screenBottom - height;

		ctx.lineWidth = 2;
		ctx.font = fontsize + "px sans-serif";
		ctx.fillStyle = "#ffffff";
		ctx.strokeStyle = "#000000";

		if (this.profile) {
			ctx.drawImage(this.profile, x - 48, y - 16);
			this.entity.drawSpeechBubbleRounded(ctx, x, y - 15, width, height);
		} else {
			this.entity.drawSpeechBubble(ctx, x, y, width, height);
		}

		ctx.fillStyle = NotificationType[this.mtype] || "#000000";

		let sy = y;
		for (let li = 0; li < lcount; li++) {
			ctx.fillText(this.lines[li], x + this.lmargin, sy);
			sy += lheight;
		}

		// prevent new listener being added for every redraw
		if (!this.listening) {
			this.listening = true;
			// add click listener to remove chat bubble
			ctx.canvas.addEventListener("click", (e) => {
				this.onClick(e);
			});
		}

		return Date.now() > this.timeStamp + 2000 + 20 * this.text.length;
	}

	override onClick(evt: MouseEvent) {
		/* FIXME:
		 * - removes sprite when clicked anywhere on screen
		 * - need to override character movement
		 * - only removes topmost sprite
		 * - event handler is never removed
		 */
		if (stendhal.ui.gamewindow.isTopNotification(this)) {
			stendhal.ui.gamewindow.removeNotifSprite(this);
		}
	}
}
