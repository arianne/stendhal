/***************************************************************************
 *                   (C) Copyright 2022 - Faiumoni e. V.                   *
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

const headless_types = ["normal", "regular",
		"significant_positive", "client", "emoji"];


/**
 * chat logger
 */
export class Chat {

	/**
	 * adds a line to the chat log
	 *
	 * @param type
	 *     Message type.
	 * @param message
	 *     Message to log.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 * @param profile
	 *     Filename of NPC profile image to display with message.
	 */
	public static log(type: string, message: string|string[]|HTMLElement, orator?: string,
			profile?: string) {
		const ChatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		if (type === "emoji" && message instanceof HTMLImageElement) {
			ChatLog.addEmojiLine(message, orator);
		} else {
			if (typeof(message) === "string") {
				ChatLog.addLine(type, message, orator);
			} else if (Object.prototype.toString.call(message) === "[object Array]") {
				ChatLog.addLines(type, message as string[], orator);
			}
		}

		if (marauroa.me && !(headless_types.indexOf(type) >= 0)) {
			let messages = [];
			if (typeof(message) === "string") {
				messages.push(message);
			} else {
				messages = message as string[];
			}

			for (const m of messages) {
				marauroa.me.addNotificationBubble(type, m, profile);
			}
		}
	}

}
