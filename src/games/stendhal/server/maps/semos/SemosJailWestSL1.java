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
 * Semos Jail - Level -1
 * 
 * @author hendrik
 */
public class SemosJailWestSL1 implements ZoneConfigurator {
	private NPCList npcs = NPCList.get();

	/**
	 * Build the Semos jail areas
	 */
	public void build() {
		StendhalRPWorld world = StendhalRPWorld.get();

		configureZone(
			(StendhalRPZone) world.getRPZone(
				new IRPZone.ID("-1_semos_jail")),
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
		buildPortals(zone);
		buildElf(zone);
		buildSoldier(zone);
		disabledMagicScrolls(zone);
	}


	private void buildPortals(StendhalRPZone zone) {
		Portal portal = new Portal();
		zone.assignRPObjectID(portal);
		portal.setX(28);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_semos_plains_w", 0);
		zone.addPortal(portal);
	}

	private void buildSoldier(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Marcus") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(9, 6));
				nodes.add(new Path.Node(21, 6));
				nodes.add(new Path.Node(21, 7));
				nodes.add(new Path.Node(9, 7));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Greetings! How may I #help you?");
				add(ConversationStates.ATTENDING, SpeakerNPC.JOB_MESSAGES, new NotInJailCondition(), ConversationStates.ATTENDING, 
								"I am the jail keeper.", null);
				add(ConversationStates.ATTENDING, SpeakerNPC.JOB_MESSAGES, new InJailCondition(), ConversationStates.ATTENDING, 
								"I am the jail keeper. You have been confined here because of your bad behaviour.", null);
				add(ConversationStates.ATTENDING, SpeakerNPC.HELP_MESSAGES, new InJailCondition(), ConversationStates.ATTENDING, 
								"Please wait for an administrator to come here and decide what to do with you. In the meantime, there is no escape for you.", null);
				add(ConversationStates.ATTENDING, SpeakerNPC.HELP_MESSAGES, new NotInJailCondition(), ConversationStates.ATTENDING, 
								"Be careful with the criminals in the cells.", null);
				addGoodbye();
			}
		};
		npcs.add(npc);
	
		zone.assignRPObjectID(npc);
		npc.put("class", "youngsoldiernpc");
		npc.set(9, 6);
		npc.initHP(100);
		zone.addNPC(npc);
	
	}

	private void buildElf(StendhalRPZone zone) {
		SpeakerNPC npc = new SpeakerNPC("Conual") {
			@Override
			protected void createPath() {
				List<Path.Node> nodes = new LinkedList<Path.Node>();
				nodes.add(new Path.Node(13, 2));
				setPath(nodes, true);
			}
	
			@Override
			protected void createDialog() {
				addGreeting("Let me out");
				addGoodbye();
			}
		};
		npcs.add(npc);
	
		zone.assignRPObjectID(npc);
		npc.put("class", "militiaelfnpc");
		npc.set(13, 2);
		npc.initHP(100);
		npc.setDirection(Direction.DOWN);
		zone.addNPC(npc);
	}

	private void disabledMagicScrolls(StendhalRPZone zone) {
		zone.setTeleportable(false);
	}

	/**
	 * Is the player speaking to us in jail?
	 */
	public static class InJailCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return Jail.isInJail(player);
		}
	}

	/**
	 * Is the player speaking to us not in jail?
	 */
	public static class NotInJailCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return !Jail.isInJail(player);
		}
	}
}
