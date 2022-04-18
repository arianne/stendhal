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
			/*
			e.addEventListener("touchstart", (event: TouchEvent) => {
				this.onTouchStart(event)
			});
			e.addEventListener("touchend", (event: TouchEvent) => {
				this.onTouchEnd(event)
			});
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
				let e = document.getElementById(this.slot + this.suffix + cnt);
				if (!e) {
					continue;
				}

				const item = <Item> o;
				let xOffset = 0;
				let yOffset = 0;
				if (item["name"] === "emerald ring" && item["amount"] == 0) {
					yOffset = -32;
				} else if (item.isAnimated()) {
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

	private onDragStart(event: DragEvent|TouchEvent) {
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
			if (event instanceof DragEvent && event.dataTransfer) {
				const img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(item.sprite.filename), 32, 32);
				event.dataTransfer.setDragImage(img, 0, 0);
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
		return false;
	}

	private onDrop(event: DragEvent|TouchEvent) {
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

	private onTouchStart(evt: TouchEvent) {
		evt.preventDefault();
		stendhal.ui.touch.timestampTouchStart = +new Date();
	}

	private onTouchEnd(evt: TouchEvent) {
		evt.preventDefault();
		stendhal.ui.touch.timestampTouchEnd = +new Date();
		if (stendhal.ui.heldItem) {
			this.onDrop(evt);
		} else {
			this.onMouseUp(evt);
		}
	}

	private onTouchMove(evt: TouchEvent) {
		if (stendhal.ui.heldItem) {
			this.onDragOver(evt);
		} else {
			this.onDragStart(evt);
		}
	}
}
