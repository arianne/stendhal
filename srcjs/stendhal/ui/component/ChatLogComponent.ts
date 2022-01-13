/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "../toolkit/Component";


/**
 * Chat Log
 */
export class ChatLogComponent extends Component {

	constructor() {
		super("chat");
	}


	public addLine(type: string, message: string) {
		let chatElement = this.componentElement;
		let date = new Date();
		let time = "" + date.getHours() + ":";
		if (date.getHours() < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		}
		time = time + date.getMinutes();

		let div = document.createElement("div");
		div.className = "log" + type;
		div.innerHTML = "[" + time + "] " + this.formatLogEntry(message);

		let isAtBottom = (chatElement.scrollHeight - chatElement.clientHeight) === chatElement.scrollTop;
		chatElement.appendChild(div);

		if (isAtBottom) {
			chatElement.scrollTop = chatElement.scrollHeight;
		}
	}


	private formatLogEntry(message: string) {
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
				}

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


	public clear() {
		this.componentElement.innerHTML = "";
	}

}
