/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var stendhal: any;

import { SlashActionRepo } from "../SlashActionRepo";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { ChatInputComponent } from "../ui/component/ChatInputComponent";


export class ChatCompletionHelper {

	/** Chat commands relating to players. */
	private readonly playerCommands: string[] = [
		"msg",
		"teleport",
		"teleportto",
		"tell"
	];

	/** Singleton instance. */
	private static instance: ChatCompletionHelper;


	/**
	 * Retrieves the singleton instance.
	 */
	static get(): ChatCompletionHelper {
		if (!ChatCompletionHelper.instance) {
			ChatCompletionHelper.instance = new ChatCompletionHelper();
		}
		return ChatCompletionHelper.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Called when tab key is pressed while chat input has focus.
	 *
	 * FIXME: need to cycle through commands & names.
	 */
	onTabKey() {
		const parts: string[] = [];
		const chatInput = (ui.get(UIComponentEnum.ChatInput) as ChatInputComponent);
		for (const p of chatInput.getText().split(" ")) {
			parts.push(p);
		}
		if (parts.length == 0 || !parts[0].startsWith("/")) {
			return;
		}
		const cmd = parts[0].substring(1, parts[0].length);
		const chatCommands = Object.getOwnPropertyNames(SlashActionRepo.get()).sort();
		if (parts.length == 1) {
			for (const c of chatCommands) {
				if (c.startsWith(cmd)) {
					chatInput.setText("/" + c);
					break;
				}
			}
			return;
		}
		if (!stendhal.players) {
			return;
		}
		if (this.playerCommands.indexOf(cmd) < 0) {
			return;
		}
		let name = parts[parts.length-1] || "";
		for (const player of stendhal.players) {
			if (player.startsWith(name)) {
				name = player;
				break;
			}
		}
		parts[parts.length-1] = name;
		chatInput.setText(parts.join(" "));
	}
}
