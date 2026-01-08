/***************************************************************************
 *                (C) Copyright 2003-2024 - Faiumoni e. V.                 *
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
import { singletons } from "../../SingletonRepo";
import { Chat } from "../../util/Chat";

import { stendhal } from "../../stendhal";


/**
 * Chat Log
 */
export class ChatLogComponent extends Component {

	/** Most recent state of chat log scrolling. */
	private scrollStateBottom: boolean;
	/** Event listener for managing scroll position when component properties change such as changes to visibility. */
	private scrollListener: EventListenerOrEventListenerObject;


	constructor() {
		super("chat");
		this.refresh();
		this.scrollStateBottom = this.isAtBottom();

		this.componentElement.addEventListener("mouseup", (evt: MouseEvent) => {
			this.onContextMenu(evt)
		});
		this.componentElement.addEventListener("touchstart", (evt: TouchEvent) => {
			const pos = stendhal.ui.html.extractPosition(evt);
			stendhal.ui.touch.onTouchStart(pos.pageX, pos.pageY);
		}, {passive: true});
		this.componentElement.addEventListener("touchend", (evt: TouchEvent) => {
			stendhal.ui.touch.onTouchEnd();
			this.onContextMenu(evt)
			// clean up
			stendhal.ui.touch.unsetOrigin();
		});

		this.scrollListener = (evt: Event) => {
			this.onScroll();
		};
		if (this.isVisible()) {
			this.componentElement.addEventListener("scroll", this.scrollListener);
		}
	}

	/**
	 * Updates font from config.
	 */
	public override refresh() {
		this.componentElement.style.setProperty("font-family", stendhal.config.get("font.chat"));
	}

	/**
	 * Generates a timestamp.
	 *
	 * @return {HTMLSpanElement}
	 *   `HTMLSpanElement` element with inner text formatted with timestamp.
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

	/**
	 * Adds a line of text.
	 *
	 * @param row {HTMLDivElement}
	 *   Div containing text to be added.
	 */
	private add(row: HTMLDivElement) {
		// check state before adding row
		const wasAtBottom = this.isAtBottom();
		this.componentElement.appendChild(row);

		if (wasAtBottom) {
			this.scrollToBottom();
		}
	}

	/**
	 * Adds a line of text.
	 *
	 * @param type
	 *   Message type.
	 * @param message
	 *   Text to be added.
	 * @param orator
	 *   Name of entity making the expression (default: `undefined`).
	 * @param timestamp
	 *   If `false`, suppresses prepending message with timestamp (default: `true`).
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
		rcol.innerHTML += Chat.formatLogEntry(message);

		const row = document.createElement("div");
		row.className = "logrow";
		row.appendChild(lcol);
		row.appendChild(rcol);

		this.add(row);
	}

	/**
	 * Adds multiple lines of text.
	 *
	 * @param type {string}
	 *   Message type.
	 * @param messages {string[]}
	 *   Array of texts to be added.
	 * @param orator
	 *   Name of entity making the expression (default: `undefined`).
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
	 * @param emoji {HTMLImageElement}
	 *   Emoji image sprite.
	 * @param orator {string}
	 *   Name of entity making the expression (default: `undefined`).
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



	/**
	 * Removes all text from log.
	 */
	public clear() {
		this.componentElement.innerHTML = "";
	}

	/**
	 * Sets scrolled position.
	 *
	 * @param scroll {number}
	 *   New scrolled position.
	 */
	private setScroll(scroll: number) {
		this.componentElement.scrollTop = scroll;
	}

	/**
	 * Sets scrolled position to end of log.
	 */
	private scrollToBottom() {
		this.setScroll(this.componentElement.scrollHeight);
	}

	/**
	 * Checks if scrolled state represents end of log.
	 *
	 * @return
	 *   `true` if recent state is less than 0 or scroll position is same as element height.
	 */
	private isAtBottom(): boolean {
		return this.componentElement.scrollHeight - this.componentElement.clientHeight
				<= this.componentElement.scrollTop + 5;
	}

	/**
	 * Called when a scroll event occurs.
	 */
	private onScroll() {
		// remember scrolled state
		this.scrollStateBottom = this.isAtBottom();
	}

	/**
	 * Doesn't listen for scroll events while chat panel/log is hidden.
	 */
	public onHide() {
		// stop listening for scroll events when chat panel is hidden
		this.componentElement.removeEventListener("scroll", this.scrollListener);
	}

	/**
	 * Re-activates scroll event listener when chat panel/log visibility is restored.
	 */
	public onUnhide() {
		if (this.scrollStateBottom) {
			this.scrollToBottom();
		}
		// don't listen for scroll events until AFTER scroll state has been updated
		this.componentElement.addEventListener("scroll", this.scrollListener);
	}

	/**
	 * Copies log text to clipboard or exports to file.
	 *
	 * @param clipboard {boolean}
	 *   If `true` text is copied to clipboard.
	 * @fixme
	 *   File export currently not supported.
	 */
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
			if (clipboard) {
				navigator.clipboard.writeText(lines.join("\n"));
				return;
			}
			singletons.getDownloadUtil().buildChatLog(lines.join("\n")).execute();
		}
	}

	/**
	 * Removes HTML tag formatting from a string.
	 *
	 * @param msg {string}
	 *   Message to format.
	 * @param tags {string[]}
	 *   List of tags to filter for removal (default: []).
	 * @return {string}
	 *   Plain text formatted message.
	 */
	private plainText(msg: string, tags?: string[]): string {
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

	/**
	 * Handles creating a custom context menu with options "Clear" & "Copy".
	 *
	 * FIXME: text highlighting interferes with opening context menu with touch
	 *
	 * @param evt {any}
	 *   Mouse or touch event.
	 */
	private onContextMenu(evt: any) {
		if (!evt || stendhal.ui.actionContextMenu.isOpen()) {
			return;
		}
		if (evt.type === "mouseup" && evt.button != 2) {
			return;
		}
		if (evt.type === "touchend" && !stendhal.ui.touch.isLongTouch(evt)) {
			evt.preventDefault();
			return;
		}

		// setting "log" to "this" here doesn't work
		const log = ui.get(UIComponentEnum.ChatLog) as ChatLogComponent;
		const options: MenuItem[] = [
			new MenuItem("Save", function() { log.exportContents(false); }),
			new MenuItem("Clear", function() { log.clear(); })
		];

		if (navigator && navigator.clipboard) {
			options.unshift(new MenuItem("Copy", function() { log.exportContents(); }));
		}

		const pos = stendhal.ui.html.extractPosition(evt);
		stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
				new LogContextMenu(options), pos.pageX - 50, pos.pageY - 5));

		evt.preventDefault();
		evt.stopPropagation();
	}
}


/**
 * Custom context menu component.
 */
class LogContextMenu extends Component {

	/** Available options. */
	options!: MenuItem[];


	constructor(options: MenuItem[]) {
		super("contextmenu-template", true);
		this.options = options;

		let content = "<div class=\"actionmenu verticalgroup\">";
		for (let i = 0; i < this.options.length; i++) {
			content += "<button class=\"actionbutton\" id=\"actionbutton." + i + "\">" + stendhal.ui.html.esc(this.options[i].title) + "</button>";
		}
		content += "</div>";
		this.componentElement.innerHTML = content;

		this.componentElement.addEventListener("click", (evt) => {
			this.onClick(evt);
		});
	}

	/**
	 * Handles executing commands when an option is clicked/tapped.
	 */
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
