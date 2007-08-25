package games.stendhal.server.maps.ados.outside;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SellerBehaviour;
import games.stendhal.server.entity.npc.ShopList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class VeterinarianNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();
	private ShopList shops = ShopList.get();

		/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildZooArea(zone, attributes);
	}

	private void buildZooArea(StendhalRPZone zone, Map<String, String> attributes) {
		SpeakerNPC npc = new SpeakerNPC("Dr. Feelgood") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(53, 28));
				nodes.add(new Node(53, 40));
				nodes.add(new Node(62, 40));
				nodes.add(new Node(62, 32));
				nodes.add(new Node(63, 32));
				nodes.add(new Node(63, 40));
				nodes.add(new Node(51, 40));
				nodes.add(new Node(51, 28));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				//Behaviours.addHelp(this,
				//				   "...");

				addReply("heal",
				        "Sorry, I'm only licensed to heal animals, not humans. (But... ssshh! I can make you an #offer.)");

				addJob("I'm the veterinarian.");
				addSeller(new SellerBehaviour(shops.get("healing")) {

					@Override
					public int getUnitPrice(String item) {
						// Player gets 20 % rebate
						return (int) (0.8f * priceList.get(item));
					}
				});

				addGoodbye("Bye!");
			}
			// remaining behaviour is defined in maps.quests.ZooFood.
		};
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "doctornpc");
		npc.setPosition(53, 28);
		//npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
