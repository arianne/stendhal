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

declare var stendhal: any;

import { Component } from "../toolkit/Component";
import { ui } from "../UI";
import { Paths } from "../../data/Paths";


/**
 * Base class for quick menu buttons.
 */
export abstract class QuickMenuButton extends Component {

	/** Property to determine if button is visible/disabled. */
	public enabled = true;
	/** Property denoting button was pressed with mouse click. */
	private clickEngaged = false;
	/** Property denoting button was pressed with tap/touch. */
	private touchEngaged = 0;


	/**
	 * Creates a new quick menu button.
	 *
	 * @param id {string}
	 *   Basename of DOM element ID.
	 * @param cid {ui.UIComponentEnum.UIComponentEnum}
	 *   ID to optionally register component.
	 */
	protected constructor(id: string, cid?: number) {
		super("qm-" + id);

		// register component
		if (typeof(cid) !== "undefined") {
			ui.registerComponent(cid, this);
		}

		(this.componentElement as HTMLImageElement).src = Paths.gui + "/quickmenu/" + id + ".png";
		this.componentElement.style["cursor"] = "url(" + Paths.sprites + "/cursor/highlight.png) 1 3, auto";
		this.componentElement.draggable = false;
		// listen for click events
		this.componentElement.addEventListener("mousedown", (evt: MouseEvent) => {
			evt.preventDefault();
			if (evt.button == 0) {
				this.clickEngaged = true;
			}
		});
		this.componentElement.addEventListener("touchstart", (evt: TouchEvent) => {
			evt.preventDefault();
			this.touchEngaged = evt.changedTouches.length;
		});
		this.componentElement.addEventListener("mouseup", (evt: MouseEvent) => {
			evt.preventDefault();
			if (this.clickEngaged && evt.button == 0) {
				// FIXME: should veto if moved too much before release
				this.onClick(evt);
			}
			this.clickEngaged = false;
		});
		this.componentElement.addEventListener("touchend", (evt: TouchEvent) => {
			evt.preventDefault();
			const target = stendhal.ui.html.extractTarget(evt, this.touchEngaged - 1);
			if (this.touchEngaged == evt.changedTouches.length && target == this.componentElement) {
				// FIXME: should veto if moved too much before release
				this.onClick(evt);
			}
			this.touchEngaged = 0;
		});
		this.update();
	}

	/**
	 * Sets button's position relative to page.
	 *
	 * @param x {number}
	 *   X coordinate pixel position.
	 * @param y {number}F
	 *   Y coordinate pixel position.
	 */
	public setPos(x: number, y: number) {
		this.componentElement.style["left"] = x + "px";
		this.componentElement.style["top"] = y + "px";
	}

	/**
	 * Sets image to be used for button.
	 *
	 * @param basename {string}
	 *   Base filename of PNG image located in &gt;data&lt;/gui/quickmenu/ directory.
	 */
	protected setImageBasename(basename: string) {
		(this.componentElement as HTMLImageElement).src = Paths.gui + "/quickmenu/" + basename + ".png";
	}

	/**
	 * Action(s) to execute when button is clicked/tapped.
	 */
	protected abstract onClick(evt: Event): void;

	/**
	 * Action(s) to execute when button state is changed.
	 *
	 * NOTE: maybe it would be better to use `ui.toolkit.Component.refresh` instead of creating new method?
	 */
	public update() {
		// implementing classes can override
	}
}
