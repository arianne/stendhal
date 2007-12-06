package games.stendhal.server.script;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.action.SetQuestAction;
import games.stendhal.server.entity.npc.condition.QuestInStateCondition;
import games.stendhal.server.entity.npc.condition.QuestNotInStateCondition;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.scripting.ScriptImpl;
import games.stendhal.server.scripting.ScriptingNPC;
import games.stendhal.server.scripting.ScriptingSandbox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import marauroa.common.game.IRPZone;

import org.apache.log4j.Logger;


/**
 * code for abstract/int_admin_playground which creates a NPC to help testers.
 *
 * @author hendrik
 */
public class Debuggera extends ScriptImpl {

//	boolean debuggeraEnabled;

	class AdminCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
			return (player.getAdminLevel() >= 5000);
		}
	}

	class DebuggeraEnablerAction extends SpeakerNPC.ChatAction {
		boolean enabled;

		public DebuggeraEnablerAction(boolean enable) {
			this.enabled = enable;
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			// TODO        debuggeraEnabled = enabled;
			if (enabled) {
				engine.say("Thanks.");
			} else {
				engine.say("OK, I will not talk to strangers");
			}
		}
	}

	class QuestsAction extends SpeakerNPC.ChatAction {
		ScriptingSandbox sandbox;

		public QuestsAction(ScriptingSandbox sandbox) {
			this.sandbox = sandbox;
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {

			// list quest
			StringBuffer sb = new StringBuffer("Your quest states are:");
			List<String> quests = player.getQuests();
			for (String quest : quests) {
				sb.append("\r\n" + quest + " = " + player.getQuest(quest));
			}

			// change quest
			String quest = sentence.getObjectName();
			int pos = quest.indexOf("=");
			if (pos > -1) {
				String value = quest.substring(pos + 1);
				quest = quest.substring(0, pos);
				sb.append("\r\n\r\nSet \"" + quest + "\" to \"" + value + "\"");
				sandbox.addGameEvent(player.getName(), "alter_quest", Arrays.asList(player.getName(), quest, value));
				player.setQuest(quest.trim(), value.trim());
			}
			engine.say(sb.toString());
		}
	}


	class TeleportNPCAction extends SpeakerNPC.ChatAction {
		ScriptingSandbox sandbox;

		public TeleportNPCAction(ScriptingSandbox sandbox) {
			this.sandbox = sandbox;
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			TurnNotifier.get().notifyInTurns(0, new TeleportScriptAction(player, engine, sentence, sandbox));
		}
	}

	static class TeleportScriptAction implements TurnListener {
		private ScriptingSandbox sandbox;

		private Player player;

		private SpeakerNPC engine;

//		private Sentence sentence;
//
//		private int destIdx = 0;

		private int counter;

		private int inversedSpeed = 3;

		private int textCounter;

		private boolean beamed;

		// syntax-error:  private final String[] MAGIC_PHRASE = {"Across the land,", "Across the sea.", "Friends forever,", "We will always be."};

		public TeleportScriptAction(Player player, SpeakerNPC engine, Sentence sentence, ScriptingSandbox sandbox) {
			this.player = player;
			this.engine = engine;
			//this.sentence = sentence;
			this.sandbox = sandbox;
		}

		public void onTurnReached(int currentTurn) {
			boolean keepRunning = true;
			counter++;
			if (!beamed) {
				// speed up
				if (counter % inversedSpeed == 0) {
					Direction direction = player.getDirection();
					direction = Direction.build((direction.get()) % 4 + 1);
					player.setDirection(direction);
					sandbox.modify(player);
					if (direction == Direction.DOWN) {
						switch (textCounter) {
						case 0:
							engine.say("Across the land,");
							inversedSpeed--;
							break;
						case 1:
							engine.say("Across the sea.");
							inversedSpeed--;
							break;
						case 2:
							engine.say("Friends forever,");
							break;
						case 3:
							engine.say("We will always be.");
							break;
						default:
							// Teleport to a near by spot

							StendhalRPZone zone = sandbox.getZone(player);
							int x = player.getX();
							int y = player.getY();
							int[][] tele_offsets = {{7, 7}, {7, -7}, {-7, 7}, {-7, -7}};
							Random random = new Random();

							for (int i = 0; i < 3; i++) {
								int r = random.nextInt(tele_offsets.length);
								if (player.teleport(zone, x + tele_offsets[r][0], y + tele_offsets[r][1], null, null)) {
									break;
								}
							}

							inversedSpeed = 1;
							beamed = true;
							break;
						}
						textCounter++;
					}
				}
			} else {
				// slow down
				if (counter % inversedSpeed == 0) {
					Direction direction = player.getDirection();
					direction = Direction.build((direction.get()) % 4 + 1);
					player.setDirection(direction);
					sandbox.modify(player);
					if (direction == Direction.DOWN) {
						inversedSpeed++;
						if (inversedSpeed == 3) {
							keepRunning = false;
						}
					}
				}
			}
			if (keepRunning) {
				TurnNotifier.get().notifyInTurns(0, this);
			}
		}
	}

	public class SightseeingAction extends SpeakerNPC.ChatAction implements TurnListener {
	//	private ScriptingSandbox sandbox;

		private Player player;

		private List<String> zones;

		private int counter;

		public SightseeingAction(ScriptingSandbox sandbox, StendhalRPWorld world) {
		//	this.sandbox = sandbox;

			zones = new ArrayList<String>();

			for(IRPZone irpZone : world) {
				StendhalRPZone zone = (StendhalRPZone) irpZone;
				zones.add(zone.getName());
			}
		}

		@Override
		public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
			this.player = player;
			counter = 0;
			player.sendPrivateText("Let's start");
			TurnNotifier.get().notifyInTurns(10, this);
		}

		public void onTurnReached(int currentTurn) {
			try {
				String zoneName = zones.get(counter);
				StendhalRPZone zone = StendhalRPWorld.get().getZone(zoneName);

				int[][] tele_xy = {{5, 5}, {50, 50}, {20, 20}, {100, 100}, {100, 5}};
				boolean foundSpot = false;

				for (int i = 0; i < tele_xy.length; i++) {
					if (player.teleport(zone, tele_xy[i][0], tele_xy[i][1], null, null)) {
						player.sendPrivateText("Welcome in " + zoneName);
						foundSpot = true;
						break;
					}
				}
				if (!foundSpot) {
					player.sendPrivateText("Sorry, did not find a free spot in " + zoneName);
				}
			} catch (Exception e) {
				Logger.getLogger(SightseeingAction.class).error(e, e);
			}

			counter++;
			if (counter < zones.size()) {
				TurnNotifier.get().notifyInTurns(10, this);
			}
		}
	}

	@Override
	public void load(Player admin, List<String> args, ScriptingSandbox sandbox) {
		super.load(admin, args, sandbox);

		// Create NPC
		ScriptingNPC npc = new ScriptingNPC("Debuggera");
		npc.setEntityClass("girlnpc");

		// Place NPC in int_admin_playground on server start
		String myZone = "int_admin_playground";
		sandbox.setZone(myZone);
		int x = 4;
		int y = 11;

		// If this script is executed by an admin, Debuggera will be placed next to him/her.
		if (admin != null) {
			sandbox.setZone(sandbox.getZone(admin));
			x = admin.getX() + 1;
			y = admin.getY();
		}

		// Set zone and position
		npc.setPosition(x, y);
		npc.setDirection(Direction.DOWN);
		sandbox.add(npc);

		//
		npc.add(ConversationStates.IDLE, Arrays.asList("hi", "hello", "greetings", "hola"), null, ConversationStates.IDLE, "My mom said, i am not allowed to talk to strangers.", null);
		npc.behave("bye", "Bye.");

		// Greating and admins may enable or disable her
		npc.add(ConversationStates.IDLE, Arrays.asList("hi", "hello", "greetings", "hola"), new AdminCondition(), ConversationStates.ATTENDING, "Hi, game master. Do you think i am #crazy?", null);
		/*    npc.add(ConversationStates.IDLE, [ "hi","hello","greetings","hola" ], new AdminCondition(), ConversationStates.QUESTION_1, "May I talk to strangers?", null);
		 npc.add(ConversationStates.QUESTION_1, SpeakerNPC.YES_MESSAGES, new AdminCondition(), ConversationStates.ATTENDING, null, new DebuggeraEnablerAction(true));
		 npc.add(ConversationStates.QUESTION_1, "no", new AdminCondition(), ConversationStates.ATTENDING, null, new DebuggeraEnablerAction(false));
		 */
		npc.behave(Arrays.asList("insane", "crazy", "mad"), "Why are you so mean? I AM NOT INSANE. My mummy says, I am a #special child.");
		npc.behave(Arrays.asList("special", "special child"), "I can see another world in my dreams. That are more thans dreams. There the people are sitting in front of machines called computers. This are realy strange people. They cannot use telepathy without something they call inter-network. But these people and machines are somehow connected to our world. If I concentrate, I can #change thinks in our world.");
		// npc.behave("verschmelzung", "\r\nYou have one hand,\r\nI have the other.\r\nPut them together,\r\nWe have each other.");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("susi"), null, ConversationStates.ATTENDING, "Yes, she is my twin sister. People consider her normal because she hides her special abilities.", null);

		// change
		npc.add(ConversationStates.ATTENDING, Arrays.asList("change", "change"), new QuestInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "I can teleport you.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("change", "change"), new QuestNotInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "Do you want to become my #friend?", null);

		// friends
		npc.add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new QuestInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "We are friends.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new QuestNotInStateCondition("debuggera", "friends"), ConversationStates.INFORMATION_1, "Please repeat:\r\n                        \"A circle is round,\"", null);
		npc.add(ConversationStates.INFORMATION_1, Arrays.asList("A circle is round,", "A circle is round"), null, ConversationStates.INFORMATION_2, "\"it has no end.\"", null);
		npc.add(ConversationStates.INFORMATION_2, Arrays.asList("it has no end.", "it has no end"), null, ConversationStates.INFORMATION_3, "\"That's how long,\"", null);
		npc.add(ConversationStates.INFORMATION_3, Arrays.asList("That's how long,", "That's how long", "Thats how long,", "Thats how long"), null, ConversationStates.INFORMATION_4, "\"I will be your friend.\"", null);
		npc.add(ConversationStates.INFORMATION_4, Arrays.asList("I will be your friend.", "I will be your friend"), null, ConversationStates.ATTENDING, "Cool. We are friends now.", new SetQuestAction("debuggera", "friends"));

		// quests
		npc.add(ConversationStates.ATTENDING, "quest", new AdminCondition(), ConversationStates.ATTENDING, null, new QuestsAction(sandbox));

		// teleport
		npc.add(ConversationStates.ATTENDING, Arrays.asList("teleport", "teleportme"), new AdminCondition(), ConversationStates.IDLE, null, new TeleportNPCAction(sandbox));

		StendhalRPWorld world = StendhalRPWorld.get();
		npc.add(ConversationStates.ATTENDING, Arrays.asList("sightseeing", "memory", "memoryhole"), new AdminCondition(), ConversationStates.IDLE, null, new SightseeingAction(sandbox, world));
	}
	/*
	 Make new friends,
	 but keep the old.
	 One is silver,
	 And the other gold,

	 You help me,
	 And I'll help you.
	 And together,
	 We will see it through.

	 The sky is blue,
	 The Earth Earth is green.
	 I can help,
	 To keep it clean.

	 */

}
