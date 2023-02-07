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

declare var stendhal: any;


export class GroupManager {

	private members: string[] = [];
	private lootmode = "";
	private leader = "";

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
		if (members) {
			var memberArray = members.substring(1, members.length - 1).split("\t");
			stendhal.data.group.members = {};
			for (var i = 0; i < memberArray.length; i++) {
				stendhal.data.group.members[memberArray[i]] = true;
			}
			stendhal.data.group.leader = leader;
			stendhal.data.group.lootmode = lootmode;
		} else {
			stendhal.data.group.members = [];
			stendhal.data.group.leader = "";
			stendhal.data.group.lootmode = "";
		}
	}
}
