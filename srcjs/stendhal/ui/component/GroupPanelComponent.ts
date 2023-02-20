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
		});
		this.componentElement.querySelector(".group-chat")!.addEventListener("click", () => {
			this.onGroupChatButtonClick();
		});
		this.componentElement.querySelector(".group-invite")!.addEventListener("click", () => {
			this.onGroupInviteButtonClick();
		});
		this.componentElement.querySelector(".group-part")!.addEventListener("click", () => {
			this.onGroupPartButtonClick();
		});
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
		this.componentElement.querySelector(".group-invites")!.append(button);
		Chat.log("client", "You received an invite to join a group. Please use the group panel to accept the invite.")
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

	isInGroup() {
		return stendhal.data.group.members && Object.keys(stendhal.data.group.members).length > 0;
	}

	updateGroupStatus() {
		if (!this.isInGroup()) {
			this.componentElement.querySelector(".group-nogroup")!.classList.remove("hidden");
			this.componentElement.querySelector(".group-group")!.classList.add("hidden");
			return;
		}

		this.invites = {};
		this.componentElement.querySelector(".group-invites")!.innerHTML = "";
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

	onGroupChatButtonClick() {
		if (!this.isInGroup()) {
			Chat.log("error", "Please invite someone into a group before trying to send group messages.");
			return;
		}
		(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/p ");
	}

	onGroupInviteButtonClick() {
		if (this.isInGroup() && (stendhal.data.group.leader !== marauroa.me["_name"])) {
			Chat.log("error", "Only the leader may invite people into the group.");
			return;
		}
		Chat.log("client", "Please Fill in the name of the player you want to invite");

		(ui.get(UIComponentEnum.ChatInput) as ChatInputComponent).setText("/group invite ");
	}

	onGroupPartButtonClick() {
		if (!this.isInGroup()) {
			Chat.log("error", "You cannot leave a group because your are not a member of a group");
			return;
		}
		const action = {
			"type": "group_management",
			"action": "part",
			"params": "",
			"zone": marauroa.currentZoneName
		};
		marauroa.clientFramework.sendAction(action);
	}

}
