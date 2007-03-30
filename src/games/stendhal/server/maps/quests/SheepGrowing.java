package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.Sign;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.Translate;

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
	
	private static final int BUYING_PRICE = 30;

	@Override
	public void addToWorld() {
		super.addToWorld();
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
				"0_semos_village_w"));
		NPCList npcs = NPCList.get();

		Sign sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(26);
		sign.setY(41);
		sign.setText(Translate._("NISHIYA'S SHEEP FARM\n\nBuy sheep from Nishiya to get the best prices!"));
		zone.add(sign);

		SpeakerNPC npc = new SpeakerNPC(Translate._("Nishiya")) {
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
							seller.say(Translate._("Hmm... I just don't think you're cut out for taking care of a whole flock of sheep at once."));
							return false;
						} else if (!player.hasSheep()) {
							if (! player.drop("money", getCharge(player))) {
								seller.say(Translate._("You don't seem to have enough money."));
								return false;
							}
							seller.say(Translate._("Here you go, a nice fluffy little sheep! Take good care of it, now..."));
							StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
									.getRPZone(seller.getID());

							Sheep sheep = new Sheep(player);
							zone.assignRPObjectID(sheep);

							sheep.setX(seller.getX());
							sheep.setY(seller.getY() + 2);

							StendhalRPWorld.get().add(sheep);

							player.setSheep(sheep);
							player.notifyWorldAboutChanges();

							return true;
						} else {
							say(Translate._("Well, why don't you make sure you can look after that sheep you already have first?"));
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put(Translate._("sheep"), BUYING_PRICE);

				addGreeting();
				addJob(Translate._("I work as a sheep seller."));
				addHelp(Translate._("I sell sheep. To buy one, just tell me you want to #buy #sheep. If you're new to this business, I can tell you how to #travel with her, take #care of her, and finally give you tips on when to #sell her. If you find any wild sheep, incidentally, you can make them your #own."));
				addGoodbye();
				addSeller(new SheepSellerBehaviour(items));
				addReply(Translate._("care"),
						Translate._("My sheep especially love to eat the red berries that grow on these little bushes. Just stand near one and your sheep will walk over to start eating. You can right-click and choose LOOK at any time, to check up on her weight; she will gain one unit of weight for every cherry she eats."));
				addReply(Translate._("travel"),
						Translate._("You'll need your sheep to be close by in order for her to follow you when you change zones; you can say #sheep to call her if she's not paying attention. If you decide to abandon her instead, you can right-click on yourself and select LEAVE SHEEP; but frankly I think that sort of behaviour is disgraceful."));
				addReply(Translate._("sell"),
						Translate._("Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you."));
				addReply(Translate._("own"),
						Translate._("If you find any wild or abandoned sheep, you can right-click on them and select OWN to tame them. Sheep need to be looked after!"));
			}
		};
		npcs.add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "sellernpc");
		npc.set(33, 44);
		npc.initHP(100);
		zone.add(npc);

		zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("0_semos_city"));

		sign = new Sign();
		zone.assignRPObjectID(sign);
		sign.setX(43);
		sign.setY(40);
		sign.setText(Translate._("Talk to Sato about selling your sheep. His prices aren't very good, but unfortunately it's a buyer's market... He pays more for bigger sheep; try to get a weight of at least 100."));
		zone.add(sign);

		npc = new SpeakerNPC(Translate._("Sato")) {
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
							} else if (getValue(sheep) < BUYING_PRICE) {
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
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "buyernpc");
		npc.set(40, 44);
		npc.initHP(100);
		zone.add(npc);
	}
}
