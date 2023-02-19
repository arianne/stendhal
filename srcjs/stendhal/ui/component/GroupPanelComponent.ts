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
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";

import { ActionContextMenu } from "../dialog/ActionContextMenu";
import { ChatInputComponent } from "./ChatInputComponent";

import { Chat } from "../../util/Chat";


declare let marauroa: any;
declare let stendhal: any;

/**
 * group management
 */
export class GroupPanelComponent extends Component {
	private invites: Record<string, HTMLButtonElement> = {}


	constructor() {
		super("group-panel");
		this.componentElement.querySelector(".group-lootmode")!.addEventListener("click", () => {
			this.onLootmodeClick();
		})
	}

	receivedInvite(leader: string) {
		if (this.invites[leader]) {
			return;
		}

		let button = document.createElement("button");
		button.innerText = "Join " + leader;
		button.title = "Join the group led by " + leader;
		button.addEventListener("click", () => {
			this.onJoinClicked(leader);
		});
		this.invites[leader] = button;
		this.componentElement.querySelector(".group-nogroup")!.append(button);
		// TODO: activte Group-tab in TabPanelComponent
	}

	onJoinClicked(leader: string) {
		let action = {
			"type": "group_management",
			"action": "join",
			"params": leader,
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

	expiredInvite(leader: string) {
		let button = this.invites[leader];
		if (button) {
			button.remove();
			delete this.invites[leader];
		}
	}

	updateGroupStatus() {
		if (!stendhal.data.group.members) {
			this.componentElement.querySelector(".group-nogroup")!.classList.remove("hidden");
			this.componentElement.querySelector(".group-group")!.classList.add("hidden");
			return;
		}

		this.componentElement.querySelector(".group-nogroup")!.classList.add("hidden");
		this.componentElement.querySelector(".group-group")!.classList.remove("hidden");

		(this.componentElement.querySelector(".group-lootmode") as HTMLElement).innerText = stendhal.data.group.lootmode;
		(this.componentElement.querySelector(".group-leader") as HTMLElement).innerText = stendhal.data.group.leader;
		(this.componentElement.querySelector(".group-members") as HTMLElement).innerText = Object.keys(stendhal.data.group.members).join(", ");

	}

	onLootmodeClick() {
		let newMode = "shared";
		if (stendhal.data.group.lootmode === "shared") {
			newMode = "single";
		}
		let action = {
			"type": "group_management",
			"action": "lootmode",
			"params": newMode,
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}
}
