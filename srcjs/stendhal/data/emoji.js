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
		const path = stendhal.data.emoji.absPath(text);
		if (!path) {
			return;
		}

		const img = new Image();
		img.src = path;
		return img;
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
		let name = stendhal.data.emoji.map[text];
		if (!name && (text.startsWith(":") && text.endsWith(":"))) {
			text = text.substr(0, text.length - 1).substr(1);
			if (stendhal.data.emoji.isAvailable(text)) {
				name = text;
			}
		}

		return name;
	},

	/**
	 * Retrieves full path to an emoji image.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     String path to emoji image.
	 */
	absPath: function(name) {
		name = stendhal.data.emoji.check(name);
		if (name) {
			return "/data/sprites/emoji/" + name + ".png";
		}
	},

	/**
	 * Checks if an emoji is registered.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     <code>true</code> if name is registered.
	 */
	isAvailable: function(name) {
		if (name.startsWith(":") && name.endsWith(":")) {
			name = name.substr(0, name.length - 1).substr(1);
		}

		return stendhal.data.emoji.registered[name] == true;
	},

	registered: {
		"angermark": true,
		"cry": true,
		"expressionless": true,
		"frown": true,
		"frownslight": true,
		"grin": true,
		"heart": true,
		"neutral": true,
		"savor": true,
		"smile": true,
		"smileinvert": true,
		"smileslight": true,
		"sweat": true,
		"tongue": true,
		"wink": true,
		"winktongue": true
	},

	map: {
		// NOTE: must use raw characters for unicode codes with more
		//       than 4 digits

		":anger:": "angermark",
		":angry:": "angermark",
		"💢": "angermark",

		":'(": "cry",
		")':": "cry",
		":crying:": "cry",
		"😢": "cry",

		"-_-": "expressionless",
		":noexpression:": "expressionless",
		"😑": "expressionless",

		":-(": "frown",
		")-:": "frown",
		":frowning:": "frown",
		"\u2639": "frown",

		":(": "frownslight",
		"):": "frownslight",
		":slightfrown:": "frownslight",
		"🙁": "frownslight",

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

		":yum:": "savor",
		"😋": "savor",

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
