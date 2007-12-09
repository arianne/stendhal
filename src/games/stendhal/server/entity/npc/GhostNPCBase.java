package games.stendhal.server.entity.npc;

import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * Base class for ghost NPCs
 * 
 * @author Martin Fuchs
 */
public abstract class GhostNPCBase extends SpeakerNPC {
	public GhostNPCBase(String name)
	{
		super(name);
	}

	@Override
	protected abstract void createPath();

	@Override
	protected void createDialog() {
		add(ConversationStates.IDLE,
			ConversationPhrases.GREETING_MESSAGES, null,
			ConversationStates.IDLE, null,
			new GhostGreetingAction());
	}

	/**
	 * ChatAction common to all ghost NPCs
	 * @author Martin Fuchs
	 */
	private static class GhostGreetingAction extends SpeakerNPC.ChatAction
	{
	    @Override
	    public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
	    	if (!player.hasQuest("find_ghosts")) {
	    		player.setQuest("find_ghosts", "looking:said");
	    	}
	    	String npcQuestText = player.getQuest("find_ghosts");
	    	String[] npcDoneText = npcQuestText.split(":");
	    	String lookStr = npcDoneText.length>1? npcDoneText[0]: "";
	    	String saidStr = npcDoneText.length>1? npcDoneText[1]: "";
	    	List<String> list = Arrays.asList(lookStr.split(";"));
	        if (!list.contains(npc.getName())) {
	    		player.setQuest("find_ghosts",
	    				lookStr + ";"
	    						+ npc.getName() + ":"
	    						+ saidStr);
	    		npc.say("Remember my name ... "
	    				+ npc.getName() + " ... "
	    				+ npc.getName() + " ...");
	    	    player.addXP(100);
	    	} else {
	    	    npc.say("Please, let the dead rest in peace");
	    	}
	    }
	}
}
