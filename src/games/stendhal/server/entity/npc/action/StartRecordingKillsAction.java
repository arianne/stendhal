package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import marauroa.common.Pair;
import java.util.HashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Starts the recording of kills.
 * 
 * @author hendrik
 */
public class StartRecordingKillsAction implements ChatAction {
	//first number in pair is required solo kills, second is required shared kills
    private final HashMap<String, Pair<Integer, Integer>> toKill;
    private final String QUEST_SLOT;
    private final int KILLS_INDEX;
	
	/**
	 * Creates a new StartRecodingKillsAction.
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
	public StartRecordingKillsAction(final String questSlot, final int startIndex, final HashMap<String, Pair<Integer, Integer>> toKill) {
		this.toKill = toKill;
		this.QUEST_SLOT=questSlot;
		this.KILLS_INDEX=startIndex;
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
		final StringBuilder sb= new StringBuilder("");
		for (final String creature : toKill.keySet()) {
			final int requiredSolo=toKill.get(creature).first();
			final int requiredShared=toKill.get(creature).second();			
			final int soloKills=player.getSoloKill(creature);
			final int sharedKills=player.getSharedKill(creature);
			sb.append(creature+","+requiredSolo+","+requiredShared+","+soloKills+","+sharedKills+",");
		}
		final String result=sb.toString().substring(0, sb.toString().length()-1);
		player.setQuest(QUEST_SLOT, KILLS_INDEX, result);
	}

	@Override
	public String toString() {
		return "StartRecordingKillsActions <" + toKill.toString() + ">";
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				StartRecordingKillsAction.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(toKill).toHashCode();
	}

}
