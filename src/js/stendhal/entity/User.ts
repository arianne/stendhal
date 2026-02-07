/***************************************************************************
 *                    Copyright © 2023-2024 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";

import { Entity } from "./Entity";
import { Player } from "./Player";

import { singletons } from "../SingletonRepo";

import { MenuItem } from "../action/MenuItem";

import { Color } from "../data/color/Color";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";
import { PlayerStatsComponent } from "../ui/component/PlayerStatsComponent";
import { StatusesListComponent } from "../ui/component/StatusesListComponent";

import { OutfitDialog } from "../ui/dialog/outfit/OutfitDialog";

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { Direction } from "../util/Direction";


/**
 * Class representing the player controlled by this client.
 */
export class User extends Player {

	override minimapStyle = Color.USER;


	constructor() {
		super();
		ui.onUserReady();
	}

	override set(key: string, value: any) {
		const oldX = this["x"];
		const oldY = this["y"];
		super.set(key, value);

		if ((key === "x" || key === "y") && this["x"] && this["y"]
				&& (this["x"] !== oldX || this["y"] !== oldY)) {
			singletons.getLoopedSoundSourceManager().onDistanceChanged(this["x"], this["y"]);
		}

		queueMicrotask( () => {
			(ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent).update(key);
			(ui.get(UIComponentEnum.Bag) as ItemInventoryComponent).update();
			(ui.get(UIComponentEnum.Keyring) as ItemInventoryComponent).update();
			(ui.get(UIComponentEnum.StatusesList) as StatusesListComponent).update(this);
		});
	}

