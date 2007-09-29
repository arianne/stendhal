package games.stendhal.server.maps.semos.house;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CatSellerNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 1;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosVillageArea(zone);
	}

	private void buildSemosVillageArea(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Felina") {
			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(7, 8));
				nodes.add(new Node(9, 8));
				nodes.add(new Node(9, 9));
				nodes.add(new Node(7, 9));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				class CatSellerBehaviour extends SellerBehaviour {
					CatSellerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					@Override
					protected boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
						if (getAmount() > 1) {
							seller.say("Hmm... I just don't think you're cut out for taking care of more than one cat at once.");
							return false;
						} else if (!player.hasPet()) {
							if (!player.drop("money", getCharge(player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here you go, a cute little kitten! Take good care of it, now...");

							Cat cat = new Cat(player);

							cat.setPosition(seller.getX(), seller.getY() + 1);

							StendhalRPZone zone = seller.getZone();
							zone.add(cat);

							player.setPet(cat);
							player.notifyWorldAboutChanges();

							return true;
						} else {
							say("Well, why don't you make sure you can look after that cat you already have first?");
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("cat", BUYING_PRICE);

				addGreeting();
				addJob("I sell cats");
				addHelp("I sell cat. To buy one, just tell me you want to #buy #cat. If you're new to this business, I can tell you how to #travel with her, take #care of her, and finally give you tips on when to #sell her. If you find any wild cat, incidentally, you can make them your #own.");
				addGoodbye();
				addSeller(new CatSellerBehaviour(items));
				addReply("care",
						"My cat especially love to eat the red berries that grow on these little bushes. Just stand near one and your cat will walk over to start eating. You can right-click and choose LOOK at any time, to check up on her weight; she will gain one unit of weight for every cherry she eats.");
				addReply("travel",
						"You'll need your cat to be close by in order for her to follow you when you change zones; you can say #cat to call her if she's not paying attention. If you decide to abandon her instead, you can right-click on yourself and select LEAVE CAT; but frankly I think that sort of behaviour is disgraceful.");
				addReply("sell",
						"Once you've gotten your cat up to a weight of 100, you can take her to Sato in Semos; he will buy her from you.");
				addReply("own",
						"If you find any wild or abandoned cat, you can right-click on them and select OWN to tame them. Cats need to be looked after!");
			}
		};

		npc.setEntityClass("woman_009_npc");
		npc.setPosition(7, 8);
		npc.initHP(100);
		zone.add(npc);
	}
}
