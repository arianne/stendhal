package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.Behaviours;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Speak with Ketteh
 * 
 * PARTICIPANTS:
 * - Ketteh
 * 
 * STEPS:
 * - Talk to Ketteh to activate the quest and keep speaking with Ketteh.
 * 
 * REWARD:
 * - No XP
 * - No money
 * 
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetKetteh implements IQuest {

	private NPCList npcs;

	private void step1() {

		SpeakerNPC npc = npcs.get("Ketteh Wehoh");

		npc.add(ConversationStates.IDLE,
				Behaviours.GREETING_MESSAGES,
				new SpeakerNPC.ChatCondition() {
					public boolean fire(Player player, SpeakerNPC engine) {
						// TODO: check whether the player is naked
						return true; // player.equals(0);
					}
				},
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {
						// A little trick to make NPC remember if it has met
						// player before anc react accordingly
						// NPC_name quest doesn't exist anywhere else neither is
						// used for any other purpose
						if (!player.isQuestCompleted("Ketteh")) {
							engine.say("Who are you? Aiiieeeee!!! You're naked! Right-click on you and choose SET OUTFIT! Shhh! Don't even think on clicking on the white bar at the bottom and write to reply to me! And if you happen to talk to anyone in the city you'd better begin the conversation saying HI. And don't be gross and just leave: say BYE to end the conversation. And use #CTRL #+ #arrow to turn around and face me when I'm talking to you! Wait! I am sure you are a friend of that onlooker Nomyr who's always peeking at the windows! Now use the #arrows and go out!");
							player.setQuest("Ketteh", "done");
						} else {
							engine.say("Hi again, " + player.getName()
									+ ". How can I #shout at you this time?");
						}
					}
				});

		npc.add(ConversationStates.ATTENDING,
				"no",
				null,
				ConversationStates.IDLE,
				"Ok, don't move. I'm calling the law enforcers!",
				null);

	}

	public MeetKetteh(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();

		step1();
	}
}
