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
import { images } from "sprite/image/ImageManager";
import { ImageRef } from "sprite/image/ImageRef";
import { RenderingContext2D } from "util/Types";

export class StatusIconDrawer {

    private icons: Record<string, ImageRef> = {};

    private load(filename: string): ImageRef {
        let imageRef = this.icons[filename];
        if (!imageRef) {
            imageRef = images.load(filename);
            this.icons[filename] = imageRef;
        }
        return imageRef;
    }

    private drawAnimatedIcon(ctx: RenderingContext2D, iconPath: string, delay: number, x: number, y: number, fWidth?: number) {
        let icon = this.load(iconPath).image;
        if (!icon) {
            return;
        }
        let dimH = icon.height;
        let dimW = fWidth || dimH;
        let nFrames = icon.width / dimW;
        let frame = Math.floor(Date.now() / delay) % nFrames;
        ctx.drawImage(icon, frame * dimW, 0, dimW, dimH, x, y, dimW, dimH);
    }

    private drawAnimatedIconWithFrames(ctx: RenderingContext2D, iconPath: string, nFrames: number, delay: number, x: number, y: number) {
        let icon = this.load(iconPath).image;
        if (!icon) {
            return;
        }
        let ydim = icon.height;
        let xdim = icon.width / nFrames;
        let frame = Math.floor(Date.now() / delay) % nFrames;
        ctx.drawImage(icon, frame * xdim, 0, xdim, ydim, x, y, xdim, ydim);
    }

	public drawStatusIcons(entity: RPEntity, ctx: RenderingContext2D) {
		let x = entity["_x"] * 32 - 10;
		let y = (entity["_y"] + 1) * 32;
		if (entity.hasOwnProperty("choking")) {
            this.load(Paths.sprites + "/ideas/choking.png").drawOnto(ctx, x, y - 10);
		} else if (entity.hasOwnProperty("eating")) {
			this.load(Paths.sprites + "/ideas/eat.png").drawOnto(ctx, x, y - 10);
		}
		// NPC and pet idea icons
		if (entity.hasOwnProperty("idea")) {
			const idea = Paths.sprites + "/ideas/" + entity["idea"] + ".png";
			const ani = htmlImageStore.animations.idea[entity["idea"]];
			if (ani) {
				this.drawAnimatedIcon(ctx, idea, ani.delay, x + ani.offsetX * entity["width"],
						y - entity["drawHeight"] + ani.offsetY);
			} else {
				this.load(idea).drawOnto(ctx, x + 32 * entity["width"],	y - entity["drawHeight"]);
			}
		}
		if (entity.hasOwnProperty("away")) {
			this.drawAnimatedIcon(ctx, Paths.sprites + "/ideas/away.png", 1500, x + 32 * entity["width"], y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("grumpy")) {
			this.drawAnimatedIcon(ctx, Paths.sprites + "/ideas/grumpy.png", 1000, x + 5, y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("last_player_kill_time")) {
			this.drawAnimatedIconWithFrames(ctx, Paths.sprites + "/ideas/pk.png", 12, 300, x, y - entity["drawHeight"]);
		}
		// status affects
		if (entity.hasOwnProperty("poisoned")) {
			this.drawAnimatedIcon(ctx, Paths.sprites + "/status/poison.png", 100, x + 32 * entity["width"] - 10, y - entity["drawHeight"]);
		}
		if (entity.hasOwnProperty("status_confuse")) {
			this.drawAnimatedIcon(ctx, Paths.sprites + "/status/confuse.png", 200, x + 32 * entity["width"] - 14, y - entity["drawHeight"] + 16);
		}
		if (entity.hasOwnProperty("status_shock")) {
			this.drawAnimatedIcon(ctx, Paths.sprites + "/status/shock.png", 200, x + 32 * entity["width"] - 25, y - 32, 38);
		}
		// NPC job icons
		let nextX = x;
		if (entity.hasOwnProperty("job_healer")) {
			this.load(Paths.sprites + "/status/healer.png").drawOnto(ctx, nextX, y - 10);
			nextX += 12;
		}
		if (entity.hasOwnProperty("job_merchant")) {
			this.load(Paths.sprites + "/status/merchant.png").drawOnto(ctx, nextX, y - 10);
			nextX += 12;
		}
		if (entity.hasOwnProperty("job_producer")) {
			this.load(Paths.sprites + "/status/producer.png").drawOnto(ctx, nextX, y - 16);
			nextX += 16;
		}
	}
}

export const statusIconDrawer = /* @__PURE__ */ new StatusIconDrawer();