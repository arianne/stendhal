package games.stendhal.server.maps.semos.city;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.maps.semos.village.SheepSellerNPC;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.Translate;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * An NPC who buys sheep from players.
 * You get the weight of the sheep * 5 in gold coins.
 */
public class SheepBuyerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosCityAreaCarmen(zone);
	}

	private void buildSemosCityAreaCarmen(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC(Translate._("Sato")) {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(40, 44));
				nodes.add(new Path.Node(58, 44));
				nodes.add(new Path.Node(58, 21));
				nodes.add(new Path.Node(39, 21));
				nodes.add(new Path.Node(39, 14));
				nodes.add(new Path.Node(23, 14));
				nodes.add(new Path.Node(23, 21));
				nodes.add(new Path.Node(23, 44));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				class SheepBuyerBehaviour extends BuyerBehaviour {
					SheepBuyerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					private int getValue(Sheep sheep) {
						return Math.round(getUnitPrice(chosenItem) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
					}
					
					@Override
					public int getCharge(Player player) {
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
							return getValue(sheep);
						} else {
							return 0;
						}
					}
					
					@Override
					public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
						// amount is currently ignored.
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
							if (seller.squaredDistance(sheep) > 5 * 5) {
								seller.say(Translate._("I can't see that sheep from here! Bring it over so I can assess it properly."));
							} else if (getValue(sheep) < SheepSellerNPC.BUYING_PRICE) {
								// prevent newbies from selling their sheep too early
								say(Translate._("Nah, that sheep looks too skinny. Feed it with red berries, and come back when it has become fatter."));
							} else {
								say(Translate._("Thanks! Here is your money."));
								payPlayer(player);

								StendhalRPRuleProcessor.get().removeNPC(sheep);
								StendhalRPWorld.get().remove(sheep.getID());
								player.removeSheep(sheep);

								player.notifyWorldAboutChanges();
								return true;
							}
						} else {
							seller.say(Translate._("You don't have any sheep, $1! What are you trying to pull?", player.get("name")));
						}

						return false;
					}
				}

				Map<String, Integer> buyitems = new HashMap<String, Integer>();
				buyitems.put(Translate._("sheep"), 150);

				addGreeting();
				addJob(Translate._("I buy sheep here in Semos, then I send them up to Ados where they are exported."));
				addHelp(Translate._("I purchase sheep, at what I think is a fairly reasonable price. Just say if you want to #sell #sheep, and I will set up a deal!"));
				addBuyer(new SheepBuyerBehaviour(buyitems));
				addGoodbye();
			}
		};
		NPCList.get().add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "buyernpc");
		npc.set(40, 44);
		npc.initHP(100);
		zone.add(npc);
	}
}
