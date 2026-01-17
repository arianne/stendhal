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

import { ChatInputComponent } from "./ChatInputComponent";

import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";

import { StatBarComponent } from "../component/StatBarComponent";

import { ActionContextMenu } from "../dialog/ActionContextMenu";

import { singletons } from "../../SingletonRepo";
import { stendhal } from "stendhal";


/**
 * a group member
 */
export class GroupMemberComponent extends Component {

	/** HP bar associated with this component. */
	private hpBar: StatBarComponent;


	constructor(private memberName: string, private isUserLeader: boolean) {
		super("group-member-template");
		this.child(".group-member-name")!.innerText = memberName;
		this.componentElement.addEventListener("mouseup", (event) => {
			this.onMouseUp(event);
		});

		this.hpBar = new StatBarComponent();
		this.hpBar.componentElement.classList.add("group-member-hpbar");
		// check for player to initialize HP bar value
		const player = singletons.getZone().findPlayer(memberName);
		if (player) {
			// component hasn't been created yet so `Player.updateGroupStatus` will fail here
			this.hpBar.draw(player["hp"] / player["base_hp"]);
		} else {
			// initialize with gray background
			this.hpBar.drawBase();
		}
		// add HP as child after name
		this.componentElement.appendChild(this.hpBar.componentElement);
	}

	/**
	 * Retrieves player name.
	 *
	 * @return {string}
	 *   Name of player associated with this component.
	 */
	getMemberName(): string {
		return this.child(".group-member-name")!.innerText;
	}

	buildActions(actions: any) {
		let playerName = this.memberName;
		actions.push({
			title: "Talk",
			action: function(_groupMemberComponent: GroupMemberComponent) {
				(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/msg "
						+ playerName
						+ " ");
			}
		});
		actions.push({
			title: "Where",
			action: function(_groupMemberComponent: GroupMemberComponent) {
				let action = {
					"type": "where",
					"target": playerName,
					"zone": marauroa.currentZoneName
				};
				marauroa.clientFramework.sendAction(action);
			}
		});
		if (this.isUserLeader && (!marauroa.me || this.memberName != marauroa.me["_name"])) {
			actions.push({
				title: "Kick",
				action: function(_groupMemberComponent: GroupMemberComponent) {
					let action = {
						"type": "group_management",
						"action": "kick",
						"params": playerName,
						"zone": marauroa.currentZoneName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
			actions.push({
				title: "Make leader",
				action: function(_groupMemberComponent: GroupMemberComponent) {
					let action = {
						"type": "group_management",
						"action": "leader",
						"params": playerName,
						"zone": marauroa.currentZoneName
					};
					marauroa.clientFramework.sendAction(action);
				}
			});
		}

	}

	onMouseUp(event: MouseEvent) {
		stendhal.ui.actionContextMenu.set(ui.createSingletonFloatingWindow("Action",
			new ActionContextMenu(this), Math.max(10, event.pageX - 50), event.pageY - 5));
	}

	/**
	 * Updates & redraws member HP bar.
	 *
	 * @param ratio {number}
	 *   Percentage value of player's current HP.
	 */
	updateHP(ratio: number) {
		this.hpBar.draw(ratio);
	}

	/**
	 * Makes member's HP status invisible to current user.
	 */
	hideStatus() {
		this.hpBar.drawBase();
	}
}
