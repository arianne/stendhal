/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { AbstractSettingsTab } from "./AbstractSettingsTab";

import { SettingsDialog } from "../SettingsDialog";

import { ui } from "../../UI";
import { UIComponentEnum } from "../../UIComponentEnum";

import { ChatLogComponent } from "../../component/ChatLogComponent";


export class VisualsTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const chatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		parent.createCheckBox("chk_light", "effect.lighting",
				"Lighting effects are enabled", "Lighting effects are disabled");

		parent.createCheckBox("chk_weather", "effect.weather",
				"Weather is enabled", "Weather is disabled", function() {
					if (chatLog) {
						chatLog.addLine("client", "Weather changes will take effect after you change maps.");
					}
				})!;

		parent.createCheckBox("chk_blood", "effect.blood",
				"Gory images are enabled", "Gory images are disabled");

		parent.createCheckBox("chk_nonude", "effect.no-nude",
				"Naked entities have undergarments", "Naked entities are not covered");

		parent.createCheckBox("chk_shadows", "effect.shadows",
				"Shadows are enabled", "Shadows are disabled");

		parent.createCheckBox("chk_activityindicator", "activity-indicator",
				"Indicator will be drawn", "Indicator will not be drawn");

		parent.createCheckBox("chk_clickindicator", "click-indicator",
				"Displaying clicks", "Not displaying clicks");
	}
}
