/***************************************************************************
 *                (C) Copyright 2022-2024 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { stendhal } from "../../stendhal";

import { ChatPanel } from "../ChatPanel";
import { UIComponentEnum } from "../UIComponentEnum";
import { ui } from "../UI";

import { Component } from "../toolkit/Component";
import { Panel } from "../toolkit/Panel";
import { TabPanelComponent } from "../toolkit/TabPanelComponent";

import { BagComponent } from "../component/BagComponent";
import { BuddyListComponent } from "../component/BuddyListComponent";
import { ChatInputComponent } from "../component/ChatInputComponent";
import { ChatLogComponent } from "../component/ChatLogComponent";
import { GroupPanelComponent } from "../component/GroupPanelComponent";
import { KeyringComponent } from "../component/KeyringComponent";
import { MiniMapComponent } from "../component/MiniMapComponent";
import { ZoneInfoComponent } from "../component/ZoneInfoComponent";
import { PlayerEquipmentComponent } from "../component/PlayerEquipmentComponent";
import { PlayerStatsComponent } from "../component/PlayerStatsComponent";

import { Layout } from "../../data/enum/Layout";


export class DesktopUserInterfaceFactory {

	/** Property to help workaround issue with group event not always received at login. */
	private checkedGroupAfterLogin = false;


	public create() {
		let topPanel = new Panel("topPanel");
		ui.registerComponent(UIComponentEnum.LeftPanel, topPanel);

		let leftPanel = new Panel("leftColumn");
		ui.registerComponent(UIComponentEnum.LeftPanel, leftPanel);

		let socialPanel = new TabPanelComponent(Layout.BOTTOM);
		ui.registerComponent(UIComponentEnum.SocialPanel, socialPanel);

		this.add(leftPanel, UIComponentEnum.MiniMap, new MiniMapComponent());
		this.add(leftPanel, UIComponentEnum.ZoneInfo, new ZoneInfoComponent());
		this.add(leftPanel, UIComponentEnum.PlayerStats, new PlayerStatsComponent());
		leftPanel.add(socialPanel);

		this.add(socialPanel, UIComponentEnum.BuddyList, new BuddyListComponent());
		this.add(socialPanel, UIComponentEnum.GroupPanel, new GroupPanelComponent());
		socialPanel.addTab("Friends");
		socialPanel.addTab("Group");

		// workaround issue where some events aren't received at login by updating when tab changes
		const that = this;
		socialPanel.onTabChanged = function() {
			if (this.getCurrentIndex() == 1) {
				if (that.checkedGroupAfterLogin) {
					return;
				}
				that.checkedGroupAfterLogin = true;
				if (stendhal.data.group.getMemberCount() == 0) {
					console.debug("group not recognized after login, contacting server for refresh ...");
					stendhal.data.group.refresh();
				}
			}
		};
		// since this isn't available at time of construction execute for good measure
		socialPanel.onTabChanged();

		let rightPanel = new Panel("rightColumn");
		ui.registerComponent(UIComponentEnum.RightPanel, rightPanel);
		this.add(rightPanel, UIComponentEnum.PlayerEquipment, new PlayerEquipmentComponent());
		this.add(rightPanel, UIComponentEnum.Bag,
			new BagComponent(undefined, "bag", 3, 4, false, undefined));

		const keyring = new KeyringComponent(undefined, "keyring", 2, 4, false, "slot-key.png");
		// hide keyring by default
		keyring.setVisible(false);
		this.add(rightPanel, UIComponentEnum.Keyring, keyring);


		// hide pouch by default

		const bottomPanel = new ChatPanel();
		ui.registerComponent(UIComponentEnum.BottomPanel, bottomPanel);

		this.add(bottomPanel, UIComponentEnum.ChatInput, new ChatInputComponent());
		this.add(bottomPanel, UIComponentEnum.ChatLog, new ChatLogComponent());
	}

	private add(panel: Panel, uiComponentEnum: UIComponentEnum, component: Component) {
		panel.add(component);
		ui.registerComponent(uiComponentEnum, component);
	}

}
