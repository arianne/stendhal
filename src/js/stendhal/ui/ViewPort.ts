/***************************************************************************
 *                   (C) Copyright 2003-2024 - Stendhal                    *
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

import { HeldObject } from "./HeldObject";
import { ui } from "./UI";
import { UIComponentEnum } from "./UIComponentEnum";

import { PlayerEquipmentComponent } from "./component/PlayerEquipmentComponent";

import { ActionContextMenu } from "./dialog/ActionContextMenu";
import { DropQuantitySelectorDialog } from "./dialog/DropQuantitySelectorDialog";

import { JoystickImpl } from "./joystick/JoystickImpl";

import { QuickMenuButton } from "./quickmenu/QuickMenuButton";

import { Client } from "../Client";
import { singletons } from "../SingletonRepo";

import { AchievementBanner } from "../sprite/AchievementBanner";
import { EmojiSprite } from "../sprite/EmojiSprite";
import { NotificationBubble } from "../sprite/NotificationBubble";
import { SpeechBubble } from "../sprite/SpeechBubble";
import { TextBubble } from "../sprite/TextBubble";

import { Chat } from "../util/Chat";
import { Point } from "../util/Point";


/**
 * game window aka world view
 */
export class ViewPort {

	/** Horizontal screen offset in pixels. */
	private offsetX = 0;
	/** Vertical screen offset in pixels. */
	private offsetY = 0;
	/** Prevents adjusting offset based on player position. */
	private freeze = false;
	/** Time of most recent redraw. */
	private timeStamp = Date.now();

	// dimensions
	// TODO: remove & use CSS style instead
	private readonly width: number;
	private readonly height: number;

	/** Drawing context. */
	private ctx: CanvasRenderingContext2D;
	/** Map tile pixel width. */
	private readonly targetTileWidth = 32;
	/** Map tile pixel height. */
	private readonly targetTileHeight = 32;
	private drawingError = false;

	/** Active speech bubbles to draw. */
	private textSprites: SpeechBubble[] = [];
	/** Active notification bubbles/achievement banners to draw. */
	private notifSprites: TextBubble[] = [];
	/** Active emoji sprites to draw. */
	private emojiSprites: EmojiSprite[] = [];
	/** Handles drawing weather in viewport. */
	private weatherRenderer = singletons.getWeatherRenderer();
	/** Coloring method of current zone. */
	private filter?: string;

	/** On-screen joystick. */
	private joystick: JoystickImpl|null = null;

	/** Styles to be applied when chat panel is not floating. */
	private readonly initialStyle: {[prop: string]: string};

