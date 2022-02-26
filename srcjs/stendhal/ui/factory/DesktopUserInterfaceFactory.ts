/***************************************************************************
 *                (C) Copyright 2022-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { UIComponentEnum } from "../UIComponentEnum";
import { ui } from "../UI";

import { Component } from "../toolkit/Component";
import { Panel } from "../toolkit/Panel";

import { BuddyListComponent } from "../component/BuddyListComponent";
import { ChatInputComponent } from "../component/ChatInputComponent";
import { ChatLogComponent } from "../component/ChatLogComponent";
import { ItemInventoryComponent } from "../component/ItemInventoryComponent";
import { KeyringComponent } from "../component/KeyringComponent";
import { MiniMapComponent } from "../component/MiniMapComponent";
import { PlayerEquipmentComponent } from "../component/PlayerEquipmentComponent";
import { PlayerStatsComponent } from "../component/PlayerStatsComponent";

export class DesktopUserInterfaceFactory {

	public create() {
		let topPanel = new Panel("topPanel");
		ui.registerComponent(UIComponentEnum.LeftPanel, topPanel);

		let leftPanel = new Panel("leftColumn");
		ui.registerComponent(UIComponentEnum.LeftPanel, leftPanel);

		this.add(leftPanel, UIComponentEnum.MiniMap, new MiniMapComponent());
		this.add(leftPanel, UIComponentEnum.PlayerStats, new PlayerStatsComponent());
		this.add(leftPanel, UIComponentEnum.BuddyList, new BuddyListComponent());


		let rightPanel = new Panel("rightColumn");
		ui.registerComponent(UIComponentEnum.RightPanel, rightPanel);
		this.add(rightPanel, UIComponentEnum.PlayerEquipment, new PlayerEquipmentComponent());
		this.add(rightPanel, UIComponentEnum.Bag,
			new ItemInventoryComponent(undefined, "bag", 3, 4, false, undefined));

		const keyring = new KeyringComponent(undefined, "keyring", 2, 4, false, "slot-key.png");
		// hide keyring by default
		keyring.setVisible(false);
		this.add(rightPanel, UIComponentEnum.Keyring, keyring);


		// hide pouch by default

		let bottomPanel = new Panel("bottomPanel");
		ui.registerComponent(UIComponentEnum.BottomPanel, bottomPanel);

		this.add(bottomPanel, UIComponentEnum.ChatInput, new ChatInputComponent());
		this.add(bottomPanel, UIComponentEnum.ChatLog, new ChatLogComponent());
	}

	private add(panel: Panel, uiComponentEnum: UIComponentEnum, component: Component) {
		panel.add(component);
		ui.registerComponent(uiComponentEnum, component);
	}

}
