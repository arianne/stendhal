/***************************************************************************
 *                     Copyright © 2003-2022 - Arianne                     *
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

stendhal.data.emoji = {

	/**
	 * Creates an emoji sprite.
	 *
	 * @param text
	 *     Text representing emoji.
	 * @return
	 *     <code>Image</code> or <code>undefined</code> if emoji isn't available.
	 */
	get: function(text) {
		let name = this.check(text);
		if (name) {
			const img = new Image();
			img.src = "/data/sprites/emoji/" + name + ".png";
			return img;
		}

		return undefined;
	},

	/**
	 * Checks if text represents an emoji.
	 *
	 * @param text
	 *     Text to be checked.
	 * @return
	 *     String representing emoji sprite filename or <code>undefined</code>.
	 */
	check: function(text) {
		let emoji = this.map[text];
		if (!emoji && (text.startsWith(":") && text.endsWith(":"))) {
			text = text.substr(0, text.length - 1).substr(1);
			if (this.available[text]) {
				emoji = text;
			}
		}

		return emoji;
	},

	available: {
		"angermark": true,
		"expressionless": true,
		"frown": true,
		"grin": true,
		"heart": true,
		"neutral": true,
		"smile": true,
		"smileinvert": true,
		"smileslight": true,
		"sweat": true,
		"tongue": true,
		"wink": true,
		"winktongue": true
	},

	map: {
		":anger:": "angermark",
		":angry:": "angermark",
		"💢": "angermark",

		"-_-": "expressionless",
		":noexpression:": "expressionless",
		"😑": "expressionless",

		":(": "frown",
		":-(": "frown",
		"):": "frown",
		")-:": "frown",
		":frowning:": "frown",
		"\u2639": "frown",

		":D": "grin",
		":-D": "grin",
		":grinning:": "grin",
		"😀": "grin",
		"😃": "grin",

		"<3": "heart",
		":love:": "heart",
		"\u2764": "heart",

		":-|": "neutral",
		":meh:": "neutral",
		":unamused:": "neutral",
		"😐": "neutral",

		":)": "smile",
		":-)": "smile",
		":smiling:": "smile",
		":smiley:": "smile",
		"\u263a": "smile",
		"\u263b": "smile",

		"(:": "smileinvert",
		"(-:": "smileinvert",
		":smileinverted:": "smileinvert",
		":invertsmile:": "smileinvert",
		":invertedsmile:": "smileinvert",
		":upsidedownsmile:": "smileinvert",
		":silly:": "smileinvert",
		"🙃": "smileinvert",

		":slightsmile:": "smileslight",
		"🙂": "smileslight",

		":sweating:": "sweat",
		":nervous:": "sweat",
		"💧": "sweat",

		":p": "tongue",
		":P": "tongue",
		":-p": "tongue",
		":-P": "tongue",
		"😛": "tongue",

		";)": "wink",
		";-)": "wink",
		":winking:": "wink",
		"😉": "wink",

		";p": "winktongue",
		";P": "winktongue",
		";-p": "winktongue",
		";-P": "winktongue",
		"😜": "winktongue"
	}
}
