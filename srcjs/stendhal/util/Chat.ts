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

const headless_types = ["normal", "regular", "privmsg",
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
	 */
	public static log(type: string, message: string|HTMLElement, orator?: string) {
		const ChatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		if (type === "emoji" && message instanceof HTMLImageElement) {
			ChatLog.addEmojiLine(message, orator);
		} else if (typeof(message) === "string") {
			if (orator) {
				message = orator + ": " + message;
			}
			ChatLog.addLine(type, message);
		}

		if (marauroa.me && !(headless_types.indexOf(type) >= 0)) {
			marauroa.me.addNotificationBubble(type, message);
		}
	}

}
