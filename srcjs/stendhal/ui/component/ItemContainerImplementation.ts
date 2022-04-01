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
import { Item } from "../../entity/Item";

declare var marauroa: any;
declare var stendhal: any;

/**
 * a container for items like a bag or corpse
 */
export class ItemContainerImplementation {
	private rightClickDuration = 300;
	private timestampMouseDown = 0;
	private timestampMouseDownPrev = 0;

	/* touch handling */

	// duration indicating long touch
	private longTouchDuration = 300;
	// timestamp of touch start
	private timestampTouchStart = 0;
	// timestamp of touch end
	private timestampTouchEnd = 0;


	// TODO: replace usage of global document.getElementById()

	/**
	 * slot name, slot size, object (a corpse or chest) or null for marauroa.me,
	 * which changes on zone change.
	 */
	constructor(private slot: string, private size: number, private object: any, private suffix: string, private quickPickup: boolean, private defaultImage?: string) {
		this.init(size);
	}

	public init(size: number) {
		this.size = size;
		for (let i = 0; i < size; i++) {
			let e = document.getElementById(this.slot + this.suffix + i)!;
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
			e.addEventListener("touchstart", (event: TouchEvent) => {
				this.onTouchStart(event)
			});
			e.addEventListener("touchend", (event: TouchEvent) => {
				this.onTouchEnd(event)
			});
			/*
			e.addEventListener("touchmove", (event: TouchEvent) => {
				this.onTouchMove(event)
			});
			e.addEventListener("touchcancel", (event: TouchEvent) => {
				this.onTouchCancel(event)
			});
			*/
			e.addEventListener("contextmenu", (event: MouseEvent) => {
				this.onContextMenu(event);
			});
			e.addEventListener("mouseenter", (event: MouseEvent) => {
				this.onMouseEnter(event);
			});
			e.addEventListener("mouseleave", (event: MouseEvent) => {
				this.onMouseLeave(event);
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

				const item = <Item> o;
				const subclass = o["subclass"];
				let xOffset = 0;
				let yOffset = 0;
				if (item["name"] === "emerald ring" && item["amount"] == 0) {
					yOffset = -32;
				} else if (item.isAnimated()) {
					// FIXME: animation does not start until item is removed &
					// picked up from ground or zone change
					item.stepAnimation();
					xOffset = -(item.getXFrameIndex() * 32);
				}

				e.style.backgroundImage = "url(/data/sprites/items/" + o["class"] + "/" + o["subclass"] + ".png)";
				e.style.backgroundPosition = xOffset + "px " + yOffset + "px";
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

		let slotNumber = (event.target as HTMLElement).id.slice(this.slot.length + this.suffix.length);
		let item = myobject[this.slot].getByIndex(slotNumber);
		if (item) {
			stendhal.ui.heldItem = {
				path: item.getIdPath(),
				zone: marauroa.currentZoneName
			} as any;
			if (event.dataTransfer) {
				const img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
				event.dataTransfer.setDragImage(img, 0, 0);
			}
		} else {
			event.preventDefault();
		}
	}

	private onDragOver(event: DragEvent) {
		event.preventDefault();
		if (event.dataTransfer) {
			event.dataTransfer.dropEffect = "move";
		}
		return false;
	}

	private onDrop(event: DragEvent) {
		const myobject = this.object || marauroa.me;
		if (stendhal.ui.heldItem) {
			const  targetPath = "[" + myobject["id"] + "\t" + this.slot + "]";
			const action = {
				"type": "equip",
				"source_path": stendhal.ui.heldItem.path,
				"target_path": targetPath,
				"zone": stendhal.ui.heldItem.zone
			} as any;

			// item was dropped
			stendhal.ui.heldItem = undefined;

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
	}

	onMouseUp(evt: MouseEvent|TouchEvent) {
		evt.preventDefault();
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
			} else if (!stendhal.ui.heldItem) {
				if (!stendhal.config.getBoolean("action.item.doubleclick") || this.isDoubleClick(event)) {
					marauroa.clientFramework.sendAction({
						type: "use",
						"target_path": (event.target as any).dataItem.getIdPath(),
						"zone": marauroa.currentZoneName
					});
				}
			}
		}
		document.getElementById("gamewindow")!.focus();
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
	private onTouchStart(evt: TouchEvent) {
		// disable default "mousedown" event
		evt.preventDefault();
		this.timestampTouchStart = +new Date();
	}

	/**
	 * Event handler when touch event ends.
	 *
	 * @param evt
	 *     The touch event.
	 */
	private onTouchEnd(evt: TouchEvent) {
		// disable default "mouseup" event
		evt.preventDefault();

		if (!stendhal.ui.heldItem) {
			this.timestampTouchEnd = +new Date();
			this.onMouseUp(evt);
		} else {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("drop"));
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

		if (stendhal.ui.heldItem) {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("dragover"));
		} else {
			this.dispatchEventOnTouchTarget(evt,
				new DragEvent("dragstart"));
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

	private onMouseEnter(evt: MouseEvent) {
		const dataItem = (evt.target as any).dataItem;
		if (dataItem) {
			if (dataItem["class"] === "scroll" && dataItem["dest"]) {
				const dest = dataItem["dest"].split(",");
				if (dest.length > 2) {
					document.getElementById((evt.target as HTMLElement).id)!
							.title = dest[0] + " " + dest[1] + "," + dest[2];
				}
			}
		}
	}

	private onMouseLeave(evt: MouseEvent) {
		if (evt.target) {
			document.getElementById((evt.target as HTMLElement).id)!.title = "";
		}
	}
}
