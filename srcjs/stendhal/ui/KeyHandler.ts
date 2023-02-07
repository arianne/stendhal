/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

declare var marauroa: any;
declare var stendhal: any;


export class KeyHandler {

	public static readonly keycode: {[key: string]: number} = {
		left: 37,
		up: 38,
		right: 39,
		down: 40
	};
	private static pressedKeys: number[] = [];


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
	 *     <code>true</code> if direction keycode found in pressed
	 *     keys list.
	 */
	private static isDirPressed(): boolean {
		for (const dir of Object.keys(KeyHandler.keycode).map((key) => KeyHandler.keycode[key])) {
			if (KeyHandler.pressedKeys.indexOf(dir) > -1) {
				return true;
			}
		}
		return false;
	}

	private static extractMoveOrFaceActionFromEvent(event: KeyboardEvent): string|null {
		if (event.ctrlKey) {
			return "face";
		} else if (event.shiftKey) {
			return null;
		}
		return "move";
	}

	private static extractDirectionFromKeyCode(code: number): number {
		var dir = code - KeyHandler.keycode.left;
		if (dir === 0) {
			dir = 4;
		}
		return dir;
	}

	static onKeyDown(e?: KeyboardEvent) {
		var event = e;
		if (!event) {
			event = window.event as any;
		}
		if (!event) {
			return;
		}

		var code = stendhal.ui.html.extractKeyCode(event);
		if (code >= KeyHandler.keycode.left && code <= KeyHandler.keycode.down) {
			// disable scrolling via arrow keys
			const target: any = event.target;
			if (target.tagName === "BODY" || target.tagName === "CANVAS") {
				event.preventDefault();
			}

			// if this is a repeated event, stop further processing
			if (KeyHandler.pressedKeys.indexOf(code) > -1) {
				return;
			}
			KeyHandler.pressedKeys.push(code);

			var type = KeyHandler.extractMoveOrFaceActionFromEvent(event);
			if (!type) {
				return;
			}
			var dir = KeyHandler.extractDirectionFromKeyCode(code);
			var action = {"type": type, "dir": ""+dir};
			marauroa.clientFramework.sendAction(action);

			// stop walking if keypress in direction of current movement
			if (marauroa.me && marauroa.me.autoWalkEnabled()) {
				if (parseInt(marauroa.me["dir"], 10) === dir) {
					marauroa.clientFramework.sendAction({"type": "walk"});
				}
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

	static onKeyUp(e?: KeyboardEvent) {
		var event = e
		if (!event) {
			event = window.event as any;
		}
		if (!event) {
			return;
		}

		var code = stendhal.ui.html.extractKeyCode(event);

		if (code >= KeyHandler.keycode.left && code <= KeyHandler.keycode.down) {
			var i = KeyHandler.pressedKeys.indexOf(code);
			if (i > -1) {
				KeyHandler.pressedKeys.splice(i, 1);
			}

			var action: {[key: string]: string} = {};
			if (!KeyHandler.isDirPressed()) {
				action["type"] = "stop";
				marauroa.clientFramework.sendAction(action);
			}

			if (KeyHandler.pressedKeys.length > 0) {
				code = KeyHandler.pressedKeys[0];
				var type = KeyHandler.extractMoveOrFaceActionFromEvent(event);
				if (!type) {
					return;
				}
				var dir = KeyHandler.extractDirectionFromKeyCode(code);
				action["type"] = type;
				action["dir"] = ""+dir;
				marauroa.clientFramework.sendAction(action);
			}
		}
	}
}
