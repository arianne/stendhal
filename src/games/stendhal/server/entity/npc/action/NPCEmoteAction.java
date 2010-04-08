package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * npc emoting to player
 */
public class NPCEmoteAction implements ChatAction {

	private final String npcAction;
	
	public void fire(final Player player, final Sentence sentence, final SpeakerNPC engine) {
		
		engine.say("!me "+npcAction+" "+player.getName());
	}
	
	/**
	 * Creates a new EmoteAction.
	 * 
	 * @param npcAction text to say
	 */
	public NPCEmoteAction(String npcAction) {
		this.npcAction = npcAction.trim();
	}
	
	@Override
	public String toString() {
		return "NPCEmoteAction";
	}


	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				NPCEmoteAction.class);
	}
}
