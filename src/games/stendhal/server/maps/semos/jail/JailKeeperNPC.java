package games.stendhal.server.maps.semos.jail;

import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.config.ZoneConfigurator;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.pathfinder.FixedPath;
import games.stendhal.server.pathfinder.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Semos Jail - Level -2
 * 
 * @author hendrik
 */
public class JailKeeperNPC implements ZoneConfigurator {
	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildJailKeeper(zone);
		disabledMagicScrolls(zone);
	}

	private void buildJailKeeper(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Sten Tanquilos") {

			@Override
			protected void createPath() {
				List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(4, 15));
				nodes.add(new Node(27, 15));
				nodes.add(new Node(27, 18));
				nodes.add(new Node(4, 18));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Please wait for an administrator to come here and decide what to do with you. In the meantime, there is no escape for you.");
				addGoodbye();
			}
		};

		npc.setEntityClass("youngsoldiernpc");
		npc.setPosition(4, 15);
		npc.initHP(100);
		zone.add(npc);
	}

	private void disabledMagicScrolls(StendhalRPZone zone) {
		zone.setTeleportAllowed(false);
	}
}
