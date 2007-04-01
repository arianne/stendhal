package games.stendhal.server.maps.semos.blacksmith;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.ProducerBehaviour;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

public class BlacksmithNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildSemosBlacksmithArea(zone, attributes);
	}

	private void buildSemosBlacksmithArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC xoderos = new SpeakerNPC("Xoderos") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(23, 11));
				nodes.add(new Path.Node(29, 11));
				nodes.add(new Path.Node(29, 4));
				nodes.add(new Path.Node(17, 4));
				nodes.add(new Path.Node(17, 8));
				nodes.add(new Path.Node(28, 8));
				nodes.add(new Path.Node(28, 11));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				add(
				        ConversationStates.ATTENDING,
				        "wood",
				        null,
				        ConversationStates.ATTENDING,
				        "I need some wood to keep my furnace lit. You can find any amount of it just lying around in the forest.",
				        null);

				add(
				        ConversationStates.ATTENDING,
				        Arrays.asList("ore", "iron", "iron_ore"),
				        null,
				        ConversationStates.ATTENDING,
				        "You can find iron ore up in the mountains west of Or'ril, near the dwarf mines. Be careful up there!",
				        null);

				add(
				        ConversationStates.ATTENDING,
				        "gold_pan",
				        null,
				        ConversationStates.ATTENDING,
				        "With this tool you are able to prospect for gold. In the south there is a lake near a waterfall where I once found a #gold_nugget. Maybe you are lucky as well.",
				        null);

				add(ConversationStates.ATTENDING, "gold_nugget", null, ConversationStates.ATTENDING,
				        "My brother Joshua lives in Ados. He can cast iron_ore to barrels of pure gold.", null);

				addHelp("If you bring me #wood and #iron_ore, I can #cast the iron for you. Then you could sell it to the dwarves, to make yourself a little money.");
				addJob("Greetings. Unfortunately, because of the war, I'm not currently allowed to sell weapons to anyone except the official armoury. However, I can still #cast iron for you, or I can make you an #offer on some good tools.");
				addGoodbye();
				// Once Ados is ready, we can have an expert tool smith; then Xoderos
				// won't sell tools anymore.
				addSeller(new SellerBehaviour(ShopList.get().get("selltools")));

				// Xoderos casts iron if you bring him wood and iron ore.
				Map<String, Integer> requiredResources = new HashMap<String, Integer>();
				requiredResources.put("wood", new Integer(1));
				requiredResources.put("iron_ore", new Integer(1));

				ProducerBehaviour behaviour = new ProducerBehaviour("xoderos_cast_iron", "cast", "iron",
				        requiredResources, 5 * 60);

				addProducer(
				        behaviour,
				        "Greetings. I am sorry to tell you that, because of the war, I am not allowed to sell you any weapons. However, I can #cast iron for you. I can also #offer you tools.");

			}
		};
		npcs.add(xoderos);
		zone.assignRPObjectID(xoderos);
		xoderos.put("class", "blacksmithnpc");
		xoderos.set(23, 11);
		xoderos.initHP(100);
		zone.add(xoderos);
	}
}
