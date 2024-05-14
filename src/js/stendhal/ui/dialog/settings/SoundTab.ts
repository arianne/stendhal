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
import { SliderComponent } from "../../toolkit/SliderComponent";

import { ConfigManager } from "../../../util/ConfigManager";


export class SoundTab extends AbstractSettingsTab {

	constructor(parent: SettingsDialog, element: HTMLElement) {
		super(element);
		const config = ConfigManager.get();
		const sound = SoundManager.get();

		const col1 = this.child("#col1")!;

		// TODO: add DOM element creation to `SettingsDialog.createCheckBox`
		const chkSound = new SettingsComponent("chk_sound", "Enable sound");
		chkSound.setValue(config.getBoolean("sound"));
		chkSound.onchange = function(evt: Event) {
			config.set("sound", (chkSound.componentElement as HTMLInputElement).checked);
			sound.onStateChanged();
		};
		chkSound.addTo(col1);

		const volMaster = new SliderComponent("setting-vol-master", "Master", 0, 100);
		volMaster.setValue(sound.getVolume("master") * 100);
		volMaster.onchange = function(evt: Event) {
			sound.setVolume("master", volMaster.getValue() / 100);
		}
		volMaster.addTo(col1);

		// TODO:
		// - add sliders for remaining sound channels
		// - disable sliders when sound is disabled
		// - show volume level value
	}
}