	override unset(key: string) {
		super.unset(key);

		queueMicrotask( () => {
			(ui.get(UIComponentEnum.StatusesList) as StatusesListComponent).update(this);
		});
	}

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);
		const charname = this["_name"];
		list.push({
			title: this.stopped() ? "Walk" : "Stop",
			action: function(_entity: any) {
				marauroa.clientFramework.sendAction({"type": "walk"});
			}
		});
		list.push({
			title: "Set outfit",
			action: function(_entity: any) {
				let outfitDialog = ui.get(UIComponentEnum.OutfitDialog);
				if (!outfitDialog) {
					const dstate = stendhal.config.getWindowState("outfit");
					outfitDialog = new OutfitDialog();
					new FloatingWindow("Choose outfit", outfitDialog, dstate.x, dstate.y).setId("outfit");
				}
			}
		});
		list.push({
			title: "Where",
			action: function(_entity: any) {
				var action = {
					"type": "where",
					"target": charname,
				};
				marauroa.clientFramework.sendAction(action);
			}
		});
	}

	//~ override onMiniMapDraw() {
	//~ }

	/**
	 * Can the player hear this chat message?
	 *
	 * @param entity
	 *     The speaking entity.
	 * @param rangeSquared
	 *     Distance squared within which the entity can be heard (-1
	 *     represents entire map).
	 */
	isInHearingRange(entity: Entity, rangeSquared?: number) {
		if (entity === marauroa.me || this.isAdmin()) {
			return true;
		}

		let hearingRange = 15; // default
		if (typeof rangeSquared !== "undefined") {
			if (rangeSquared < 0) {
				hearingRange = -1;
			} else {
				hearingRange = Math.sqrt(rangeSquared);
			}
		}
		return (hearingRange < 0)
			|| ((Math.abs(this["x"] - entity["x"]) < hearingRange)
				&& (Math.abs(this["y"] - entity["y"]) < hearingRange));
	}

	/**
	 * Checks if player is within range to hear a sound event.
	 *
	 * @param {number} radius
	 *   Distance at which sound can be heard.
	 * @param {Entity} entity
	 *   Entity emitting sound event.
	 * @returns {boolean}
	 *   `true` if sound should be loaded (when event radius is more than -1 & user position is within
	 *   radial distance from event origin).
	 */
	isInSoundRange(radius: number, entity: Entity): boolean {
		if (entity === this) {
			return true;
		}
		if (radius < 0) {
			return false;
		}
		return Math.abs(this["x"] - entity["x"]) + Math.abs(this["y"] - entity["y"]) <= radius;
	}

	/**
	 * Actions when player leaves a zone.
	 */
	override onExitZone() {
		super.onExitZone();
		// speech bubbles & emojis from viewport
		stendhal.ui.gamewindow.onExitZone();
		// stop sounds & clear map sounds cache on zone change
		const msgs: string[] = [];
		const lssm = singletons.getLoopedSoundSourceManager();
		if (!lssm.removeAll()) {
			let tmp = "LoopedSoundSourceManager reported not all sources stopped on zone change:";
			const loopSources = lssm.getSources();
			for (const id in loopSources) {
				const snd = loopSources[id].sound;
				tmp += "\n- ID: " + id + " (" + snd.src + ")";
			}
			msgs.push(tmp);
		}
		if (!stendhal.sound.stopAll()) {
			let tmp = "SoundManager reported not all sounds stopped on zone change:";
			for (const snd of stendhal.sound.getActive()) {
				tmp += "\n- " + snd.src;
				if (snd.loop) {
					tmp += " (loop)";
				}
			}
			msgs.push(tmp);
		}

		for (const msg of msgs) {
			console.warn(msg);
		}
	}

	/**
	 * Actions when player enters a zone.
	 */
	override onEnterZone() {
		super.onEnterZone();
		// play looped sound sources
		singletons.getLoopedSoundSourceManager().onZoneReady();
	}

	/**
	 * Checks if player has autowalk set.
	 */
	public autoWalkEnabled(): boolean {
		return typeof(this["autowalk"]) !== "undefined";
	}

	/**
	 * Checks if player meets requirements to invite others into a group.
	 *
	 * @return
	 *     <code>true</code> if player is not in a group or is leader of a group.
	 */
	public canInviteToGroup(): boolean {
		const gman = singletons.getGroupManager();
		return gman.getMemberCount() == 0 || gman.getLeader() === this["name"];
	}

	/**
	 * Sends event to server to set direction user should face.
	 *
	 * @param dir {util.Direction.Direction}
	 *   New direction to face.
	 */
	public faceTo(dir: Direction) {
		marauroa.clientFramework.sendAction({type: "face", dir: ""+dir.val});
	}

	/**
	 * Sends event to server to set or stop player's walking direction.
	 *
	 * @param dir {util.Direction.Direction}
	 *   Direction for character to move or stop.
	 * @param cancelAutoWalk {boolean}
	 *   If `true` stops movement if auto-walk is active & "dir" matches user's current direction of
	 *   movement.
	 */
	public setDirection(dir: Direction, cancelAutoWalk=false) {
		if (dir == Direction.STOP) {
			this.stop();
			return;
		}
		// in case viewport frozen from view change event
		stendhal.ui.viewport.freeze = false;
		if (cancelAutoWalk && this.autoWalkEnabled() && this.getWalkDirection() == dir) {
			// cancel auto-walk if enabled & new direction is same as current direction of movement
			marauroa.clientFramework.sendAction({type: "walk"});
			return;
		}
		marauroa.clientFramework.sendAction({type: "move", dir: ""+dir.val});
	}

	/**
	 * Sends action to server to stop player's walking direction.
	 */
	public stop() {
		marauroa.clientFramework.sendAction({type: "stop"});
	}

	/**
	 * Sends a pathfinding action to server.
	 *
	 * @param action {object}
	 *   Action definition.
	 */
	public moveTo(action: object): void;
	/**
	 * Sends a pathfinding action to server.
	 *
	 * @param x {number}
	 *   Target position X coordinate.
	 * @param y {number}
	 *   Target position Y coordinate.
	 * @param zone {string}
	 *   User's zone.
	 */
	public moveTo(x: number, y: number, zone?: string): void;
	public moveTo(p1: object|number, p2?: number, p3?:string) {
		// in case viewport frozen from view change event
		stendhal.ui.viewport.freeze = false;
		let action: any = {};
		if (typeof(p1) === "object") {
			action = p1;
		} else {
			action.x = p1.toString();
			action.y = p2!.toString();
			if (p3) {
				action.zone = p3;
			}
		}
		action.type = "moveto";
		marauroa.clientFramework.sendAction(action);
	}
}
