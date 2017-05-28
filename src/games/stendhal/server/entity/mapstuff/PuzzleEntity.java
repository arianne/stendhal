/***************************************************************************
 *                   (C) Copyright 2016-2016 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.entity.mapstuff;

import games.stendhal.server.entity.mapstuff.puzzle.PuzzleBuildingBlock;

/**
 * interface for entities which act as PuzzleBuildingBlocks.
 *
 * @author hendrik
 */
public interface PuzzleEntity {

	/**
	 * invoked when the expressions of a PuzzleBuildingBlock have been updated.
	 * It is ensured that there was an actual change.
	 */
	public void puzzleExpressionsUpdated();

	/**
	 * sets the PuzzleBuildingBlock
	 *
	 * @param buildingBlock PuzzleBuildingBlock
	 */
	public void setPuzzleBuildingBlock(PuzzleBuildingBlock buildingBlock);
}
