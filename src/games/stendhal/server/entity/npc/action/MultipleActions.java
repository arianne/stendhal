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

}