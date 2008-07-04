package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.server.entity.item.Item;
import marauroa.common.game.RPObject;

public class FlowerGrower extends VegetableGrower {

	private static final String ITEM_NAME = "flower";
	private String[] description = { "0", "1", "2", "3", "4" };

	public FlowerGrower(RPObject object) {
		super(object, ITEM_NAME);
		setMaxRipeness(4);
		setVegetableName("rose");

	}

	public FlowerGrower() {
		super(ITEM_NAME);
		setMaxRipeness(4);
		setVegetableName("rose");
	}

	public FlowerGrower(String infoString) {
		super(infoString);
		setMaxRipeness(4);
	}

	@Override
	public void onFruitPicked(Item picked) {
		getZone().remove(this);
		notifyWorldAboutChanges();
	}

	@Override
	protected int getRandomTurnsForRegrow() {
		return 3;
	}

	@Override
	public String describe() {
		if (getRipeness() < 0 || getRipeness() > getMaxRipeness()) {
			return super.describe();
		} else {
			return description[getRipeness()];
		}
	}

}
