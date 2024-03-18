/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.core.engine;

import com.google.common.collect.ImmutableList;


/**
 * Portal attributes for facing direction and continuous movement.
 *
 * Values represent tile indexes in logic/portal.png tileset.
 *
 * Legend:
 * - UP:
 *     stairs lead up one level
 * - DOWN:
 *     stairs lead down one level
 * - FN, FE, FS, FW:
 *     destination character facing direction
 * - CM:
 *     destination continuous movement
 */
public class StairsIndex {

	/** Up one level. */
	public static final int UP = 2;
	/** Down one level. */
	public static final int DOWN = 3;
	/** Up one level, dest faces north/up. */
	public static final int UP_FN = 8;
	/** Up one level, dest faces east/right. */
	public static final int UP_FE = 9;
	/** Up one level, dest faces south/down. */
	public static final int UP_FS = 10;
	/** Up one level, dest faces west/left. */
	public static final int UP_FW = 11;
	/** Down one level, dest faces north/up. */
	public static final int DOWN_FN = 12;
	/** Down one level, dest faces east/right. */
	public static final int DOWN_FE = 13;
	/** Down one level, dest faces south/down. */
	public static final int DOWN_FS = 14;
	/** Down one level, dest faces west/left. */
	public static final int DOWN_FW = 15;
	/** Up one level, dest faces north/up with continuous movement. */
	public static final int UP_FN_CM = 16;
	/** Up one level, dest faces east/right with continuous movement. */
	public static final int UP_FE_CM = 17;
	/** Up one level, dest faces south/down with continuous movement. */
	public static final int UP_FS_CM = 18;
	/** Up one level, dest faces west/left with continuous movement. */
	public static final int UP_FW_CM = 19;
	/** Down one level, dest faces north/up with continuous movement. */
	public static final int DOWN_FN_CM = 20;
	/** Down one level, dest faces east/right with continuous movement. */
	public static final int DOWN_FE_CM = 21;
	/** Down one level, dest faces south/down with continuous movement. */
	public static final int DOWN_FS_CM = 22;
	/** Down one level, dest faces west/left with continuous movement. */
	public static final int DOWN_FW_CM = 23;

	/** Group of indexes for stairs leading up. */
	public static final ImmutableList<Integer> up = ImmutableList.<Integer>builder().add(
		StairsIndex.UP,
		StairsIndex.UP_FN,
		StairsIndex.UP_FE,
		StairsIndex.UP_FS,
		StairsIndex.UP_FW,
		StairsIndex.UP_FN_CM,
		StairsIndex.UP_FE_CM,
		StairsIndex.UP_FS_CM,
		StairsIndex.UP_FW_CM
	).build();

	/** Group of indexes for stairs leading down. */
	public static final ImmutableList<Integer> down = ImmutableList.<Integer>builder().add(
		StairsIndex.DOWN,
		StairsIndex.DOWN_FN,
		StairsIndex.DOWN_FE,
		StairsIndex.DOWN_FS,
		StairsIndex.DOWN_FW,
		StairsIndex.DOWN_FN_CM,
		StairsIndex.DOWN_FE_CM,
		StairsIndex.DOWN_FS_CM,
		StairsIndex.DOWN_FW_CM
	).build();


	/**
	 * Hidden constructor.
	 */
	private StairsIndex() {
		// do nothing
	}
}
