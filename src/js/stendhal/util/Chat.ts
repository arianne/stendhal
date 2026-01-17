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

import { stendhal } from "stendhal";
import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { ChatLogComponent } from "../ui/component/ChatLogComponent";

import { marauroa } from "marauroa"


/**
 * chat logger
 */
export class Chat {

	public static debugLogEnabled = false;
	private static clog: ChatLogComponent;

	// available actions from attending NPC
	public static attending?: string;
	// initialize with "hello" since we won't have received a ChatOptionsEvent at startup
	public static options: string[] = ["hello"];


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

	public static debug(message: string) {
		if (Chat.debugLogEnabled) {
			Chat.log("client", message);
		}
	}

	/**
	 * Formats displayed text.
	 *
	 * @param message {string}
	 *   Text to parse for special characters.
	 * @return {string}
	 *   Text formatted with keywords & item names highlighted.
	 */
	public static formatLogEntry(message: string): string {
		if (!message) {
			return "";
		}
		let res = "";
		let delims = [" ", ",", ".", "!", "?", ":", ";"];
		let length = message.length;
		let inHighlight = false, inUnderline = false,
			inHighlightQuote = false, inUnderlineQuote = false;
		for (let i = 0; i < length; i++) {
			let c = message[i];

			if (c === "\\") {
				let n = message[i + 1];
				res += n;
				i++;

			// Highlight Start?
			} else if (c === "#") {
				if (inHighlight) {
					res += c;
					continue;
				}
				let n = message[i + 1];
				if (n === "#") {
					res += c;
					i++;
					continue;
				}
				if (n === "'") {
					inHighlightQuote = true;
					i++;
				}
				inHighlight = true;
				res += "<span class=\"logh\">";

			// Underline start?
			} else if (c === "§") {
				if (inUnderline) {
					res += c;
					continue;
				}
				let n = message[i + 1];
				if (n === "§") {
					res += c;
					i++;
					continue;
				}
				if (n === "'") {
					inUnderlineQuote = true;
					i++;
				}
				inUnderline = true;
				res += "<span class=\"logi\">";

			// End Highlight and Underline?
			} else if (c === "'") {
				if (inUnderlineQuote) {
					inUnderline = false;
					inUnderlineQuote = false;
					res += "</span>";
					continue;
				}
				if (inHighlightQuote) {
					inHighlight = false;
					inHighlightQuote = false;
					res += "</span>";
					continue;
				}
				res += c;

			// HTML escape
			} else if (c === "<") {
				res += "&lt;";

			// End of word
			} else if (delims.indexOf(c) > -1) {
				let n = message[i + 1];
				if (c === " " || n === " " || n == undefined) {
					if (inUnderline && !inUnderlineQuote && !inHighlightQuote) {
						inUnderline = false;
						res += "</span>" + c;
						continue;
					}
					if (inHighlight && !inUnderlineQuote && !inHighlightQuote) {
						inHighlight = false;
						res += "</span>" + c;
						continue;
					}
				}
				res += c;

			// Normal characters
			} else {
				res += c;
			}
		}

		// Close opened formattings
		if (inUnderline) {
			res += "</span>";
		}
		if (inHighlight) {
			res += "</span>";
		}

		return res;
	}
}
