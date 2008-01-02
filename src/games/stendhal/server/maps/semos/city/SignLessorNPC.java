	package games.stendhal.server.maps.semos.city;

import games.stendhal.server.entity.npc.ConversationPhrases;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPCFactory;
import games.stendhal.server.entity.npc.condition.AndCondition;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.condition.TextHasParameterCondition;
import games.stendhal.server.entity.player.Player;

/**
 * A merchant (original name: XXX) who rents signs to players.
 *
 * The player has to have at least level 5 to prevent abuse by newly created characters.
 */
public class SignLessorNPC extends SpeakerNPCFactory {
	protected String text;

	@Override
	public void createDialog(SpeakerNPC npc) {
		npc.addGreeting();
		npc.addJob("I #rent signs for a day.");
		npc.addHelp("If you want to #rent a sign, just tell me what i should write up on it.");

		npc.add(ConversationStates.ATTENDING, "rent", 
			new LevelLessThanCondition(6), 
			ConversationStates.ATTENDING, 
			"Oh sorry, i don't rent signs to people who have so little experience like you.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new NotCondition(new TextHasParameterCondition())), 
			ConversationStates.ATTENDING, 
			"Just tell me #rent followed by the text i should write on it.",
			null);

		npc.add(ConversationStates.ATTENDING, "rent", 
			new AndCondition(new LevelGreaterThanCondition(5), new TextHasParameterCondition()), 
			ConversationStates.BUY_PRICE_OFFERED, 
			"A sign costs 100 money for 24 hours. Do you want to rent one?",
			new SpeakerNPC.ChatAction() {

				@Override
				public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
					String temp = sentence.getOriginalText().trim();
					text = temp.substring(5).trim();
				}
			
		});
		
		// TODO: implement yes in case a sign can be added
		// TODO: implement yes in case there is no spot for another sign

		npc.add(ConversationStates.BUY_PRICE_OFFERED, 
			ConversationPhrases.NO_MESSAGES, null,
			ConversationStates.ATTENDING,
			"If you change your mind, just talk to me again.", null);

		npc.addGoodbye();
	}
}
