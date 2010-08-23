package games.stendhal.server.maps.semos.blacksmith;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * The blacksmith's young assistant (original name: Hackim Easso).
 * He smuggles out weapons.
 * 
 * @see games.stendhal.server.maps.quests.MeetHackim
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class BlacksmithAssistantNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
		        ConversationStates.ATTENDING,
		        null,
		        new ChatAction() {
			        public void fire(final Player player, final Sentence sentence, final EventRaiser npc) {
				        if (player.hasQuest("meet_hackim")) {
					        npc.say("Hi again, " + player.getTitle()
					                + ". How can I #help you this time?");
				        } else {
					        npc.say("Hi stranger, I'm Hackim Easso, the blacksmith's assistant. Have you come here to buy weapons?");
					        player.setQuest("meet_hackim", "start");
				        }
			        }
		        });
		npc.addHelp("I'm the blacksmith's assistant. Tell me... Have you come here to buy weapons?");
		npc.addJob("I help Xoderos the blacksmith to make weapons for Deniran's army. I mostly only bring the coal for the fire and put the weapons up on the shelves. Sometimes, when Xoderos isn't looking, I like to use one of the swords to pretend I'm a famous adventurer!");
		npc.addGoodbye();
		
		npc.setDescription("You see Hackim Easso, the blacksmiths assistant.");
	}
}
