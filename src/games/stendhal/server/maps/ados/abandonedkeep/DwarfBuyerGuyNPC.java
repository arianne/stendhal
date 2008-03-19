package games.stendhal.server.maps.ados.abandonedkeep;

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
 * Inside Ados Abandoned Keep - level -3 .
 */
public class DwarfBuyerGuyNPC implements ZoneConfigurator {
    private ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		builddwarfguy(zone);
	}

	private void builddwarfguy(StendhalRPZone zone) {
		SpeakerNPC dwarfguy = new SpeakerNPC("Ritati Dragontracker") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(25, 32));
				nodes.add(new Node(38, 32));
				setPath(new FixedPath(nodes, true));

			}

			@Override
			protected void createDialog() {
				addGreeting("What do you want?");
				addJob("I buy odds and ends. Somebody has to do it.");
				addHelp("Look at me! I am reduced to buying trinkets! How can I help YOU?");
				addOffer("Look at blackboard to see my offer.");
				addQuest("Unless you want to buy this place, you cannot do anything for me.");
				addGoodbye("Be off with you!");
 				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buyoddsandends")), false);
			}
		};

		dwarfguy.setEntityClass("olddwarfnpc");
		dwarfguy.setPosition(25, 32);
		dwarfguy.initHP(100);
		zone.add(dwarfguy);
	}
}
