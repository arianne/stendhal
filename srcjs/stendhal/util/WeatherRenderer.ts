/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { singletons } from "../SingletonRepo";
import { TileStore } from "../data/TileStore";
import { Sound } from "../ui/SoundManager";

declare var stendhal: any;


// TODO: incorporate thunder
const weatherLoops = {
	"rain": true,
	"rain_heavy": true,
	"rain_light": true
} as {[key: string]: boolean};

interface WeatherSprite extends HTMLImageElement {
	frames: number[];
	delays: number[];
}

export class WeatherRenderer {

	private enabled = true;
	private warned: {[key: string]: boolean} = {};
	private sprite?: WeatherSprite;
	private frameIdx = 0;
	private lastUpdate = 0;
	private tilesX = 0;
	private tilesY = 0;
	private audio?: Sound;
	private soundLayer = singletons.getSoundManager().layers.indexOf("ambient");

	/** Singleton instance. */
	private static instance: WeatherRenderer;


	/**
	 * Retrieves singleton instance.
	 *
	 * @return
	 *     WeatherRenderer static instance.
	 */
	static get(): WeatherRenderer {
		if (!WeatherRenderer.instance) {
			WeatherRenderer.instance = new WeatherRenderer();
		}
		return WeatherRenderer.instance;
	}

	/**
	 * Hidden singleton constructor.
	 *
	 * Use <code>WeatherRenderer.get()</code>.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Called when zone is updated to configure the type of weather.
	 *
	 * @param weather
	 *     Weather type identifier.
	 */
	update(weather?: string) {
		this.enabled = stendhal.config.getBoolean("gamescreen.weather");
		this.frameIdx = 0;
		this.lastUpdate = Date.now();
		// reset warning messages
		this.warned = {};

		const soundMan = singletons.getSoundManager();
		// stop previous sounds
		if (this.audio) {
			soundMan.stop(this.soundLayer, this.audio);
			this.audio = undefined;
		}

		if (!weather) {
			this.sprite = undefined;
		} else {
			const img = stendhal.paths.weather + "/" + weather + ".png";
			this.sprite = <WeatherSprite> stendhal.data.sprites.get(img);
			/* FIXME:
			 *   "TypeError: $stendhal$$.data.$tileset$.$weatherAnimationMap$
			 *   is undefined". TileStore.weatherMap is not always loaded
			 *   before this is called.
			 */
			const animationMap = TileStore.get().getWeatherMap()[img];

			if (!this.sprite || !this.sprite.src) {
				console.error("weather sprite for '" + weather + "' not found");
				return;
			}
			//~ if (!animationMap) {
				//~ console.error("weather animation map for '" + weather + "' not loaded");
				//~ return;
			if (animationMap && Object.keys(animationMap).length == 0) {
				console.error("weather animation map for '" + weather + "' is empty");
				return;
			}

			if (animationMap) {
				this.sprite.frames = animationMap[0].frames;
				this.sprite.delays = animationMap[0].delays;
			} else {
				// weather is not animated
				this.sprite.frames = [0];
			}

			let spriteH = this.sprite.height;
			// failsafe assumes min sprite dimensions to be 32x32
			if (!spriteH) {
				spriteH = 32;
				console.log("using failsafe sprite height: " + spriteH);
			}

			const canvas = document.getElementById("gamewindow") as HTMLCanvasElement;
			this.tilesX = Math.ceil(canvas.width / spriteH);
			this.tilesY = Math.ceil(canvas.height / spriteH);

			if (weatherLoops[weather]) {
				this.audio = soundMan.playGlobalizedLoop("weather/" + weather,
						this.soundLayer);
			}
		}
	}

	/**
	 * Draws the weather animation.
	 *
	 * TODO: don't move animation with character movement.
	 *
	 * @param ctx
	 *    Drawing target element.
	 */
	draw(ctx: CanvasRenderingContext2D) {
		if (this.enabled && this.sprite && this.sprite.frames) {
			if (!this.tilesX || !this.tilesY) {
				if (!this.warned.tiling) {
					console.warn("cannot tile weather animation");
					this.warned.tiling = true;
				}
				return;
			}
			if (!this.sprite.height) {
				if (!this.warned.imgReady) {
					console.warn("waiting on image to load before drawing weather");
					this.warned.imgReady = true;
				}
				return;
			}

			// width & height dimensions should be the same
			const dim = this.sprite.height;
			for (let ix = 0; ix < this.tilesX; ix++) {
				for (let iy = 0; iy < this.tilesY; iy++) {
					ctx.drawImage(this.sprite,
							this.sprite.frames[this.frameIdx]*dim,
							0,
							dim, dim,
							(ix*dim)+stendhal.ui.gamewindow.offsetX,
							(iy*dim)+stendhal.ui.gamewindow.offsetY,
							dim, dim);
				}
			}

			if (this.sprite.delays) {
				const cycleTime = Date.now();
				const elapsed = cycleTime - this.lastUpdate;
				if (elapsed >= this.sprite.delays[this.frameIdx]) {
					this.lastUpdate = cycleTime;
					this.frameIdx = this.getNextFrame(elapsed);
				}
			}
		}
	}

	/**
	 * Calculates the next frame to display incorporating frame skipping.
	 *
	 * @param elapsed
	 *     Amount of time (ms) that has elapsed.
	 * @return
	 *     The next frame index that should be drawn.
	 */
	private getNextFrame(elapsed: number): number {
		let delayComb = 0, idx = this.frameIdx;
		for (idx; delayComb < elapsed; idx++) {
			delayComb += this.sprite!.delays[idx];
			if (idx + 1 >= this.sprite!.delays.length) {
				idx = -1;
			}
		}
		return idx;
	}
}
