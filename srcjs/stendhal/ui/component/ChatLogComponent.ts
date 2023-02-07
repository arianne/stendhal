/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
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

		this.componentElement.addEventListener("mouseup", (evt: MouseEvent) => {
			this.onContextMenu(evt)
		});
	}


	public override refresh() {
		this.componentElement.style.setProperty("font-family", stendhal.config.get("ui.font.chat"));
	}


	/**
	 * Generates a timestamp.
	 *
	 * @return
	 *     Timestamp formatted <code>HTMLSpanElement</code> element.
	 */
	private createTimestamp(): HTMLSpanElement {
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


	private add(row: HTMLDivElement) {
		const chatElement = this.componentElement;
		const isAtBottom = (chatElement.scrollHeight - chatElement.clientHeight) <= chatElement.scrollTop + 5;
		chatElement.appendChild(row);

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

		const lcol = document.createElement("div");
		lcol.className = "logcolL";
		if (timestamp) {
			lcol.appendChild(this.createTimestamp());
		} else {
			// add whitespace to preserve margin of right column
			lcol.innerHTML = " ";
		}

		const rcol = document.createElement("div");
		rcol.className = "logcolR log" + type;
		rcol.innerHTML += this.formatLogEntry(message);

		const row = document.createElement("div");
		row.className = "logrow";
		row.appendChild(lcol);
		row.appendChild(rcol);

		this.add(row);
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
		const lcol = document.createElement("div");
		lcol.className = "logcolL";
		lcol.appendChild(this.createTimestamp());

		const rcol = document.createElement("div");
		rcol.className = "logcolR lognormal";
		if (orator) {
			rcol.innerHTML += orator + ": ";
		}
		// create a copy so old emoji line isn't removed
		rcol.appendChild(emoji.cloneNode());

		const row = document.createElement("div");
		row.className = "logrow";
		row.appendChild(lcol);
		row.appendChild(rcol);

		this.add(row);
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


	public clear() {
		this.componentElement.innerHTML = "";
	}


	public exportContents(clipboard=true) {
		if (clipboard && (!navigator || !navigator.clipboard)) {
			console.warn("copying to clipboard not supported by this browser");
			return;
		}

		const lines = [];
		// try to get highlightext text first
		if (window.getSelection) {
			const sel = window.getSelection();
			let value;
			if (sel && sel.type == "Range") {
				// FIXME: how to get only text from log when text from multiple elements is selected?
				if (sel.rangeCount == 1) {
					value = sel.toString()
				}
			}
			if (value && value !== "") {
				lines.push(value);
			}
		//~ } else if (document.selection && document.selection.type == "Text") {
			//~ lines.push(document.selection.createRange().text);
		}
		// if no highlighted text, copy the entire log
		if (lines.length == 0) {
			const children = this.componentElement.children;
			for (let idx = 0; idx < children.length; idx++) {
				const row = children[idx];
				let text = row.children[0].innerHTML.trim() + " ";
				if (text.trim() === "") {
					text = "    ";
				}
				text = this.plainText(text + row.children[1].innerHTML,
						["span", "div"]);
				lines.push(text.replace("&lt;", "<").replace("&gt;", ">"));
			}
		}

		if (lines.length > 0) {
			// TODO: export to file
			if (clipboard) {
				navigator.clipboard.writeText(lines.join("\n"));
			}
		}
	}

	/**
	 * Removes HTML tag formatting from a string.
	 *
	 * @param msg
	 *     Message to format.
	 * @param tags
	 *     Only remove listed tags.
	 * @return
	 *     Formatted message.
	 */
	private plainText(msg: string, tags: string[]|undefined=undefined): string {
		if (!tags) {
			msg = msg.replace(/<.*?>/g, "");
		} else {
			for (const tag of tags) {
				msg = msg.replace(new RegExp("<" + tag + ".*?>", "g"), "")
						.replace(new RegExp("</" + tag + ">", "g"), "");
			}
		}

		return msg;
	}


	private onContextMenu(evt: MouseEvent) {
		if (!evt || evt.button != 2 || stendhal.ui.actionContextMenu.isOpen()) {
			return;
		}

		// setting "log" to "this" here doesn't work
		const log = ui.get(UIComponentEnum.ChatLog) as ChatLogComponent;
		const options = [
			{
				title: "Clear",
				action: function() {log.clear();}
			}
		] as MenuItem[];

		if (navigator && navigator.clipboard) {
			options.unshift({
				title: "Copy",
				action: function() {log.exportContents();}
			});
		}

		const pos = stendhal.ui.html.extractPosition(evt);
		stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
				new LogContextMenu(options), pos.pageX - 50, pos.pageY - 5));

		evt.preventDefault();
		evt.stopPropagation();
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
