package games.stendhal.server.maps.semos.dungeon;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An orcish NPC who buys sheep from players.
 * You get the weight of the sheep * 15 in gold coins.
 */
public class SheepBuyerNPC implements ZoneConfigurator {

	public void configureZone(final StendhalRPZone zone,
			final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Tor'Koom") {
			@Override
			public void createDialog() {
				// TODO: The code is similar to Sato's SheepBuyerBehaviour.
				// Only the phrasing is different, and Sato doesn't buy
				// skinny sheep. Get rid of the code duplication.
				addGreeting(getName() + " see hooman!");
				addJob(getName() + " is buy real cheep from hoomans.");
				addHelp(getName() + " buy sheep! Sell me sheep! "
						+ getName()	+ " is hungry!");
				addGoodbye("*grunt*");
			}

			/* (non-Javadoc)
			 * @see games.stendhal.server.entity.npc.SpeakerNPC#createPath()
			 */
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(67, 13));
				nodes.add(new Node(59, 13));
				nodes.add(new Node(59, 17));
				nodes.add(new Node(67, 17));
				setPath(new FixedPath(nodes, true));
			}
			
		};
		final Map<String, Integer> buyitems = new HashMap<String, Integer>();
		buyitems.put("sheep", 1500);
		new BuyerAdder().add(npc, new SheepBuyerBehaviour(buyitems), true);
		npc.setPosition(67, 13);
		npc.setEntityClass("orcbuyernpc");
		zone.add(npc);
	}
	
	class SheepBuyerBehaviour extends BuyerBehaviour {
		SheepBuyerBehaviour(final Map<String, Integer> items) {
			super(items);
		}

		@Override
		public int getCharge(final Player player) {
			if (player.hasSheep()) {
				final Sheep sheep = player.getSheep();
				return Math.round(getUnitPrice(chosenItemName) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
			} else {
				return 0;
			}
		}

		@Override
		public boolean transactAgreedDeal(final EventRaiser seller, final Player player) {
			// amount is currently ignored.
			final Sheep sheep = player.getSheep();

			if (sheep != null) {
				if (seller.getEntity().squaredDistance(sheep) > 5 * 5) {
					seller.say("*drool* Sheep flesh! Bring da sheep here!");
				} else {
					seller.say("Mmm... Is look yummy! Here, you take dis gold!");
					payPlayer(player);

					player.removeSheep(sheep);
					player.notifyWorldAboutChanges();

					sheep.getZone().remove(sheep);

					return true;
				}
			} else {
				seller.say("Whut? Is not unnerstand... Maybe I hit you until you make sense!");
			}
			return false;
		}
	}
}