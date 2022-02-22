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

import { ui } from "../UI";

import { ActionContextMenu } from "../dialog/ActionContextMenu";
import { DropQuantitySelectorDialog } from "../dialog/DropQuantitySelectorDialog";

declare var marauroa: any;
declare var stendhal: any;

/**
 * a container for items like a bag or corpse
 */
export class ItemContainerImplementation {
	private rightClickDuration = 300;
	private timestampMouseDown = 0;
	private timestampMouseDownPrev = 0;
	private dragData: DataTransfer|null = null;

	/* touch handling */

	// duration indicating long touch
	private longTouchDuration = 300;
	// timestamp of touch start
	private timestampTouchStart = 0;
	// timestamp of touch end
	private timestampTouchEnd = 0;
	// indicates drag event using touch is active
	private touchDragActive = false;


	// TODO: replace usage of global document.getElementById()

	/**
	 * slot name, slot size, object (a corpse or chest) or null for marauroa.me,
	 * which changes on zone change.
	 */
	constructor(private slot: string, private size: number, private object: any, private suffix: string, private quickPickup: boolean, private defaultImage?: string) {
		for (let i = 0; i < size; i++) {
			let e = document.getElementById(slot + suffix + i)!;
			e.setAttribute("draggable", "true");
			e.addEventListener("dragstart", (event: DragEvent) => {
				this.onDragStart(event)
			});
			e.addEventListener("dragover", (event: DragEvent) => {
				this.onDragOver(event)
			});
			e.addEventListener("drop", (event: DragEvent) => {
				this.onDrop(event)
			});
			e.addEventListener("mousedown", (event: MouseEvent) => {
				this.onMouseDown(event)
			});
			e.addEventListener("mouseup", (event: MouseEvent) => {
				this.onMouseUp(event)
			});
			// let default "mousedown" handler handle "touchstart"
			/*
			e.addEventListener("touchstart", (event: TouchEvent) => {
				this.onTouchStart(event)
			});
			*/
			e.addEventListener("touchend", (event: TouchEvent) => {
				this.onTouchEnd(event)
			});
			e.addEventListener("touchmove", (event: TouchEvent) => {
				this.onTouchMove(event)
			});
			e.addEventListener("touchcancel", (event: TouchEvent) => {
				this.onTouchCancel(event)
			});
			e.addEventListener("contextmenu", (event: MouseEvent) => {
				this.onContextMenu(event)
			});
		}
		this.update();
	}

	public update() {
		this.render();
	}

	public render() {
		let myobject = this.object || marauroa.me;
		let cnt = 0;
		if (myobject && myobject[this.slot]) {
			for (let i = 0; i < myobject[this.slot].count(); i++) {
				let o = myobject[this.slot].getByIndex(i);
				let e = document.getElementById(this.slot + this.suffix + cnt)!;
				e.style.backgroundImage = "url(/data/sprites/items/" + o["class"] + "/" + o["subclass"] + ".png " + ")";
				e.textContent = o.formatQuantity();
				(e as any).dataItem = o;
				cnt++;
			}
		}

		for (let i = cnt; i < this.size; i++) {
			let e = document.getElementById(this.slot +this. suffix + i)!;
			if (this.defaultImage) {
				e.style.backgroundImage = "url(/data/gui/" + this.defaultImage + ")";
			} else {
				e.style.backgroundImage = "none";
			}
			e.textContent = "";
			(e as any).dataItem = undefined;
		}
	}

	private onDragStart(event: DragEvent) {
		let myobject = this.object || marauroa.me;
		if (!myobject[this.slot]) {
			event.preventDefault();
			return;
		}

		// FIXME: touch events not picking up item

		// long touches dispatch dragstart event same as mouse
		// XXX: this may not be the case for all devices
		this.dragData = event.dataTransfer;

		let slotNumber = (event.target as HTMLElement).id.slice(this.slot.length + this.suffix.length);
		let item = myobject[this.slot].getByIndex(slotNumber);
		if (item) {
			let img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			event.dataTransfer!.setDragImage(img, 0, 0);
			event.dataTransfer!.setData("Text", JSON.stringify({
				path: item.getIdPath(),
				zone: marauroa.currentZoneName
			}));
		} else {
			event.preventDefault();
		}
	}

	private onDragOver(event: DragEvent) {
		event.preventDefault();
		event.dataTransfer!.dropEffect = "move"; // FIXME: dropEffect is null for touch events
		return false;
	}

