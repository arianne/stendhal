/***************************************************************************
 *                   Copyright (C) 2003-2022 - Arianne                     *
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

import java.util.Arrays;

import games.stendhal.server.maps.quests.ThePiedPiper;
import games.stendhal.server.util.Observable;
import games.stendhal.server.util.Observer;


/**
 * helper class for normal switching phase to next phase,
 * wrapper of observer around a function.
 *
 * @author yoriy
 */
public final class PhaseSwitcher implements Observer {

	private ITPPQuest myphase;

	@Override
	public void update(Observable arg0, Object arg1) {
		myphase.phaseToNextPhase(
				ThePiedPiper.getNextPhaseClass(ThePiedPiper.getPhase()),
				Arrays.asList("normal switching"));
	}

	public PhaseSwitcher(ITPPQuest phase) {
		myphase = phase;
	}

}
