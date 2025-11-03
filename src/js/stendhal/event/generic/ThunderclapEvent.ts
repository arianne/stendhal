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

	/** Thunder image. */
	private static readonly image = store.get(Paths.maps + "/effect/lightning.png");

	/** Determines if screen should flash white. */
	private readonly flash;
	/** Determines if lightning bolt should be drawn. */
	private readonly lightning;
	/** Time that event execution began. */
	private startTime;


	constructor(flags: string[]) {
		super(flags);
		this.flash = !this.flagEnabled("no-flash");
		this.lightning = !this.flagEnabled("no-lightning");
		this.startTime = 0;
	}

	override execute(entity: any) {
		this.startTime = Date.now();
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
			ctx.fillRect(offsetX, offsetY, ctx.canvas.width, ctx.canvas.height);
			// restore composite info to make lightning opaque
			ctx.restore();
		}
		if (this.lightning && ThunderclapEvent.image.height) {
			ctx.drawImage(ThunderclapEvent.image, offsetX, offsetY);
		}
	}
}
