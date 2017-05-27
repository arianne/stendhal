/* $Id$ */
package games.stendhal.server.script;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.creature.impl.DropItem;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;
import marauroa.common.Pair;

/**
 * Runs the calculator for best creature of moneys amount (converting all to moneys from their corpse).
 *
 * @author yoriy
 */
public class CreaturesRatioCalculator extends ScriptImpl {

	private final Logger logger = Logger.getLogger(this.getClass());

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

	Collection<Creature> creatures = SingletonRepository.getEntityManager().getCreatures();
	List<Pair<String, BuyerBehaviour>> buyers = SingletonRepository.getMerchantsRegister().getBuyers();
	List<Pair<Double, String>> listEfficiency = new LinkedList<Pair<Double, String>>();

	/**
	 * Search for max price of item.
	 * @param item
	 *
	 * @return maximum price
	 */
	private Integer getMaxPrice(String item) {
		Integer maxPrice = 0;
		Integer tempPrice;

		if(item.equals("money")) {
			return(1);
		}

		for (Pair<String, BuyerBehaviour> c:buyers) {
			if(c.second().hasItem(item)) {
				tempPrice = c.second().getUnitPrice(item);
				if (tempPrice > maxPrice) {
					maxPrice = tempPrice;
				}
			}
		}

		return(maxPrice);
	}


	private Double getRatio(Creature creature) {
		Double ratio = 0.0;
		CountCreature c = new CountCreature(creature);
		for (DropItem i:c.getDropList()) {
			Integer price = getMaxPrice(i.name);
			Double prob = i.probability;
			Double amount = (i.max + i.min) / 2.0;

			ratio = ratio + price * amount * prob / 100;
		}
		logger.info("creature "+creature.getName()+": "+ratio);
		return ratio;
	}

	private void fillList() {
		List<Pair<Double,String>> tempList = new LinkedList<Pair<Double,String>>();
		for (Creature c:creatures) {

			tempList.add(new Pair<Double,String>(getRatio(c), c.getName()));
		}
		logger.info("list filled.");

		listEfficiency.clear();
		int s = tempList.size();

		for (int j=0; j<s; j++) {
			Integer index = -1;
			Double d = 0.0;
			for(int i=0; i<s; i++) {
				if (tempList.get(i).first()>d) {
					index = i;
					d = tempList.get(i).first();
				}
			}
			if (index!=-1) {
				listEfficiency.add(new Pair<Double,String>(
									tempList.get(index).first(),
									tempList.get(index).second()
									));
				tempList.set(index, new Pair<Double,String>(0.0, ""));
			}
		}
		logger.info("list sorted.");
	}

	@Override
	public void execute(final Player admin, final List<String> args) {
		fillList();
		final StringBuilder r=new StringBuilder();
		int range;
		try {
			if(args.isEmpty()) {
				range = 10;
				r.append("list range value omitted, used 10.\n");
			} else {
				range = Integer.valueOf(args.get(0));
				if((range < 1) || (range > creatures.size()-1)) {
					r.append("too big or too small list range value, used 10.\n");
					range = 10;
				}
			}
		} catch (NumberFormatException e) {
			r.append("cant read properly list range value, used 10.\n");
			range = 10;
		}
		r.append("count results:\n");
		for (int i=0; i<range; i++) {
			r.append((i+1)+". "+listEfficiency.get(i).second()+" ("+listEfficiency.get(i).first()+ " )\n");
		}
		admin.sendPrivateText(r.toString());
	}
}
