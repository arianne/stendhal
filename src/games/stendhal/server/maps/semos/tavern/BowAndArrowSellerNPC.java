package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 1 (upstairs)
 */
public class BowAndArrowSellerNPC implements ZoneConfigurator {
	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildOuchit(zone);
	}

	private void buildOuchit(StendhalRPZone zone) {
		SpeakerNPC ouchit = new SpeakerNPC("Ouchit") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(21, 3));
				nodes.add(new Node(25, 3));
				nodes.add(new Node(25, 5));
				nodes.add(new Node(29, 5));
				nodes.add(new Node(25, 5));
				nodes.add(new Node(25, 3));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I sell bows and arrows.");
				addHelp("I sell several items, ask me for my #offer.");
				new SellerAdder().addSeller(this, new SellerBehaviour(shops.get("sellrangedstuff")));
				addGoodbye();
			}
		};

		ouchit.setEntityClass("weaponsellernpc");
		ouchit.setPosition(21, 3);
		ouchit.initHP(100);
		zone.add(ouchit);
	}
}
