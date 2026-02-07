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

import { Paths } from "../data/Paths";
import { TileStore } from "../data/TileStore";

import { SoundObject } from "../data/sound/SoundFactory";
import { Canvas, RenderingContext2D } from "./Types";

import { stendhal } from "../stendhal";
import { ImageRef } from "sprite/image/ImageRef";
import { images } from "sprite/image/ImageManager";


const weatherLoops = {
	"rain": true,
	"rain_heavy": true,
	"rain_light": true
} as {[key: string]: boolean};

interface WeatherSprite extends ImageRef {
	frames: number[];
	delays: number[];
}

export class WeatherRenderer {

	private enabled = true;
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
	async update(weather?: string) {
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

		// stop previous sounds
		// FIXME: should continue playing if weather is same on next map
		if (this.audio) {
			stendhal.sound.stop(this.soundLayer, this.audio);
			this.audio = undefined;
		}

		if (!weather) {
			this.sprite = undefined;
		} else {
			const img = Paths.weather + "/" + weather + ".png";
			let sprite = <WeatherSprite> images.load(img)
			await sprite.waitFor();
			if (weather != this.weatherName || !sprite.image) {
				sprite.free();
				return;
			}

			/* FIXME:
			 *   "TypeError: $stendhal$$.data.$tileset$.$weatherAnimationMap$
			 *   is undefined". TileStore.weatherMap is not always loaded
			 *   before this is called.
			 */
			const animationMap = TileStore.get().getWeatherMap()[img];
			if (animationMap && Object.keys(animationMap).length == 0) {
				console.error("weather animation map for '" + weather + "' is empty");
				return;
			}

			if (animationMap) {
				sprite.frames = animationMap[0].frames;
				sprite.delays = animationMap[0].delays;
			} else {
				// weather is not animated
				sprite.frames = [0];
			}

			let spriteH = sprite.image.height;
			const canvas = document.getElementById("viewport") as Canvas;
			this.tilesX = Math.ceil(canvas.width / spriteH) + 1;
			this.tilesY = Math.ceil(canvas.height / spriteH) + 1;

			if (weatherLoops[weather]) {
				this.audio = stendhal.sound.playGlobalizedLoop("weather/" + weather, this.soundLayer);
			}
			this.sprite = sprite;
		}
	}

	/**
	 * Draws the weather animation.
	 *
	 * @param ctx
	 *    Drawing target element.
	 */
	draw(ctx: RenderingContext2D) {
		if (!this.enabled || !this.sprite || !this.sprite.frames) {
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

	/**
	 * Draws clouds animation.
	 *
	 * @param {RenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawClouds(ctx: RenderingContext2D, offsetX: number, offsetY: number) {
		let image = this.sprite?.image;
		if (!image) {
			return
		}
		const drawStart = Date.now();
		const timeDiff = drawStart - this.lastUpdate;
		const dim = {width: image.width, height: image.height};

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
				ctx.drawImage(image,
						0, 0, dim.width, dim.height,
						dx, dy, dim.width, dim.height);
			}
		}
	}

	/**
	 * Draws fog animation.
	 *
	 * @param {RenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawFog(ctx: RenderingContext2D, offsetX: number, offsetY: number) {
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
	 * @param {RenderingContext2D) ctx
	 * @param {number} offsetX
	 * @param {number} offsetY
	 */
	private drawOther(ctx: RenderingContext2D, offsetX: number, offsetY: number) {
		let image = this.sprite?.image;
		if (!image) {
			return
		}
		// width & height dimensions should be the same
		const dim = image.height;
		const clipLeft = offsetX % dim;
		const clipTop = offsetY % dim;
		for (let ix = 0; ix < this.tilesX; ix++) {
			for (let iy = 0; iy < this.tilesY; iy++) {
				ctx.drawImage(image,
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
