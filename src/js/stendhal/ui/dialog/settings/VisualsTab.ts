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

import { SettingsComponent } from "../../toolkit/SettingsComponent";

import { singletons } from "../../../SingletonRepo";

import { StandardMessages } from "../../../util/StandardMessages";


export class VisualsTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = singletons.getConfigManager();
		const chatLog = (ui.get(UIComponentEnum.ChatLog) as ChatLogComponent);

		const col1 = this.child("#col1")!;

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

		parent.createCheckBox("chk_clickindicator", "click-indicator",
				"Displaying clicks", "Not displaying clicks");

		let indicateActivity = config.getBoolean("activity-indicator");
		let animate = config.getBoolean("activity-indicator.animate");
		const chkAnimate = new SettingsComponent("chk_animate", "Animate");
		chkAnimate.setValue(animate);
		chkAnimate.setEnabled(indicateActivity);
		chkAnimate.onchange = (evt: Event) => {
			animate = chkAnimate.getValue() as boolean;
			config.set("activity-indicator.animate", animate);
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		}

		const chkActivityInd = new SettingsComponent("chk_activityindicator", "Object activity indicator");
		chkActivityInd.setValue(indicateActivity);
		chkActivityInd.componentElement.title = indicateActivity ? "Indicator will be drawn" : "Indicator will not be drawn";
		chkActivityInd.onchange = (evt: Event) => {
			indicateActivity = chkActivityInd.getValue() as boolean;
			config.set("activity-indicator", indicateActivity);
			chkActivityInd.componentElement.title = indicateActivity ? "Indicator will be drawn" : "Indicator will not be drawn";
			chkAnimate.setEnabled(indicateActivity);
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		};
		chkActivityInd.addTo(col1);
		chkAnimate.addTo(col1);
		chkAnimate.componentElement.classList.add("indented");
	}
}
