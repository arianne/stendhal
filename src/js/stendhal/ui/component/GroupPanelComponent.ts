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
import { GroupMemberComponent } from "./GroupMemberComponent";

import { ui } from "../UI";
import { UIComponentEnum } from "../UIComponentEnum";

import { Component } from "../toolkit/Component";
import { Panel } from "../toolkit/Panel";
import { TabPanelComponent } from "../toolkit/TabPanelComponent";

import { Chat } from "../../util/Chat";
import { stendhal } from "stendhal";


/**
 * group management
 */
export class GroupPanelComponent extends Panel {

	private invites: Record<string, HTMLButtonElement> = {}

	/** Registered members for faster lookup. */
	private members = new Map<String, GroupMemberComponent>();


	constructor() {
		super("group-panel");
		this.containerElement = this.child(".group-members")!;
		this.child(".group-lootmode")!.addEventListener("click", () => {
			this.onLootmodeClick();
		});
		this.child(".group-chat")!.addEventListener("click", () => {
			this.onGroupChatButtonClick();
		});
		this.child(".group-invite")!.addEventListener("click", () => {
			this.onGroupInviteButtonClick();
		});
		this.child(".group-part")!.addEventListener("click", () => {
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
		this.child(".group-invites")!.append(button);
		Chat.log("client", "You received an invite to join a group. Please use the group panel to accept the invite.");
		// show group panel
		// FIXME: should get tab index dynamically
		(ui.get(UIComponentEnum.SocialPanel) as TabPanelComponent).setCurrentTab(1);
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
			this.child(".group-nogroup")!.classList.remove("hidden");
			this.child(".group-group")!.classList.add("hidden");
			return;
		}

		this.invites = {};
		this.child(".group-invites")!.innerHTML = "";
		this.child(".group-nogroup")!.classList.add("hidden");
		this.child(".group-group")!.classList.remove("hidden");

		this.child(".group-lootmode")!.innerText = stendhal.data.group.lootmode;
		this.child(".group-leader")!.innerText = stendhal.data.group.leader;
		this.renderGroupMembers();
	}

	override add(child: Component) {
		super.add(child);
		if (child instanceof GroupMemberComponent) {
			const memberComponent = child as GroupMemberComponent;
			this.members.set(memberComponent.getMemberName(), memberComponent);
		}
	}

	override clear() {
		super.clear();
		for (const name of this.members.keys()) {
			this.members.delete(name);
		}
	}

	renderGroupMembers() {
		this.clear();
		for (let member of Object.keys(stendhal.data.group.members)) {
			this.add(new GroupMemberComponent(member, stendhal.data.group.leader === marauroa.me["_name"]));
		}
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

	/**
	 * Retrieves membership component.
	 *
	 * @param name {string}
	 *   Name of player.
	 * @return {ui.component.GroupMemberComponent.GroupMemberComponent}
	 *   The component associated with player or `undefined`.
	 */
	getMemberComponent(name: string): GroupMemberComponent|undefined {
		if (!this.isInGroup()) {
			return undefined;
		}
		for (const entry of this.members.entries()) {
			if (entry[0] === name) {
				return entry[1];
			}
		}
	}
}
