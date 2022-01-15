/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Component } from "./Component";

declare var stendhal: any;

export class FloatingWindow extends Component {

	private readonly closeSound = "click-1";
	private opened = true;

	private onMouseMovedDuringDragListener: EventListener;
	private onMouseUpDuringDragListener: EventListener;
	private offsetX = 0;
	private offsetY = 0;

	constructor(title: string, protected contentComponent: Component, x: number, y: number) {
		super("window-template");

		// create HTML code for window
		this.componentElement.style.position = "absolute";
		this.componentElement.style.left = x + "px";
		this.componentElement.style.top = y + "px";
		if (title) {
			this.componentElement.querySelector(".windowtitle")!.textContent = title;
		} else {
			this.componentElement.querySelector(".windowtitlebar")!.classList.add("hidden");
		}
		this.componentElement.querySelector(".windowcontent")!.append(contentComponent.componentElement);

		// register and prepare event listeners
		this.componentElement.querySelector(".windowtitlebar")!.addEventListener("mousedown", (event) => {
			this.onMouseDown(event as MouseEvent)
		});
		this.componentElement.querySelector(".windowtitleclose")!.addEventListener("click", (event) => {
			this.onClose(event);
		});
		this.onMouseMovedDuringDragListener = (event: Event) => {
			this.onMouseMovedDuringDrag(event as MouseEvent);
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
		stendhal.ui.sound.playGlobalizedEffect(this.closeSound);
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
		event.preventDefault();
		let box = this.componentElement.getBoundingClientRect();
		this.offsetX = event.clientX - box.left - window.pageXOffset;
		this.offsetY = event.clientY - box.top - window.pageYOffset;
	}

	/**
	 * updates position of popup window during drag
	 */
	private onMouseMovedDuringDrag(event: MouseEvent) {
		this.componentElement.style.left = event.clientX - this.offsetX + 'px';
		this.componentElement.style.top = event.clientY - this.offsetY + 'px';
	}

	/**
	 * deregister global event listeners used for dragging popup window
	 */
	private onMouseUpDuringDrag() {
		window.removeEventListener("mousemove", this.onMouseMovedDuringDragListener, true);
		window.removeEventListener("mouseup", this.onMouseUpDuringDragListener, true);
	}
}
