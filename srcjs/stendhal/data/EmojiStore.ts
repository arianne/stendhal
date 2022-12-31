/***************************************************************************
 *                    Copyright © 2003-2022 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "../data/Paths";

declare var stendhal: any;


const emojilist: {[name: string]: boolean} = {
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
};

const emojimap: {[key: string]: string} = {
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
};


export class EmojiStore {

	private static instance: EmojiStore;


	/**
	 * Retrieves singleton instance.
	 *
	 * @return
	 *     EmojiStore static instance.
	 */
	static get(): EmojiStore {
		if (!EmojiStore.instance) {
			EmojiStore.instance = new EmojiStore();
		}
		return EmojiStore.instance;
	}

	/**
	 * Private singleton constructor.
	 *
	 * Use <code>EmojiStore.get()</code>.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Creates an emoji sprite.
	 *
	 * @param text
	 *     Text representing emoji.
	 * @return
	 *     <code>Image</code> or <code>undefined</code> if emoji isn't available.
	 */
	create(text: string): HTMLImageElement|undefined {
		const filename = this.absPath(text);
		if (!filename) {
			return;
		}
		return stendhal.data.sprites.get(filename);
	}

	/**
	 * Checks if text represents an emoji.
	 *
	 * @param text
	 *     Text to be checked.
	 * @return
	 *     String representing emoji sprite filename or <code>undefined</code>.
	 */
	check(text: string): string|undefined {
		let name = emojimap[text];
		if (!name && (text.startsWith(":") && text.endsWith(":"))) {
			text = text.substr(0, text.length - 1).substr(1);
			if (this.isAvailable(text)) {
				name = text;
			}
		}
		return name;
	}

	/**
	 * Retrieves full path to an emoji image.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     String path to emoji image.
	 */
	absPath(name: string): string|undefined {
		const checked = this.check(name);
		if (checked) {
			return Paths.sprites + "/emoji/" + checked + ".png";
		}
	}

	/**
	 * Checks if an emoji is registered.
	 *
	 * @param name
	 *     Text representing emoji image filename.
	 * @return
	 *     <code>true</code> if name is registered.
	 */
	isAvailable(name: string): boolean {
		if (name.startsWith(":") && name.endsWith(":")) {
			name = name.substr(0, name.length - 1).substr(1);
		}
		return emojilist[name] == true;
	}
}
