/***************************************************************************
 *                  Copyright (C) 2003-2022 - Arianne                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.server.maps.quests.tutorial;

import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.SpeakerNPC;


public class PrepareStepQuests extends TutorialStep {

	public void init(final String pname) {
		final ChatCondition onQuestsStep = new QuestInStateCondition(SLOT, 0, ST_QUESTS);
	}
}
