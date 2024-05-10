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

import { SoundManager } from "../../SoundManager";

import { SettingsComponent } from "../../toolkit/SettingsComponent";

import { ConfigManager } from "../../../util/ConfigManager";


export class SoundTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = ConfigManager.get();

		const col1 = this.child("#col1")!;

		// TODO: add DOM element creation to `SettingsDialog.createCheckBox`
		const chkSound = new SettingsComponent("chk_sound", "Enable sound");
		chkSound.setValue(config.getBoolean("sound"));
		chkSound.onchange = function(evt: Event) {
			config.set("sound", (chkSound.componentElement as HTMLInputElement).checked);
			SoundManager.get().onStateChanged();
		};
		chkSound.addTo(col1);

		// TODO: add sliders to adjust each layer's volume
	}
}
