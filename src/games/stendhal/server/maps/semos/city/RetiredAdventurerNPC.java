package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.player.Player;


/**
 * An old hero (original name: Hayunn Naratha) who guards the dungeon entrance.
 *
 * @see games.stendhal.server.maps.quests.BeerForHayunn
 * @see games.stendhal.server.maps.quests.MeetHayunn
 */
public class RetiredAdventurerNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
				ConversationStates.ATTENDING,
		        null,
		        new SpeakerNPC.ChatAction() {
			        @Override
			        public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
				        // A little trick to make NPC remember if it has met
				        // player before anc react accordingly
				        // NPC_name quest doesn't exist anywhere else neither is
				        // used for any other purpose
				        if (!player.hasQuest("meet_hayunn")) {
					        npc.say("You've probably heard of me; Hayunn Naratha, a retired adventurer. Have you read my book? No? It's called \"Know How To Kill Creatures\". Maybe we could talk about adventuring, if you like?");
					        player.setQuest("meet_hayunn", "start");
				        } else {
					        npc.say("Hi again, " + player.getTitle() + ". How can I #help you this time?");
				        }
			        }
		        });
		npc.addHelp("As I say, I'm a retired adventurer, and now I teach people. Do you want me to teach you about killing creatures?");
		npc.addJob("My job is to guard the people of Semos from any creature that might escape this vile dungeon! With all our young people away battling Blordrough's evil legions to the south, the monsters down there are getting more confident about coming to the surface.");
		npc.addGoodbye();
		// further behaviour is defined in quests.
	}
}
