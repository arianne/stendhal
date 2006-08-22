package games.stendhal.server.maps.quests;

import games.stendhal.server.entity.Player;
import games.stendhal.server.entity.npc.SpeakerNPC;

/**
 * QUEST: Speak with Monogenes
 * PARTICIPANTS:
 * - Monogenes
 *
 * STEPS:
 * - Talk to Monogenes to activate the quest and keep speaking with Monogenes.
 *
 * REWARD:
 * - 10 XP (check that user's level is lesser than 5)
 * - No money
 * 
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetMonogenes extends AbstractQuest {

	private void step_1() {
		SpeakerNPC npc=npcs.get("Monogenes");
		
		npc.add(1,"yes",null,50,"Only ask them about what they bring the conversation around to: the WORDS that are bolded in blue color. Otherwise, you can get a harsh answer although you usually can ask about their #job , #help, #offer or #quest. Do you want to know where city's main buildings are?", null);
		npc.add(1,"no",null,0,"And how are you supposed to know what's happening? By reading the Semos tribune? Bye!", null);
		
		npc.add(50,"yes",null,1,"Sometimes it is helpful to read the city's wooden signs by right-clicking on them and choosing LOOK. I can direct you to the #bank, the #library, the #tavern, the #temple, the #blacksmith or the #village.", null);
		npc.add(50,"no",null,0,"Oh I see... You are of that kind of persons that don't like asking for directions huh? Well, good luck finding the secretly hidden mmhmmhmm!", null);
		
		npc.addReply("bank", "The bank is precisely this building next to me. I thought the big chest on the front would have given you a clue.");
		npc.addReply("library", "The library is west from here, following the path. There's an OPEN BOOK AND A FEATHER sign over one of the two doors.");
		npc.addReply("tavern", "The tavern is southeast from here, following the path. You'll see a big INN sign over the door. You can't miss it.");
		npc.addReply("temple", "The temple is the second building southeast from here, following the path. There's a small CROSS over the roof.");
		npc.addReply("blacksmith", "The blacksmith's shop is southwest from here, following the path. There's a small SWORD sign over the door.");
		npc.addReply("village", "The village is southwest from here, following the path. There you can buy sheeps to breed.");
		
		/** Give the reward to the polite newcomer user */
		npc.add(1,"bye",null,0,null,new SpeakerNPC.ChatAction() {
			public void fire(Player player, String text, SpeakerNPC engine) {
				int level=player.getLevel();
				if (level < 15) {
					engine.say("Bye, my friend. I hope my indications have been helpful...");
					player.addXP(10);
					player.notifyWorldAboutChanges();
				} else {
					engine.say("It's curious... Now that I think about it, I would have betted I had seen you in Semos before...");
				}
			}
		});
	}
	
	@Override
	public void addToWorld() {
		super.addToWorld();

		step_1();
	}
}
