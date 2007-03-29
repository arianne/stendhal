package games.stendhal.server.maps.abstrakt;

import games.stendhal.common.Direction;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.StandardInteraction;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.TurnListener;
import games.stendhal.server.events.TurnNotifier;
import games.stendhal.server.maps.ZoneConfigurator;
import games.stendhal.server.scripting.ScriptAction;
import games.stendhal.server.scripting.ScriptInGroovy;
import games.stendhal.server.scripting.ScriptingNPC;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * code for abstract/int_admin_playground which creates a NPC to help testers.
 * 
 * @author hendrik
 */
public class IL0_AdminPlayground implements ZoneConfigurator {

	// WARNING: This code has been ported from a groovy script to java on a very low level.
	//          It compiles fine, but will throw an NullPointerException at runtime because of
	//          the missing "game" attribute. It needs some refactoring.


	// TODO remove this dependency
	private static ScriptInGroovy game = null;

	/**
	 * Configure a zone.
	 *
	 * @param	zone		The zone to be configured.
	 * @param	attributes	Configuration attributes.
	 */
	public void configureZone(StendhalRPZone zone, Map<String, String> attributes) {
		buildDebuggera(zone, attributes);
	}


	boolean debuggeraEnabled = false;

	class AdminCondition extends SpeakerNPC.ChatCondition {
		@Override
		public boolean fire(Player player, String text, SpeakerNPC engine) {
			return (player.has("adminlevel") && (player.getInt("adminlevel") >= 5000));
		}
	}

	class DebuggeraEnablerAction extends SpeakerNPC.ChatAction {
		boolean enabled = false;

		public DebuggeraEnablerAction(boolean enable) {
			this.enabled = enable;
		}

		@Override
		public void fire(Player player, String text, SpeakerNPC engine) {
			// TODO        debuggeraEnabled = enabled;
			if (enabled) {
				engine.say("Thanks.");
			} else {
				engine.say("OK, I will not talk to strangers");
			}
		}
	}

	class QuestsAction extends SpeakerNPC.ChatAction {
		ScriptInGroovy game;

		public QuestsAction(ScriptInGroovy game) {
			this.game = game;
		}

		@Override
		public void fire(Player player, String text, SpeakerNPC engine) {

			// list quest
			StringBuffer sb = new StringBuffer("Your quest states are:");
			List<String> quests = player.getQuests();
			for (String quest : quests) {
				sb.append("\r\n" + quest + " = " + player.getQuest(quest));
			}

			// change quest
			int pos = text.indexOf(" ");
			if (pos > -1) {
				String quest = text.substring(pos + 1);
				pos = quest.indexOf("=");
				if (pos > -1) {
					String value = quest.substring(pos + 1);
					quest = quest.substring(0, pos);
					sb.append("\r\n\r\nSet \"" + quest + "\" to \"" + value + "\"");
					game.addGameEvent(player.getName(), "alter_quest", Arrays.asList(player.getName(), quest, value));
					player.setQuest(quest.trim(), value.trim());
				}
			}
			engine.say(sb.toString());
		}
	}


	class TeleportNPCAction extends SpeakerNPC.ChatAction {
		ScriptInGroovy game;

		public TeleportNPCAction(ScriptInGroovy game) {
			this.game = game;
		}

		@Override
		public void fire(Player player, String text, SpeakerNPC engine) {
			game.add(null, new TeleportScriptAction(player, engine, text, game));
		}
	}

	class TeleportScriptAction extends ScriptAction {
		private ScriptInGroovy game;

		private Player player;

		private SpeakerNPC engine;

		private String text;

		private int destIdx = 0;

		private int counter = 0;

		private int inversedSpeed = 3;

		private int textCounter = 0;

		private boolean beamed = false;

		// syntax-error:  private final String[] MAGIC_PHRASE = {"Across the land,", "Across the sea.", "Friends forever,", "We will always be."};

		public TeleportScriptAction(Player player, SpeakerNPC engine, String text, ScriptInGroovy game) {
			this.player = player;
			this.engine = engine;
			this.text = text;
			this.game = game;
		}

		@Override
		public void fire() {
			counter++;
			if (!beamed) {
				// speed up
				if (counter % inversedSpeed == 0) {
					Direction direction = player.getDirection();
					direction = Direction.build((direction.get()) % 4 + 1);
					player.setDirection(direction);
					game.modify(player);
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
							game.transferPlayer(player, "int_admin_playground", 10, 10);
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
					game.modify(player);
					if (direction == Direction.DOWN) {
						inversedSpeed++;
						if (inversedSpeed == 3) {
							game.remove(this);
						}
					}
				}
			}
		}
	}

	public class SightseeingAction extends SpeakerNPC.ChatAction implements TurnListener {
		private ScriptInGroovy game;

		private Player player;

		private List<String> zones;

		private int counter = 0;

