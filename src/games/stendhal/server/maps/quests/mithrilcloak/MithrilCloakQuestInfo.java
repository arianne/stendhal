package games.stendhal.server.maps.quests.mithrilcloak;

import games.stendhal.server.entity.player.Player;

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
