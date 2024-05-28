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

import { SettingsComponent } from "../../toolkit/SettingsComponent";

import { singletons } from "../../../SingletonRepo";

import { StandardMessages } from "../../../util/StandardMessages";


export class VisualsTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = singletons.getConfigManager();

		const col1 = this.child("#col1")!;

		parent.createCheckBox("chk_light", "effect.lighting",
				"Lighting effects are enabled", "Lighting effects are disabled", function() {
					StandardMessages.changeNeedsRefresh();
				});

		parent.createCheckBox("chk_weather", "effect.weather",
				"Weather is enabled", "Weather is disabled", function() {
					StandardMessages.changeNeedsRefresh();
				})!;

		parent.createCheckBox("chk_blood", "effect.blood",
				"Gory images are enabled", "Gory images are disabled");

		parent.createCheckBox("chk_nonude", "effect.no-nude",
				"Naked entities have undergarments", "Naked entities are not covered");

		parent.createCheckBox("chk_shadows", "effect.shadows",
				"Shadows are enabled", "Shadows are disabled");

		parent.createCheckBox("chk_clickindicator", "click-indicator",
				"Displaying clicks", "Not displaying clicks");

		const chkAnimate = new SettingsComponent("chk_animate", "Animate");
		chkAnimate.setConfigId("activity-indicator.animate");
		chkAnimate.setEnabled(config.getBoolean("activity-indicator"));
		chkAnimate.addListener((evt: Event) => {
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		});

		const chkActivityInd = new SettingsComponent("chk_activityindicator", "Object activity indicator");
		chkActivityInd.setConfigId("activity-indicator");
		chkActivityInd.setTooltip("Display an indictor over certain interactive objects and corpses"
				+ " that aren't empty");
		chkActivityInd.addListener((evt: Event) => {
			chkAnimate.setEnabled(chkActivityInd.getValue() as boolean);
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		});
		chkActivityInd.addTo(col1);
		chkAnimate.addTo(col1);
		chkAnimate.componentElement.classList.add("indented");

		const chkParallax = new SettingsComponent("chk_parallax", "Parallax scrolling backgrounds");
		chkParallax.setTooltip("Parallax scrolling enabled", "Parallax scrolling disabled");
		chkParallax.setConfigId("effect.parallax");
		chkParallax.addListener((evt: Event) => {
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		});
		chkParallax.addTo(col1);

		const chkEntityOverlay = new SettingsComponent("chk_entity_overlay",
				"Entity overlay effects");
		chkEntityOverlay.setValue(config.getBoolean("effect.entity-overlay"));
		chkEntityOverlay.addListener((evt: Event) => {
			config.set("effect.entity-overlay", chkEntityOverlay.getValue());
			StandardMessages.changeNeedsRefresh();
			parent.refresh();
		});
		chkEntityOverlay.addTo(col1);
	}
}
