package games.stendhal.server.maps.quests.houses;

class HouseChatAction {

	/** Cost to buy spare keys. */
	static final int COST_OF_SPARE_KEY = 1000;
	protected String questslot;

	protected HouseChatAction(final String questslot) {
		this.questslot = questslot;
	}
}
