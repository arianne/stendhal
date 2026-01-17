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

import { marauroa } from "marauroa"

/**
 * handles player trade events
 */
export class GroupInviteEvent extends RPEvent {
	public expire!: string;
	public leader!: string;

	public execute(entity: any): void {
		if (entity !== marauroa.me) {
			return;
		}

		if (this["expire"]) {
			(ui.get(UIComponentEnum.GroupPanel) as GroupPanelComponent).expiredInvite(this["leader"]);
		} else {
			(ui.get(UIComponentEnum.GroupPanel) as GroupPanelComponent).receivedInvite(this["leader"]);
		}
	}
}
