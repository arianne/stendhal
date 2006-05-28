package games.stendhal.server.maps.quests;

import marauroa.common.game.IRPZone;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.StendhalRPRuleProcessor;

import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.NPCList;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.pathfinder.Path;


import java.util.*;


/**
 * QUEST: Grow sheep
 * PARTICIPANTS:
 * - Nishiya
 * - Sato
 *
 * STEPS:
 * - Buy a sheep from Nishiya in Village
 * - Grow sheep in plains
 * - Sell sheep to Sato in City
 *
 * REWARD:
 * - You get the weight of the sheep * 5 in gold coins.
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class SheepGrowing implements IQuest {
	public SheepGrowing(StendhalRPWorld world, StendhalRPRuleProcessor rules) {
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID(
				"0_semos_village_w"));
		NPCList npcs = NPCList.get();

		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(26);
		sign.sety(41);
		sign.setText("Talk to Nishiya to buy a sheep!.|He has the best prices for miles.");
		zone.add(sign);

		SpeakerNPC npc = new SpeakerNPC("Nishiya") {
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(33, 44));
				nodes.add(new Path.Node(33, 42));
				nodes.add(new Path.Node(23, 42));
				nodes.add(new Path.Node(23, 44));
				setPath(nodes, true);
			}

			protected void createDialog() {
				class SheepSellerBehaviour extends Behaviours.SellerBehaviour {
					SheepSellerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					@Override
					protected boolean transactAgreedSale(SpeakerNPC seller, Player player) {
						if (!player.hasSheep()) {
							if (! player.drop("money", getCharge(player))) {
								seller.say("A real pity! You don't have enough money!");
								return false;
							}
							seller.say("Congratulations! Here is your sheep! Keep it safe!");
							StendhalRPZone zone = (StendhalRPZone) world
									.getRPZone(seller.getID());

							Sheep sheep = new Sheep(player);
							zone.assignRPObjectID(sheep);

							sheep.setx(seller.getx());
							sheep.sety(seller.gety() + 2);

							world.add(sheep);

							player.setSheep(sheep);
							world.modify(player);

							return true;
						} else {
							say("You already have a sheep. Take care of it first!");
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("sheep", 30);

				Behaviours.addGreeting(this);
				Behaviours.addJob(this, "I work as a sheep seller.");
				Behaviours.addHelp(
						this,
						"I just sell sheeps. Just tell me #buy #sheep and I will sell you a nice sheep! Ask me how to take #care of her, how to #travel with her, how to #sell her or how to #own a wild sheep.");
				Behaviours.addGoodbye(this);
				Behaviours.addSeller(this, new SheepSellerBehaviour(items));
				Behaviours.addReply(
						this,
						"care",
						"To feed your sheep just stand near bushes and she'll eat red cherries from them. She won't lose weight neither die from starvation. Right-click on her and choose LOOK to see her weight.");
				Behaviours.addReply(
						this,
						"travel",
						"Sometimes you'll have to say #sheep to call her because you have to be close to her to change between zones. Be patient and don't right-click on YOU and choose LEAVE SHEEP: she would never do that with you!");
				Behaviours.addReply(
						this,
						"sell",
						"When your sheep weighs 100 (same as the number of cherries eaten) sell her to Sato, but only then or surely you'll feel cheated with his buying price.");
				Behaviours.addReply(
						this,
						"own",
						"If you happen to see a wild or better unfairly abandoned sheep, you can own her by right-clicking on HER and choosing OWN.");
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "sellernpc");
		npc.set(33, 44);
		npc.initHP(100);
		zone.addNPC(npc);

		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_semos_city"));

		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(43);
		sign.sety(40);
		sign.setText("Talk to Sato to sell your sheep!.|He probably won't give you a fair price but this is a small village...|The price he will offer you depends on the weight of your sheep.");
		zone.add(sign);

		npc = new SpeakerNPC("Sato") {
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

			protected void createDialog() {
				class SheepBuyerBehaviour extends Behaviours.BuyerBehaviour {
					SheepBuyerBehaviour(Map<String, Integer> items) {
						super(items);
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
					public boolean onBuy(SpeakerNPC seller, Player player) {
						// amount is currently ignored.
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) world.get(player.getSheep());
							if (seller.distance(sheep) > 5 * 5) {
								seller.say("Ya sheep is too far away. I can't see it from here. Go and bring it here.");
							} else {
								say("Thanks! Here is your money.");
								payPlayer(player);

								rp.removeNPC(sheep);
								world.remove(sheep.getID());
								player.removeSheep(sheep);

								world.modify(player);
								return true;
							}
						} else {
							seller.say("You ain't got a sheep!! What game you trying to play, "
									   + player.get("name") + "?");
						}

						return false;
					}
				}

				Map<String, Integer> buyitems = new HashMap<String, Integer>();
				buyitems.put("sheep", 150);

				Behaviours.addGreeting(this);
				Behaviours.addJob(this,
						"I work as the main Semos' sheep buyer.");
				Behaviours
						.addHelp(this,
								"I just buy sheeps. Just tell me sell sheep and I will buy your nice sheep!.");
				Behaviours.addBuyer(this, new SheepBuyerBehaviour(buyitems));
				Behaviours.addGoodbye(this);
			}
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "buyernpc");
		npc.set(40, 44);
		npc.initHP(100);
		zone.addNPC(npc);
	}
}
