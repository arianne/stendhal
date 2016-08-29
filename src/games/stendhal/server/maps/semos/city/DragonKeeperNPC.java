package games.stendhal.server.maps.semos.city;

import games.stendhal.common.grammar.ItemParserResult;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.BabyDragon;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class DragonKeeperNPC implements ZoneConfigurator {

	public static final int BUYING_PRICE = 1;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildHouseArea(zone);
	}

	private void buildHouseArea(final StendhalRPZone zone) {

		final SpeakerNPC npc = new SpeakerNPC("The Dragon Keeper") {

			@Override
			protected void createDialog() {
				class DragonSellerBehaviour extends SellerBehaviour {
					DragonSellerBehaviour(final Map<String, Integer> items) {
						super(items);
					}

					@Override
					public boolean transactAgreedDeal(ItemParserResult res, final EventRaiser seller, final Player player) {
						if (res.getAmount() > 1) {
							seller.say("Hmm... I just don't think you're cut out for taking care of more than one dragon at a time.");
							return false;
						} else if (player.hasPet()) {
							say("Well, you should look after that pet you already have first.");
							return false;
						} else {
							if (!player.drop("money", getCharge(res, player))) {
								seller.say("You don't seem to have enough money.");
								return false;
							}
							seller.say("Here give this dragon a work out! It should fight at your side and #grow with you.");

							final BabyDragon baby_dragon = new BabyDragon(player);
							
							Entity sellerEntity = seller.getEntity();
							baby_dragon.setPosition(sellerEntity.getX(), sellerEntity.getY() + 1);

							player.setPet(baby_dragon);
							player.notifyWorldAboutChanges();

							return true;
						}
					}
				}

				final Map<String, Integer> items = new HashMap<String, Integer>();
				items.put("dragon", BUYING_PRICE);

				addGreeting();
				addJob("I fight demons with a dragon by my side. I might have one for you.");
				addHelp("I sell dragons. To buy one, just tell me you want to #buy #dragon.");
				addGoodbye();
				addReply("grow","Take it into battle and it will gain experince and improve.");
				new SellerAdder().addSeller(this, new DragonSellerBehaviour(items));
			}
		};

		npc.setEntityClass("man_005_npc");
		npc.setPosition(17, 7);
		npc.initHP(85);
		npc.setDescription("The Dragon Keeper just flew into town on the back of a mighty winged dragon.");
		zone.add(npc);

	}
}