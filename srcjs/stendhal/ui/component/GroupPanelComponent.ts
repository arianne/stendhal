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

import { Chat } from "../../util/Chat";


declare let marauroa: any;
declare let stendhal: any;

/**
 * group management
 */
export class GroupPanelComponent extends Component {


	constructor() {
		super("group-panel");
	}

	receivedInvite(leader: string) {
		// TODO:
		Chat.log("normal", "You have been invited by " + leader + " to join a group.");
		Chat.log("normal", "To join, type: /group join " + leader);
		Chat.log("normal", "To leave the group at any time, type: /group part");
	}

	expiredInvite(leader: string) {
		// TODO:
		Chat.log("normal", "Your group invite by " + leader + " has expired.");
	}

	updateGroupStatus() {

	}

}
