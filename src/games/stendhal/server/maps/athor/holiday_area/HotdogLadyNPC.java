package games.stendhal.server.maps.athor.holiday_area;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Outside holiday area on Athor Island)
 */
public class HotdogLadyNPC implements ZoneConfigurator {
	private ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildhotdoglady(zone);
	}

	private void buildhotdoglady(final StendhalRPZone zone) {
		SpeakerNPC hotdoglady = new SpeakerNPC("Sara Beth") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(34, 67));
				nodes.add(new Node(34, 68));
				nodes.add(new Node(39, 68));
				nodes.add(new Node(39, 67));
				nodes.add(new Node(39, 68));
				nodes.add(new Node(34, 68));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Well hello there, y'all. What kin I do fer ya today?");
				addJob("Why I'm just yer ordinary Hotdog Lady.");
				addHelp("I buy and sell lotsa things. Just take a gander at the blackboards.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellhotdogs")), false);
				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buy4hotdogs")), false);
				addOffer("Lookit the blackboards ta see my offers.");
				addQuest("Why you can't do nuthin special fer little ole me. But thank y'all fer askin.");
				addGoodbye("Thanks so much fer stoppin by. Come on back next time yer on vacation.");
			}
		};

		hotdoglady.setEntityClass("woman_013_npc");
		hotdoglady.setPosition(34, 67);
		hotdoglady.initHP(100);
		zone.add(hotdoglady);
	}
}
