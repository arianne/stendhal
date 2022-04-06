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


stendhal.data.tileset.loadAnimations = function() {
	fetch("/data/tileset/animation.json", {"Content-Type": "application/json"})
		.then(resp => resp.json())
		.then(animations => {
			stendhal.data.tileset.landscapeAnimationMap = animations["landscape"];
			stendhal.data.tileset.weatherAnimationMap = animations["weather"];
		});
}
