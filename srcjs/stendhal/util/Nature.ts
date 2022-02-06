/***************************************************************************
 *                   (C) Copyright 2003-2022 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

/**
 * Available natures.
 *
 * Note that Nature is related to the magic system, and is not intended
 * to support other kinds of "effects", like slowdown, drop, paralysis, etc.
 */
export class Nature {

	/** physical attack */
	public static readonly CUT   = new Nature("#c0c0c0", "/data/sprites/combat/blade_strike_cut.png");
	/** fire magic */
	public static readonly FIRE  = new Nature("#ff6400", "/data/sprites/combat/blade_strike_fire.png");
	/** ice magic */
	public static readonly ICE   = new Nature("#8c8cff", "/data/sprites/combat/blade_strike_ice.png");
	/** light magic */
	public static readonly LIGHT = new Nature("#fff08c", "/data/sprites/combat/blade_strike_light.png");
	/** dark magic */
	public static readonly DARK  = new Nature("#404040", "/data/sprites/combat/blade_strike_dark.png");

	public static readonly VALUES = [Nature.CUT, Nature.FIRE, Nature.ICE, Nature.LIGHT, Nature.DARK];

	constructor(
		public readonly color: string,
		public readonly imagePath: string) {
	}

}
