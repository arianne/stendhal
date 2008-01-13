package games.stendhal.server.maps.quests;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
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
 * Creates the Stendhal Deathmatch Game for Fado.
 */
public class FadoDeathmatch extends AbstractQuest {
	private StendhalRPZone zone;

	private Area arena;

	private DeathmatchInfo deathmatchInfo;

	public FadoDeathmatch() {
		// constructor for quest system
	}

	public FadoDeathmatch(StendhalRPZone zone, Area arena) {
		Spot entrance = new Spot(zone, 96, 75);
		this.zone = zone;
		this.arena = arena;
		deathmatchInfo = new DeathmatchInfo(arena, zone, entrance);
		zone.setTeleportAllowed(false);
	}

	/**
	 * Shows the player the potential prize.
	 *
	 * @param x
	 *            x-position of legs
	 * @param y
	 *            y-position of legs
	 */
	public void createLegs(int x, int y) {
		Item legs = StendhalRPWorld.get().getRuleManager().getEntityManager()
				.getItem("golden legs");
		// we are using these until better
		// ones are found/committed. TODO: trophy legs
		legs.put("def", "10");
		legs.setDescription("This is the grand prize for the Battle Arena winners.");
		legs.setPosition(x, y);
		legs.setPersistent(true);
		zone.add(legs);
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
						"Welcome to the Fado Battle Arena! Please talk to #Thonatun if you want to join",
						null);
				add(
						ConversationStates.INFORMATION_1,
						"Thontun",
						null,
						ConversationStates.INFORMATION_1,
						"Thonatun is the official Battle Arena. He should be wandering around in this building.",
						null);

				// player is inside
				add(ConversationStates.IDLE,
						ConversationPhrases.GREETING_MESSAGES,
						new PlayerInAreaCondition(arena),
						ConversationStates.ATTENDING,
						"Welcome to Fado Battle Arena! Do you need #help?",
						null);
				addJob("I'm the battle arena assistant. Tell me if you need #help with anything.");
				addHelp("Say '#start' when you're ready! Keep killing #everything that #appears. Say 'victory' when you survived.");
				addGoodbye("I hope you enjoy the Battle Arena!");

				add(
						ConversationStates.ATTENDING,
						Arrays.asList("everything", "appears"),
						ConversationStates.ATTENDING,
						"Each round you will face stronger enemies. Defend well, kill them or tell me if you want to #bail!",
						null);
				add(
						ConversationStates.ATTENDING,
						Arrays.asList("prize", "legs"),
						ConversationStates.ATTENDING,
						"If you win the Battle Arena, we reward you with some legs. Each #victory will strengthen it.",
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

		// We create another NPC
		SpeakerNPC npc1 = new SpeakerNPC("Marcelo") {

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
						"Welcome to the Fado Battle Arena! Please talk to #Brutus if you want to join",
						null);
				add(
						ConversationStates.INFORMATION_1,
						"Brutus",
						null,
						ConversationStates.INFORMATION_1,
						"Brutus is the official Battle Arena recruiter. He should be wandering around in this building. Maybe you could check the basement.",
						null);
				add(
						ConversationStates.INFORMATION_1,
						"Thonatun",
						null,
						ConversationStates.INFORMATION_1,
						"Thonaton is the official Battle Arena manager. He usually stays in the arena to help the fighters.",
						null);
				addJob("I'm the battle arena assistant. Tell me if you need #help with anything. Talk to #Brutus or #Thonaton if you need anything else.");
				addHelp("I can't help you too much, but you can talk to Brutus or Thonatun if you need help.");
				addGoodbye("I hope you enjoy the Battle Arena!");

			}
		};

		npc1.setEntityClass("darkwizardnpc");
		npc1.setPosition(33, 41);
		npc1.setDirection(Direction.DOWN);
		npc1.initHP(100);
		zone.add(npc1);
	}
}
