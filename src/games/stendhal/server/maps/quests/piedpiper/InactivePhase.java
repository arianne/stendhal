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
import java.util.Map;


public class InactivePhase extends TPPQuest {
	
	private int minPhaseChangeTime;
	private int maxPhaseChangeTime;
	
	public InactivePhase(Map<String, Integer> timings) {
		super(timings);
		minPhaseChangeTime=timings.get(INACTIVE_TIME_MIN);
		maxPhaseChangeTime=timings.get(INACTIVE_TIME_MAX);		
	}


	public int getMinTimeOut() {
		return minPhaseChangeTime;
	}
	

	public int getMaxTimeOut() {
		return maxPhaseChangeTime;
	}
	

	

	public void phaseToDefaultPhase(List<String> comments) {
		// not used		
	}


	public void phaseToNextPhase(ITPPQuest nextPhase, List<String> comments) {
		super.phaseToNextPhase(nextPhase, comments);
	}
	

	public void prepare() {

	}
	

	public TPP_Phase getPhase() {
		return TPP_Phase.TPP_INACTIVE;
	}
	
}
