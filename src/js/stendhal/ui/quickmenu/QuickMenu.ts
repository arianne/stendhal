/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ChatButton } from "./ChatButton";
import { ChatOptsButton } from "./ChatOptsButton";
import { JoystickButton } from "./JoystickButton";
import { TravelLogButton } from "./TravelLogButton";
import { MenuButton } from "./MenuButton";
import { PathFindingButton } from "./PathFindingButton";
import { QuickMenuButton } from "./QuickMenuButton";
import { RotateLButton } from "./RotateLButton";
import { RotateRButton } from "./RotateRButton";
import { SettingsButton } from "./SettingsButton";
import { SoundButton } from "./SoundButton";

import { ui } from "../UI";

import { Paths } from "../../data/Paths";

import { ElementClickListener } from "../../util/ElementClickListener";


/**
 * Main button to show/hide quick menu buttons.
 *
 * @todo
 *   Make subclass of `ui.toolkit.Component` or possibly subclass of `ui.quickmenu.QuickMenuButton`.
 */
export class QuickMenu {

	/** Property set to prevent re-initialization. */
	private static initialized = false;
	/** Determines if sub-buttons are visible. */
	private static expanded = false;
	/** Horizontally grouped sub-buttons. */
	private static readonly buttonListX: QuickMenuButton[] = [];
	/** Vertially grouped sub-buttons. */
	private static readonly buttonListY: QuickMenuButton[] = [];


	/**
	 * Static properties & methods only.
	 *
	 * NOTE: should this be singleton?
	 */
	private constructor() {
		// do nothing
	}

	/**
	 * Initializes quick menu & all sub-buttons.
	 */
	public static init() {
		if (QuickMenu.initialized) {
			console.warn("WARNING: attempted to re-initialize quick menu buttons")
			return;
		}
		QuickMenu.initialized = true;

		const btn_main = QuickMenu.getElement();
		// ensure main button is visible at startup
		btn_main.style["display"] = "block";
		btn_main.style["cursor"] = "url(" + Paths.sprites + "/cursor/highlight.png) 1 3, auto";
		btn_main.draggable = false;

		// horizontal sub-buttons
		QuickMenu.buttonListX.push(new JoystickButton());
		QuickMenu.buttonListX.push(new PathFindingButton());
		QuickMenu.buttonListX.push(new SoundButton());
		QuickMenu.buttonListX.push(new TravelLogButton());
		QuickMenu.buttonListX.push(new SettingsButton());
		QuickMenu.buttonListX.push(new MenuButton());
		// vertical sub-buttons
		QuickMenu.buttonListY.push(new ChatButton());
		QuickMenu.buttonListY.push(new ChatOptsButton());
		QuickMenu.buttonListY.push(new RotateLButton());
		QuickMenu.buttonListY.push(new RotateRButton());

		btn_main.onload = () => {
			// remove listener
			btn_main.onload = null;
			this.refresh();
		}
		btn_main.src = Paths.gui + "/quickmenu/main.png";

		new ElementClickListener(btn_main).onClick = function(evt: Event) {
			QuickMenu.toggle();
		};

		// hide buttons by default
		QuickMenu.update();
	}

	/**
	 * Enables or disables a sub-button.
	 *
	 * @param id {number}
	 *   Registered `ui.UIComponentEnum` ID.
	 */
	public static setButtonEnabled(id: number, enabled: boolean) {
		const button = (ui.get(id) as QuickMenuButton);
		if (button) {
			button.enabled = enabled;
			QuickMenu.refresh();
			QuickMenu.update();
		}
	}

	/**
	 * Updates menu positioning relative to upper-right corner of viewport.
	 */
	public static refresh() {
		// place buttons in upper-right corner of viewport
		const btn_main = QuickMenu.getElement();
		const rect = document.getElementById("viewport")!.getBoundingClientRect();

		let drawLeft = rect.right - btn_main.width;
		let drawTop = rect.top;
		// main button
		btn_main.style["left"] = drawLeft + "px";
		btn_main.style["top"] = drawTop + "px";

		// horizontal buttons
		for (const btn of QuickMenu.buttonListX) {
			if (!btn.enabled) {
				continue;
			}
			// all buttons should be same size
			drawLeft -= btn_main.width;
			btn.setPos(drawLeft, drawTop);
		}

		// vertical buttons
		drawLeft = rect.right - btn_main.width
		for (const btn of QuickMenu.buttonListY) {
			if (!btn.enabled) {
				continue;
			}
			// all buttons should be same size
			drawTop += btn_main.height;
			btn.setPos(drawLeft, drawTop);
		}
	}

	/**
	 * Toggles expanded state.
	 */
	private static toggle() {
		QuickMenu.expanded = !QuickMenu.expanded;
		QuickMenu.update();
	}

	/**
	 * Updates main button image & shows or hides sub-buttons depending on expanded state.
	 */
	private static update() {
		if (!QuickMenu.isVisible()) {
			// menu is disabled so we don't need to update visibility of sub-buttons
			return;
		}
		QuickMenu.getElement().style["transform"] = "rotate("
				+ (QuickMenu.expanded ? 90 : 0) + "deg)";
		for (const btn of [...QuickMenu.buttonListX, ...QuickMenu.buttonListY]) {
			btn.setVisible(QuickMenu.expanded && btn.enabled);
		}
	}

	/**
	 * Determines enabled (visible) state of menu.
	 *
	 * @param visible {boolean}
	 *   Whether quick menu should be shown (default: `true`).
	 */
	public static setVisible(visible=true) {
		if (QuickMenu.isVisible() == visible) {
			// menu is already in target state
			return;
		}
		// reset to collapsed state
		QuickMenu.expanded = false;
		QuickMenu.update();
		// hide main button
		QuickMenu.getElement().style["display"] = visible ? "block" : "none";
		if (visible) {
			QuickMenu.refresh();
		}
	}

	/**
	 * Checks if menu is in enabled (visible) state.
	 *
	 * @return {boolean}
	 *   `true` if main button element's display style is not set to "none".
	 */
	private static isVisible(): boolean {
		return QuickMenu.getElement().style["display"] !== "none";
	}

	/**
	 * Retrieves the master element associated with the menu.
	 *
	 * @return
	 *   Main `HTMLImageElement`.
	 */
	private static getElement(): HTMLImageElement {
		return document.getElementById("qm-main")! as HTMLImageElement;
	}
}