		public SightseeingAction(ScriptInGroovy game, StendhalRPWorld world) {
			this.game = game;

			zones = new ArrayList();
			Iterator itr = world.iterator();
			while (itr.hasNext()) {
				StendhalRPZone zone = (StendhalRPZone) itr.next();
				zones.add(zone.getID().getID());
			}
		}

		@Override
		public void fire(Player player, String text, SpeakerNPC engine) {
			this.player = player;
			counter = 0;
			player.sendPrivateText("Let's start");
			TurnNotifier.get().notifyInTurns(10, this, null);
		}

		public void onTurnReached(int currentTurn, String message) {
			try {
				String zoneName = zones.get(counter);
				if (!game.transferPlayer(player, zoneName, 5, 5)) {
					if (!game.transferPlayer(player, zoneName, 50, 50)) {
						if (!game.transferPlayer(player, zoneName, 20, 20)) {
							if (!game.transferPlayer(player, zoneName, 100, 100)) {
								if (!game.transferPlayer(player, zoneName, 100, 5)) {
									player.sendPrivateText("Sorry, did not find a free spot in " + zoneName);
								} else {
									player.sendPrivateText("Welcome in " + zoneName);
								}
							} else {
								player.sendPrivateText("Welcome in " + zoneName);
							}
						} else {
							player.sendPrivateText("Welcome in " + zoneName);
						}
					} else {
						player.sendPrivateText("Welcome in " + zoneName);
					}
				} else {
					player.sendPrivateText("Welcome in " + zoneName);
				}
			} catch (Exception e) {
				Logger.getLogger(SightseeingAction.class).error(e, e);
			}

			counter++;
			if (counter < zones.size()) {
				TurnNotifier.get().notifyInTurns(10, this, null);
			}
		}
	}



	private void buildDebuggera(StendhalRPZone zone, Map<String, String> attributes) {

		// Create NPC
		ScriptingNPC npc = new ScriptingNPC("Debuggera");
		npc.setClass("girlnpc");

		// Place NPC in int_admin_playground 
		// if this script is executed by an admin
		String myZone = "int_admin_playground";
		game.setZone(myZone);
		npc.set(4, 11);
		npc.setDirection(Direction.DOWN);
		game.add(npc);

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
		npc
						.behave(
										Arrays.asList("special", "special child"),
										"I can see another world in my dreams. That are more thans dreams. There the people are sitting in front of machines called computers. This are realy strange people. They cannot use telepathy without something they call inter-network. But these people and machines are somehow connected to our world. If I concentrate, I can #change thinks in our world.");
		// npc.behave("verschmelzung", "\r\nYou have one hand,\r\nI have the other.\r\nPut them together,\r\nWe have each other.");
		npc.add(ConversationStates.ATTENDING, Arrays.asList("susi"), null, ConversationStates.ATTENDING, "Yes, she is my twin sister. People consider her normal because she hides her special abilities.", null);

		// change
		npc.add(ConversationStates.ATTENDING, Arrays.asList("change", "change"), new StandardInteraction.QuestInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "I can teleport you.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("change", "change"), new StandardInteraction.QuestNotInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "Do you want to become my #friend?", null);

		// friends
		npc.add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new StandardInteraction.QuestInStateCondition("debuggera", "friends"), ConversationStates.ATTENDING, "We are friends.", null);
		npc.add(ConversationStates.ATTENDING, Arrays.asList("friend", "friends"), new StandardInteraction.QuestNotInStateCondition("debuggera", "friends"), ConversationStates.INFORMATION_1, "Please repeat:\r\n                        \"A circle is round,\"", null);
		npc.add(ConversationStates.INFORMATION_1, Arrays.asList("A circle is round,", "A circle is round"), null, ConversationStates.INFORMATION_2, "\"it has no end.\"", null);
		npc.add(ConversationStates.INFORMATION_2, Arrays.asList("it has no end.", "it has no end"), null, ConversationStates.INFORMATION_3, "\"That's how long,\"", null);
		npc.add(ConversationStates.INFORMATION_3, Arrays.asList("That's how long,", "That's how long", "Thats how long,", "Thats how long"), null, ConversationStates.INFORMATION_4, "\"I will be your friend.\"", null);
		npc.add(ConversationStates.INFORMATION_4, Arrays.asList("I will be your friend.", "I will be your friend"), null, ConversationStates.ATTENDING, "Cool. We are friends now.", new StandardInteraction.SetQuestAction("debuggera", "friends"));

		// quests
		npc.add(ConversationStates.ATTENDING, "quest", new AdminCondition(), ConversationStates.ATTENDING, null, new QuestsAction(game));

		// teleport
		npc.add(ConversationStates.ATTENDING, Arrays.asList("teleport", "teleportme"), new AdminCondition(), ConversationStates.IDLE, null, new TeleportNPCAction(game));

		StendhalRPWorld world = StendhalRPWorld.get();
		npc.add(ConversationStates.ATTENDING, Arrays.asList("sightseeing", "memory", "memoryhole"), new AdminCondition(), ConversationStates.IDLE, null, new SightseeingAction(game, world));
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
