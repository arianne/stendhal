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
package games.stendhal.server.maps.quests.mithrilcloak;

class MithrilCloakQuestInfo {
	private static final String QUEST_SLOT = "mithril_cloak";

	private static final String MITHRIL_SHIELD_SLOT = "mithrilshield_quest";

	private static final String FABRIC = "mithril fabric";

	public String getQuestSlot() {
		return QUEST_SLOT;
	}

	public String getShieldQuestSlot() {
		return MITHRIL_SHIELD_SLOT;
	}

	public String getFabricName() {
		return FABRIC;
	}

}
