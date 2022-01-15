/***************************************************************************
 *                   (C) Copyright 2022-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "../ui/UI";
import { UIComponentEnum } from "../ui/UIComponentEnum";
import { FloatingWindow } from "../ui/toolkit/FloatingWindow";

import { Action } from "./Action";
import { Chat } from "../util/Chat";
import { ShowFloatingWindowComponent } from "../ui/component/ShowFloatingWindowComponent";
import { Panel } from "ui/toolkit/Panel";

declare var marauroa: any;

/**
 * performances debugging actions
 */
export class DebugAction extends Action {
	readonly minParams = 0;
	readonly maxParams = 0;
	private uiPopped = false;

	uiAction(params: string[]) {
		console.log(UIComponentEnum);
		for (let i in UIComponentEnum) {
			console.log("in", i);
		}
		if (params[1] === "pop") {
			this.uiPop();
		}
	}

	uiPop() {
		this.uiFloatComponent(UIComponentEnum.LeftPanel, "Left panel", 10, 10);
		this.uiFloatComponent(UIComponentEnum.RightPanel, "Right panel", 500, 10);
		this.uiFloatComponent(UIComponentEnum.TopPanel, "Top panel", 200, 10);
		this.uiFloatComponent(UIComponentEnum.BottomPanel, "Bottom panel", 10, 500);

		this.uiFloatComponent(UIComponentEnum.MiniMap, "Map", 10, 50);
		this.uiFloatComponent(UIComponentEnum.PlayerStats, "Stats", 10, 190);
		this.uiFloatComponent(UIComponentEnum.BuddyList, "Buddies", 10, 250);

		this.uiFloatComponent(UIComponentEnum.PlayerEquipment, marauroa.me["_name"], 500, 50);
		this.uiFloatComponent(UIComponentEnum.Bag, "Bag", 500, 200);
		this.uiFloatComponent(UIComponentEnum.Keyring, "Keyring", 500, 300);
		this.uiFloatComponent(UIComponentEnum.ChatInput, "Chat", 100, 500);
		this.uiFloatComponent(UIComponentEnum.ChatLog, "Chat log", 100, 560);

		this.uiPopped = true;
	}


	uiFloatComponent(uiComponentEnum: UIComponentEnum, title: string, x: number, y: number) {
		let component = ui.get(uiComponentEnum);
		if (!component) {
			return;
		}
		component.componentElement.dispatchEvent(new Event("close"));
		component.componentElement.remove();
		new FloatingWindow(title, component, x, y);

		if (!this.uiPopped) {
			let bottomPannel = ui.get(UIComponentEnum.BottomPanel) as Panel;
			bottomPannel.add(new ShowFloatingWindowComponent(uiComponentEnum, title, x, y))
		}
	}

	execute(_type: string, params: string[], _remainder: string) {
		if (params.length < 1) {
			Chat.log("error", "Expected parameters")
		}
		if (params[0] === "ui") {
			this.uiAction(params);
		}
		return true;
	}

};
