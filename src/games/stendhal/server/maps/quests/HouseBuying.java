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
package games.stendhal.server.maps.quests;

import games.stendhal.server.maps.quests.houses.HouseBuyingMain;

public class HouseBuying extends AbstractQuest {
	private static final String QUEST_SLOT = "house";


	@Override
	public String getSlotName() {
		return QUEST_SLOT;
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		HouseBuyingMain quest = new HouseBuyingMain();
		quest.addToWorld();
	}
	@Override
	public String getName() {
		return "HouseBuying";
	}
	
	@Override
	public int getMinLevel() {
		return 50;
	}
}
