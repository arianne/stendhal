package games.stendhal.server.maps.semos.house;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Cat;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
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
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(7, 7));
				nodes.add(new Path.Node(9, 7));
				nodes.add(new Path.Node(9, 8));
				nodes.add(new Path.Node(7, 8));
				setPath(nodes, true);
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
							if (! player.drop("money", getCharge(player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here you go, a cute little kitten! Take good care of it, now...");
							StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get()
									.getRPZone(seller.getID());

							Cat cat = new Cat(player);
							zone.assignRPObjectID(cat);

							cat.setX(seller.getX());
							cat.setY(seller.getY() + 2);

							StendhalRPWorld.get().add(cat);

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
		NPCList.get().add(npc);

		zone.assignRPObjectID(npc);
		npc.put("class", "woman_009_npc");
		npc.set(7, 7);
		npc.initHP(100);
		zone.add(npc);

	}
}
