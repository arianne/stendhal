/***************************************************************************
 *                    Copyright © 2023-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../UI";
import { DialogContentComponent } from "../toolkit/DialogContentComponent";
import { Chat } from "../../util/Chat";
import { singletons } from "../../SingletonRepo";

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";


/**
 * A dialog window populated with keywords recognized by the attending NPC.
 *
 * TODO: support options from multiple attending NPCs if possible or change server to only allow
 *       attending if no other NPC is attending to player
 */
export class ChatOptionsDialog extends DialogContentComponent {

	/** Dialog instance that is currently visible. */
	private static activeInstance?: ChatOptionsDialog;

	/**
	 * Keywords to be replaced by a common alias.
	 *
	 * Some keywords don't need repeated in NPC options. Others, such as "task" or "favor" which
	 * serve as alternatives to "quest", may be highlighted in dialogue so keep those.
	 */
	private static readonly aliases: {[keyword: string]: string} = {
		"bye": "goodbye",
		"hi": "hello",
	}


	/**
	 * Creates a new chat options dialog.
	 */
	private constructor() {
		super("keywordmap-template");

		// common chat options
		const custom_options = [];
		for (let opt of stendhal.config.get("chat-opts.custom").split(",")) {
			opt = opt.trim();
			if (opt) {
				custom_options.push(opt);
			}
		}

		// attending NPC (note that options are parsed from most recent NPC if there are multiple attending)
		const npc_options = [];
		for (let opt of Chat.options) {
			opt = opt.toLowerCase();
			const original = ChatOptionsDialog.aliases[opt];
			if (original) {
				opt = original;
			}
			// don't add duplicates if already in custom options
			if (custom_options.indexOf(opt) == -1) {
				npc_options.push(opt);
			}
		}
		if (npc_options.length > 0) {
			this.addGroup(!Chat.attending ? undefined : Chat.attending, npc_options);
		}

		if (custom_options.length > 0) {
			this.addGroup("Custom", custom_options);
		}
	}

	/**
	 * Adds a section of grouped keywords to dialog.
	 *
	 * @param title {string}
	 *   Text label for group.
	 * @param options {string[]}
	 *   Keywords available in group.
	 */
	private addGroup(title: string|undefined, options: string[]) {
		if (title) {
			const titleElement = document.createElement("div");
			titleElement.innerText = title;
			this.componentElement.appendChild(titleElement);
		}

		const row: HTMLDivElement = document.createElement("div");
		this.componentElement.appendChild(row);
		for (let idx = 0; idx < options.length; idx++) {
			const button = document.createElement("button");
			button.className = "shortcut-button";
			button.innerText = options[idx];
			button.onclick = () => {
				this.onButtonPressed(button);
			};
			row.appendChild(button);
		}
	}

	/**
	 * Executed when a button representing a keyword is pressed.
	 *
	 * @param button {HTMLButtonElement}
	 *   The button that was pressed.
	 */
	private onButtonPressed(button: HTMLButtonElement) {
		// change button color for visual feedback
		button.style.setProperty("background-color", "#b3b3b3");
		window.setTimeout(function() {
			button.style.removeProperty("background-color");
		}, 600);

		let keyword = button.innerText;
		if (keyword.endsWith("...")) {
			// add to chat input instead of sending text
			while (keyword.endsWith(".")) {
				keyword = keyword.substring(0, keyword.length-1);
			}
			singletons.getChatInput().setText(keyword);
		} else {
			const action = {
				"type": "chat",
				"text": keyword
			} as any;
			marauroa.clientFramework.sendAction(action);
			// add to chat history
			singletons.getChatInput().remember(keyword);
		}
	}

	/**
	 * Parses options and creates dialog window.
	 */
	public static createOptions() {
		const wstate = stendhal.config.getWindowState("shortcuts");
		const content = new ChatOptionsDialog();
		const dialog = ui.createSingletonFloatingWindow("Chat Options", content, wstate.x, wstate.y);
		dialog.setId("shortcuts");
		// needed in order to close dialog from within
		content.setFrame(dialog);
		ChatOptionsDialog.activeInstance = content;
	}

	public override onParentClose() {
		ChatOptionsDialog.activeInstance = undefined;
	}

	/**
	 * Checks for an active chat options dialog window.
	 *
	 * @return {boolean}
	 *   `true` if there is currently a window open.
	 */
	public static isActive(): boolean {
		return typeof(ChatOptionsDialog.activeInstance) !== "undefined";
	}

	/**
	 * Closes the active dialog window & calls cleanup functions.
	 */
	public static closeActiveInstance() {
		if (ChatOptionsDialog.activeInstance) {
			ChatOptionsDialog.activeInstance.close();
		}
	}
}
