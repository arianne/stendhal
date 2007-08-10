package games.stendhal.server.maps.semos.kanmararn;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.BuyerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DwarfGuardNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

		/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildPrisonArea(zone, attributes);
	}

	private void buildPrisonArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Hunel") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(10, 22));
				nodes.add(new Node(12, 22));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			        addQuest("I'm too scared to leave here yet... can you offer me some really good equipment?");
				addJob("I'm was the guard of this Prison. Until .. well you know the rest.");
				addBuyer(new BuyerBehaviour(shops.get("buychaos")) {

				});

				addGoodbye("Bye .. be careful ..");
			}
			// remaining behaviour is defined in maps.quests.JailedDwarf.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "dwarfguardnpc");
		npc.set(10, 22);
		npc.initHP(100);
		zone.add(npc);
	}
}