	/** Singleton instance. */
	private static instance: ViewPort;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): ViewPort {
		if (!ViewPort.instance) {
			ViewPort.instance = new ViewPort();
		}
		return ViewPort.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		const element = this.getElement() as HTMLCanvasElement;
		this.ctx = element.getContext("2d")!;
		//~ this.compositeOperation = this.ctx.globalCompositeOperation;
		this.width = element.width;
		this.height = element.height;

		this.initialStyle = {};
		//~ const stylesheet = getComputedStyle(element);
		// FIXME: how to get literal "calc()" instead of value of calc()?
		//~ this.initialStyle["max-width"] = stylesheet.getPropertyValue("max-width");
		//~ this.initialStyle["max-height"] = stylesheet.getPropertyValue("max-height");
		// NOTE: this doesn't work if properties set in css
		this.initialStyle["max-width"] = "calc((100dvh - 5em) * 640 / 480)";
		this.initialStyle["max-height"] = "calc(100dvh - 5em)";
	}

	/**
	 * Retrieves the viewport element.
	 *
	 * @return {HTMLElement}
	 *   Viewport `HTMLElement`.
	 */
	public getElement(): HTMLElement {
		return document.getElementById("viewport")!;
	}

	/**
	 * Draws terrain tiles & entity sprites in the viewport.
	 */
	draw() {
		var startTime = new Date().getTime();

		if (marauroa.me && document.visibilityState === "visible") {
			if (marauroa.currentZoneName === stendhal.data.map.currentZoneName
				|| stendhal.data.map.currentZoneName === "int_vault"
				|| stendhal.data.map.currentZoneName === "int_adventure_island"
				|| stendhal.data.map.currentZoneName === "tutorial_island") {
				this.drawingError = false;

				this.ctx.globalAlpha = 1.0;
				this.adjustView(this.ctx.canvas);
				this.ctx.fillStyle = "black";
				this.ctx.fillRect(0, 0, 10000, 10000);

				var tileOffsetX = Math.floor(this.offsetX / this.targetTileWidth);
				var tileOffsetY = Math.floor(this.offsetY / this.targetTileHeight);

				// FIXME: filter should not be applied to "blend" layers
				this.applyFilter();
				stendhal.data.map.strategy.render(this.ctx.canvas, this, tileOffsetX, tileOffsetY, this.targetTileWidth, this.targetTileHeight);

				this.weatherRenderer.draw(this.ctx);
				this.removeFilter();
				this.drawEntitiesTop();
				this.drawEmojiSprites();
				this.drawTextSprites();
				this.drawTextSprites(this.notifSprites);

				// redraw inventory sprites
				stendhal.ui.equip.update();
				(ui.get(UIComponentEnum.PlayerEquipment) as PlayerEquipmentComponent).update();
			}
		}
		window.setTimeout(function() {
			stendhal.ui.gamewindow.draw.apply(stendhal.ui.gamewindow, arguments);
		}, Math.max((1000/20) - (new Date().getTime()-startTime), 1));
	}

	/**
	 * Adds map's coloring filter to viewport.
	 *
	 * FIXME:
	 * - colors are wrong
	 * - doesn't support "blend" layers
	 */
	applyFilter() {
		if (this.filter && stendhal.config.getBoolean("effect.lighting")) {
			// FIXME: wrong colors
			this.ctx.filter = this.filter;
		}
	}

	/**
	 * Removes map's coloring filter from viewport.
	 */
	removeFilter() {
		this.ctx.filter = "none";
	}

	/**
	 * Draws overall entity sprites.
	 */
	drawEntities() {
		var currentTime = new Date().getTime();
		var time = currentTime - this.timeStamp;
		this.timeStamp = currentTime;
		for (var i in stendhal.zone.entities) {
			var entity = stendhal.zone.entities[i];
			if (typeof(entity.draw) != "undefined") {
				entity.updatePosition(time);
				entity.draw(this.ctx);
			}
		}
	}

	/**
	 * Draws titles & HP bars associated with entities.
	 */
	drawEntitiesTop() {
		var i;
		for (i in stendhal.zone.entities) {
			const entity = stendhal.zone.entities[i];
			if (typeof(entity.setStatusBarOffset) !== "undefined") {
				entity.setStatusBarOffset();
			}
			if (typeof(entity.drawTop) != "undefined") {
				entity.drawTop(this.ctx);
			}
		}
	}

	/**
	 * Draws active notifications or speech bubbles associated with characters, NPCs, & creatures.
	 *
	 * @param sgroup {sprite.TextBubble[]}
	 *   Sprite group to drawn, either speech bubbles or notifications/achievements (default: speech bubbles).
	 */
	drawTextSprites(sgroup: TextBubble[]=this.textSprites) {
		for (var i = 0; i < sgroup.length; i++) {
			var sprite = sgroup[i];
			var remove = sprite.draw(this.ctx);
			if (remove) {
				sgroup.splice(i, 1);
				sprite.onRemoved();
				i--;
			}
		}
	}

	/**
	 * Adds an emoji sprite to viewport.
	 *
	 * @param sprite {sprite.EmojiSprite}
	 *   Sprite definition.
	 */
	addEmojiSprite(sprite: EmojiSprite) {
		this.emojiSprites.push(sprite);
	}

	/**
	 * Draws active emoji sprites.
	 */
	drawEmojiSprites() {
		for (let i = 0; i < this.emojiSprites.length; i++) {
			const sprite = this.emojiSprites[i];
			const remove = sprite.draw(this.ctx);
			if (remove) {
				this.emojiSprites.splice(i, 1);
				i--;
			}
		}
	}

	/**
	 * Updates viewport drawing position of map based on player position.
	 *
	 * @param canvas {HTMLCanvasElement}
	 *   Viewport canvas element.
	 */
	adjustView(canvas: HTMLCanvasElement) {
		// IE does not support ctx.resetTransform(), so use the following workaround:
		this.ctx.setTransform(1, 0, 0, 1, 0, 0);

		// Coordinates for a screen centered on player
		let centerX: number, centerY: number;
		if (this.freeze) {
			centerX = this.offsetX + this.targetTileWidth / 2;
			centerY = this.offsetY + this.targetTileHeight / 2;
		} else {
			centerX = marauroa.me["_x"] * this.targetTileWidth + this.targetTileWidth / 2 - canvas.width / 2;
			centerY = marauroa.me["_y"] * this.targetTileHeight + this.targetTileHeight / 2 - canvas.height / 2;
		}

		// Keep the world within the screen view
		centerX = Math.min(centerX, stendhal.data.map.zoneSizeX * this.targetTileWidth - canvas.width);
		centerX = Math.max(centerX, 0);

		centerY = Math.min(centerY, stendhal.data.map.zoneSizeY * this.targetTileHeight - canvas.height);
		centerY = Math.max(centerY, 0);

		if (this.freeze) {
			this.ctx.translate(-Math.round(centerX), -Math.round(centerY));
			return;
		}
		this.offsetX = Math.round(centerX);
		this.offsetY = Math.round(centerY);
		this.ctx.translate(-this.offsetX, -this.offsetY);
	}

	/**
	 * Adds a speech bubble to viewport.
	 *
	 * @param sprite {sprite.SpeechBubble}
	 *   Sprite definition.
	 */
	addTextSprite(sprite: SpeechBubble) {
		this.textSprites.push(sprite);
		sprite.onAdded(this.ctx);
	}

	/**
	 * Adds a notification bubble to viewport.
	 *
	 * @param mtype {string}
	 *   Message type.
	 * @param text {string}
	 *   Text contents.
	 * @param profile {string}
	 *   Optional entity image filename to show as the speaker.
	 */
	addNotifSprite(mtype: string, text: string, profile?: string) {
		const bubble = new NotificationBubble(mtype, text, profile);
		this.notifSprites.push(bubble);
		bubble.onAdded(this.ctx);
	}

	/**
	 * Adds an achievement banner to viewport.
	 *
	 * @param cat {string}
	 *   Achievement categroy.
	 * @param title {string}
	 *   Achievement title.
	 * @param desc {string}
	 *   Achievement description.
	 */
	addAchievementNotif(cat: string, title: string, desc: string) {
		const banner = new AchievementBanner(cat, title, desc);
		this.notifSprites.push(banner);
		banner.onAdded(this.ctx);
	}

	/**
	 * Removes a speech bubble. Looks for topmost sprite at "x","y". Otherwise removes "sprite".
	 *
	 * @param sprite {sprite.TextBubble}
	 *   Sprite that is to be removed.
	 * @param x {number}
	 *   X coordinate to check for overlapping sprite.
	 * @param y {number}
	 *   Y coordinate to check for overlapping sprite.
	 */
	removeTextBubble(sprite: TextBubble, x: number, y: number) {
		for (let idx = this.notifSprites.length-1; idx >= 0; idx--) {
			const topSprite = this.notifSprites[idx];
			if (topSprite == sprite || topSprite.clipsPoint(x, y)) {
				this.notifSprites.splice(idx, 1);
				topSprite.onRemoved();
				return;
			}
		}

		for (let idx = this.textSprites.length-1; idx >= 0; idx--) {
			const topSprite = this.textSprites[idx];
			if (topSprite == sprite || topSprite.clipsPoint(x, y)) {
				this.textSprites.splice(idx, 1);
				topSprite.onRemoved();
				return;
			}
		}
	}

	/**
	 * Checks for an active speech bubble.
	 *
	 * @param x {number}
	 *   X coordinate to check.
	 * @param y {number}
	 *   Y coordinate to check.
	 * @return {boolean}
	 *   `true` if there is a text bubble at position.
	 */
	textBubbleAt(x: number, y: number): boolean {
		for (const sprite of this.notifSprites) {
			if (sprite.clipsPoint(x, y)) {
				return true;
			}
		}
		for (const sprite of this.textSprites) {
			if (sprite.clipsPoint(x, y)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Called when `entity.User` instance exits a zone.
	 */
	onExitZone() {
		// clear speech bubbles & emojis so they don't appear on the new map
		for (const sgroup of [this.textSprites, this.emojiSprites]) {
			for (let idx = sgroup.length-1; idx >= 0; idx--) {
				const sprite = sgroup[idx];
				sgroup.splice(idx, 1);
				if (sprite instanceof SpeechBubble) {
					sprite.onRemoved();
				}
			}
		}
	}

	/**
	 *  Mouse click handling.
	 */
	onMouseDown = (function() {
		var entity: any;
		var startX: number;
		var startY: number;

		const mHandle: any = {};

		mHandle._onMouseDown = function(e: MouseEvent|TouchEvent) {
			var pos = stendhal.ui.html.extractPosition(e);
			if (stendhal.ui.touch.isTouchEvent(e)) {
				if (stendhal.ui.touch.holding()) {
					// prevent default viewport action when item is "held"
					return;
				}
				stendhal.ui.touch.onTouchStart(pos.pageX, pos.pageY);
			}
			if (stendhal.ui.globalpopup) {
				stendhal.ui.globalpopup.close();
			}

			startX = pos.canvasRelativeX;
			startY = pos.canvasRelativeY;

			var x = pos.canvasRelativeX + stendhal.ui.gamewindow.offsetX;
			var y = pos.canvasRelativeY + stendhal.ui.gamewindow.offsetY;

			// override ground/entity action if there is a text bubble
			if (stendhal.ui.gamewindow.textBubbleAt(x, y+15)) {
				return;
			}

			entity = stendhal.zone.entityAt(x, y);
			stendhal.ui.timestampMouseDown = +new Date();

			if (e.type !== "dblclick" && e.target) {
				e.target.addEventListener("mousemove", mHandle.onDrag);
				e.target.addEventListener("mouseup", mHandle.onMouseUp);
				e.target.addEventListener("touchmove", mHandle.onDrag);
				e.target.addEventListener("touchend", mHandle.onMouseUp);
			} else if (entity == stendhal.zone.ground) {
				entity.onclick(pos.canvasRelativeX, pos.canvasRelativeY, true);
			}
		}

		mHandle.isRightClick = function(e: MouseEvent) {
			if (+new Date() - stendhal.ui.timestampMouseDown > 300) {
				return true;
			}
			if (e.which) {
				return (e.which === 3);
			} else {
				return (e.button === 2);
			}
		}

		mHandle.onMouseUp = function(e: MouseEvent|TouchEvent) {
			const is_touch = stendhal.ui.touch.isTouchEvent(e);
			if (is_touch) {
				stendhal.ui.touch.onTouchEnd(e);
			}
			var pos = stendhal.ui.html.extractPosition(e);
			const long_touch = is_touch && stendhal.ui.touch.isLongTouch(e);
			if ((e instanceof MouseEvent && mHandle.isRightClick(e)) || long_touch) {
				if (entity != stendhal.zone.ground) {
					const append: any[] = [];
					/*
					if (long_touch) {
						// TODO: add option for "hold" to allow splitting item stacks
					}
					*/
					stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
						new ActionContextMenu(entity, append), pos.pageX - 50, pos.pageY - 5));
				}
			} else {
				entity.onclick(pos.canvasRelativeX, pos.canvasRelativeY);
			}
			mHandle.cleanUp(pos);
			pos.target.focus();
			e.preventDefault();
		}

		mHandle.onDrag = function(e: MouseEvent) {
			if (stendhal.ui.touch.isTouchEvent(e)) {
				stendhal.ui.gamewindow.onDragStart(e);
			}

			var pos = stendhal.ui.html.extractPosition(e);
			var xDiff = startX - pos.offsetX;
			var yDiff = startY - pos.offsetY;
			// It's not really a click if the mouse has moved too much.
			if (xDiff * xDiff + yDiff * yDiff > 5) {
				mHandle.cleanUp(e);
			}
		}

		mHandle.cleanUp = function(e: Event) {
			entity = null;
			if (!e.target) {
				return;
			}
			e.target.removeEventListener("mouseup", mHandle.onMouseUp);
			e.target.removeEventListener("mousemove", mHandle.onDrag);
			e.target.removeEventListener("touchend", mHandle.onMouseUp);
			e.target.removeEventListener("touchmove", mHandle.onDrag);
		}

		return mHandle._onMouseDown;
	})()

	/**
	 * Updates cursor style when positioned over an element or entity.
	 */
	onMouseMove(e: MouseEvent) {
		var pos = stendhal.ui.html.extractPosition(e);
		var x = pos.canvasRelativeX + stendhal.ui.gamewindow.offsetX;
		var y = pos.canvasRelativeY + stendhal.ui.gamewindow.offsetY;
		var entity = stendhal.zone.entityAt(x, y);
		stendhal.ui.gamewindow.getElement().style.cursor = entity.getCursor(x, y);
	}

	/**
	 * Changes character facing direction dependent on direction of wheel scroll.
	 */
	onMouseWheel(e: WheelEvent) {
		if (marauroa.me) {
			e.preventDefault();

			// previous event may have changed type to string
			const currentDir = parseInt(marauroa.me["dir"], 10);
			let newDir = null;

			if (typeof(currentDir) === "number") {
				if (e.deltaY >= 100) {
					// clockwise
					newDir = currentDir + 1;
					if (newDir > 4) {
						newDir = 1;
					}
				} else if (e.deltaY <= -100) {
					// counter-clockwise
					newDir = currentDir - 1;
					if (newDir < 1) {
						newDir = 4;
					}
				}
			}

			if (newDir != null) {
				marauroa.clientFramework.sendAction({"type": "face", "dir": ""+newDir});
			}
		}
	}

	/**
	 * Handles engaging an item or corpse to be dragged.
	 */
	onDragStart(e: DragEvent) {
		var pos = stendhal.ui.html.extractPosition(e);
		let draggedEntity;
		for (const obj of stendhal.zone.getEntitiesAt(pos.canvasRelativeX + stendhal.ui.gamewindow.offsetX,
				pos.canvasRelativeY + stendhal.ui.gamewindow.offsetY)) {
			if (obj.isDraggable()) {
				draggedEntity = obj;
			}
		}

		var img = undefined;
		let heldObject: HeldObject;
		if (draggedEntity && draggedEntity.type === "item") {
			img = stendhal.data.sprites.getAreaOf(stendhal.data.sprites.get(draggedEntity.sprite.filename), 32, 32);
			heldObject = {
				path: draggedEntity.getIdPath(),
				zone: marauroa.currentZoneName,
				quantity: draggedEntity.hasOwnProperty("quantity") ? draggedEntity["quantity"] : 1
			}
		} else if (draggedEntity && draggedEntity.type === "corpse") {
			img = stendhal.data.sprites.get(draggedEntity.sprite.filename);
			heldObject = {
				path: draggedEntity.getIdPath(),
				zone: marauroa.currentZoneName,
				quantity: 1
			}
		} else {
			e.preventDefault();
			return;
		}

		if (stendhal.ui.touch.isTouchEvent(e)) {
			singletons.getHeldObjectManager().set(heldObject, img, new Point(pos.pageX, pos.pageY));
			stendhal.ui.touch.setHolding(true);
		} else {
			stendhal.ui.heldObject = heldObject;
		}

		if (e.dataTransfer) {
			window.event = e; // required by setDragImage polyfil
			e.dataTransfer.setDragImage(img, 0, 0);
		}
	}

	/**
	 * Displays a corpse or item sprite while dragging.
	 */
	onDragOver(e: DragEvent): boolean {
		e.preventDefault(); // Necessary. Allows us to drop.
		if (e.dataTransfer) {
			e.dataTransfer.dropEffect = "move";
		}
		return false;
	}

	/**
	 * Handles releasing an item or corpse from drag event.
	 */
	onDrop(e: DragEvent) {
		if (stendhal.ui.heldObject) {
			var pos = stendhal.ui.html.extractPosition(e);
			const targetSlot = stendhal.ui.html.parseSlotName((pos.target as HTMLElement).id);
			const action: any = {
				"zone": stendhal.ui.heldObject.zone
			};
			if (targetSlot === "viewport") {
				action.x = Math.floor((pos.canvasRelativeX + stendhal.ui.gamewindow.offsetX) / 32).toString();
				action.y = Math.floor((pos.canvasRelativeY + stendhal.ui.gamewindow.offsetY) / 32).toString();

				var id = stendhal.ui.heldObject.path.substr(1, stendhal.ui.heldObject.path.length - 2);
				var drop = /\t/.test(id);
				if (drop) {
					action["type"] = "drop";
					action["source_path"] = stendhal.ui.heldObject.path;
				} else {
					action["type"] = "displace";
					action["baseitem"] = id;
				}
			} else {
				let objectId = marauroa.me["id"];
				if (e.type === "touchend" && targetSlot === "content") {
					// find the actual target ID for touch events
					const container = stendhal.ui.equip.getByElement(stendhal.ui.html.extractTarget(event).parentElement!);
					if (container && container.object) {
						objectId = container.object.id;
					}
				}
				action["type"] = "equip";
				action["source_path"] = stendhal.ui.heldObject.path;
				action["target_path"] = "[" + objectId + "\t" + targetSlot + "]";
			}

			const quantity = stendhal.ui.heldObject.quantity;
			const sourceSlot = stendhal.ui.heldObject.slot || "viewport";
			// item was dropped
			stendhal.ui.heldObject = undefined;

			const touch_held = stendhal.ui.touch.holding() && quantity > 1;
			// if ctrl is pressed or holding stackable item from touch event, we ask for the quantity
			// NOTE: don't create selector if touch source is ground
			if (e.ctrlKey || (touch_held && sourceSlot !== targetSlot)) {
				ui.createSingletonFloatingWindow("Quantity", new DropQuantitySelectorDialog(action, touch_held), pos.pageX - 50, pos.pageY - 25);
			} else {
				singletons.getHeldObjectManager().onRelease();
				marauroa.clientFramework.sendAction(action);
			}
		}

		e.stopPropagation();
		e.preventDefault();
	}

	/**
	 * This is a workaround until it's figured out how to make it work using the same methods as mouse event.
	 */
	onTouchEnd(e: TouchEvent) {
		stendhal.ui.touch.onTouchEnd();
		stendhal.ui.gamewindow.onDrop(e);
		if (stendhal.ui.touch.holding()) {
			stendhal.ui.touch.setHolding(false);
			stendhal.ui.touch.unsetOrigin();
		}
		// execute here because "touchend" event propagation is cancelled on the veiwport
		Client.handleClickIndicator(e);
	}

	onContentMenu(e: MouseEvent) {
		e.preventDefault();
	}

	/**
	 * Creates a screenshot from viewport area & executes an anchor click event to be handled by browser for PNG download.
	 */
	createScreenshot() {
		Chat.log("client", "creating screenshot ...");
		const uri = this.ctx.canvas.toDataURL("image/png");

		const d = new Date();
		const ts = {
			yyyy: "" + d.getFullYear(),
			mm: ("00" + (d.getMonth() + 1)).slice(-2),
			dd: ("00" + d.getDate()).slice(-2),
			HH: ("00" + d.getHours()).slice(-2),
			MM: ("00" + d.getMinutes()).slice(-2),
			SS: ("00" + d.getSeconds()).slice(-2),
			ms: "" + d.getMilliseconds()
		};

		while (ts.ms.length < 3) {
			ts.ms = "0" + ts.ms;
		}

		// date/time stamp is formatted as yyyy-mm-dd_HH.MM.SS
		const filename = "stendhal_" + ts.yyyy + "-" + ts.mm + "-"
				+ ts.dd + "_" + ts.HH + "." + ts.MM + "."
				+ ts.SS + ".png";

		const anchor = document.createElement("a");
		anchor.download = filename;
		anchor.target = "_blank";
		anchor.href = uri;
		anchor.click();
	}

	/**
	 * Removes joystick elements from DOM and unsets `ui.ViewPort.ViewPort.joystick` property.
	 */
	private removeJoystick() {
		if (this.joystick) {
			this.joystick.removeFromDOM();
		}
		this.joystick = null;
	}

	/**
	 * Updates the on-screen joystick.
	 */
	updateJoystick() {
		// update quick menu button image
		const joystickButton = ui.get(UIComponentEnum.QMJoystick) as QuickMenuButton;
		if (joystickButton) {
			joystickButton.update();
		}

		if (!stendhal.config.getBoolean("joystick")) {
			this.removeJoystick();
			return;
		}
		const newJoystick = stendhal.config.get("joystick.style") === "dpad"
				? singletons.getDirectionPad() : singletons.getJoystick();
		if (this.joystick && newJoystick === this.joystick) {
			this.joystick.update();
		} else {
			this.removeJoystick();
			this.joystick = newJoystick;
			newJoystick.addToDOM();
		}
	}

	/**
	 * Updates viewport layout to compensate for chat panel style.
	 */
	public onChatPanelRefresh(floating: boolean) {
		const element = this.getElement();
		for (const prop of Object.keys(this.initialStyle)) {
			if (floating) {
				element.style.removeProperty(prop);
			} else {
				element.style.setProperty(prop, this.initialStyle[prop]);
			}
		}
	}
}
