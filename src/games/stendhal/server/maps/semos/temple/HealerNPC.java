package games.stendhal.server.maps.semos.temple;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.HealerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.SellerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HealerNPC implements ZoneConfigurator {
	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosTempleArea(zone, attributes);
	}

	private void buildSemosTempleArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Ilisa") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(9, 6));
				nodes.add(new Node(14, 6));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("My special powers help me to heal wounded people. I also sell potions and antidotes.");
				addHelp("I can #heal you here for free, or you can take one of my prepared medicines with you on your travels; just ask for an #offer.");
				addSeller(new SellerBehaviour(shops.get("healing")));
				new HealerAdder().addHealer(this, 0);
				addGoodbye();
			}
		};

		npc.setEntityClass("welcomernpc");
		npc.setPosition(9, 6);
		npc.initHP(100);
		zone.add(npc);
	}
}
