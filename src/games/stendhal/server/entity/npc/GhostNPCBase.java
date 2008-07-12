package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for ghost NPCs.
 * 
 * @author Martin Fuchs
 */
public abstract class GhostNPCBase extends SpeakerNPC {
	public GhostNPCBase(final String name) {
		super(name);
	}

	@Override
	protected abstract void createPath();

	@Override
	protected void createDialog() {
		add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES,
				null, ConversationStates.IDLE, null, new GhostGreetingAction());
	}

	/**
	 * ChatAction common to all ghost NPCs.
	 * 
	 * @author Martin Fuchs
	 */
	private static class GhostGreetingAction extends SpeakerNPC.ChatAction {
		@Override
		public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
			if (!player.hasQuest("find_ghosts")) {
				player.setQuest("find_ghosts", "looking:said");
			}
			final String npcQuestText = player.getQuest("find_ghosts");
			final String[] npcDoneText = npcQuestText.split(":");
			final String lookStr;
			if (npcDoneText.length > 1) {
				lookStr = npcDoneText[0];
			} else {
				lookStr = "";
			}
			final String saidStr;
			if (npcDoneText.length > 1) {
				saidStr = npcDoneText[1];
			} else {
				saidStr = "";
			}
			final List<String> list = Arrays.asList(lookStr.split(";"));
			if (list.contains(npc.getName()) || player.isQuestCompleted("find_ghosts")) {
				npc.say("Please, let the dead rest in peace.");
			} else {
				player.setQuest("find_ghosts", lookStr + ";" + npc.getName()
						+ ":" + saidStr);
				npc.say("Remember my name ... " + npc.getName() + " ... "
						+ npc.getName() + " ...");
				player.addXP(100);
			}
		}
	}
}