	private onDrop(event: DragEvent) {
		let myobject = this.object || marauroa.me;
		let datastr = event.dataTransfer?.getData("Text") || event.dataTransfer?.getData("text/x-stendhal");
		if (datastr) {
			let data = JSON.parse(datastr);
			let targetPath = "[" + myobject["id"] + "\t" + this.slot + "]";
			let action = {
				"type": "equip",
				"source_path": data.path,
				"target_path": targetPath,
				"zone" : data.zone
			};
			// if ctrl is pressed, we ask for the quantity
			if (event.ctrlKey) {
				ui.createSingletonFloatingWindow("Quantity",
					new DropQuantitySelectorDialog(action),
					event.pageX - 50, event.pageY - 25);
			} else {
				marauroa.clientFramework.sendAction(action);
			}
		}
		event.stopPropagation();
		event.preventDefault();

		this.dragData = null;
	}

	private onContextMenu(event: MouseEvent) {
		event.preventDefault();
	}

	isRightClick(event: MouseEvent) {
		if (event.which) {
			return (event.which === 3);
		} else {
			return (event.button === 2);
		}
	}

	isDoubleClick(evt: MouseEvent) {
		return (this.timestampMouseDown - this.timestampMouseDownPrev <= this.rightClickDuration);
	}

	private isLongTouch() {
		return (this.timestampTouchEnd - this.timestampTouchStart > this.longTouchDuration);
	}

	onMouseDown(event: MouseEvent|TouchEvent) {
		this.timestampMouseDownPrev = this.timestampMouseDown;
		this.timestampMouseDown = +new Date();
		if (event.type === "touchstart") {
			this.timestampTouchStart = this.timestampMouseDown;
		}
	}

	onMouseUp(evt: MouseEvent|TouchEvent) {
		/*
		if (evt.type !== "touchend") {
			evt.preventDefault();
		}
		*/
		let event = stendhal.ui.html.extractPosition(evt);
		if ((event.target as any).dataItem) {
			if (this.quickPickup) {
				marauroa.clientFramework.sendAction({
					type: "equip",
					"source_path": (event.target as any).dataItem.getIdPath(),
					"target_path": "[" + marauroa.me["id"] + "\tbag]",
					"clicked": "", // useful for changing default target in equip action
					"zone": marauroa.currentZoneName
				});
				return;
			}

			if (this.isRightClick(event) || (evt.type === "touchend" && this.isLongTouch())) {
				ui.createSingletonFloatingWindow("Action",
					new ActionContextMenu((event.target as any).dataItem),
					event.pageX - 50, event.pageY - 5);
			//} else if (this.isDoubleClick(event)) {
			} else if (!this.touchDragActive) { // some players might like single click
				marauroa.clientFramework.sendAction({
					type: "use",
					"target_path": (event.target as any).dataItem.getIdPath(),
					"zone": marauroa.currentZoneName
				});
			}
		}
		document.getElementById("gamewindow")!.focus();

		this.dragData = null;
	}

	private dispatchEventOnTouchTarget(evt: TouchEvent, newEvent: Event) {
		const target = stendhal.ui.html.extractPosition(evt).target;
		if (target !== null && typeof target !== "undefined") {
			target.dispatchEvent(newEvent);
		}
	}

	/**
	 * Event handler when touch event starts.
	 *
	 * @param evt
	 *     The touch event.
	 */
	/*
	private onTouchStart(evt: TouchEvent) {
		// ANDROID NOTE: "touchstart" event also dispatches "mousedown"
		// don't override default "mousedown" event

		//this.onMouseDown(evt);
		//this.timestampTouchStart = this.timestampMouseDown;

		this.timestampTouchStart = +new Date();
	}
	*/

	/**
	 * Event handler when touch event ends.
	 *
	 * @param evt
	 *     The touch event.
	 */
	private onTouchEnd(evt: TouchEvent) {
		// ANDROID NOTE: "touchend" event also dispatches "mouseup"

		// override default "mouseup" event
		evt.preventDefault();

		if (!this.touchDragActive) {
			this.timestampTouchEnd = +new Date();
			this.onMouseUp(evt);
		} else {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("drop", {dataTransfer: this.dragData}));
			this.touchDragActive = false;
		}
	}

	/**
	 * Event handler when touch drag occurs.
	 *
	 * @param evt
	 *     The touch event.
	 */
	private onTouchMove(evt: TouchEvent) {
		evt.preventDefault(); // FIXME: can't prevent scrolling?

		if (this.touchDragActive) {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("dragover", {dataTransfer: this.dragData}));
		} else {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("dragstart", {dataTransfer: this.dragData}));

			// the first move is used to pick up the item
			this.touchDragActive = true;
		}
	}

	/**
	 * Event handler when touch event is cancelled.
	 *
	 * @param evt
	 *     The touch event.
	 */
	private onTouchCancel(evt: TouchEvent) {
		// DEBUG:
		console.log("onTouchCancel: " + evt.type);
	}
}
