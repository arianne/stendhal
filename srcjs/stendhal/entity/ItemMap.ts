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

export const ItemMap: {[index: string]: {[index: string]: any}} = {
	["class"]: {
		["box"]: {
			cursor: "bag"
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
	}
};
