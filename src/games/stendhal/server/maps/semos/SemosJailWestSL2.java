package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.Jail;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.maps.ZoneConfigurator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import marauroa.common.game.IRPZone;

/**
 * Semos Jail - Level -2
 * 
 * @author hendrik
 */
public class SemosJailWestSL2 implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Build the Semos jail areas
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-2_semos_jail")),
			java.util.Collections.EMPTY_MAP);
	}


	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone,
	 Map<String, String> attributes) {
		buildJailKeeper(zone);
		disabledMagicScrolls(zone);
	}


	private void buildJailKeeper(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Sten Tanquilos") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(4, 14));
				nodes.add(new Path.Node(27, 14));
				nodes.add(new Path.Node(27, 17));
				nodes.add(new Path.Node(4, 17));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				addJob("I am the jail keeper. You have been confined here because of your bad behaviour.");
				addHelp("Please wait for an administrator to come here and decide what to do with you. In the meantime, there is no escape for you.");
				addGoodbye();
			}
		};
		npcs.add(npc);
	
		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(4, 14);
		npc.initHP(100);
		zone.addNPC(npc);
	}

	private void disabledMagicScrolls(StendhalRPZone zone) {
		zone.setTeleportable(false);
	}
}
