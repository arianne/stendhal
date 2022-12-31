/***************************************************************************
 *                    Copyright 2003-2022 © - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Animation } from "../data/tileset/Animation";

declare var stendhal: any;


interface WeatherSprite extends HTMLImageElement {
	frames: number[];
	delays: number[];
}

export class WeatherRenderer {

	private static instance: WeatherRenderer;

	private enabled = true;
	private warned = false;
	private sprite?: WeatherSprite;
	private frameIdx = 0;
	private lastUpdate = 0;
	private tilesX = 0;
	private tilesY = 0;


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
	 * Private singleton constructor.
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
	update(weather: string) {
		this.enabled = stendhal.config.getBoolean("gamescreen.weather");
		this.frameIdx = 0;
		this.lastUpdate = Date.now();
		this.warned = false;

		if (!weather) {
			this.sprite = undefined;
		} else {
			const img = stendhal.paths.weather + "/" + weather + ".png";
			this.sprite = <WeatherSprite> stendhal.data.sprites.get(img);
			/* FIXME:
			 *   "TypeError: $stendhal$$.data.$tileset$.$weatherAnimationMap$
			 *   is undefined". Animation.weatherMap is not always loaded
			 *   before this is called.
			 */
			const animationMap = Animation.get().getWeatherMap()[img];

			if (!this.sprite || !this.sprite.src) {
				console.error("weather sprite not found: " + weather);
				return;
			}
			if (!animationMap) {
				console.error("weather animation map not loaded");
				return;
			} else if (Object.keys(animationMap).length == 0) {
				console.error("weather animation map is empty");
				return;
			}

			this.sprite.frames = animationMap[0].frames;
			this.sprite.delays = animationMap[0].delays;

			this.tilesX = Math.ceil(stendhal.ui.gamewindow.width
					/ this.sprite.height);
			this.tilesY = Math.ceil(stendhal.ui.gamewindow.height
					/ this.sprite.height);
			if (!this.tilesX || !isFinite(this.tilesX)) {
				// failsafe max width in case gamewindow.width is not set
				this.tilesX = Math.ceil(640 / this.sprite.height);
			}
			if (!this.tilesY || !isFinite(this.tilesY)) {
				// failsafe max height in case gamewindow.height is not set
				this.tilesY = Math.ceil(480 / this.sprite.height);
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
				if (!this.warned) {
					console.warn("client too small to tile weather");
					this.warned = true;
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

			const cycleTime = Date.now();
			if (cycleTime - this.lastUpdate >= this.sprite.delays[this.frameIdx]) {
				this.lastUpdate = cycleTime;
				this.frameIdx++;
				if (this.frameIdx + 1 > this.sprite.frames.length) {
					this.frameIdx = 0;
				}
			}
		}
	}
}
