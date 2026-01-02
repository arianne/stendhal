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
import { JSONLoader } from "../util/JSONLoader";
import { singletons } from "../SingletonRepo";


export class EmojiStore {

	private emojilist: string[] = [];
	private emojimap: {[key: string]: string} = {};

	/** Singleton instance. */
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
	 * Hidden singleton constructor.
	 *
	 * Use <code>EmojiStore.get()</code>.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Loads emoji data from JSON.
	 */
	public init() {
		const loader = new JSONLoader();
		loader.onDataReady = () => {
			this.emojilist = loader.data["emojilist"];
			this.emojimap = loader.data["emojimap"];
		}
		loader.load(Paths.sprites + "/emoji/emojis.json");
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
		return singletons.getSpriteStore().get(filename);
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
		text = text.replace(/\\\\/g, "\\");
		let name = this.emojimap[text];
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
			name = name.substr(1, name.length-2)
		}
		return this.emojilist.indexOf(name) > -1;
	}

	/**
	 * Get a list of available emojis.
	 *
	 * @return
	 *     A copy of the emoji list.
	 */
	getEmojiList(): string[] {
		return [...this.emojilist];
	}
}
