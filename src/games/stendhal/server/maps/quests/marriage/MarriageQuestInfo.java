package games.stendhal.server.maps.quests.marriage;

import games.stendhal.server.entity.player.Player;

class MarriageQuestInfo {
	private static final String QUEST_SLOT = "marriage";

	// The spouse's name is stored in one of the player's quest slots.
	// This is necessary to disallow polygamy.
	private final String SPOUSE_QUEST_SLOT = "spouse";
	
	public String getQuestSlot() {
		return QUEST_SLOT;
	}
	
	public String getSpouseQuestSlot() {
		return SPOUSE_QUEST_SLOT;
	}
	
	public boolean isMarried(final Player player) {
		return player.hasQuest(SPOUSE_QUEST_SLOT);
	} 
	
    public boolean isEngaged(final Player player) {
        return (player.hasQuest(QUEST_SLOT) && (player.getQuest(QUEST_SLOT).startsWith("engaged") || player.getQuest(QUEST_SLOT).startsWith("forging;")));
    }

}
