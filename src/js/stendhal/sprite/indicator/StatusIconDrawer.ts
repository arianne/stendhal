/***************************************************************************
 *                   (C) Copyright 2003-2026 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   entity program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { htmlImageStore } from "data/HTMLImageStore";
import { Paths } from "data/Paths";
import { RPEntity } from "entity/RPEntity";
import { RenderingContext2D } from "util/Types";

export class StatusIconDrawer {

	public drawStatusIcons(entity: RPEntity, ctx: RenderingContext2D) {

		function _drawAnimatedIcon(icon: CanvasImageSource, delay: number, nFrames: number, xdim: number, ydim: number, x: number, y: number) {
			var frame = Math.floor(Date.now() / delay) % nFrames;
			ctx.drawImage(icon, frame * xdim, 0, xdim, ydim, x, y, xdim, ydim);
		}
		function drawAnimatedIcon(iconPath: string, delay: number, x: number, y: number, fWidth?: number) {
			var icon = htmlImageStore.get(iconPath);
			var dimH = icon.height;
			var dimW = typeof(fWidth) !== "undefined" ? fWidth : dimH;
			var nFrames = icon.width / dimW;
			_drawAnimatedIcon(icon, delay, nFrames, dimW, dimH, x, y);
		}
		function drawAnimatedIconWithFrames(iconPath: string, nFrames: number, delay: number, x: number, y: number) {
			var icon = htmlImageStore.get(iconPath);
			var ydim = icon.height;
			var xdim = icon.width / nFrames;
			_drawAnimatedIcon(icon, delay, nFrames, xdim, ydim, x, y);
		}

		var x = entity["_x"] * 32 - 10;
		var y = (entity["_y"] + 1) * 32;
		if (entity.hasOwnProperty("choking")) {
			ctx.drawImage(htmlImageStore.get(Paths.sprites + "/ideas/choking.png"), x, y - 10);
		} else if (entity.hasOwnProperty("eating")) {
			ctx.drawImage(htmlImageStore.get(Paths.sprites + "/ideas/eat.png"), x, y - 10);
		}
		// NPC and pet idea icons
		if (entity.hasOwnProperty("idea")) {
			const idea = Paths.sprites + "/ideas/" + entity["idea"] + ".png";
			const ani = htmlImageStore.animations.idea[entity["idea"]];
			if (ani) {
				drawAnimatedIcon(idea, ani.delay, x + ani.offsetX * entity["width"],
						y - entity["drawHeight"] + ani.offsetY);
			} else {
				ctx.drawImage(htmlImageStore.get(idea), x + 32 * entity["width"],
						y - entity["drawHeight"]);
			}
		}
		if (entity.hasOwnProperty("away")) {
			drawAnimatedIcon(Paths.sprites + "/ideas/away.png", 1500, x + 32 * entity["width"], y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("grumpy")) {
			drawAnimatedIcon(Paths.sprites + "/ideas/grumpy.png", 1000, x + 5, y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("last_player_kill_time")) {
			drawAnimatedIconWithFrames(Paths.sprites + "/ideas/pk.png", 12, 300, x, y - entity["drawHeight"]);
		}
		// status affects
		if (entity.hasOwnProperty("poisoned")) {
			drawAnimatedIcon(Paths.sprites + "/status/poison.png", 100, x + 32 * entity["width"] - 10, y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("status_confuse")) {
			drawAnimatedIcon(Paths.sprites + "/status/confuse.png", 200, x + 32 * entity["width"] - 14, y - entity["drawHeight"] + 16);
		}
		if (entity.hasOwnProperty("status_shock")) {
			drawAnimatedIcon(Paths.sprites + "/status/shock.png", 200, x + 32 * entity["width"] - 25, y - 32, 38);
		}
		// NPC job icons
		let nextX = x;
		if (entity.hasOwnProperty("job_healer")) {
			ctx.drawImage(htmlImageStore.get(Paths.sprites + "/status/healer.png"), nextX, y - 10);
			nextX += 12;
		}
		if (entity.hasOwnProperty("job_merchant")) {
			ctx.drawImage(htmlImageStore.get(Paths.sprites + "/status/merchant.png"), nextX, y - 10);
			nextX += 12;
		}
		if (entity.hasOwnProperty("job_producer")) {
			ctx.drawImage(htmlImageStore.get(Paths.sprites + "/status/producer.png"), nextX, y - 16);
			nextX += 16;
		}
	}
}

export const statusIconDrawer = /* @__PURE__ */ new StatusIconDrawer();