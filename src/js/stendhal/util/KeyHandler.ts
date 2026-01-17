/***************************************************************************
 *                 Copyright © 2003-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"

import { ChatCompletionHelper } from "./ChatCompletionHelper";
import { Direction } from "./Direction";
import { KeyCode } from "./KeyCode";
import { singletons } from "../SingletonRepo";
import { ChatPanel } from "../ui/ChatPanel";
import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";


/**
 * Class for managing direction presses using the keyboard.
 */
export class KeyHandler {

	private static readonly dirCodes: {[key: string]: number} = {
		left: KeyCode.ARROW_LEFT,
		up: KeyCode.ARROW_UP,
		right: KeyCode.ARROW_RIGHT,
		down: KeyCode.ARROW_DOWN
	};

	private static readonly translations: {[key: string]: string} = {
		[""+KeyCode.CHAR_A]: "left",
		[""+KeyCode.CHAR_W]: "up",
		[""+KeyCode.CHAR_D]: "right",
		[""+KeyCode.CHAR_S]: "down"
	};

	/** List of currently pressed direction keys. */
	private static pressedKeys: number[] = [];

	private static asdwEnabled = false;


	/**
	 * Static members & methods only.
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Checks if any direction key is currently pressed.
	 *
	 * @return
	 *   `true` if direction keycode found in pressed keys list.
	 */
	private static isDirPressed(): boolean {
		for (const dir of Object.keys(KeyHandler.dirCodes).map((key) => KeyHandler.dirCodes[key])) {
			if (KeyHandler.pressedKeys.indexOf(dir) > -1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks keyboard event & determines resulting action.
	 *
	 * @param event:
	 *   Keyboard event.
	 * @return {number}
	 *   Ternary representation of action to be executed:
	 *   -  0: no action
	 *   - -1: face to action
	 *   -  1: move/walk action
	 */
	private static checkActionEvent(event: KeyboardEvent): number {
		if (event.shiftKey) {
			return 0;
		}
		if (event.ctrlKey) {
			return -1;
		}
		return 1;
	}

	/**
	 * Converts keyboard code to `Direction`.
	 *
	 * @param code
	 *   Code of pressed key.
	 * @return
	 *   Direction representation.
	 */
	private static extractDirectionFromKeyCode(code: number): Direction {
		let dir = code - KeyCode.ARROW_LEFT;
		if (dir < Direction.UP.val) {
			dir = Direction.LEFT.val;
		}
		return Direction.VALUES[dir];
	}

	/**
	 * Translates asdw key code to standard direction.
	 *
	 * @param code
	 *   Code of pressed key.
	 * @return
	 *   Translated code.
	 */
	private static translate(code: number): number {
		if (KeyHandler.asdwEnabled) {
			// FIXME: "up" (w) & "down" (s) not found in KeyHandler.dirCodes keys
			const translated = KeyHandler.dirCodes[KeyHandler.translations[""+code]];
			if (typeof(translated) !== "undefined") {
				return translated;
			}
		}
		return code;
	}

	/**
	 * Action when a key is pressed.
	 *
	 * @param e
	 *   Keyboard event.
	 */
	static onKeyDown(e?: KeyboardEvent) {
		var event = e;
		if (!event) {
			event = window.event as any;
		}
		if (!event) {
			return;
		}

		var code = KeyHandler.translate(KeyCode.extract(event));

		if (code == KeyCode.TAB) {
			event.preventDefault();
			ChatCompletionHelper.get().onTabKey();
			return;
		}
		// reset chat completion prefixes
		ChatCompletionHelper.get().reset();

		// handle toggling chat panel
		if (code == KeyCode.ENTER && !singletons.getChatInput().hasFocus()) {
			(ui.get(UIComponentEnum.BottomPanel) as ChatPanel).onEnterPressed();
			return;
		}

		if (code >= KeyCode.ARROW_LEFT && code <= KeyCode.ARROW_DOWN) {
			// disable scrolling via arrow keys
			const target: any = event.target;
			if (target.tagName === "BODY" || target.tagName === "CANVAS") {
				event.preventDefault();
			}

			if (!marauroa.me) {
				return;
			}
			// if this is a repeated event, stop further processing
			if (KeyHandler.pressedKeys.indexOf(code) > -1) {
				return;
			}
			KeyHandler.pressedKeys.push(code);

			const actionType = KeyHandler.checkActionEvent(event);
			if (!actionType) {
				return;
			}
			const dir = KeyHandler.extractDirectionFromKeyCode(code);
			if (actionType < 0) {
				marauroa.me.faceTo(dir);
			} else {
				marauroa.me.setDirection(dir, true);
			}
		} else {
			// move focus to chat-input on keydown
			// but don't do that for Ctrl+C, etc.
			if (!event.altKey && !event.metaKey && !event.ctrlKey && event.key !== "Control") {
				if (document.activeElement && document.activeElement.localName !== "input") {
					document.getElementById("chatinput")!.focus();
				}
			}
		}
	}

	/**
	 * Action when a key is released.
	 *
	 * @param e
	 *   Keyboard event.
	 */
	static onKeyUp(e?: KeyboardEvent) {
		var event = e
		if (!event) {
			event = window.event as any;
		}
		if (!event) {
			return;
		}

		var code = KeyHandler.translate(KeyCode.extract(event));

		if (code >= KeyCode.ARROW_LEFT && code <= KeyCode.ARROW_DOWN) {
			if (!marauroa.me) {
				return;
			}
			const i = KeyHandler.pressedKeys.indexOf(code);
			if (i > -1) {
				// remove from list of currently pressed keys
				KeyHandler.pressedKeys.splice(i, 1);
			}

			if (!KeyHandler.isDirPressed()) {
				marauroa.me.stop();
				return;
			}

			// get previously pressed direction
			code = KeyHandler.pressedKeys[0];
			const actionType = KeyHandler.checkActionEvent(event);
			if (!actionType) {
				return;
			}
			const dir = KeyHandler.extractDirectionFromKeyCode(code);
			if (actionType < 0) {
				marauroa.me.faceTo(dir);
			} else {
				// NOTE: do not cancel auto-walk on key release
				marauroa.me.setDirection(dir);
			}
		}
	}
}
