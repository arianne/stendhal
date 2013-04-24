package games.stendhal.server.core.rp.economy;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.mapstuff.spawner.CreatureRespawnPoint;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Represents Stendhal's economy based on the values of the existing items.
 * 
 * - Rarity calculation is based on the script ItemRarity (author kiheru)
 * - Value is based on rarity and stats (for different classes different
 * 	 stats are applied as parameters for the value calculation)
 * 
 * @author madmetzger
 */
public class StendhalEconomy {
	
	private static StendhalEconomy instance;
	
	/**
	 * private constructor for singleton pattern
	 */
	private StendhalEconomy() {
		// nothing to do yet
	}
	
	/**
	 * @return Singleton factory method
	 */
	public static StendhalEconomy get() {
		if(instance == null) {
			instance = new StendhalEconomy();
		}
		return instance;
	}
	
	/**
	 * A map of probabilities. Probability for at least one item being dropped
	 */
	private final Map<String, Double> rarity = new HashMap<String, Double>();
	
	/**
	 * Calculate a metric for the rarity of an item
	 * 
	 * @param item
	 * @return the actual rarity value
	 */
	public double getRarity(String item) {
		final StendhalRPWorld world = SingletonRepository.getRPWorld();
		for (final IRPZone irpZone : world) {
			final StendhalRPZone zone = (StendhalRPZone) irpZone;
			for (CreatureRespawnPoint point : zone.getRespawnPointList()) {
				CountCreature creature = new CountCreature(point.getPrototypeCreature());
				processCreature(creature);
			}			
		}
		return rarity.get(item);
	}
	
	/**
	 * Calculate the value of an item for the economy
	 * 
	 * @param item
	 * @return the item's value
	 */
	public double getValue(String item) {
		return 1;
	}
	
	/**
	 * Process the drop probabilities of a creature.
	 * 
	 * @param creature the creature to be processed
	 */
	private void processCreature(CountCreature creature) {
		for (DropItem item : creature.getDropList()) {
			/*
			 * Probability for a creature with that item spawning at a given
			 * turn
			 */
			double probability = item.probability / 100 / creature.getRespawnTime();
			addToProbability(item.name, probability);
		}
	}
	
	/**
	 * Add drop probability to items old probability.
	 * 
	 * @param item the item to add to
	 * @param probability additional probability
	 */
	private void addToProbability(String item, double probability) {
		double old = 0.0;
		Double oldProbability = rarity.get(item);
		if (oldProbability != null) {
			old = oldProbability;
		}
		
		// Calculate through complements to add them up properly
		double newProb = 1 - (1 - old) * (1 - probability);
		rarity.put(item, newProb);
	}
	
	/**
	 * A creature for counting item probabilities. Needed for gaining
	 * access to the protected fields of <code>Creature</code>
	 */
	private static class CountCreature extends Creature {
		public CountCreature(Creature copy) {
			super(copy);
		}
		
		/**
		 * Get list of droppable items
		 * 
		 * @return list of droppable items
		 */
		public List<DropItem> getDropList() {
			return dropsItems;
		}
	}
	
}
