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
	}

	receivedInvite(leader: string) {
		if (this.invites[leader]) {
			return;
		}

		let button = document.createElement("button");
		button.innerText = "Join " + leader;
		button.title = "Join the group led by " + leader;
		button.addEventListener("click", () => {
			this.join(leader);
		});
		this.invites[leader] = button;
		this.componentElement.querySelector(".group-nogroup")!.append(button);
		// TODO: activte Group-tab in TabPanelComponent
	}

	join(leader: string) {
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

	}

}
