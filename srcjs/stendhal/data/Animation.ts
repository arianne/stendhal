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

import { Paths } from "./Paths";

declare var stendhal: any;


type AnimationMap = {
	[key: string]: any;
}


export class Animation {
	private static instance?: Animation;

	private readonly DEFAULT_DELAY = 500;

	private landscapeMap?: AnimationMap;
	private weatherMap?: AnimationMap;


	static get(): Animation {
		if (!this.instance) {
			this.instance = new Animation();
		}
		return this.instance;
	}

	private constructor() {
		// do nothing
	}

	getLandscapeMap(): AnimationMap {
		return this.landscapeMap || {};
	}

	getWeatherMap(): AnimationMap {
		return this.weatherMap || {};
	}

	/**
	 * Initializes the <code>landscapeAnimationMap</code> &
	 * <code>weatherAnimationMap</code> objects.
	 */
	loadAnimations() {
		fetch(Paths.tileset + "/animation.json", {
				headers: {"Content-Type": "application/json"}
		}).then(resp => resp.json()).then(animations => {
			this.landscapeMap = this.formatAnimations(
					animations["landscape"], Paths.tileset + "/");
			this.weatherMap = this.formatAnimations(
					animations["weather"], stendhal.paths.weather + "/");
		});
	}

	/**
	 * Animations are stored using the tileset name as key with value
	 * being a map indexed by initial frames. Frame map contains two
	 * lists: <code>frames</code> (a list of frames used in animation)
	 * & <code>delays</code> (a list of delay values for the
	 * corresponding frame of each index).
	 *
	 * ani = animationMap[tileset_name];
	 * frames = ani[0].frames
	 * delays = ani[0].delays
	 *
	 * @param animations
	 *     Unformatted animations lists indexed by tileset name.
	 *     Example: {ts1: [ani1, ani2, ...], ts2: [ani1, ani2, ...], ...}
	 * @param prefix
	 *     Parent directory containing the target tileset images.
	 * @return
	 *     Map of animations.
	 */
	formatAnimations(animations: any, prefix: string): AnimationMap {
		const ani: AnimationMap = {};
		for (const tsname of Object.keys(animations)) {
			const def: {[index: number]: any} = {};
			for (let li of animations[tsname]) {
				// clean whitespace
				li = li.trim();
				li = li.replace(/\t/g, " ");
				li = li.replace(/  /g, " ");

				li = li.split(" ");
				if (li.length > 1) {
					let id = li[0];
					let delay = this.DEFAULT_DELAY;

					if (id.includes("@")) {
						const idtemp = id.split("@");
						id = idtemp[0];
						if (idtemp.length > 1) {
							delay = parseInt(idtemp[1], 10);
						} else {
							delay = this.DEFAULT_DELAY;
						}
					}

					const frames = [];
					const delays = [];

					for (let frame of li[1].split(":")) {
						if (frame.includes("@")) {
							const ftemp = frame.split("@");
							delay = parseInt(ftemp[1], 10);
							frame = ftemp[0];
						}

						frame = parseInt(frame, 10);
						frames.push(frame);
						delays.push(delay);
					}

					let first_frame = frames[0];
					if (id !== "*") {
						first_frame = parseInt(id, 10);
					}

					def[first_frame] = {
						frames: frames,
						delays: delays
					};
				}
			}

			ani[prefix + tsname + ".png"] = def;
		}

		return ani;
	}
}
