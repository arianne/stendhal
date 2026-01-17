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

import { marauroa } from "marauroa"

import { ChatPanel } from "../ChatPanel";
import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";
import { ChatOptionsDialog } from "../dialog/ChatOptionsDialog";
import { EmojiMapDialog } from "../dialog/EmojiMapDialog";
import { Component } from "../toolkit/Component";
import { singletons } from "../../SingletonRepo";
import { ChatCompletionHelper } from "../../util/ChatCompletionHelper";
import { KeyCode } from "../../util/KeyCode";
import { Paths } from "../../data/Paths";
import { stendhal } from "stendhal";


/**
 * Chat input text field.
 */
export class ChatInputComponent extends Component {

	/** Remembered input values for quick lookup. */
	private history: string[];
	/** Selected index in history entries. */
	private historyIndex: number;
	/** DOM element. */
	private inputElement: HTMLInputElement;

	constructor() {
		super("chatinput");
		this.inputElement = this.componentElement as HTMLInputElement;
		this.componentElement.addEventListener("keydown", (event: KeyboardEvent) => {
			this.onKeyDown(event);
		});
		this.componentElement.addEventListener("keypress", (event: KeyboardEvent) => {
			this.onKeyPress(event);
		});
		// restore from previous session
		this.history = stendhal.config.getObject("chat.history") || [];
		this.historyIndex = stendhal.config.getInt("chat.history.index", 0);

		const btn_send = document.getElementById("send-button")!;
		btn_send.addEventListener("click", (e) => {
			this.send();
		});

		// ** keyword shortcuts ** //
		const btn_keyword = document.getElementById("keywords-button")!;
		// event to bring up keywords dialog
		btn_keyword.addEventListener("click", (e) => {
			ChatOptionsDialog.createOptions();
		});

		// ** emoji shortcuts ** //
		const btn_emoji = document.getElementById("emojis-button")!;
		// event to bring up emoji dialog
		btn_emoji.addEventListener("click", (e) => {
			this.buildEmojiMap();
		});

		this.refresh();
	}

	/**
	 * Updates attributes of DOM element & shortcuts buttons.
	 */
	public override refresh() {
		super.refresh();

		const btn_emoji = document.getElementById("emojis-button")!;;
		// remove any previous child elements
		while (btn_emoji.lastChild != null) {
			btn_emoji.removeChild(btn_emoji.lastChild)
		}
		// update button
		if (stendhal.config.getBoolean("emojis.native")) {
			btn_emoji.innerText = "☺";
		} else {
			// set image for emoji button
			btn_emoji.appendChild(singletons.getSpriteStore().get(Paths.sprites + "/emoji/smile.png").cloneNode());
		}
	}

	/**
	 * Removes all text from input.
	 */
	public clear() {
		this.inputElement.value = "";
		// reset chat completion prefixes
		ChatCompletionHelper.get().reset();
	}

	/**
	 * Sets text value of input.
	 *
	 * @param text {string}
	 *   Text to be set.
	 */
	public setText(text: string) {
		const chatPanel = ui.get(UIComponentEnum.BottomPanel)!;
		if (!chatPanel.isVisible()) {
			chatPanel.setVisible(true);
		}
		this.inputElement.value = text;
		this.inputElement.focus();
	}

	/**
	 * Appends to current value of input.
	 *
	 * @param text {string}
	 *   Text to be added to end of value.
	 */
	public appendText(text: string) {
		this.setText(this.inputElement.value + text);
	}

	/**
	 * Retrieves the current text value.
	 */
	public getText(): string {
		return this.inputElement.value;
	}

	/**
	 * Sets text value from history.
	 *
	 * @param i {number}
	 *   History index to use.
	 */
	private fromHistory(i: number) {
		this.historyIndex = this.historyIndex + i;
		if (this.historyIndex < 0) {
			this.historyIndex = 0;
		}
		if (this.historyIndex >= this.history.length) {
			this.historyIndex = this.history.length;
			this.clear();
		} else {
			this.inputElement.value = this.history[this.historyIndex];
		}
	}

	/**
	 * Handles shift+arrow keypress events to cycle through history entries.
	 */
	onKeyDown(event: KeyboardEvent) {
		let code = KeyCode.extract(event);

		if (event.shiftKey) {
			// chat history
			if (code === KeyCode.ARROW_UP) {
				event.preventDefault();
				this.fromHistory(-1);
			} else if (code === KeyCode.ARROW_DOWN){
				event.preventDefault();
				this.fromHistory(1);
			}
		}
	}

	/**
	 * Executes send action when enter key is pressed.
	 */
	private onKeyPress(event: KeyboardEvent) {
		if (event.keyCode === KeyCode.ENTER) {
			this.send();
			event.preventDefault();
		}
	}

	/**
	 * Adds line of text to history.
	 *
	 * After reaching maximum of 100 entries oldest entry is overwritten in favor of new text.
	 *
	 * NOTE: Would it be beneficial at all to allow configuring history lines buffer in settings?
	 *
	 * @param text {string}
	 *   Text to be added. Usually value of the text input.
	 */
	public remember(text: string) {
		// don't add duplicates of last remembered string
		if (this.history.length > 0 && text === this.history[this.history.length-1]) {
			this.historyIndex = this.history.length;
			stendhal.config.set("chat.history.index", this.historyIndex);
			return;
		}

		if (this.history.length > 100) {
			this.history.shift();
		}
		this.history[this.history.length] = text;
		this.historyIndex = this.history.length;
		// preserve across sessions
		// XXX: should this be done at logout/destruction for performance?
		stendhal.config.set("chat.history", this.history);
		stendhal.config.set("chat.history.index", this.historyIndex);
	}

	/**
	 * Processes current text value & executes appropriate action.
	 */
	private send() {
		let val = this.inputElement.value;
		let array = val.split(" ");
		if (array[0] === "/choosecharacter") {
			marauroa.clientFramework.chooseCharacter(array[1]);
		} else if (val === '/close') {
			marauroa.clientFramework.close();
		} else {
			if (stendhal.actions.execute(val)) {
				this.remember(val);
			}
		}
		this.clear();
		// romove focus to trigger hiding software keyboard
		if (this.hasFocus()) {
			this.inputElement.blur();
		}

		(ui.get(UIComponentEnum.BottomPanel) as ChatPanel).onMessageSent();
	}

	/**
	 * Creates & shows the emoji map dialog.
	 */
	private buildEmojiMap() {
		const wstate = stendhal.config.getWindowState("shortcuts");
		const content = new EmojiMapDialog();
		const dialog = ui.createSingletonFloatingWindow("Emojis", content, wstate.x, wstate.y);
		dialog.setId("shortcuts");
		// needed in order to close dialog from within
		content.setFrame(dialog);
	}
}
