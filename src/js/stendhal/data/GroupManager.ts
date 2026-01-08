/***************************************************************************
 *                (C) Copyright 2017-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../stendhal";

import { SlashActionRepo } from "../SlashActionRepo";


export class GroupManager {

	private members: string[] = [];
	private lootmode = "";
	private leader = "";
	private count = 0;

	/** Singleton instance. */
	private static instance: GroupManager;


	/**
	 * Retrieves singleton instance.
	 */
	static get(): GroupManager {
		if (!GroupManager.instance) {
			GroupManager.instance = new GroupManager();
		}
		return GroupManager.instance;
	}

	/**
	 * Hidden singleton constructor.
	 */
	private constructor() {
		// do nothing
	}

	updateGroupStatus(members?: string, leader?: string, lootmode?: string) {
		this.count = 0;
		if (members) {
			var memberArray = members.substring(1, members.length - 1).split("\t");
			stendhal.data.group.members = {};
			for (var i = 0; i < memberArray.length; i++) {
				stendhal.data.group.members[memberArray[i]] = true;
				this.count++;
			}
			stendhal.data.group.leader = leader;
			stendhal.data.group.lootmode = lootmode;
		} else {
			stendhal.data.group.members = []; // XXX: should this be an object instead of an array?
			stendhal.data.group.leader = "";
			stendhal.data.group.lootmode = "";
		}
	}

	/**
	 * Retrieves the name of the leader of this group or an empty string.
	 */
	getLeader(): string {
		return this.leader;
	}

	/**
	 * Retrieves number of members in group.
	 */
	getMemberCount(): number {
		return this.count;
	}

	/**
	 * Requests a group management event from server to refresh status in client.
	 */
	refresh() {
		// NOTE: sending action directly with `marauroa.clientFramework.sendAction` doesn't work here???
		SlashActionRepo.get().execute("/group status");
	}
}
