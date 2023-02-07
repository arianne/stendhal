/***************************************************************************
 *                (C) Copyright 2003-2023 - Faiumoni e. V.                 *
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

	// slot index where cursor is hovering
	private selectedIdx: string|undefined = undefined;

	// marked for updating certain attributes
	private dirty = false;


	// TODO: replace usage of global document.getElementById()

	/**
	 * slot name, slot size, object (a corpse or chest) or null for marauroa.me,
	 * which changes on zone change.
	 */
	constructor(private parentElement: Document|HTMLElement, private slot: string, private size: number, public object: any, private suffix: string, private quickPickup: boolean, private defaultImage?: string) {
		this.init(size);
	}

	public init(size: number) {
		this.size = size;
		for (let i = 0; i < size; i++) {
			let e = this.parentElement.querySelector("#" + this.slot + this.suffix + i) as HTMLElement;
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

	/**
	 * Marks items to update cursors & tooltips.
	 */
	public markDirty() {
		this.dirty = true;
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
				let e = this.parentElement.querySelector("#" + this.slot + this.suffix + cnt) as HTMLElement;
				if (!e) {
					continue;
				}

				this.dirty = this.dirty || o !== (e as any).dataItem;
				const item = <Item> o;
				let xOffset = 0;
				let yOffset = 0;
				if (item["name"] === "emerald ring" && item["amount"] == 0) {
					yOffset = -32;
				} else if (item.isAnimated()) {
					item.stepAnimation();
					xOffset = -(item.getXFrameIndex() * 32);
				}

				e.style.backgroundImage = "url("
						+ stendhal.data.sprites.checkPath(stendhal.paths.sprites
								+ "/items/" + o["class"] + "/" + o["subclass"] + ".png")
						+ ")";
				e.style.backgroundPosition = (xOffset+1) + "px " + (yOffset+1) + "px";
				e.textContent = o.formatQuantity();
				if (this.dirty) {
					this.updateCursor(e, item);
					this.updateToolTip(e, item);
				}
				(e as any).dataItem = o;
				cnt++;
			}
		}

		for (let i = cnt; i < this.size; i++) {
			let e = this.parentElement.querySelector("#" + this.slot +this. suffix + i) as HTMLElement;
			if (this.defaultImage) {
				e.style.backgroundImage = "url(" + stendhal.paths.gui + "/" + this.defaultImage + ")";
			} else {
				e.style.backgroundImage = "none";
			}
			e.textContent = "";
			if (this.dirty) {
				this.updateCursor(e);
				this.updateToolTip(e);
			}
			(e as any).dataItem = undefined;
		}

		this.dirty = false;
	}

	private onDragStart(event: DragEvent|TouchEvent) {
		let myobject = this.object || marauroa.me;
		if (!myobject[this.slot]) {
			event.preventDefault();
			return;
		}

		let target
		if (event instanceof DragEvent) {
			target = (event.target as HTMLElement);
		} else {
			// touch event
			const touch = event.touches[0] || event.targetTouches[0] || event.changedTouches[0];
			target = (touch.target as HTMLElement);
		}

		const slotNumber = target.id.slice(this.slot.length + this.suffix.length);
		let item = myobject[this.slot].getByIndex(slotNumber);
		if (item) {
			stendhal.ui.heldItem = {
				path: item.getIdPath(),
				zone: marauroa.currentZoneName,
				slot: this.slot
			} as any;

			const img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
			if (event instanceof DragEvent && event.dataTransfer) {
				event.dataTransfer.setDragImage(img, 0, 0);
			} else if (event instanceof TouchEvent) {
				stendhal.ui.touch.setHeldItem(img);
			}
		} else {
			event.preventDefault();
		}
	}

	private onDragOver(event: DragEvent|TouchEvent) {
		event.preventDefault();
		if (event instanceof DragEvent && event.dataTransfer) {
			event.dataTransfer.dropEffect = "move";
		}

		// store index of where cursor is located
		const id = (event.target as HTMLElement).id;
		if (id.includes(".")) {
			const tmp = id.split(".");
			const idx = tmp[tmp.length - 1];
			if (!isNaN(parseInt(idx, 10))) {
				this.selectedIdx = idx;
			}
		}

		return false;
	}

	private onDrop(event: DragEvent|TouchEvent) {
		const myobject = this.object || marauroa.me;
		if (stendhal.ui.heldItem) {
			const action = {
				"source_path": stendhal.ui.heldItem.path
			} as any;

			if (stendhal.ui.heldItem.slot === this.slot) {
				action["type"] = "reorder";
				action["new_position"] = this.selectedIdx || "" + (this.size - 1);
			} else {
				action["type"] = "equip";
				action["target_path"] = "[" + myobject["id"] + "\t" + this.slot + "]";
				action["zone"] = stendhal.ui.heldItem.zone;
			}

			stendhal.ui.heldItem = undefined;

			// if ctrl is pressed, we ask for the quantity
			if (event instanceof DragEvent && event.ctrlKey) {
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

	onMouseDown(event: MouseEvent|TouchEvent) {
		this.timestampMouseDownPrev = this.timestampMouseDown;
		this.timestampMouseDown = +new Date();
	}

	onMouseUp(evt: MouseEvent|TouchEvent) {
		if (evt instanceof MouseEvent) {
			evt.preventDefault();
		}
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

			if (this.isRightClick(event) || stendhal.ui.touch.isLongTouch()) {
				stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
					new ActionContextMenu((event.target as any).dataItem),
					event.pageX - 50, event.pageY - 5));
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

	private onMouseEnter(evt: MouseEvent) {
		// nothing
	}

	private onMouseLeave(evt: MouseEvent) {
		// nothing
	}

	private onTouchStart(evt: TouchEvent) {
		stendhal.ui.touch.onTouchStart();
	}

	private onTouchEnd(evt: TouchEvent) {
		stendhal.ui.touch.onTouchEnd();
		if (stendhal.ui.touch.isLongTouch() && !stendhal.ui.touch.held) {
			// don't call this.onMouseUp
			evt.preventDefault();

			this.onDragStart(evt);
		} else if (stendhal.ui.touch.held) {
			// don't call this.onMouseUp
			evt.preventDefault();

			this.onDrop(evt);
			stendhal.ui.touch.unsetHeldItem();
		}
	}

	/**
	 * Updates cursor to display for targeted item.
	 *
	 * @param target
	 *     HTMLElement representing item.
	 * @param item
	 *     Object containing item information.
	 */
	private updateCursor(target: HTMLElement, item?: Item) {
		if (item) {
			target.style.cursor = item.getCursor(0, 0);
			return;
		}
		target.style.cursor = "url(" + stendhal.paths.sprites
				+ "/cursor/normal.png) 1 3, auto";
	}

	/**
	 * Sets tooltip to be shown for item.
	 *
	 * @param target
	 *     HTMLElement representing item.
	 * @param item
	 *     Object containing item information.
	 */
	private updateToolTip(target: HTMLElement, item?: Item) {
		target.title = typeof(item) !== "undefined" ? item.getToolTip() : "";
	}
}
