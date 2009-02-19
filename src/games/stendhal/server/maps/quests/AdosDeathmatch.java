package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.common.Grammar;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.PlayerInAreaCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.deathmatch.BailAction;
import games.stendhal.server.maps.deathmatch.DeathmatchInfo;
import games.stendhal.server.maps.deathmatch.DoneAction;
import games.stendhal.server.maps.deathmatch.LeaveAction;
import games.stendhal.server.maps.deathmatch.Spot;
import games.stendhal.server.maps.deathmatch.StartAction;
import games.stendhal.server.util.Area;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Creates the Ados Deathmatch Game.
 */
public class AdosDeathmatch extends AbstractQuest {

		/** the logger instance. */
	private static final Logger logger = Logger.getLogger(AdosDeathmatch.class);

	private StendhalRPZone zone;

	private static Area arena;

	private DeathmatchInfo deathmatchInfo;

	public AdosDeathmatch() {
	    // constructor for quest system
	    logger.debug("little constructor for quest system", new Throwable());
	}
	@Override
	public String getSlotName() {
		return "adosdeathmatch";
	}

	public AdosDeathmatch(final StendhalRPZone zone, final Area area) {
		this.zone = zone;
		arena = area;
		logger.debug("big constructor for zone", new Throwable());
		final Spot entrance = new Spot(zone, 96, 75);
		deathmatchInfo = new DeathmatchInfo(arena, zone, entrance);
		zone.setTeleportAllowed(false);
	}

	/**
	 * Shows the player the potential trophy.
	 *
	 * @param x
	 *            x-position of helmet
	 * @param y
	 *            y-position of helmet
	 */
	public void createHelmet(final int x, final int y) {
		final Item helmet = SingletonRepository.getEntityManager()
				.getItem("trophy helmet");
		helmet.put("def", "20");
		helmet.setDescription("This is the grand prize for Deathmatch winners.");
		helmet.setPosition(x, y);
		helmet.setPersistent(true);
		zone.add(helmet);
	}

	public void createNPC(final String name, final int x, final int y) {

		// We create an NPC
		final SpeakerNPC npc = new SpeakerNPC(name) {

			@Override
			protected void createPath() {
				setPath(null);
			}

			@Override
			protected void createDialog() {

				// player is outside the fence. after 'hi' use ConversationStates.INFORMATION_1 only.
				add(
						ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new NotCondition(new PlayerInAreaCondition(arena)),
						ConversationStates.INFORMATION_1,
						"Welcome to Ados Deathmatch! Please talk to #Thonatus if you want to join.",
						null);
				add(
						ConversationStates.INFORMATION_1,
						"Thonatus",
						null,
						ConversationStates.INFORMATION_1,
						"Thonatus is the official #Deathmatch Recruitor. He is in the #swamp south west of Ados.",
						null);

                add(
					ConversationStates.INFORMATION_1,
					"swamp",
					null,
					ConversationStates.INFORMATION_1,
					"Yes, south west from here, as I said. Beware, as the swamp is populated with some evil creatures.",
					null);


				add(
					ConversationStates.INFORMATION_1,
					"deathmatch",
					null,
					ConversationStates.INFORMATION_1,
					"If you accept the #challenge from #Thonatus, you will arrive here. Strong enemies will surround you and you must kill them all to claim #victory.",
					null);

                add(
                    ConversationStates.INFORMATION_1,
                    "challenge",
                    null,
                    ConversationStates.INFORMATION_1,
                    "Remember the name death in #Deathmatch. Do not accept the challenge unless you think you can defend well. And be sure to check that there is not any elite warrior already inside, battling strong beasts!",
                    null);

				add(
                    ConversationStates.INFORMATION_1,
                    "victory",
                    null,
                    ConversationStates.INFORMATION_1,
                    "The prize is a helmet like the one you see displayed here. The defence it gives increases for every deathmatch round you successfully complete, up to a maximum dependent on your level.",
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
						Arrays.asList("everything", "appears", "deathmatch"),
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

	private void recruiterInformation() {
		final SpeakerNPC npc2 = npcs.get("Thonatus");

		npc2.add(ConversationStates.ATTENDING, Arrays.asList("heroes", "who", "hero", "status") , null, ConversationStates.ATTENDING,
				    null,
				    new ChatAction() {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
					    final List<Player> dmplayers = arena.getPlayers();
					    final List<String> dmplayernames = new LinkedList<String>();
					    for (Player dmplayer : dmplayers) {
						dmplayernames.add(dmplayer.getName());
					    }
					    if (dmplayers.size() == 0){
						// No players inside so continue as normal
						npc.say("Are you a hero? Make the #challenge if you are sure you want to join the deathmatch.");
					    } else {
						// List the players inside deathmatch
						npc.say("There are heroes battling right now in the deathmatch. If you want to go and join "
							+ Grammar.enumerateCollection(dmplayernames) + ", then make the #challenge.");
					    }
					}});
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		recruiterInformation();
	}
}
