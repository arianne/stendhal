package games.stendhal.server.maps.semos.kanmararn;

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

public class DwarfGuardNPC implements ZoneConfigurator {
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
				nodes.add(new Node(10, 23));
				nodes.add(new Node(12, 23));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			    addQuest("I'm too scared to leave here yet... can you offer me some really good equipment?");
				addJob("I'm was the guard of this Prison. Until .. well you know the rest.");
				new BuyerAdder().add(this, new BuyerBehaviour(shops.get("buychaos")), true);

				addGoodbye("Bye .. be careful ..");
			}
			// remaining behaviour is defined in maps.quests.JailedDwarf.
		};

		npc.setEntityClass("dwarfguardnpc");
		npc.setPosition(10, 23);
		npc.initHP(100);
		zone.add(npc);
	}
}
