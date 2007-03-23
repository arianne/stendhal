package games.stendhal.server.maps.fado.church;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.pathfinder.Path;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IL0_VergerNPC implements ZoneConfigurator {

	private NPCList npcs = NPCList.get();

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildNPC(zone);
	}

	private void buildNPC(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Lukas") {

			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(22, 8));
				nodes.add(new Path.Node(16, 8));
				nodes.add(new Path.Node(16, 3));
				nodes.add(new Path.Node(19, 3));
				nodes.add(new Path.Node(19, 2));
				nodes.add(new Path.Node(22, 2));
				setPath(nodes, true);
			}

			@Override
			protected void createDialog() {
				addGreeting("Welcome to this place of worship. Are you here to be #married?");
				addJob("I am the church verger. I help with small menial tasks, but I do not mind, as my reward will come in the life beyond.");
				addHelp("My only advice is to love and be kind to one another");
				addQuest("I have eveything I need. But it does bring me pleasure to see people #married.");
				addReply("married", "Sister Benedicta will explain the rituals to you.");
				addReply("yes", "Congratulations!");
				addReply("no", "A pity. I do hope you find a partner one day.");
				addGoodbye("Goodbye, go safely.");
			}
		};
		npc.setDescription("You see Lukas, the humble church verger.");
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		npc.put("class", "vergernpc");
		npc.set(22, 8);
		npc.initHP(100);
		zone.add(npc);

	}
}
