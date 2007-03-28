package games.stendhal.server.maps.ados;

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

/**
 * Builds ados mayor NPC.
 * He may give an items quest later
 * Now he sells ados scrolls
 * @author kymara
 */
public class IL0_Mayor implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	private ShopList shops = ShopList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildMayor(zone);
	}

	private void buildMayor(StendhalRPZone zone) {
		SpeakerNPC mayor = new SpeakerNPC("Mayor Chalmers") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				setPath(nodes, false);
			}

			@Override
			protected void createDialog() {
				addGreeting("On behalf of the citizens of Ados, welcome.");
				addJob("I'm the mayor of Ados. I can #offer you the chance to return here easily.");
				addHelp("Ask me about my #offer to return here.");
				addQuest("I don't know you well yet. Perhaps later in the year I can trust you with something.");
				addSeller(new SellerBehaviour(shops.get("adosscrolls")));
				addGoodbye("Good day to you.");
			}
		};
		mayor.setDescription("You see the mayor of Ados");
		npcs.add(mayor);
		zone.assignRPObjectID(mayor);
		mayor.put("class", "badmayornpc");
		mayor.set(3, 9);
		mayor.initHP(100);
		zone.add(mayor);

	}
}
