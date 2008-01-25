package games.stendhal.server.maps.ados.abandonedkeep;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.Map;

/**
 * Inside Ados Abandoned Keep - level -1 .
 */
public class OrcWeaponArmorGuyNPC implements ZoneConfigurator {
    private ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildHagnurk(zone);
	}

	private void buildHagnurk(StendhalRPZone zone) {
		SpeakerNPC hagnurk = new SpeakerNPC("Hagnurk") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I am Salesman. What you?");
				addHelp("I buy and sell items, look at blackboard on wall.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellbetterstuff1")), false);
				addOffer("Look at blackboard on wall to see my offer.");
				addQuest("I am so happy as I am. I want nothing.");
				addGoodbye();
			}
		};

		hagnurk.setEntityClass("orcsalesmannpc");
		hagnurk.setPosition(106, 5);
		hagnurk.initHP(100);
		zone.add(hagnurk);
	}
}
