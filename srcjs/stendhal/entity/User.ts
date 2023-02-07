/***************************************************************************
 *                       Copyright © 2023 - Stendhal                       *
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

import { Entity } from "./Entity";
import { Player } from "./Player";

import { singletons } from "../SingletonRepo";

import { MenuItem } from "../action/MenuItem";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";
import { PlayerStatsComponent } from "../ui/component/PlayerStatsComponent";

import { OutfitDialog } from "../ui/dialog/outfit/OutfitDialog";

import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { Color } from "../util/Color";


/**
 * Class representing the player controlled by this client.
 */
export class User extends Player {

	private readonly soundMan = singletons.getSoundManager();
	private readonly lssMan = singletons.getLoopedSoundSourceManager();

	override minimapStyle = Color.USER;


	override destroy(parent: any) {
		this.onExitZone();
		super.destroy(parent);
	}

	override set(key: string, value: any) {
		const oldX = this["x"];
		const oldY = this["y"];
		super.set(key, value);

		if ((key === "x" || key === "y") && this["x"] && this["y"]
				&& (this["x"] !== oldX || this["y"] !== oldY)) {
			this.lssMan.onDistanceChanged(this["x"], this["y"]);
		}

		queueMicrotask( () => {
			(ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent).update(key);
			(ui.get(UIComponentEnum.Bag) as ItemInventoryComponent).update();
			(ui.get(UIComponentEnum.Keyring) as ItemInventoryComponent).update();
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
					const dstate = stendhal.config.dialogstates["outfit"];
					outfitDialog = new OutfitDialog();
					new FloatingWindow("Choose outfit", outfitDialog, dstate.x, dstate.y);
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
	 * Actions when player leaves a zone.
	 */
	onExitZone() {
		// stop sounds & clear map sounds cache on zone change
		const msgs: string[] = [];
		if (!this.lssMan.removeAll()) {
			let tmp = "LoopedSoundSourceManager reported not all sources stopped on zone change:";
			const loopSources = this.lssMan.getSources();
			for (const id in loopSources) {
				const snd = loopSources[id].sound;
				tmp += "\n- ID: " + id + " (" + snd.src + ")";
			}
			msgs.push(tmp);
		}
		if (!this.soundMan.stopAll()) {
			let tmp = "SoundManager reported not all sounds stopped on zone change:";
			for (const snd of this.soundMan.getActive()) {
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
	onEnterZone() {
		// play looped sound sources
		this.lssMan.onZoneReady();
	}

	/**
	 * Checks if player has autowalk set.
	 */
	public autoWalkEnabled(): boolean {
		return typeof(this["autowalk"]) !== "undefined";
	}
}
