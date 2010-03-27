package games.stendhal.server.maps.ados.forest;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Builds Karl, the farmer NPC.
 * He gives horse hairs needed for the BowsForOuchit quest
 * He gives help to newcomers about the area
 * He suggests you can buy milk from his wife Philomena
 * @author kymara
 */
public class FarmerNPC implements ZoneConfigurator {

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildFarmer(zone);
	}

	private void buildFarmer(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Karl") {

			/* 
			 * Karl walks around near the red barn and along the path some way.
			 */
			
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(64, 75));
				nodes.add(new Node(64, 86));	
				nodes.add(new Node(68, 86));	
				nodes.add(new Node(68, 84));
				nodes.add(new Node(76, 84));
				nodes.add(new Node(68, 84));
				nodes.add(new Node(68, 86));
				nodes.add(new Node(60, 86));
				nodes.add(new Node(60, 89));
				nodes.add(new Node(60, 86));
				nodes.add(new Node(64, 86));
				setPath(new FixedPath(nodes, true));
			}
			
			@Override
			protected void createDialog() {
				addGreeting("Heyho! Nice to see you here at our farm.");
				addJob("Oh, working here is hard, I don't think that you can help me here.");
				addOffer("Our milk is really tasty. Ask my wife #Philomena for some.");
				addReply("Philomena","She's in the farm house just south west from here.");
				addHelp("You need help? I can tell you a bit about the #neighborhood.");
				addReply("neighborhood.","In the north is a cave with bears and other creatures. If you go to the north-east " +
						"you will reach after some time the great city Ados. At the east is a biiig rock. Does Balduin " +
						"still live there? You want to go south-east? Well.. you can reach Ados there too, but I think the " +
						"way is a bit harder.");
				addQuest("I don't have time for those things, sorry. Working..working..working..");
				addGoodbye("Bye bye. Be careful on your way.");
			}
		};

		npc.setDescription("You see Karl, a friendly elderly farmer.");
		npc.setEntityClass("beardmannpc");
		npc.setPosition(64, 75);
		npc.initHP(100);
		zone.add(npc);
	}
}
