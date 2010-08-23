package games.stendhal.server.maps.semos.tavern;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.SellerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Inside Semos Tavern - Level 1 (upstairs)
 */
public class BowAndArrowSellerNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildOuchit(zone);
	}

	private void buildOuchit(final StendhalRPZone zone) {
		final SpeakerNPC ouchit = new SpeakerNPC("Ouchit") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
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
		ouchit.setDescription("You see Ouchit. He loves wooden weapons...");
		zone.add(ouchit);
	}
}
