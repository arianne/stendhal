package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import java.util.Arrays;
import java.util.List;

/**
 * executes a list of actions
 */
public class MultipleActions extends SpeakerNPC.ChatAction {

	private List<SpeakerNPC.ChatAction> actions;

	/**
	 * Creates a new MultipleActions
	 *
	 * @param action list of actions to execute
	 */
	public MultipleActions(SpeakerNPC.ChatAction... action) {
		this.actions = Arrays.asList(action);
	}

	@Override
	public void fire(Player player, String text, SpeakerNPC npc) {
		for (SpeakerNPC.ChatAction action : actions) {
			action.fire(player, text, npc);
		}
	}

	@Override
	public String toString() {
		return actions.toString();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((actions == null) ? 0 : actions.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final MultipleActions other = (MultipleActions) obj;
		if (actions == null) {
			if (other.actions != null) return false;
		} else if (!actions.equals(other.actions)) return false;
		return true;
	}

}