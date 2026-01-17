/***************************************************************************
 *                 (C) Copyright 2005-2023 - Faiumoni e. V.                *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { RPEvent } from "marauroa"
import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { GroupPanelComponent } from "../ui/component/GroupPanelComponent";

import { stendhal } from "../stendhal";
import { marauroa } from "marauroa"

/**
 * handles player trade events
 */
export class GroupChangeEvent extends RPEvent {
	public members!: string[];
	public leader!: string;
	public lootmode!: string;

	public execute(entity: any): void {
		if (entity !== marauroa.me) {
			return;
		}
		stendhal.data.group.updateGroupStatus(this["members"], this["leader"], this["lootmode"]);
		(ui.get(UIComponentEnum.GroupPanel) as GroupPanelComponent).updateGroupStatus();
	}

}
