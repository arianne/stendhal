/***************************************************************************
 *                (C) Copyright 2003-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../../stendhal";

import { ui } from "../UI";

import { ActionContextMenu } from "../dialog/ActionContextMenu";
import { DropQuantitySelectorDialog } from "../dialog/DropQuantitySelectorDialog";
import { Item } from "../../entity/Item";

import { singletons } from "../../SingletonRepo";

import { Point } from "../../util/Point";
import { Paths } from "../../data/Paths";
import { HTMLImageElementUtil } from "sprite/image/HTMLImageElementUtil";


/**
 * a container for items like a bag or corpse
 */
export class ItemContainerImplementation {

	private rightClickDuration = 300;
	private timestampMouseDown = 0;
	private timestampMouseDownPrev = 0;
	private lastClickedId = "";

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
			e.addEventListener("touchmove", (event: TouchEvent) => {
				this.onTouchMove(event);
			}, {passive: true});
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
			}, {passive: true});
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
	 * Retrieves the inventory component element associated with this container.
	 *
	 * @return {HTMLElement}
	 *   Parent element.
	 */
	public getParentElement(): HTMLElement {
		return this.parentElement as HTMLElement;
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
				let yOffset = (item["state"] || 0) * -32;
				if (item.isAnimated()) {
					item.stepAnimation();
					xOffset = -(item.getXFrameIndex() * 32);
				}

				e.style.backgroundImage = "url("
						+ singletons.getSpriteStore().checkPath(Paths.sprites
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
				e.style.backgroundImage = "url(" + Paths.gui + "/" + this.defaultImage + ")";
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
		// some mobile browsers such as Chrome call "dragstart" via long touch
		if (!myobject[this.slot] || (event.type === "dragstart" && stendhal.ui.touch.isTouchEngaged())) {
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
			const heldObject = {
				path: item.getIdPath(),
				zone: marauroa.currentZoneName,
				slot: this.slot,
				quantity: item.hasOwnProperty("quantity") ? item["quantity"] : 1
			};

			const img = HTMLImageElementUtil.getAreaOf(singletons.getSpriteStore().get(item.sprite.filename), 32, 32);
			if (event instanceof DragEvent && event.dataTransfer) {
				stendhal.ui.heldObject = heldObject;
				event.dataTransfer.setDragImage(img, 0, 0);
			} else if (stendhal.ui.touch.isTouchEvent(event)) {
				stendhal.ui.touch.setHolding(true);
				// TODO: move when supported by mouse events
				const pos = stendhal.ui.html.extractPosition(event);
				singletons.getHeldObjectManager().set(heldObject, img, new Point(pos.pageX, pos.pageY));
			}
		} else {
			event.preventDefault();
		}
	}

	/**
	 * Handles displaying an icon for objects dragged with touch.
	 */
	private onTouchMove(event: TouchEvent) {
		if (stendhal.ui.heldObject) {
			return;
		}
		this.onDragStart(event);
	}

	private onDragOver(event: DragEvent|TouchEvent) {
		event.preventDefault();
		if (event instanceof DragEvent && event.dataTransfer) {
			event.dataTransfer.dropEffect = "move";
		}
		return false;
	}

	/**
	 * Extracts slot index from element ID.
	 */
	private parseIndex(id: string): string|undefined {
		// NOTE: element ID is formatted as "<name>-<id>-<index>"
		//       - name:  inventory name (e.g. "bag")
		//       - id:    inventory ID number
		//       - index: inventory index of this element
		// See `ui.component.ItemInventoryComponent.ItemInventoryComponent`
		if (id.includes("-")) {
			const tmp = id.split("-");
			const idx = tmp[tmp.length - 1];
			if (!isNaN(parseInt(idx, 10))) {
				return idx;
			}
		}
	}

	private onDrop(event: DragEvent|TouchEvent) {
		const myobject = this.object || marauroa.me;
		if (stendhal.ui.heldObject) {
			const pos = stendhal.ui.html.extractPosition(event);
			const id = (pos.target as HTMLElement).id;
			const targetSlot = stendhal.ui.html.parseSlotName(id);
			if (event.type === "touchend" && id === "viewport") {
				stendhal.ui.gamewindow.onDrop(event);
				event.stopPropagation();
				event.preventDefault();
				return;
			}

			let objectId = myobject["id"];
			if (event.type === "touchend") {
				// find the actual target ID for touch events
				if (targetSlot === "content") {
					const container = stendhal.ui.equip.getByElement(stendhal.ui.html.extractTarget(event).parentElement!);
					if (container && container.object) {
						objectId = container.object.id;
					}
				} else {
					// moving from content container (corpse, chest) to player container (bag, keyring, etc.)
					objectId = marauroa.me["id"];
				}
			}

			const action = {
				"source_path": stendhal.ui.heldObject.path
			} as any;
			const sameSlot = stendhal.ui.heldObject.slot === targetSlot;
			if (sameSlot) {
				action["type"] = "reorder";
				action["new_position"] = this.parseIndex(id) || "" + (this.size - 1);
			} else {
				action["type"] = "equip";
				action["target_path"] = "[" + objectId + "\t" + targetSlot + "]";
				action["zone"] = stendhal.ui.heldObject.zone;
			}

			const quantity = stendhal.ui.heldObject.quantity;
			stendhal.ui.heldObject = undefined;

			// if ctrl is pressed or holding stackable item from touch event, we ask for the quantity
			const touch_held = stendhal.ui.touch.holding() && quantity > 1;
			const split_action = !sameSlot && ((event instanceof DragEvent && event.ctrlKey) || touch_held);
			if (split_action) {
				const pos = stendhal.ui.html.extractPosition(event);
				ui.createSingletonFloatingWindow("Quantity",
					new DropQuantitySelectorDialog(action, touch_held),
					pos.pageX - 50, pos.pageY - 25);
			} else {
				singletons.getHeldObjectManager().onRelease();
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
		if (this.timestampMouseDown - this.timestampMouseDownPrev <= this.rightClickDuration) {
			// reset so subsequent single clicks/taps aren't counted
			this.timestampMouseDown = 0;
			return (stendhal.ui.html.extractTarget(evt) as HTMLElement).id === this.lastClickedId;
		}
		this.lastClickedId = (stendhal.ui.html.extractTarget(evt) as HTMLElement).id;
		return false;
	}

	onMouseDown(evt: MouseEvent|TouchEvent) {
		this.timestampMouseDownPrev = this.timestampMouseDown;
		this.timestampMouseDown = +new Date();
	}

	onMouseUp(evt: MouseEvent|TouchEvent) {
		if (evt instanceof MouseEvent) {
			evt.preventDefault();
		}

		// workaround to prevent accidentally using items when disengaging joystick
		// FIXME: a global solution would be better
		if (singletons.getJoystickController().isEngaged()) {
			return;
		}

		let event = stendhal.ui.html.extractPosition(evt);
		if ((event.target as any).dataItem) {
			const long_touch = stendhal.ui.touch.isLongTouch(evt);
			const context_action = (evt instanceof MouseEvent && this.isRightClick(evt)) || long_touch;
			if (this.quickPickup && !context_action) {
				marauroa.clientFramework.sendAction({
					type: "equip",
					"source_path": (event.target as any).dataItem.getIdPath(),
					"target_path": "[" + marauroa.me["id"] + "\tbag]",
					"clicked": "", // useful for changing default target in equip action
					"zone": marauroa.currentZoneName
				});
				return;
			}

			if (this.isRightClick(event) || long_touch) {
				const append = [];
				if (long_touch) {
					// XXX: better way to pass instance to action function?
					const tmp = this;
					// action to "hold" item for moving or dropping using touch
					// XXX: temporary workaround, should use drag-and-drop instead
					append.push({
						title: "Hold",
						action: function(entity: any) {
							tmp.onDragStart(evt as TouchEvent);
						}
					});
				}
				stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
					new ActionContextMenu((event.target as any).dataItem, append),
					event.pageX - 50, event.pageY - 5));
			} else if (!stendhal.ui.heldObject) {
				if (!stendhal.config.getBoolean("inventory.double-click") || this.isDoubleClick(event)) {
					marauroa.clientFramework.sendAction({
						type: "use",
						"target_path": (event.target as any).dataItem.getIdPath(),
						"zone": marauroa.currentZoneName
					});
				}
			}
		}

		// clean up item held via touch
		stendhal.ui.touch.setHolding(false);

		document.getElementById("viewport")!.focus();
	}

	private onMouseEnter(evt: MouseEvent) {
		// nothing
	}

	private onMouseLeave(evt: MouseEvent) {
		// nothing
	}

	private onTouchStart(evt: TouchEvent) {
		const pos = stendhal.ui.html.extractPosition(evt);
		stendhal.ui.touch.onTouchStart(pos.pageX, pos.pageY);
	}

	private onTouchEnd(evt: TouchEvent) {
		stendhal.ui.touch.onTouchEnd();
		if (stendhal.ui.touch.isLongTouch(evt) && !stendhal.ui.touch.holding()) {
			this.onMouseUp(evt);
		} else if (stendhal.ui.touch.holding()) {
			evt.preventDefault();

			this.onDrop(evt);
			stendhal.ui.touch.setHolding(false);
		}
		// clean up touch handler
		stendhal.ui.touch.unsetOrigin();
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
			if (this.slot === "content" && stendhal.config.getBoolean("inventory.quick-pickup")) {
				target.style.cursor = "url(" + Paths.sprites
						+ "/cursor/itempickupfromslot.png) 1 3, auto";
				return;
			}
			target.style.cursor = item.getCursor(0, 0);
			return;
		}
		target.style.cursor = "url(" + Paths.sprites
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
