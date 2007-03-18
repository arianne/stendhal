package games.stendhal.server.maps.nalwor.postoffice;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SellerBehaviour;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class IL0_PostNPC implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildNPC(zone);
	}


	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Lorithien") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(11, 2));
				nodes.add(new Path.Node(16, 2));
				nodes.add(new Path.Node(16, 7));
				nodes.add(new Path.Node(11, 7));
				nodes.add(new Path.Node(11, 4));
				nodes.add(new Path.Node(7, 4));
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(3, 1));
				nodes.add(new Path.Node(3, 4));
				nodes.add(new Path.Node(3, 1));
				nodes.add(new Path.Node(7, 1));
				nodes.add(new Path.Node(7, 4));
				nodes.add(new Path.Node(11, 4));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Hi, can I #help you?");
				addJob("I work in this post office. But I'm new and I haven't been trusted with much yet.");
				addHelp("I've not had this #job long ... come back soon and I might have been given something interesting to do.");
				addSeller(new SellerBehaviour(shops.get("nalworscrolls")));
				addGoodbye("Bye - nice to meet you!");
			}
		};
		npc.setDescription("You see a pretty elf girl.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "postelfnpc");
		npc.set(11, 2);
		npc.initHP(100);
		zone.add(npc);

	
	}
}
