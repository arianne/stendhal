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
			config.set("sound", chkSound.getValue() as boolean);
			sound.onStateChanged();
		};
		chkSound.addTo(col1);

		const layers = [
			["master", "Master"],
			["gui", "GUI"],
			["sfx", "Effects"],
			["creature", "Creatures"],
			["ambient", "Ambient"],
			["music", "Music"]
		];

		for (const group of layers) {
			const layer = group[0];
			const label = group[1];
			const slider = new SliderComponent("setting-vol-" + layer, label, 0, 100);
			slider.setValue(sound.getVolume(layer) * 100);
			slider.onchange = function(evt: Event) {
				sound.setVolume(layer, slider.getValue() / 100);
			}
			slider.addTo(col1);
		}

		// TODO:
		// - disable sliders when sound is disabled
		// - show volume level value
	}
}
