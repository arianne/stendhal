/***************************************************************************
 *                    Copyright © 2003-2023 - Stendhal                     *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { MenuItem } from "../action/MenuItem";
import singletons from "../util/SingletonRepo";

const config = singletons.getConfigManager();


/**
 * Retrieves cursor to be shown for "use" action.
 *
 * @return
 *     Dependent on "action.item.doubleclick" config setting.
 */
function getItemUseCursor() {
	if (config.getBoolean("action.item.doubleclick")) {
		return "itemuse";
	}
	return "activity";
}

export const ItemMap: {[index: string]: any} = {
	["class"]: {
		["box"]: {
			cursor: "bag",
			actions: [
				{
					title: "Open",
					type: "use",
					index: 0
				}
			]
		},
		["drink"]: {
			cursor: getItemUseCursor
		},
		["food"]: {
			cursor: getItemUseCursor
		},
		["scroll"]: {
			cursor: getItemUseCursor
		}
	},

	["name"]: {
		["bestiary"]: {
			cursor: getItemUseCursor
		},
		["bulb"]: {
			cursor: getItemUseCursor
		},
		["food mill"]: {
			cursor: getItemUseCursor
		},
		["metal detector"]: {
			cursor: getItemUseCursor
		},
		["rotary cutter"]: {
			cursor: getItemUseCursor
		},
		["scroll eraser"]: {
			cursor: getItemUseCursor
		},
		["seed"]: {
			cursor: getItemUseCursor
		},
		["snowglobe"]: {
			cursor: getItemUseCursor
		},
		["sugar mill"]: {
			cursor: getItemUseCursor
		},
		["teddy"]: {
			cursor: getItemUseCursor
		},
		["wedding ring"]: {
			cursor: getItemUseCursor
		}
	},

	/**
	 * Retrieves list of menu actions defined for item.
	 *
	 * @param item
	 *     Object containing item defintion.
	 * @return
	 *     Actions.
	 */
	getActions: function(item: any): MenuItem[] {
		let actions: MenuItem[] = [];
		if (!item) {
			return actions;
		}

		let imap = ItemMap["name"][item["name"]];
		if (imap && imap.actions) {
			actions = actions.concat(imap.actions);
		}
		imap = ItemMap["class"][item["class"]];
		if (imap && imap.actions) {
			actions = actions.concat(imap.actions);
		}
		return actions;
	}
};
