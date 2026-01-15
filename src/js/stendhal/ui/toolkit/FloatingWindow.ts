/***************************************************************************
 *                (C) Copyright 2015-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../../stendhal";
import { Component } from "./Component";
import { ui } from "../UI";
import { Point } from "../../util/Point";


export class FloatingWindow extends Component {

	private readonly closeSound = "click-1";
	private opened = true;

	private onMouseMovedDuringDragListener: EventListener;
	private onMouseUpDuringDragListener: EventListener;
	private offsetX = 0;
	private offsetY = 0;

	private content: Component;

	private windowId?: string;


	constructor(title: string, protected contentComponent: Component, x: number, y: number) {
		super("window-template");

		this.content = contentComponent;

		// create HTML code for window
		this.componentElement.style.position = "absolute";
		this.componentElement.style.left = x + "px";
		this.componentElement.style.top = y + "px";

		const titleBar = this.child(".windowtitlebar")!;

		// apply theme
		stendhal.config.applyTheme(titleBar);

		if (title) {
			this.child(".windowtitle")!.textContent = title;
		} else {
			titleBar.classList.add("hidden");
		}
		this.child(".windowcontent")!.append(contentComponent.componentElement);

		// register and prepare event listeners
		titleBar.addEventListener("mousedown", (event) => {
			this.onMouseDown(event as MouseEvent)
		});
		titleBar.addEventListener("touchstart", (event) => {
			this.onTouchStart(event as TouchEvent)
		});
		const closeButton = this.child(".windowtitleclose")!;
		closeButton.addEventListener("click", (event) => {
			this.onClose(event);
			stendhal.sound.playGlobalizedEffect(this.closeSound);
		});
		closeButton.addEventListener("touchend", (event) => {
			this.onClose(event);
			stendhal.sound.playGlobalizedEffect(this.closeSound);
		});
		this.onMouseMovedDuringDragListener = (event: Event) => {
			if (event.type === "mousemove") {
				this.onMouseMovedDuringDrag(event as MouseEvent);
			} else {
				this.onTouchMovedDuringDrag(event as TouchEvent);
			}
		}
		this.onMouseUpDuringDragListener = () => {
			this.onMouseUpDuringDrag();
		}
		contentComponent.componentElement.addEventListener("close", (event) => {
			this.onClose(event);
		})
		this.contentComponent.parentComponent = this;

		// add window to DOM
		let popupcontainer = document.getElementById("popupcontainer")!;
		popupcontainer.appendChild(this.componentElement);
	}


	public close() {
		this.componentElement.remove();
		this.contentComponent.onParentClose();
		this.opened = false;
		this.contentComponent.parentComponent = undefined;
	}

	private onClose(event: Event) {
		this.close();
		event.preventDefault();
	}

	public isOpen() {
		return this.opened;
	}

	/**
	 * start draging of popup window
	 */
	private onMouseDown(event: MouseEvent) {
		window.addEventListener("mousemove", this.onMouseMovedDuringDragListener, true);
		window.addEventListener("mouseup", this.onMouseUpDuringDragListener, true);
		window.addEventListener("touchmove", this.onMouseMovedDuringDragListener, true);
		window.addEventListener("touchend", this.onMouseUpDuringDragListener, true);

		event.preventDefault();
		let box = this.componentElement.getBoundingClientRect();
		this.offsetX = event.clientX - box.left - window.pageXOffset;
		this.offsetY = event.clientY - box.top - window.pageYOffset;
	}

	private onTouchStart(event: TouchEvent) {
		const firstT = event.changedTouches[0];
		const simulated = new MouseEvent("mousedown", {
			screenX: firstT.screenX, screenY: firstT.screenY,
			clientX: firstT.clientX, clientY: firstT.clientY
		})
		firstT.target.dispatchEvent(simulated);

		event.preventDefault();
	}

	/**
	 * updates position of popup window during drag
	 */
	private onMouseMovedDuringDrag(event: MouseEvent) {
		this.componentElement.style.left = event.clientX - this.offsetX + 'px';
		this.componentElement.style.top = event.clientY - this.offsetY + 'px';
		this.onMoved();
	}

	private onTouchMovedDuringDrag(event: TouchEvent) {
		const firstT = event.changedTouches[0];
		const simulated = new MouseEvent("mousemove", {
			screenX: firstT.screenX, screenY: firstT.screenY,
			clientX: firstT.clientX, clientY: firstT.clientY
		})
		firstT.target.dispatchEvent(simulated);

		// FIXME: how to disable scrolling
		//event.preventDefault();
	}

	/**
	 * deregister global event listeners used for dragging popup window
	 */
	private onMouseUpDuringDrag() {
		window.removeEventListener("mousemove", this.onMouseMovedDuringDragListener, true);
		window.removeEventListener("mouseup", this.onMouseUpDuringDragListener, true);
		window.removeEventListener("touchmove", this.onMouseMovedDuringDragListener, true);
		window.removeEventListener("touchend", this.onMouseUpDuringDragListener, true);
		this.onMoved();
	}

	/**
	 * Keeps dialog window within browser page.
	 */
	private checkPos(): Point {
		if (this.content) {
			this.content.onMoved();
		}

		const dialogArea = this.componentElement.getBoundingClientRect();
		const clientArea = document.documentElement.getBoundingClientRect();

		// clientArea.height is 0, if there are now child elements (e. g. on login / choose character dialogs)
		let clientAreaHeight = clientArea.height;
		if (clientAreaHeight == 0) {
			clientAreaHeight = window.visualViewport?.height || 200;
		}

		const offset = ui.getPageOffset();

		let newX = dialogArea.x;
		let newY = dialogArea.y;

		if (newX < 0) {
			newX = 0;
			this.componentElement.style.left = (offset.x + newX) + "px";
		} else if (dialogArea.x + dialogArea.width > clientArea.right + offset.x) {
			newX = clientArea.right - dialogArea.width;
			this.componentElement.style.left = (offset.x + newX) + "px";
		}
		if (newY < 0) {
			newY = 0;
			this.componentElement.style.top = (offset.y + newY) + "px";
		} else if (dialogArea.y + dialogArea.height > clientAreaHeight) {
			newY = clientAreaHeight - dialogArea.height;
			this.componentElement.style.top = (offset.y + newY) + "px";
		}

		return new Point(newX + offset.x, newY + offset.y);
	}

	public override onMoved() {
		const pos = this.checkPos();
		if (typeof(this.windowId) !== "undefined") {
			stendhal.config.setWindowState(this.windowId, pos.x, pos.y);
		}
	}

	public setId(id: string|undefined) {
		this.windowId = id;
	}

	/**
	 * Sets visibility of close button.
	 *
	 * Default is shown.
	 *
	 * @param {boolean} enable
	 *   Set to `false` to hide.
	 */
	enableCloseButton(enable: boolean) {
		this.child(".windowtitleclose")!.style.display = enable ? "" : "none";
	}
}
