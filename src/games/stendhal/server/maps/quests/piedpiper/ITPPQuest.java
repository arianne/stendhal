/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.piedpiper;

import java.util.List;

public interface ITPPQuest extends ITPPQuestConstants {

	/**
	 * Function will change phase to next phase.
	 *
	 * @param nextPhase
	 * @param comments
	 */
	void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments);

	/**
	 * function will reset quest state to default phase (INACTIVE).
	 *
	 * @param comments
	 */
	void phaseToDefaultPhase(List<String> comments);

	/**
	 * function return shout message when quest going to next phase
	 * @return next phase message
	 */
	String getSwitchingToNextPhaseMessage();

	/**
	 * function return shout message when quest going to default phase
	 * @return default phase message
	 */
	String getSwitchingToDefPhaseMessage();

	/**
	 * return minimal timeout period for quest phase
	 * @return minimum timeout
	 */
	int getMinTimeOut();

	/**
	 * return maximal timeout period for quest phase
	 * @return maximum timeout
	 */
	int getMaxTimeOut();

	/**
	 * Get the current phase.
	 *
	 * @return quest phase
	 */
	TPP_Phase getPhase();

	/**
	 * function will perform necessary actions at phase start
	 */
	void prepare();
}
