package games.stendhal.server.maps.quests;

import games.stendhal.server.maps.quests.houses.HouseBuyingMain;

public class HouseBuying extends AbstractQuest {
	private static final String QUEST_SLOT = "house";

	@Override
	public void init(final String name) {
		super.init(name);
	}
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
}
