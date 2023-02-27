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
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";

import { ActionContextMenu } from "../dialog/ActionContextMenu";
import { ChatInputComponent } from "./ChatInputComponent";

declare let marauroa: any;
declare let stendhal: any;

/**
 * a group member
 */
export class GroupMemberComponent extends Component {

	constructor(private memberName: string, private isUserLeader: boolean) {
		super("group-member-template");
		this.child(".group-member-name")!.innerText = memberName;
		this.componentElement.addEventListener("mouseup", (event) => {
			this.onMouseUp(event);
		});
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
}
