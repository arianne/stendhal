/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
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
stendhal.ui = stendhal.ui || {};

stendhal.ui.sound = {
	playEffect: function(soundName, volume) {
		if (!stendhal.config.sound.play) {
			return;
		}

		var sound = new Audio();
		sound.autoplay = true;
		sound.volume = volume;
		sound.src = "/data/sounds/" + soundName + ".ogg";
	}
};
