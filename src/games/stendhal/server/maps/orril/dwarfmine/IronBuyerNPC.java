package games.stendhal.server.maps.orril.dwarfmine;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.adder.BuyerAdder;
import games.stendhal.server.entity.npc.behaviour.impl.BuyerBehaviour;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Configure Orril Dwarf Mine (Underground/Level -2).
 */
public class IronBuyerNPC implements ZoneConfigurator {
	private ShopList shops;

	public IronBuyerNPC() {
		this.shops = ShopList.get();
	}

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildDwarfMineArea(zone);
	}

	private void buildDwarfMineArea(StendhalRPZone zone) {
		// NOTE: This is a female character ;)
		SpeakerNPC loretta = new SpeakerNPC("Loretta") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(49, 68));
				nodes.add(new Node(45, 68));
				nodes.add(new Node(45, 72));
				nodes.add(new Node(45, 68));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm the supervisor responsible for maintaining the mine-cart rails in this mine. But, ironically, we ran out of cast iron to fix them with! Maybe you can #offer me some?");
				addHelp("If you want some good advice, you'll not go further south; there's an evil dragon living down there!");
				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buyiron")), true);
				addGoodbye("Farewell - and be careful: the other dwarves don't like strangers running around here!");
			}
		};

		loretta.setDescription("You see Loretta, an elderly female dwarf. She is working on the mine-cart rails.");
		loretta.setEntityClass("greendwarfnpc");
		loretta.setPosition(49, 68);
		loretta.initHP(100);
		zone.add(loretta);
	}
}
