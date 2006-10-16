package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Speak with Io
 * PARTICIPANTS:
 * - Io
 *
 * STEPS:
 * - Talk to Io to activate the quest and keep speaking with Io.
 *
 * REWARD:
 * - 10 XP (check that user's level is lesser than 15)
 * - 5 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetIo extends AbstractQuest {

	private void step_1() {

		SpeakerNPC npc = npcs.get("Io Flotto");

		npc.add(ConversationStates.ATTENDING,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_1,
				"Type #/who to ascertain the names of those adventurers who are currently present in the world of Stendhal. Do you want to learn the second basic element of telepathy?",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_2,
				"Type #/where #username to discern where in Stendhal that person is currently roaming; you can use #/where #sheep to keep track of any sheep you might own. To understand the system used for defining positions in Stendhal, try asking #Zynn; he knows more about it that I do. Ready for the third lesson?",
				null);

		npc.add(ConversationStates.INFORMATION_2,
				"Zynn",
				null,
				ConversationStates.INFORMATION_2,
				"His full name is Zynn Iwuhos. He spends most of his time in the library, making maps and writing historical record books. Ready for the next lesson?",
				null);

		npc.add(ConversationStates.INFORMATION_2,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_3,
				"Type #/tell #username #message or #/msg #username #message to talk to anybody you wish, no matter where in Stendhal that person is; and without the risk of being overheard.  You can type #// #response to continue talking to the last person you send a message to. Ready to learn my fourth tip?",
				null);

		npc.add(ConversationStates.INFORMATION_3,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_4,
				"Press #Shift+Up at the same time to recall things you previously said, in case you need to repeat yourself. You can also use #Ctrl+L if you are having trouble. Okay, shall we move on to the fifth lesson?",
				null);

		npc.add(ConversationStates.INFORMATION_4,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_5,
				"Type #/support #<message> to report a problem to any administrators who happen to be online at that moment. You can also try IRC, if you are still having problems; start up any IRC client and join channel ##arianne on the server #irc.freenode.net\nOkay, time for your last lesson in mental manipulation!",
				null);

		npc.add(ConversationStates.INFORMATION_5,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_6,
				"You can travel to the astral plane at any time, thereby saving and closing your game. Just type #/quit, or press the #Esc key, or even simply close the window. Okay! Hmm, I think you want to learn how to float in the air like I do.",
				null);

		/** Give the reward to the patient newcomer user */
		npc.add(ConversationStates.INFORMATION_6,
				SpeakerNPC.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text, SpeakerNPC engine) {
						int level = player.getLevel();
						String answer;
						if (level < 15) {
							StackableItem money = (StackableItem) StendhalRPWorld.get()
									.getRuleManager().getEntityManager().getItem(
											"money");
		
							money.setQuantity(10);
							player.equip(money);
		
							player.addXP(10);
		
							player.notifyWorldAboutChanges();
		
							answer = "Remember, don't let anything disturb your concentration.";
						} else {
							answer = "Hey! I know what you're thinking, and I don't like it!";
						}
		
						engine.say("*yawns* Maybe I'll show you later... I don't want to overload you with too much information at once. You can get a summary of all those lessons at any time, incidentally, just by typing #/help.\n " + answer);
					}
				});

		npc.add(ConversationStates.ANY,
				"no",
				null,
				ConversationStates.IDLE,
				"If you ever decide to widen the frontiers of your mind a bit more, drop by and say hello. Farewell for now!",
				null);
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
