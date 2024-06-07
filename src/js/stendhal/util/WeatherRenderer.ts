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

import { TileStore } from "../data/TileStore";

import { SoundObject } from "../data/sound/SoundFactory";

declare var stendhal: any;


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
	private audio?: SoundObject;
	private soundLayer = stendhal.sound.layers.indexOf("ambient");

	private weatherName?: string;

	/** Special handling for fog animation. */
	private fog: boolean;
	private heavyFog: boolean;

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
		this.fog = false;
		this.heavyFog = false;
	}

	/**
	 * Called when zone is updated to configure the type of weather.
	 *
	 * @param weather
	 *     Weather type identifier.
	 */
	update(weather?: string) {
		this.enabled = stendhal.config.getBoolean("effect.weather");
		if (!this.enabled) {
			// prevent playing sound & other weather-related instructions
			return;
		}
		this.heavyFog = weather === "fog_heavy";
		this.fog = this.heavyFog || weather === "fog";
		if (this.fog) {
			weather = "fog_ani";
		}

		this.weatherName = weather;

		this.frameIdx = 0;
		this.lastUpdate = Date.now();
		// reset warning messages
		this.warned = {};

		// stop previous sounds
		// FIXME: should continue playing if weather is same on next map
		if (this.audio) {
			stendhal.sound.stop(this.soundLayer, this.audio);
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

			const canvas = document.getElementById("viewport") as HTMLCanvasElement;
			this.tilesX = Math.ceil(canvas.width / spriteH) + 1;
			this.tilesY = Math.ceil(canvas.height / spriteH) + 1;

			if (weatherLoops[weather]) {
				this.audio = stendhal.sound.playGlobalizedLoop("weather/" + weather, this.soundLayer);
			}
		}
	}

	/**
	 * Draws the weather animation.
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
			if (this.weatherName === "clouds") {
				ctx.save();
				ctx.globalAlpha = 0.80;
				this.drawClouds(ctx, stendhal.ui.gamewindow.offsetX, stendhal.ui.gamewindow.offsetY);
				ctx.restore();
			} else if (this.fog) {
				this.drawFog(ctx, stendhal.ui.gamewindow.offsetX, stendhal.ui.gamewindow.offsetY);
			} else {
				this.drawOther(ctx, stendhal.ui.gamewindow.offsetX, stendhal.ui.gamewindow.offsetY);
			}
		}
	}

	/**
	 * Draws clouds animation.
	 *
	 * @param {CanvasRenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawClouds(ctx: CanvasRenderingContext2D, offsetX: number, offsetY: number) {
		const drawStart = Date.now();
		const timeDiff = drawStart - this.lastUpdate;
		const dim = {width: this.sprite!.width, height: this.sprite!.height};

		// horizontal drift rate for wind effect (1 pixel per 100 milliseconds)
		let wind = Math.floor(timeDiff / 100);
		if (wind >= dim.width) {
			wind = 0;
			this.lastUpdate = drawStart;
		}

		// drift relative to movement for depth effect
		const driftX = offsetX - Math.floor(offsetX * 0.25);
		const driftY = offsetY - Math.floor(offsetY * 0.25);

		const clipLeft = offsetX - (offsetX % dim.width) + driftX + wind;
		const clipTop = offsetY - (offsetY % dim.height) + driftY;

		for (let dy = -clipTop; dy < offsetY + ctx.canvas.height; dy += dim.height) {
			for (let dx = -clipLeft; dx < offsetX + ctx.canvas.width; dx += dim.width) {
				ctx.drawImage(this.sprite!,
						0, 0, dim.width, dim.height,
						dx, dy, dim.width, dim.height);
			}
		}
	}

	/**
	 * Draws fog animation.
	 *
	 * @param {CanvasRenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawFog(ctx: CanvasRenderingContext2D, offsetX: number, offsetY: number) {
		ctx.save();
		if (!this.heavyFog) {
			// reduce opacity for light fog
			ctx.globalAlpha = 0.5;
		}
		this.drawClouds(ctx, offsetX, offsetY);
		ctx.restore();
	}

	/**
	 * Draws types of weather other than fog.
	 *
	 * @param {CanvasRenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawOther(ctx: CanvasRenderingContext2D, offsetX: number, offsetY: number) {
		// width & height dimensions should be the same
		const dim = this.sprite!.height;
		const clipLeft = offsetX % dim;
		const clipTop = offsetY % dim;
		for (let ix = 0; ix < this.tilesX; ix++) {
			for (let iy = 0; iy < this.tilesY; iy++) {
				ctx.drawImage(this.sprite!,
						this.sprite!.frames[this.frameIdx]*dim,
						0,
						dim, dim,
						(ix*dim)+offsetX-clipLeft,
						(iy*dim)+offsetY-clipTop,
						dim, dim);
			}
		}

		if (this.sprite!.delays) {
			const cycleTime = Date.now();
			const elapsed = cycleTime - this.lastUpdate;
			if (elapsed >= this.sprite!.delays[this.frameIdx]) {
				this.lastUpdate = cycleTime;
				this.frameIdx = this.getNextFrame(elapsed);
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
