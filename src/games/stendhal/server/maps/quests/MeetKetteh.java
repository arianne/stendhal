package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Outfit;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Speak with Ketteh
 * 
 * PARTICIPANTS:
 * - Ketteh Wehoh, a woman who lives in the house next to the bakery.
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
public class MeetKetteh extends AbstractQuest{

	private void step1() {

		SpeakerNPC npc = npcs.get("Ketteh Wehoh");

		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
				null,
				new SpeakerNPC.ChatAction() {
					@Override
					public void fire(Player player, String text,
							SpeakerNPC engine) {

						Outfit outfit = player.getOutfit();
                        if (outfit.getDress() == 0) {
                            // The player is naked
                            engine.say("Who are you? Aiiieeeee!!! You're naked! Quickly, right-click on yourself and choose SET OUTFIT!\nShhh! Don't even think on clicking on the white bar at the bottom and writing to reply to me! And if you happen to talk to any of the other citizens, you'd better begin the conversation saying \"hi\". And don't be rude and just leave; say \"bye\" to end the conversation.\nAnd use Ctrl+Arrows to turn around and face me when I'm talking to you! Wait! I'm sure I've seen you with that fellow Nomyr, who's always peeking at the windows! Now use the arrow keys and get out of my room!");
                            player.setQuest("Ketteh", "seen_naked");

                        } else if (player.hasQuest("Ketteh") && player.getQuest("Ketteh").equals("seen_naked")) {
                            // OK, player is NOT naked this time, but was last time.
							engine.say("Hi again, " + player.getName() + ". How can I #shout at you this time?");
                            player.setQuest("Ketteh", "seen"); // don't be unforgiving

                        } else if (player.hasQuest("Ketteh")){
                            // We have met the player before and he was NOT naked last time nor is he now
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
				"If you don't put on some clothes and leave, I shall scream!",
				null);

        npc.add(ConversationStates.ATTENDING,
                "shout",
                null,
                ConversationStates.ATTENDING,
                null,
                new SpeakerNPC.ChatAction() {
                    @Override
					public void fire(Player player, String text,
                            SpeakerNPC engine) {
                        if (player.hasQuest("Ketteh") && player.getQuest("Ketteh").equals("seen_naked")) {
                            engine.say("I am glad to see you've acquired some clothes. There really is no need for walking around naked.");
                        } else {
                            engine.say("Sometimes naked people pass by; it makes me very angry. They are bringing down the tone of the whole place!");
                        }
                    }
                });
	}

	@Override
	public void addToWorld() {
		super.addToWorld();

		step1();
	}
}
