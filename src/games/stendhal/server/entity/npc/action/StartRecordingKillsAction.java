package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.parser.Sentence;
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
public class StartRecordingKillsAction implements ChatAction {
	private final List<String> toKill;

	/**
	 * Creates a new StartRecodingKillsAction.
	 * 
	 * @param toKill
	 *            list of creatures which should be killed by the player
	 */
	public StartRecordingKillsAction(final List<String> toKill) {
		this.toKill = toKill;
	}

	/**
	 * Creates a new StartRecodingKillsAction.
	 * 
	 * @param toKill
	 *            creatures which should be killed by the player
	 */
	public StartRecordingKillsAction(final String... toKill) {
		this.toKill = Arrays.asList(toKill);
	}

	public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc) {
		for (final String creature : toKill) {
			player.removeKill(creature);
		}
	}

	@Override
	public String toString() {
		return "StartRecordingKillsActions <" + toKill + ">";
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
