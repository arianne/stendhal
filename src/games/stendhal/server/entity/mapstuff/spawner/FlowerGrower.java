package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.server.core.engine.StendhalRPObjectFactory;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.area.FertileGround;

import marauroa.common.game.RPObject;

/**
 * Is an item producer that destroys itself when item is removed.
 * <p>
 * The standard vegetable grower would restart production cycle after removal of
 * fruit.
 * 
 * Fruit is only grown when FlowerGrower is in the same place as an entity in
 * zone that implements FertileGround
 * 
 */
public class FlowerGrower extends VegetableGrower {
	/** 5 hours for one growing step */
	private static final int GROW_TIME_TURNS = 60000;
	private static final String ITEM_NAME = "lilia";
    /** The description depends upon the ripeness of the flower grower */
	private final String[] description = {
			"You see a seed which has just been planted.",
			"Something is sprouting from the ground.",
			"A plant is growing here, and you can already see foliage.",
			"You see a plant growing a " + Grammar.fullForm(getVegetableName())
					+ ", it is nearly at full maturity.",
			"You see a fully grown " + Grammar.fullForm(getVegetableName())
					+ ", ready to pull from the ground." };

	/**
	 * Constructor for loading Flowergrower from the stored zone used by
	 * StendhalRPObjectFactory.
	 * 
	 * @see StendhalRPObjectFactory
	 * 
	 * @param object
	 *            the restored object from db
	 * @param itemname
	 *            the item to grow
	 */
	public FlowerGrower(final RPObject object, final String itemname) {
		super(object, itemname);
		meanTurnsForRegrow = GROW_TIME_TURNS;
		setMaxRipeness(4);
		store();
	}

	/**
	 * Constructor.
	 * 
	 * Default Flowergrower produces lilia.
	 */
	public FlowerGrower() {
		this(ITEM_NAME);

		store();
	}

	/**
	 * Constructor of a Flowergrower growing an item with the name specified in
	 * infostring.
	 * 
	 * @param infoString
	 *            the name of the item to produce
	 * 
	 */

	public FlowerGrower(final String infoString) {
		super(infoString);
		setMaxRipeness(4);
		meanTurnsForRegrow = GROW_TIME_TURNS;
		store();
	}

	/**
	 * Removes this from world. This method is called when the fruit of this
	 * grower is picked.
	 */
	@Override
	public void onFruitPicked(final Item picked) {
		getZone().remove(this);
		notifyWorldAboutChanges();
	}

	@Override
	protected int getRandomTurnsForRegrow() {
		return Rand.randGaussian(meanTurnsForRegrow, (int) (0.1 * meanTurnsForRegrow));
	}

    /** The description depends upon the ripeness of the flower grower */
	@Override
	public String describe() {
		if ((getRipeness() < 0) || (getRipeness() > getMaxRipeness())) {
			return super.describe();
		} else {
			return description[getRipeness()];
		}
	}

	/**
	 * Checks if this entity is on a free fertile spot.
	 * 
     * If yes, the flower can grow. Otherwise it withers and dies.
     *
	 * @return true if there is an item implementing FertileGround in the zone,
	 *         and the position of this is in its area.
	 */
	public boolean isOnFreeFertileGround() {
		if (this.getZone() == null) {
			return false;
		} else {
			
			final StendhalRPZone zone = this.getZone();
			boolean passes = false; 
			for (Entity entity : zone.getEntitiesAt(getX(), getY())) {
				if (entity instanceof FlowerGrower) {
					if (!equals(entity)) {
						// There's already something else growing here
						return false;
					}
				} else {
					if (entity instanceof FertileGround) {
						passes = true;
					}
				}
			}
			
			return passes;
		}
	}

	@Override
	protected void growNewFruit() {
		if (isOnFreeFertileGround()) {
			super.growNewFruit();
		} else {
			if (getZone() != null) {
				getZone().remove(this);
			}
		}
	}

}
