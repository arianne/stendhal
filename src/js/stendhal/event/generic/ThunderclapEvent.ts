/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { SubEvent } from "./SubEvent";

import { Paths } from "../../data/Paths";
import { store } from "../../data/SpriteStore";

import { Color } from "../../data/color/Color";

import { SoundLayer } from "../../data/enum/SoundLayer";

import { SoundID } from "../../data/sound/SoundID";

import { SoundManager } from "../../ui/SoundManager";
import { ViewPort } from "../../ui/ViewPort";


/**
 * Creates a thunder & lightning effect.
 */
export class ThunderclapEvent extends SubEvent {

	private startTime = 0;
	private image = store.get(Paths.maps + "/effect/lightning.png");
	private flash = true;
	private lightning = true;


	override execute(entity: any, flags: string[]) {
		this.startTime = Date.now();
		this.flash = flags.indexOf("no-flash") < 0;
		this.lightning = flags.indexOf("no-lightning") < 0;
		// thunder sound
		SoundManager.get().playLocalizedEffect(entity["x"], entity["y"], SoundManager.DEFAULT_RADIUS,
				SoundLayer.SFX.value, SoundID["thunderclap"]!);
		if (this.flash || this.lightning) {
			// lightning visual effect
			const viewport = ViewPort.get();
			viewport.onSceneComplete = (ctx: CanvasRenderingContext2D, offsetX: number,
					offsetY: number) => {
				this.drawLightning(ctx, offsetX, offsetY);
			}
			window.setTimeout(function() { viewport.onSceneComplete = undefined; }, 300);
		}
	}

	/**
	 * Draws a lightning effect on the viewport.
	 *
	 * @param ctx {CanvasRenderingContext2D}
	 *   Canvas drawing context.
	 * @param offsetX {number}
	 *   Canvas horizontal offset.
	 * @param offsetY {number}
	 *   Canvas vertical offset.
	 */
	private drawLightning(ctx: CanvasRenderingContext2D, offsetX: number, offsetY: number) {
		const timeDiff = Date.now() - this.startTime;
		if (this.flash) {
			ctx.save();
			if (timeDiff <= 100 || timeDiff > 200) {
				ctx.globalAlpha = 0.5;
			} else {
				ctx.globalAlpha = 0.75;
			}
			ctx.fillStyle = Color.WHITE;
			ctx.fillRect(offsetX, offsetY, 640, 480);
			ctx.restore();
		}
		if (this.lightning && this.image.height) {
			ctx.drawImage(this.image, offsetX, offsetY);
		}
		ctx.restore();
	}
}
