package games.stendhal.server.maps.semos.house;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class BabyDragonSellerNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 1;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildHouseArea(zone);
	}

	private void buildHouseArea(StendhalRPZone zone) {

		SpeakerNPC npc = new SpeakerNPC("Dragonus") {
			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				class BabyDragonSellerBehaviour extends SellerBehaviour {
					BabyDragonSellerBehaviour(Map<String, Integer> items) {
						super(items);
					}

					@Override
					protected boolean transactAgreedDeal(SpeakerNPC seller, Player player) {
						if (getAmount() > 1) {
							seller.say("Hmm... I just don't think you're cut out for taking care of more than one baby dragon at once.");
							return false;
						} else if (!player.hasPet()) {
							if (!player.drop("money", getCharge(player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here you go, nippy little baby dragon. It will eat any piece of chicken you place on the ground.");

							BabyDragon babydragon = new BabyDragon(player);

							babydragon.setPosition(seller.getX(), seller.getY() + 1);

							StendhalRPZone zone = seller.getZone();
							zone.add(babydragon);

							player.setPet(babydragon);
							player.notifyWorldAboutChanges();

							return true;
						} else {
							say("Well, why don't you make sure you can look after that pet you already have first?");
							return false;
						}
					}
				}

				Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("baby_dragon", BUYING_PRICE);

				addGreeting();
				addJob("I sell baby_dragons.");
				addHelp("I sell baby dragons. To buy one, just tell me you want to #buy #baby_dragon. If you're new to this business, I can tell you how to #travel with it and take #care of it. If you find any wild baby dragon, incidentally, you can make them your #own.");
				addGoodbye();
				addSeller(new BabyDragonSellerBehaviour(items));
				addReply("care",
						"Baby dragons love chicken. Just place a piece on the ground and your baby dragon will run over to eat it. You can right-click on it and choose 'Look' at any time, to check up on its weight; it will gain one unit of weight for every piece of chicken it eats.");
				addReply("travel",
						"You'll need your baby dragon to be close by in order for it to follow you when you change zones; you can say #pet to call it if it's not paying attention. If you decide to abandon it instead, you can right-click on yourself and select 'Leave Pet'; but frankly I think that sort of behaviour is disgraceful.");
				addReply("sell",
						"Sell??? What kind of a monster are you? Why would you ever sell your beautiful baby dragon?");
				addReply("own",
						"If you find any wild or abandoned baby dragon, you can right-click on them and select 'Own' to tame them. It will start following you immediately. Baby dragons go a bit crazy without an owner!");
			}
		};

		npc.setEntityClass("man_004_npc");
		npc.setPosition(13, 2);
		npc.initHP(100);
		zone.add(npc);
		
	}
}
