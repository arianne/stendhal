package games.stendhal.server.entity.npc.action;

import games.stendhal.common.NotificationType;
import games.stendhal.common.parser.Sentence;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;

/**
 * Debugging ChatAction that sends a quest slot's content to the firing player
 *
 * @author madmetzger
 */
public class OutputQuestSlotAction implements ChatAction {

	private final String questSlot;

	/**
	 * Create a new OutputQuestSlotAction
	 * @param quest the quest to plot
	 */
	public OutputQuestSlotAction(String quest) {
		this.questSlot = quest;
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if(player.hasQuest(this.questSlot)) {
			player.sendPrivateText(NotificationType.INFORMATION, player.getQuest(this.questSlot));
		}
	}


	@Override
	public int hashCode() {
		return 5351 * questSlot.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof OutputQuestSlotAction)) {
			return false;
		}
		final OutputQuestSlotAction other = (OutputQuestSlotAction) obj;
		return questSlot.equals(other.questSlot);
	}
}
