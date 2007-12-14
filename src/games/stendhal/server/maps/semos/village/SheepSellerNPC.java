package games.stendhal.server.maps.semos.village;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.Sheep;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SheepSellerNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 30;

	/**
	 * Configure a zone.
	 * 
	 * @param zone
	 *            The zone to be configured.
	 * @param attributes
	 *            Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
			Map<String, String> attributes) {
		buildSemosVillageArea(zone);
	}

	private void buildSemosVillageArea(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Nishiya") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(33, 45));
				nodes.add(new Node(33, 43));
				nodes.add(new Node(23, 43));
				nodes.add(new Node(23, 45));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class SheepSellerBehaviour extends SellerBehaviour {
					SheepSellerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					@Override
					public boolean transactAgreedDeal(SpeakerNPC seller,
							Player player) {
						if (getAmount() > 1) {
							seller.say("Hmm... I just don't think you're cut out for taking care of a whole flock of sheep at once.");
							return false;
						} else if (!player.hasSheep()) {
							if (!player.drop("money", getCharge(player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here you go, a nice fluffy little sheep! Take good care of it, now...");

							Sheep sheep = new Sheep(player);

							sheep.setPosition(seller.getX(), seller.getY() + 1);

							seller.getZone().add(sheep);

							player.setSheep(sheep);
							player.notifyWorldAboutChanges();

							return true;
						} else {
							say("Well, why don't you make sure you can look after that sheep you already have first?");
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("sheep", BUYING_PRICE);

				addGreeting();
				addJob("I work as a sheep seller.");
				addHelp("I sell sheep. To buy one, just tell me you want to #buy #sheep. If you're new to this business, I can tell you how to #travel with her, take #care of her, and finally give you tips on when to #sell her. If you find any wild sheep, incidentally, you can make them your #own.");
				addGoodbye();
				new SellerAdder().addSeller(this, new SheepSellerBehaviour(
						items));
				addReply(
						"care",
						"My sheep especially love to eat the red berries that grow on these little bushes. Just stand near one and your sheep will walk over to start eating. You can right-click and choose LOOK at any time, to check up on her weight; she will gain one unit of weight for every cherry she eats.");
				addReply(
						"travel",
						"You'll need your sheep to be close by in order for her to follow you when you change zones; you can say #sheep to call her if she's not paying attention. If you decide to abandon her instead, you can right-click on yourself and select LEAVE SHEEP; but frankly I think that sort of behaviour is disgraceful.");
				addReply(
						"sell",
						"Once you've gotten your sheep up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.");
				addReply(
						"own",
						"If you find any wild or abandoned sheep, you can right-click on them and select OWN to tame them. Sheep need to be looked after!");
			}
		};

		npc.setEntityClass("sellernpc");
		npc.setPosition(33, 45);
		npc.initHP(100);
		zone.add(npc);
	}
}
