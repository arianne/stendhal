package games.stendhal.server.entity.mapstuff.spawner;

import games.stendhal.common.Grammar;
import games.stendhal.common.Rand;
import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.engine.StendhalRPObjectFactory;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.mapstuff.area.FertileGround;

import java.util.List;

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

	/* 50 hours for one growing step */
	private static final int REGROWTURN_50_HOURS = 60000;
	private static final String ITEM_NAME = "lilia";
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
		meanTurnsForRegrow = REGROWTURN_50_HOURS;
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
		meanTurnsForRegrow = REGROWTURN_50_HOURS;
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

	@Override
	public String describe() {
		if ((getRipeness() < 0) || (getRipeness() > getMaxRipeness())) {
			return super.describe();
		} else {
			return description[getRipeness()];
		}
	}

	/**
	 * Checks if this entity is on a fertile spot.
	 * 
	 * @return true if there is an item implementing FertileGround in the zone,
	 *         and the position of this is in its area.
	 */
	public boolean isOnFertileGround() {
		if (this.getZone() == null) {
			return false;
		} else {
			final StendhalRPZone zone = this.getZone();
			final List<Entity> ferts = zone
					.getFilteredEntities(new FilterCriteria<Entity>() {

						public boolean passes(final Entity o) {
							if (o instanceof FertileGround) {
								return o.getArea().contains(getX(), getY());
							}
							return false;
						}
					});

			return !ferts.isEmpty();

		}
	}

	@Override
	protected void growNewFruit() {
		if (isOnFertileGround()) {
			super.growNewFruit();
		} else {
			if (getZone() != null) {
				getZone().remove(this);
				
				notifyWorldAboutChanges();
			}

		}
	}

}
