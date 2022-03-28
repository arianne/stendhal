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

import { ui } from "../UI";
import { Component } from "../toolkit/Component";
import { MenuItem } from "../../action/MenuItem";
import { UIComponentEnum } from "../UIComponentEnum";

declare var stendhal: any;


/**
 * Chat Log
 */
export class ChatLogComponent extends Component {

	constructor() {
		super("chat");
		this.refresh();

		this.componentElement.addEventListener("mouseup", this.onMouseUp);
	}


	public refresh() {
		this.componentElement.style.setProperty("font-family", stendhal.config.get("ui.font.chat"));
	}


	/**
	 * Generates a timestamp.
	 *
	 * @return
	 *     Timestamp formatted <code>HTMLSpanElement</code> element.
	 */
	private createTimestamp() {
		const date = new Date();
		let time = "" + date.getHours() + ":";
		if (date.getHours() < 10) {
			time = "0" + time;
		}
		if (date.getMinutes() < 10) {
			time = time + "0";
		}
		time = time + date.getMinutes();

		const timestamp = document.createElement("span");
		timestamp.className = "logtimestamp";
		timestamp.innerHTML = "[" + time + "]";

		return timestamp;
	}


	private add(div: HTMLDivElement) {
		const chatElement = this.componentElement;
		const isAtBottom = (chatElement.scrollHeight - chatElement.clientHeight) === chatElement.scrollTop;
		chatElement.appendChild(div);

		if (isAtBottom) {
			chatElement.scrollTop = chatElement.scrollHeight;
		}
	}


	/**
	 * Adds a line of text.
	 *
	 * @param type
	 *     Message type.
	 * @param message
	 *     Text to be added.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 * @param timestamp
	 *     If <code>false</code>, suppresses prepending message with timestamp.
	 */
	public addLine(type: string, message: string, orator?: string, timestamp=true) {
		if (orator) {
			message = orator + ": " + message;
		}

		const div = document.createElement("div");
		div.className = "log" + type;
		if (timestamp) {
			div.appendChild(this.createTimestamp());
			div.innerHTML += " ";
		}
		div.innerHTML += this.formatLogEntry(message);

		this.add(div);
	}


	/**
	 * Adds multiple lines of text.
	 *
	 * @param type
	 *     Message type.
	 * @param messages
	 *     Texts to be added.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 */
	public addLines(type: string, messages: string[], orator?: string) {
		let stamped = false;
		for (const line of messages) {
			if (!stamped) {
				this.addLine(type, line, orator);
				stamped = true;
			} else {
				this.addLine(type, line, orator, false);
			}
		}
	}


	/**
	 * Adds a line displaying an emoji image.
	 *
	 * @param emoji
	 *     Emoji image sprite.
	 * @param orator
	 *     Name of entity making the expression (default: <code>undefined</code>).
	 */
	public addEmojiLine(emoji: HTMLImageElement, orator?: string) {
		const div = document.createElement("div");
		div.className = "lognormal";
		div.appendChild(this.createTimestamp());
		div.innerHTML += " ";
		if (orator) {
			div.innerHTML += orator + ": ";
		}
		div.appendChild(emoji);

		this.add(div);
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


	public copyToClipboard() {
		if (!navigator || !navigator.clipboard) {
			console.warn("copying to clipboard not supported by this browser");
			return;
		}

		const lines = [];
		const children = this.componentElement.children;
		for (let idx = 0; idx < children.length; idx++) {
			lines.push(stendhal.ui.html.plainText(children[idx].innerHTML, ["span"])
					.replace("<br>", "\n").replace("&lt;", "<").replace("&gt;", ">"));
		}

		if (lines.length > 0) {
			navigator.clipboard.writeText(lines.join("\n"));
		}
	}

	private onMouseUp(evt: MouseEvent) {
		if (stendhal.ui.actionContextMenu.isOpen()) {
			stendhal.ui.actionContextMenu.close();
		}

		if (evt.button === 2) {
			evt.stopPropagation();
			// setting "log" to "this" here doesn't work
			const log = ui.get(UIComponentEnum.ChatLog) as ChatLogComponent;
			const options = [
				{
					title: "Copy",
					action: function() {log.copyToClipboard();}
				},
				{
					title: "Clear",
					action: function() {log.clear();}
				}
			] as MenuItem[];

			const pos = stendhal.ui.html.extractPosition(evt);
			stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
					new LogContextMenu(options), pos.pageX - 50, pos.pageY - 5));
		}
	}
}


class LogContextMenu extends Component {

	options!: MenuItem[];

	constructor(options: MenuItem[]) {
		super("contextmenu-template");
		this.options = options;

		let content = "<div class=\"actionmenu\">";
		for (let i = 0; i < this.options.length; i++) {
			content += "<button id=\"actionbutton." + i + "\">" + stendhal.ui.html.esc(this.options[i].title) + "</button><br>";
		}
		content += "</div>";
		this.componentElement.innerHTML = content;

		this.componentElement.addEventListener("click", (evt) => {
			this.onClick(evt);
		});
	}

	private onClick(evt: Event) {
		let iStr = (evt.target as HTMLElement).getAttribute("id")?.substring(13);
		if (iStr === undefined || iStr === "") {
			return;
		}
		let i = parseInt(iStr);
		if (i < 0) {
			return;
		}

		this.componentElement.dispatchEvent(new Event("close"));

		if (i >= this.options.length) {
			throw new Error("actions index is larger than number of actions");
		}

		const action = this.options[i].action;
		if (action) {
			action();
		} else {
			console.error("chat log context menu action failed");
		}
	}
}
