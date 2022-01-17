/***************************************************************************
 *                (C) Copyright 2003-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ItemInventoryComponent } from "./ItemInventoryComponent";

declare var marauroa: any;


export class KeyringComponent extends ItemInventoryComponent {

	override update() {
		let features = null;
		if (marauroa.me != null) {
			features = marauroa.me["features"];
		}

		let keyringEnabled = false;
		if (features != null) {
			keyringEnabled = features["keyring"] != null;
		}

		if (keyringEnabled && !this.isVisible()) {
			this.setVisible(true);
		} else if (!keyringEnabled && this.isVisible()) {
			this.setVisible(false);
		}

		super.update();
	}
}
