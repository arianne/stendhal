/***************************************************************************
 *                 Copyright © 2023-2024 - Faiumoni e. V.                  *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { ui } from "ui/UI";
import { stendhal } from "./stendhal";

import { RPObject, PerceptionListener } from "marauroa";
import { UIComponentEnum } from "ui/UIComponentEnum";
import { MiniMapComponent } from "ui/component/MiniMapComponent";
import { BuddyListComponent } from "ui/component/BuddyListComponent";
import { PlayerEquipmentComponent } from "ui/component/PlayerEquipmentComponent";
import { singletons } from "SingletonRepo";
import { Client } from "Client";


/**
 * Class to override `marauroa.perceptionListener` & handle creating
 * User object.
 */
export class StendhalPerceptionListener extends PerceptionListener {

	/**
	 * Action when an object is added to the world.
	 *
	 * @param obj {entity.RPObject}
	 *   Object to be added.
	 * @return {boolean}
	 *   `true` if object was added.
	 * @see marauroa.perceptionListener.onAdded
	 */
	override onAdded(obj: RPObject): boolean {
		if (obj["c"] === "player") {
			if (obj.hasOwnProperty("a")) {
				if (obj["a"]["name"] === stendhal.session.getCharName()) {
					// create User object for player controlled by this client
					obj["c"] = "user";
				}
			}
		}
		return super.onAdded(obj);
	}

	override onPerceptionEnd(_type: Int8Array, _timestamp: number) {
		stendhal.zone.sortEntities();
		(ui.get(UIComponentEnum.MiniMap) as MiniMapComponent).draw();
		(ui.get(UIComponentEnum.BuddyList) as BuddyListComponent).update();
		stendhal.ui.equip.update();
		(ui.get(UIComponentEnum.PlayerEquipment) as PlayerEquipmentComponent).update();
		if (!Client.get().loaded) {
			Client.get().loaded = true;
			// delay visibile change of client a little to allow for initialisation in the background for a smoother experience
			window.setTimeout(function() {
				let body = document.getElementById("body")!;
				body.style.cursor = "auto";
				document.getElementById("client")!.style.display = "block";
				document.getElementById("loginpopup")!.style.display = "none";

				// initialize observer after UI is ready
				singletons.getUIUpdateObserver().init();
				ui.onDisplayReady();
			}, 300);
		}
	}
}
