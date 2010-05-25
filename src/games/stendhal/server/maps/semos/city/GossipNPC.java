	package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

/**
 * A guy (original name: Nomyr Ahba) who looks into the windows of the bakery
 * and the house next to it.
 * 
 * Basically all he does is sending players to the retired adventurer at
 * the dungeon entrance. 
 */
//TODO: take NPC definition elements which are currently in XML and include here
public class GossipNPC extends SpeakerNPCFactory {

	@Override
	public void createDialog(final SpeakerNPC npc) {
		npc.add(ConversationStates.IDLE,
				ConversationPhrases.GREETING_MESSAGES,
				null,
		        ConversationStates.ATTENDING,
		        null,
		        new ChatAction() {
			        public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
				        // A little trick to make NPC remember if it has met
				        // player before anc react accordingly
				        // NPC_name quest doesn't exist anywhere else neither is
				        // used for any other purpose
				        if (player.isQuestCompleted("Nomyr")) {
					        npc.say("Hi again, " + player.getTitle()
					                + ". How can I #help you this time?");
				        } else {
					        npc.say("Heh heh... Oh, hello stranger! You look a bit disoriented... d'you want to hear the latest gossip?");
					        player.setQuest("Nomyr", "done");
				        }
			        }
		        });

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.INFORMATION_1,
				"The young people have joined the Imperial Deniran Army to fight in the south, so the city has been left almost unprotected against the hordes of monsters coming from the dungeons. Can you help us?",
				null);

		npc.add(ConversationStates.ATTENDING,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Huh. Well, you could help me by taking a peek through that other window, if you're not busy... I'm trying to figure out what's going on inside.",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				ConversationPhrases.YES_MESSAGES,
				null,
				ConversationStates.IDLE,
				"First of all, you should go talk to Hayunn Naratha. He's an great old hero, and he's also pretty much our only defender here... I'm sure he will gladly give you some advice! Good luck.",
				null);

		npc.add(ConversationStates.INFORMATION_1,
				ConversationPhrases.NO_MESSAGES,
				null,
				ConversationStates.IDLE,
				"Awww... so you're a coward, then? Huh.",
				null);

		npc.addHelp("I'm a... let's call me an \"observer\". I can tell you about all the latest rumours. Do you want to hear?");
		npc.addJob("I know every rumour that exists in Semos, and I invented most of them! The one about Hackim smuggling in weapons for wandering adventurers like you is true, though.");
		npc.addQuest("Thanks for asking, but I don't need anything right now.");
		npc.addGoodbye();
	}
}
