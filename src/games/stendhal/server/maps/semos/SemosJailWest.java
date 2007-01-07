package games.stendhal.server.maps.semos;

import games.stendhal.common.Direction;
import games.stendhal.server.Jail;
import games.stendhal.server.RespawnPoint;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.portal.Portal;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.rule.defaultruleset.DefaultEntityManager;

import java.util.LinkedList;
import java.util.List;

import marauroa.common.game.IRPZone;

/**
 * Semos Jail
 * 
 * @author hendrik
 */
public class SemosJailWest {
	private NPCList npcs = NPCList.get();

	/**
	 * Build the Semos jail areas
	 */
	public void build() {
		buildPortals();
		zoneSub1SemosJailSoldier();
		zoneSub1SemosJailElf();
		zoneSub2SemosJail();
		disabledMagicScrolls();
	}

	private void buildPortals() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zoneOutside = (StendhalRPZone) world.getRPZone(new IRPZone.ID("0_semos_plains_w"));
		StendhalRPZone sub1semosJail = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-1_semos_jail"));
		Portal portal = new Portal();
		zoneOutside.assignRPObjectID(portal);
		portal.setX(86);
		portal.setY(26);
		portal.setNumber(0);
		portal.setDestination("-1_semos_jail", 0);
		zoneOutside.addPortal(portal);
		
		portal = new Portal();
		sub1semosJail.assignRPObjectID(portal);
		portal.setX(28);
		portal.setY(17);
		portal.setNumber(0);
		portal.setDestination("0_semos_plains_w", 0);
		sub1semosJail.addPortal(portal);
	}

	private void zoneSub1SemosJailSoldier() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-1_semos_jail"));
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

	private void zoneSub1SemosJailElf() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID("-1_semos_jail"));
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

	private void zoneSub2SemosJail() {
		StendhalRPZone zone = (StendhalRPZone) StendhalRPWorld.get().getRPZone(new IRPZone.ID(
		"-2_semos_jail"));
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
	
	private void disabledMagicScrolls() {
		StendhalRPWorld world = StendhalRPWorld.get();
		StendhalRPZone zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-1_semos_jail"));
		zone.setTeleportable(false);
		zone = (StendhalRPZone) world.getRPZone(new IRPZone.ID("-2_semos_jail"));
		zone.setTeleportable(false);
	}

	/**
	 * Is the player speaking to us in jail?
	 */
	public static class InJailCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return Jail.isInJail(player);
		}
	}

	/**
	 * Is the player speaking to us not in jail?
	 */
	public static class NotInJailCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, SpeakerNPC engine) {
			return !Jail.isInJail(player);
		}
	}
}
