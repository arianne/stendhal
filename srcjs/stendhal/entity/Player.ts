/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Entity } from "./Entity";
import { RPEntity } from "./RPEntity";

import { MenuItem} from "../action/MenuItem";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { ItemInventoryComponent } from "../ui/component/ItemInventoryComponent";
import { PlayerStatsComponent } from "../ui/component/PlayerStatsComponent";
import { OutfitDialog } from "../ui/dialog/outfit/OutfitDialog";

import { Color } from "../util/Color";

declare var marauroa: any;
declare var stendhal: any;


export class Player extends RPEntity {
	override minimapShow = true;
	override minimapStyle = Color.PLAYER;
	override dir = 3;

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "ghostmode") {
			this.minimapShow = false;
		}

		// stats
		if (marauroa.me !== this) {
			return;
		}

		queueMicrotask( () => {
			(ui.get(UIComponentEnum.PlayerStats) as PlayerStatsComponent).update(key);
			(ui.get(UIComponentEnum.Bag) as ItemInventoryComponent).update();
			(ui.get(UIComponentEnum.Keyring) as ItemInventoryComponent).update();
		});
	}

	override createTitleTextSprite() {
		// HACK: titleStyle should be overridden when player is created
		if (this.isAdmin()) {
			this.titleStyle = "#FFFF00";
		}

		super.createTitleTextSprite();
	}

	/**
	 * Is this player an admin?
	 */
	isAdmin() {
		return (typeof(this["adminlevel"]) !== "undefined" && this["adminlevel"] > 600);
	}

	override buildActions(list: MenuItem[]) {
		super.buildActions(list);
		var playerName = this["_name"];
		var isUnknown = (marauroa.me !== this) && ((marauroa.me["buddies"] == null) || !(playerName in marauroa.me["buddies"]));
		if (isUnknown) {
			list.push({
				title: "Add to buddies",
				action: function(_entity: any) {
					var action = {
						"type": "addbuddy",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}

		if (this.isIgnored()) {
			list.push({
				title: "Remove ignore",
				action: function(_entity: any) {
					var action = {
						"type": "unignore",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		} else if (isUnknown) {
			list.push({
				title: "Ignore",
				action: function(_entity: any) {
					var action = {
						"type": "ignore",
						"zone": marauroa.currentZoneName,
						"target": playerName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}
		if (marauroa.me === this) {
			let walk_label = "Walk";
			if (!this.stopped()) {
				walk_label = "Stop";
			}

			list.push({
				title: walk_label,
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
						"target": playerName,
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}
		/*
		list.push({
			title: "Trade",
			type: "trade"
		})
		list.add(ActionType.INVITE.getRepresentation());
		*/
	}

	isIgnored() {
		if (!marauroa.me || !marauroa.me["!ignore"]) {
			return false;
		}
		var temp = marauroa.me["!ignore"]._objects;
		return temp.length > 0 && ("_" + this["_name"]) in temp[0];
	}

	public override onMiniMapDraw() {
		if (marauroa.me === this) {
			// FIXME: is it possible to do this in constructor or after construction
			this.minimapStyle = Color.USER;
		} else if (stendhal.data.group.members[this["name"]]) {
			this.minimapStyle = Color.GROUP;
		} else {
			this.minimapStyle = Color.PLAYER;
		}
	}

	override draw(ctx: CanvasRenderingContext2D) {
		if (this.isIgnored()) {
			return;
		}
		// TODO: grey out instead of hiding completely because they still cause a collision
		super.draw(ctx);
	}

	override getResistance() {
		if (typeof(this["ghostmode"]) !== "undefined") {
			return 0;
		}
		return this["resistance"];
	}

	/**
	 * says a text
	 *
	 * @param text
	 *     Message contents.
	 * @param rangeSquared
	 *     Distance squared within which the entity can be heard (-1
	 *     represents entire map).
	 */
	override say(text: string, rangeSquared?: number) {
		if (this.isIgnored()) {
			return;
		}
		super.say(text, rangeSquared);
	}

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
		let hearingRange = 15; // default
		if (typeof rangeSquared !== "undefined") {
			if (rangeSquared < 0) {
				hearingRange = -1;
			} else {
				hearingRange = Math.sqrt(rangeSquared);
			}
		}

		return (this.isAdmin()
			|| (hearingRange < 0)
			|| ((Math.abs(this["x"] - entity["x"]) < hearingRange)
				&& (Math.abs(this["y"] - entity["y"]) < hearingRange)));
	}

	override getCursor(_x: number, _y: number) {
		if (this.isVisibleToAction(false)) {
			return "url(" + stendhal.paths.sprites + "/cursor/look.png) 1 3, auto";
		}
		return "url(" + stendhal.paths.sprites + "/cursor/walk.png) 1 3, auto";
	}

	public autoWalkEnabled(): boolean {
		return typeof(this["autowalk"]) !== "undefined";
	}

	override drawHealthBar(ctx: CanvasRenderingContext2D, x: number, y: number) {
		// offset so not hidden by other entity bars
		super.drawHealthBar(ctx, x, y + 6);
	}

	override drawTitle(ctx: CanvasRenderingContext2D, x: number, y: number) {
		// offset to match health bar
		super.drawTitle(ctx, x, y + 6);
	}
}
