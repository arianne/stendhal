package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.SpeakerNPC.ChatAction;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Starts the recording of kills.
 * 
 * @author hendrik
 */
public class StartRecordingKillsAction extends ChatAction {
	private List<String> toKill;

	/**
	 * Creates a new StartRecodingKillsAction.
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
	public StartRecordingKillsAction(List<String> toKill) {
		this.toKill = toKill;
	}

	/**
	 * Creates a new StartRecodingKillsAction.
	 * 
	 * @param toKill
	 *            creatures which should be killed by the player
	 */
	public StartRecordingKillsAction(String... toKill) {
		this.toKill = Arrays.asList(toKill);
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC npc) {
		for (String creature : toKill) {
			player.removeKill(creature);
		}
	}

	@Override
	public String toString() {
		return "StartRecordingKillsActions <" + toKill + ">";
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				StartRecordingKillsAction.class);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(toKill).toHashCode();
	}

}
