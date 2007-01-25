package games.stendhal.server.maps.orril;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/*
 * Configure Orril Dwarf Mine (Underground/Level -2).
 */
public class USL2_DwarfMine implements ZoneConfigurator {

	private NPCList npcs;
	
	private ShopList shops;


	public USL2_DwarfMine() {
		this.npcs = NPCList.get();
		this.shops = ShopList.get();
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildDwarfMineArea(zone);
	}


	private void buildDwarfMineArea(StendhalRPZone zone) {
		// NOTE: This is a female character ;)
		SpeakerNPC loretta = new SpeakerNPC("Loretta") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(49, 67));
				nodes.add(new Path.Node(45, 67));
				nodes.add(new Path.Node(45, 71));
				nodes.add(new Path.Node(45, 67));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting();
				addJob("I'm the supervisor responsible for maintaining the mine-cart rails in this mine. But, ironically, we ran out of cast iron to fix them with! Maybe you can #offer me some?");
				addHelp("If you want some good advice, you'll not go further south; there's an evil dragon living down there!");
				addBuyer(new BuyerBehaviour(shops.get("buyiron")), true);
				addGoodbye("Farewell - and be careful: the other dwarves don't like strangers running around here!");
			}
		};
		npcs.add(loretta);

		loretta.setDescription("You see Loretta, an elderly female dwarf. She is working on the mine-cart rails.");
		zone.assignRPObjectID(loretta);
		loretta.put("class", "greendwarfnpc");
		loretta.set(49, 67);
		loretta.initHP(100);
		zone.addNPC(loretta);
	}
}
