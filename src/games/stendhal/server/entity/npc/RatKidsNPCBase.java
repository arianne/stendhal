package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

// import org.apache.log4j.Logger;

/**
 * Base class for rat kid NPCs.
 * 
 * @author Norien
 */
public abstract class RatKidsNPCBase extends SpeakerNPC {

	//	private static Logger logger = Logger.getLogger(RatKidsBase.class);
	private static final String QUEST_SLOT = "find_rat_kids";

	public RatKidsNPCBase(final String name) {
		super(name);
	}

	@Override
	protected abstract void createPath();

	@Override
	protected void createDialog() {
		add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				null, ConversationStates.IDLE, null, new RatKidGreetingAction());
	}

	/**
	 * ChatAction common to all rat kid NPCs.
	 * 
	 * @author Norien
	 */
	private static class RatKidGreetingAction implements ChatAction {
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			if (!player.hasQuest(QUEST_SLOT) || player.isQuestInState(QUEST_SLOT, "rejected")) {
				npc.say("Mother says I mustn't talk to strangers.");
			} else {
				final String npcQuestText = player.getQuest(QUEST_SLOT);
				final String[] npcDoneText = npcQuestText.split(":");

				final String lookStr;
				final String saidStr;
				if (npcDoneText.length > 1) {
					lookStr = npcDoneText[0].toLowerCase();
					saidStr = npcDoneText[1].toLowerCase();			 

					final List<String> list = Arrays.asList(lookStr.split(";"));
					String npcName = npc.getName().toLowerCase();			
					if (list.contains(npcName) || player.isQuestCompleted(QUEST_SLOT)) {
						npc.say("Oh hello again.");
					} else if ( npcDoneText.length > 1) {
						player.setQuest(QUEST_SLOT, lookStr + ";" + npcName
								+ ":" + saidStr);//
						npc.say("Hello my name is " + npc.getName() + ". Please tell mother that I am ok.");
						player.addXP(500);
					} else {
						npc.say("Mother says I mustn't talk to strangers.");
					}
				}
			}
		}
	}
}