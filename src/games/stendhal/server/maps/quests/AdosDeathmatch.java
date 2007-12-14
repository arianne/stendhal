package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerInAreaCondition;
import games.stendhal.server.maps.deathmatch.BailAction;
import games.stendhal.server.maps.deathmatch.DeathmatchInfo;
import games.stendhal.server.maps.deathmatch.DoneAction;
import games.stendhal.server.maps.deathmatch.LeaveAction;
import games.stendhal.server.maps.deathmatch.Spot;
import games.stendhal.server.maps.deathmatch.StartAction;
import games.stendhal.server.util.Area;

import java.util.Arrays;

/**
 * Creating the Stendhal Deathmatch Game
 */
public class AdosDeathmatch extends AbstractQuest {
	private StendhalRPZone zone;

	private Area arena;

	private DeathmatchInfo deathmatchInfo;

	public AdosDeathmatch() {
		// constructor for quest system
	}

	public AdosDeathmatch(StendhalRPZone zone, Area arena) {
		this.zone = zone;
		this.arena = arena;
		Spot entrance = new Spot(zone, 96, 75);

		deathmatchInfo = new DeathmatchInfo(arena, zone, entrance);
		zone.setTeleportAllowed(false);
	}

	/**
	 * show the player the potential trophy
	 *
	 * @param x
	 *            x-position of helmet
	 * @param y
	 *            y-position of helmet
	 */
	public void createHelmet(int x, int y) {
		Item helmet = StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("trophy_helmet");
		helmet.put("def", "20");
		helmet.setDescription("This is the grand prize for Deathmatch winners.");
		helmet.setPosition(x, y);
		helmet.setPersistent(true);
		zone.add(helmet);
	}

	public void createNPC(String name, int x, int y) {

		// We create an NPC
		SpeakerNPC npc = new SpeakerNPC(name) {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {

				// player is outside the fence
				add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new NotCondition(new PlayerInAreaCondition(arena)),
						ConversationStates.INFORMATION_1,
						"Welcome to Ados Deathmatch! Please talk to #Thonatus if you want to join",
						null);
				add(
						ConversationStates.INFORMATION_1,
						"Thonatus",
						null,
						ConversationStates.INFORMATION_1,
						"Thonatus is the official Deathmatch Recrutor. He is in the swamp south west of Ados.",
						null);

				// player is inside
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new PlayerInAreaCondition(arena),
						ConversationStates.ATTENDING,
						"Welcome to Ados Deathmatch! Do you need #help?", null);
				addJob("I'm the deathmatch assistant. Tell me, if you need #help on that.");
				addHelp("Say '#start' when you're ready! Keep killing #everything that #appears. Say 'victory' when you survived.");
				addGoodbye("I hope you enjoy the Deathmatch!");

				add(
						ConversationStates.ATTENDING,
						Arrays.asList("everything", "appears"),
						ConversationStates.ATTENDING,
						"Each round you will face stronger enemies. Defend well, kill them or tell me if you want to #bail!",
						null);
				add(
						ConversationStates.ATTENDING,
						Arrays.asList("trophy", "helm", "helmet"),
						ConversationStates.ATTENDING,
						"If you win the deathmatch, we reward you with a trophy helmet. Each #victory will strengthen it.",
						null);

				// 'start' command will start spawning creatures
				add(ConversationStates.ATTENDING, Arrays.asList("start", "go",
						"fight"), null, ConversationStates.IDLE, null,
						new StartAction(deathmatchInfo));

				// 'victory' command will scan, if all creatures are killed and
				// reward the player
				add(ConversationStates.ATTENDING, Arrays.asList("victory",
						"done", "yay"), null, ConversationStates.ATTENDING,
						null, new DoneAction());

				// 'leave' command will send the victorious player home
				add(ConversationStates.ATTENDING, Arrays
						.asList("leave", "home"), null,
						ConversationStates.ATTENDING, null, new LeaveAction());

				// 'bail' command will teleport the player out of it
				add(ConversationStates.ANY, Arrays.asList("bail", "flee",
						"run", "exit"), null, ConversationStates.ATTENDING,
						null, new BailAction());
			}
		};

		npc.setEntityClass("darkwizardnpc");
		npc.setPosition(x, y);
		npc.setDirection(Direction.DOWN);
		npc.initHP(100);
		zone.add(npc);
	}
}
