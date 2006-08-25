package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.Path;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;


/**
 * QUEST: Grow sheep
 * PARTICIPANTS:
 * - Nishiya
 * - Sato
 *
 * STEPS:
 * - Buy a sheep from Nishiya in Semos village
 * - Grow sheep in plains
 * - Sell sheep to Sato in Semos city
 *
 * REWARD:
 * - You get the weight of the sheep * 5 in gold coins.
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class SheepGrowing extends AbstractQuest {

	@Override
	public void addToWorld() {
		super.addToWorld();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"0_semos_village_w"));
		NPCList npcs = NPCList.get();

		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(26);
		sign.sety(41);
		sign.setText("Talk to Nishiya to buy a sheep!\nHe has the best prices for miles.");
		zone.add(sign);

		SpeakerNPC npc = new SpeakerNPC("Nishiya") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(33, 44));
				nodes.add(new Path.Node(33, 42));
				nodes.add(new Path.Node(23, 42));
				nodes.add(new Path.Node(23, 44));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				class SheepSellerBehaviour extends SellerBehaviour {
					SheepSellerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					@Override
					protected boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
						if (amount > 1) {
							seller.say("Sorry! You don't look like someone who is able to take care of several sheep at a time.");
							return false;
						} else if (!player.hasSheep()) {
							if (! player.drop("money", getCharge(player))) {
								seller.say("A real pity! You don't have enough money!");
								return false;
							}
							seller.say("Congratulations! Here is your sheep! Keep it safe!");
							StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
									.getRPZone(seller.getID());

							Sheep sheep = new Sheep(player);
							zone.assignRPObjectID(sheep);

							sheep.setx(seller.getx());
							sheep.sety(seller.gety() + 2);

							StendhalRPWorld.get().add(sheep);

							player.setSheep(sheep);
							player.notifyWorldAboutChanges();

							return true;
						} else {
							say("You already have a sheep. Take care of it first!");
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("sheep", 30);

				addGreeting();
				addJob("I work as a sheep seller.");
				addHelp("I just sell sheeps. Just tell me #buy #sheep and I will sell you a nice sheep! Ask me how to take #care of her, how to #travel with her, how to #sell her or how to #own a wild sheep.");
				addGoodbye();
				addSeller(new SheepSellerBehaviour(items));
				addReply("care",
						"To feed your sheep just stand near bushes and she'll eat red cherries from them. She won't lose weight neither die from starvation. Right-click on her and choose LOOK to see her weight.");
				addReply("travel",
						"Sometimes you'll have to say #sheep to call her because you have to be close to her to change between zones. Be patient and don't right-click on YOU and choose LEAVE SHEEP: she would never do that with you!");
				addReply("sell",
						"When your sheep weighs 100 (same as the number of cherries eaten) sell her to Sato, but only then or surely you'll feel cheated with his buying price.");
				addReply("own",
						"If you happen to see a wild or better unfairly abandoned sheep, you can own her by right-clicking on HER and choosing OWN.");
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "sellernpc");
		npc.set(33, 44);
		npc.initHP(100);
		zone.addNPC(npc);

		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_city"));

		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setx(43);
		sign.sety(40);
		sign.setText("Talk to Sato to sell your sheep!\nHe probably won't give you a fair price but this is a small village...\nThe price he will offer you depends on the weight of your sheep.");
		zone.add(sign);

		npc = new SpeakerNPC("Sato") {
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

					@Override
					public int getCharge(Player player) {
						if (player.hasSheep()) {
							Sheep sheep = (Sheep) StendhalRPWorld.get().get(player.getSheep());
							return Math.round(getUnitPrice(chosenItem) * ((float) sheep.getWeight() / (float) sheep.MAX_WEIGHT));
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
								seller.say("Ya sheep is too far away. I can't see it from here. Go and bring it here.");
							} else {
								say("Thanks! Here is your money.");
								payPlayer(player);

								StendhalRPRuleProcessor.get().removeNPC(sheep);
								StendhalRPWorld.get().remove(sheep.getID());
								player.removeSheep(sheep);

								player.notifyWorldAboutChanges();
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

				addGreeting();
				addJob("I work as the main Semos' sheep buyer.");
				addHelp("I just buy sheeps. Just tell me sell sheep and I will buy your nice sheep!");
				addBuyer(new SheepBuyerBehaviour(buyitems));
				addGoodbye();
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
