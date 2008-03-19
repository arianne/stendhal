package games.stendhal.server.maps.nalwor.assassinhq;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Inside Nalwor Assassin Headquarters - cellar .
 */
public class ChiefFalatheenDishwasherNPC implements ZoneConfigurator {
    private ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		builddishwasher(zone);
	}

	private void builddishwasher(StendhalRPZone zone) {
		SpeakerNPC dishwasher = new SpeakerNPC("Chief Falatheen Humble Dishwasher") {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {
				addGreeting("You better have a good excuse for bothering me. I'm up to my neck in dishwater!");
				addJob("It is my job to wash all the dishes for all these dirty little brats.");
				addHelp("I can buy your vegetables and herbs.  Please see blackboards on wall for what i need.");
				addOffer("Look at blackboards on wall to see my offers.");
				addQuest("You could try to help me escape from these hoodlums. Well... maybe not.");
				addGoodbye("Don't forget where I am now. Come back and see me some time. I do get lonely.");
 				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buyveggiesandherbs")), false);
			}
		};

		dishwasher.setEntityClass("chieffalatheennpc");
		dishwasher.setPosition(20, 3);
		dishwasher.initHP(100);
		zone.add(dishwasher);
	}
}
