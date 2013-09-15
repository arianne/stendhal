package games.stendhal.server.entity.npc.action;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.Status;

/**
 * inflicts a status on an NPC
 *
 * @author hendrik
 */
@Dev(category = Category.STATS, label="Status")
public class InflictStatusOnNPCAction implements ChatAction {
	private Status status;

	/**
	 * InflictStatusOnNPCAction
	 *
	 * @param status status to inflict
	 */
	public InflictStatusOnNPCAction(Status status) {
		this.status = status;
	}

	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (npc.getEntity() instanceof RPEntity) {
			((RPEntity) npc.getEntity()).getStatusList().inflictStatus(status, null);
		}
	}

}
