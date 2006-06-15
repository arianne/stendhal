package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPRuleProcessor;
import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.Player;
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
				SpeakerNPC.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					public void fire(Player player, String text,
							SpeakerNPC engine) {

                        int outfit = player.getInt("outfit");
                        int clothes_index = (outfit / 100) % 100;

                        // Check whether player is naked
                        if (clothes_index == 0) {
                            engine.say("Who are you? Aiiieeeee!!! You're naked! Right-click on you and choose SET OUTFIT! Shhh! Don't even think on clicking on the white bar at the bottom and write to reply to me! And if you happen to talk to anyone in the city you'd better begin the conversation saying HI. And don't be gross and just leave: say BYE to end the conversation. And use #CTRL #+ #arrow to turn around and face me when I'm talking to you! Wait! I am sure you are a friend of that onlooker Nomyr who's always peeking at the windows! Now use the #arrows and go out!");
                            player.setQuest("Ketteh", "seen_naked");

                        // OK, player is NOT naked this time, but was last time.
                        } else if (player.hasQuest("Ketteh") && player.getQuest("Ketteh").equals("seen_naked")) {
							engine.say("Hi again, " + player.getName()
									+ ". How can I #shout at you this time?");
                            player.setQuest("Ketteh", "seen"); // don't be unforgiving

                        // We have met the player before and it was NOT naked last time nor is it know
                        } else if (player.hasQuest("Ketteh")){
                            engine.say("Hi again, " + player.getName() + ".");
                        } else {

                            // We haver never seen the player before.
                            engine.say("Hi " + player.getName() + ", nice to meet you.");
                            player.setQuest("Ketteh", "seen");
                        }
					}
				});

		npc.add(ConversationStates.ATTENDING,
				"no",
				null,
				ConversationStates.IDLE,
				"Ok, don't move. I'm calling the law enforcers!",
				null);

        npc.add(ConversationStates.ATTENDING,
                "shout",
                null,
                ConversationStates.ATTENDING,
                null,
                new SpeakerNPC.ChatAction() {
                    public void fire(Player player, String text,
                            SpeakerNPC engine) {
                        if (player.hasQuest("Ketteh") && player.getQuest("Ketteh").equals("seen_naked")) {
                            engine.say("Oh, good, you wear clothes this time. The last time we met, you were naked!");
                        } else {
                            engine.say("Sometimes naked people pass by. I'll get really mad if this happens.");
                        }
                    }
                });
	}

	public MeetKetteh(StendhalRPWorld w, StendhalRPRuleProcessor rules) {
		this.npcs = NPCList.get();

		step1();
	}
}
