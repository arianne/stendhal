package games.stendhal.server.maps.deathmatch;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.maps.quests.AbstractQuest;
import games.stendhal.server.pathfinder.Path;
import games.stendhal.server.util.Area;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Creating the Stendhal Deathmatch Game
 */
public class Deathmatch extends AbstractQuest {
	private NPCList npcs = NPCList.get();
	private StendhalRPZone zone = null;
	private Area arena = null;
	private DeathmatchInfo deathmatchInfo = null;
	
	public Deathmatch() {
		// constructor for quest system
	}
	
	public Deathmatch(String zoneName, StendhalRPZone zone, Area arena) {
		this.zone = zone;
		this.arena = arena;
		deathmatchInfo = new DeathmatchInfo(arena, zoneName, zone);
		zone.setTeleportable(false);
		DeathmatchInfo.add(deathmatchInfo);
	}

	/**
	 * show the player the potential trophy
	 *
	 * @param x x-position of helmet
	 * @param y y-position of helmet
	 */
	public void createHelmet(int x, int y) {
		Item helmet = StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("trophy_helmet");
		zone.assignRPObjectID(helmet);
		helmet.put("def", "20");
		helmet.setDescription("This is the grand prize for Deathmatch winners.");
		helmet.setX(x);
		helmet.setY(y);
		helmet.put("persistent", 1);
		zone.add(helmet);
	}


	public void createNPC(String name, int x, int y) {

		// We create an NPC
		SpeakerNPC npc=new SpeakerNPC(name) {

			@Override
			protected void createPath() {
				setPath(new ArrayList<Path.Node>(), false);
			}

			@Override
			protected void createDialog() {

				// player is outside the fence
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
						new StandardInteraction.Not(new StandardInteraction.PlayerInAreaCondition(arena)),
						ConversationStates.INFORMATION_1, "Welcome to Ados Deathmatch! Please talk to #Thonatus if you want to join", null);
				add(ConversationStates.INFORMATION_1, "Thonatus", null, ConversationStates.INFORMATION_1,
						"Thonatus is the official Deathmatch Recrutor. He is in the swamp south west of Ados.", null);


				// player is inside
				add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, new StandardInteraction.PlayerInAreaCondition(arena),
						ConversationStates.ATTENDING, "Welcome to Ados Deathmatch! Do you need #help?", null);
				addJob("I'm the deathmatch assistant. Tell me, if you need #help on that.");
				addHelp("Say '#start' when you're ready! Keep killing #everything that #appears. Say 'victory' when you survived.");
				addGoodbye("I hope you enjoy the Deathmatch!");

				add(ConversationStates.ATTENDING, Arrays.asList("everything", "appears"), ConversationStates.ATTENDING, 
						"Each round you will face stronger enemies. Defend well, kill them or tell me if you want to #bail!", null);
				add(ConversationStates.ATTENDING, Arrays.asList("trophy","helm","helmet"), ConversationStates.ATTENDING,
						"If you win the deathmatch, we reward you with a trophy helmet. Each #victory will strengthen it.", null);

				// 'start' command will start spawning creatures
				add(ConversationStates.ATTENDING, Arrays.asList("start", "go", "fight"), null, 
						ConversationStates.ATTENDING, null, new StartAction(deathmatchInfo));
				
				// 'victory' command will scan, if all creatures are killed and reward the player
				add(ConversationStates.ATTENDING, Arrays.asList("victory", "done", "yay"), null,
						ConversationStates.ATTENDING, null, new DoneAction());
				
				// 'leave' command will send the victorious player home
				add(ConversationStates.ATTENDING, Arrays.asList("leave", "home"), null, 
						ConversationStates.ATTENDING, null, new LeaveAction());
				
				// 'bail' command will teleport the player out of it
				add(ConversationStates.ATTENDING, Arrays.asList("bail", "flee", "run", "exit"), null,
						ConversationStates.ATTENDING, null, new BailAction());
			}
		};

		npc.put("class", "darkwizardnpc");
		npc.set(x, y);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		npcs.add(npc);
		zone.assignRPObjectID(npc);
		zone.add(npc);
	}

	@Override
	public void onPlayerLogin(Player player) {
		super.onPlayerLogin(player);
		// need to do this on the next turn
		TurnNotifier.get().notifyInTurns(1, new DealWithLogoutCoward(player), null);
	}

}
