package games.stendhal.server.maps.semos.kanmararn;

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

public class DwarfGuardNPC implements ZoneConfigurator {
	private final ShopList shops = SingletonRepository.getShopList();

		/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildPrisonArea(zone, attributes);
	}

	private void buildPrisonArea(final StendhalRPZone zone, final Map<String, String> attributes) {
		final SpeakerNPC npc = new SpeakerNPC("Hunel") {

			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(10, 23));
				nodes.add(new Node(12, 23));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
			    addQuest("I'm too scared to leave here yet... I'm waiting or someone to #offer me some better equipment.");
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
