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

import { marauroa } from "marauroa"
import { stendhal } from "../stendhal";

import { RPEntity } from "./RPEntity";

import { MenuItem } from "../action/MenuItem";

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";

import { GroupPanelComponent } from "../ui/component/GroupPanelComponent";

import { Color } from "../data/color/Color";
import { RenderingContext2D } from "util/Types";
import { Paths } from "../data/Paths";


export class Player extends RPEntity {

	override minimapShow = true;
	override minimapStyle = Color.PLAYER;
	override dir = 3;
	// shift hp bar & title to avoid overlap with other entities
	override titleDrawYOffset = 6;


	constructor() {
		super();
		queueMicrotask(() => {
			this.onEnterZone();
		});
	}

	override destroy(parent: any) {
		this.onExitZone();
		super.destroy(parent);
	}

	override set(key: string, value: any) {
		super.set(key, value);
		if (key === "ghostmode") {
			this.minimapShow = false;
		} else if (["hp", "base_hp"].indexOf(key) !== -1) {
			this.updateGroupStatus(true);
		}

		// partial workaround to issue with `event.PlayerLoggedOnEvent.PlayerLoggedOnEvent` not always
		// being received
		// NOTE: this may actually be a fix as so far I have only noticed players on same map as user
		//       at time of login don't always invoke a "player_logged_on" event
		if (key === "name" && stendhal.players.indexOf(value) < 0) {
			stendhal.players.push(value);
			stendhal.players = stendhal.players.sort();
		}
	}

	override createTitleTextSprite() {
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

		if (marauroa.me !== this) {
			list.push({
				title: "Trade",
					action: function(_entity: any) {
						var action = {
							"type": "trade",
							"action": "offer_trade",
							"zone": marauroa.currentZoneName,
							"target": playerName
						};
						marauroa.clientFramework.sendAction(action);
					}
			});
			if (marauroa.me.canInviteToGroup()) {
				list.push({
					title: "Invite",
					action: (_entity: any) => {
						marauroa.clientFramework.sendAction({
							"type": "group_management",
							"action": "invite",
							"params": this["name"]
						});
					}
				});
			}
		}
	}

	isIgnored() {
		if (!marauroa.me || !marauroa.me["!ignore"]) {
			return false;
		}
		var temp = marauroa.me["!ignore"]._objects;
		return temp.length > 0 && ("_" + this["_name"]) in temp[0];
	}

	public override onMiniMapDraw() {
		if (this === marauroa.me) {
			// handled in User class
			return;
		}
		if (stendhal.data.group.members[this["name"]]) {
			this.minimapStyle = Color.GROUP;
		} else {
			this.minimapStyle = Color.PLAYER;
		}
	}

	override draw(ctx: RenderingContext2D) {
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
	 * Actions to execute when player is removed from zone.
	 */
	onExitZone() {
		// HP status of current user should always be visible
		if (this != marauroa.me) {
			this.updateGroupStatus(false);
		}
	}

	/**
	 * Actions to execute when player is created in zone.
	 */
	onEnterZone() {
		this.updateGroupStatus(true);
	}

	/**
	 * Updates group membership HP bar.
	 *
	 * @param visible {boolean}
	 *   If `false`, player's group status is not visible to current user.
	 */
	updateGroupStatus(visible: boolean) {
		const memberComponent = (ui.get(UIComponentEnum.GroupPanel)! as GroupPanelComponent).getMemberComponent(this["name"]);
		if (memberComponent) {
			if (visible) {
				memberComponent.updateHP(this["hp"] / this["base_hp"]);
			} else {
				memberComponent.hideStatus();
			}
		}
	}

	override getCursor(_x: number, _y: number) {
		if (this.isVisibleToAction(false)) {
			return "url(" + Paths.sprites + "/cursor/look.png) 1 3, auto";
		}
		return "url(" + Paths.sprites + "/cursor/walk.png) 1 3, auto";
	}
}
