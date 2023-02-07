/***************************************************************************
 *                (C) Copyright 2022-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { ChatLogComponent } from "../ui/component/ChatLogComponent";

declare let marauroa: any;
declare let stendhal: any;


/**
 * chat logger
 */
export class Chat {

	private static clog: ChatLogComponent;


	/**
	 * Adds a line to the chat log.
	 *
	 * @param type
	 *     Message type.
	 * @param message
	 *     Message to log.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 * @param profile
	 *     Optional entity image filename to show as the speaker.
	 * @param headed
	 *     Set to <code>true</code> to show a notification bubble.
	 */
	public static log(type: string, message: string|string[]|HTMLElement,
			orator?: string, profile?: string, headed=false) {
		headed = headed || typeof(profile) !== "undefined";

		if (!Chat.clog) {
			Chat.clog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);
		}

		if (type === "emoji" && message instanceof HTMLImageElement) {
			Chat.clog.addEmojiLine(message, orator);
		} else {
			if (typeof(message) === "string") {
				Chat.clog.addLine(type, message, orator);
			} else if (Object.prototype.toString.call(message) === "[object Array]") {
				Chat.clog.addLines(type, message as string[], orator);
			}
		}

		// shows a notification bubble
		if (marauroa.me && headed) {
			let messages = [];
			if (typeof(message) === "string") {
				messages.push(message);
			} else {
				messages = message as string[];
			}
			for (const m of messages) {
				stendhal.ui.gamewindow.addNotifSprite(type, m, profile);
			}
		}
	}

	/**
	 * Adds a line to the chat log & a notification bubble to gamewindow.
	 *
	 * @param type
	 *     Message type.
	 * @param message
	 *     Message to log.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 * @param profile
	 *     Optional entity image filename to show as the speaker.
	 */
	public static logH(type: string, message: string|string[]|HTMLElement,
			orator?: string, profile?: string) {
		Chat.log(type, message, orator, profile, true);
	}
}
