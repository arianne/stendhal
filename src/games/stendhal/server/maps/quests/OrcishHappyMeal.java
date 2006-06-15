package games.stendhal.server.maps.quests;

import games.stendhal.server.*;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.*;

import marauroa.common.game.IRPZone;

/** 
 * QUEST: Orcish Happy Meal
 * PARTICIPANTS: 
 * - Nishiya 
 * - Tor'Koom 
 * 
 * STEPS: 
 * - Buy a sheep from Nishiya in Village 
 * - Grow sheep in plains 
 * - Sell sheep to Tor'Koom in Dungeon 
 * 
 * REWARD: 
 * - You get the weight of the sheep * 50 in gold coins.
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class OrcishHappyMeal implements IQuest {
	public OrcishHappyMeal(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"-4_semos_dungeon"));
		NPCList npcs = NPCList.get();
		
		// Nishiya's part has already been defined in the quest SheepGrowing.

		SpeakerNPC npc = new SpeakerNPC("Tor'Koom") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(67, 12));
				nodes.add(new Path.Node(59, 12));
				nodes.add(new Path.Node(59, 16));
				nodes.add(new Path.Node(67, 16));
				setPath(nodes, true);
			}

			protected void createDialog() {
				// TODO: The code is identical to Sato's SheepBuyerBehaviour,
				// except that the phrasing is different. Unite them.
				class SheepBuyerBehaviour extends BuyerBehaviour {
					SheepBuyerBehaviour(StendhalRPWorld world, Map<String, Integer> items) {
						super(world, items);
					}

					@Override
					public int getCharge(Player player) {
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) world.get(player.getSheep());
							return Math.round(getUnitPrice(chosenItem) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
						} else {
							return 0;
						}
					}

					@Override
					public boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
						// amount is currently ignored.
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) world.get(player.getSheep());
							if (seller.distance(sheep) > 5 * 5) {
								seller.say("*drool* Sheep flesh! Bring da sheep here!");
							} else {
								say("*LOVELY*. Take dis money!");

								rp.removeNPC(sheep);
								world.remove(sheep.getID());
								player.removeSheep(sheep);

								payPlayer(player);

								world.modify(player);
								return true;
							}
						} else {
							seller.say("Sell what? Don't cheat me or I might 'ave to hurt you!");
						}
						return false;
					}
				}

				Map<String, Integer> buyitems = new HashMap<String, Integer>();
				buyitems.put("sheep", 1500);

				addGreeting();
				addJob(getName() + " du buy cheepz frrom humanz.");
				addHelp(getName()
						+ " buy sheep! Sell me sheep! " + getName()
						+ " is hungry!");
				addBuyer(new SheepBuyerBehaviour(world, buyitems));
				addGoodbye();
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "orcbuyernpc");
		npc.set(67, 12);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}