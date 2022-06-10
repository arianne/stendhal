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

declare let marauroa: any;
declare let stendhal: any;

/**
 * chat input text field
 */
export class ChatInputComponent extends Component {

	private history: string[] = [];
	private historyIndex = 0;
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
	}

	public clear() {
		this.inputElement.value = "";
	}

	public setText(text: string) {
		this.inputElement.value = text;
		this.inputElement.focus();
	}

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

	onKeyDown(event: KeyboardEvent) {
		let code = stendhal.ui.html.extractKeyCode(event);

		if (event.shiftKey && event.ctrlKey) {
			if (this.inputElement.setSelectionRange !== undefined) {
				event.stopPropagation();

				/* Use Ctrl+Shift+arrow to move caret without moving character
				 * nor highlighting text. left/right moves caret 1 position
				 * to left or right. up/down moves caret to beginning or
				 * end of line. */
				let idx = this.inputElement.selectionEnd || 0;
				if (code === stendhal.ui.keycode.left) {
					event.preventDefault();

					idx--;
					if (idx < 0) {
						idx = 0;
					}
					this.inputElement.setSelectionRange(idx, idx);
				} else if (code === stendhal.ui.keycode.right) {
					event.preventDefault();

					idx++;
					if (idx > this.inputElement.value.length) {
						idx = this.inputElement.value.length;
					}
					this.inputElement.setSelectionRange(idx, idx);
				} else if (code === stendhal.ui.keycode.up) {
					event.preventDefault();

					idx = 0;
					this.inputElement.setSelectionRange(idx, idx);
				} else if (code === stendhal.ui.keycode.down) {
					event.preventDefault();

					idx = this.inputElement.value.length;
					this.inputElement.setSelectionRange(idx, idx);
				}
			}
		} else if (event.shiftKey) {
			// chat history
			if (code === stendhal.ui.keycode.up) {
				event.preventDefault();
				this.fromHistory(-1);
			} else if (code === stendhal.ui.keycode.down){
				event.preventDefault();
				this.fromHistory(1);
			}
		}
	}

	private onKeyPress(event: KeyboardEvent) {
		if (event.keyCode === 13) {
			this.send();
			event.preventDefault();
		}
	}

	private remember(text: string) {
		if (this.history.length > 100) {
			this.history.shift();
		}
		this.history[this.history.length] = text;
		this.historyIndex = this.history.length;
	}

	private send() {
		let val = this.inputElement.value;
		let array = val.split(" ");
		if (array[0] === "/choosecharacter") {
			marauroa.clientFramework.chooseCharacter(array[1]);
		} else if (val === '/close') {
			marauroa.clientFramework.close();
		} else {
			if (stendhal.slashActionRepository.execute(val)) {
				this.remember(val);
			}
		}
		this.clear();
	}

}
