/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Paths } from "../data/Paths";


/**
 * Available natures.
 *
 * Note that Nature is related to the magic system, and is not intended
 * to support other kinds of "effects", like slowdown, drop, paralysis, etc.
 */
export class Nature {

	/** physical attack */
	public static readonly CUT   = new Nature("#c0c0c0");
	/** fire magic */
	public static readonly FIRE  = new Nature("#ff6400", "fire");
	/** ice magic */
	public static readonly ICE   = new Nature("#8c8cff", "ice");
	/** light magic */
	public static readonly LIGHT = new Nature("#fff08c", "light");
	/** dark magic */
	public static readonly DARK  = new Nature("#404040", "dark");

	public static readonly VALUES = [Nature.CUT, Nature.FIRE, Nature.ICE, Nature.LIGHT, Nature.DARK];

	private constructor(
		public readonly color: string,
		public readonly elem?: string) {
	}

	/**
	 * Retrieves image for weapon & element type.
	 *
	 * @param weapon
	 *     Weapon type (blade_strike (default), axe, club, sword).
	 * @return
	 *     Sprite path.
	 */
	getWeaponPath(weapon: string): string {
		let path = Paths.sprites + "/combat/" + weapon;
		if (typeof(this.elem) !== "undefined") {
			path += "_" + this.elem;
		}

		return path + ".png";
	}
}
