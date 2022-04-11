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

"use strict";

var stendhal = window.stendhal = window.stendhal || {};
stendhal.data = stendhal.data || {};
stendhal.data.tileset = stendhal.data.tileset || {};

const DEFAULT_DELAY = 500;


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
const formatAnimations = function(animations, prefix) {
	const ani = {}
	for (const tsname of Object.keys(animations)) {
		const def = {}
		for (let li of animations[tsname]) {
			// make sure whitespace is clean
			li = li.trim();
			if (li.includes("\t")) {
				li = li.replaceAll("\t", " ");
			}
			while (li.includes("  ")) {
				li = li.replaceAll("  ", " ");
			}

			li = li.split(" ");
			if (li.length > 1) {
				let id = li[0];
				let delay = DEFAULT_DELAY;

				if (id.includes("@")) {
					const idtemp = id.split("@");
					id = idtemp[0];
					if (idtemp.length > 1) {
						delay = parseInt(idtemp[1], 10);
					} else {
						delay = DEFAULT_DELAY;
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

/**
 * Initializes the <code>landscapeAnimationMap</code> &
 * <code>weatherAnimationMap</code> objects.
 */
stendhal.data.tileset.loadAnimations = function() {
	fetch("/tiled/tileset/animation.json", {"Content-Type": "application/json"})
		.then(resp => resp.json())
		.then(animations => {
			stendhal.data.tileset.landscapeAnimationMap
					= formatAnimations(animations["landscape"], "/tileset/");
			stendhal.data.tileset.weatherAnimationMap
					= formatAnimations(animations["weather"], "/data/sprites/weather/");
		});
}
