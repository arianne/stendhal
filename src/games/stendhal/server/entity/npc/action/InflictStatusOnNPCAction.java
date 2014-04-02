package games.stendhal.server.entity.npc.action;

import com.google.common.base.Objects;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.item.ConsumableItem;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.EventRaiser;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.status.EatStatus;
import games.stendhal.server.entity.status.PoisonStatus;
import games.stendhal.server.entity.status.Status;

/**
 * inflicts a status on an NPC
 *
 * @author hendrik
 */
@Dev(category = Category.STATS, label="Status")
public class InflictStatusOnNPCAction implements ChatAction {
	private Status status;
	private String itemName;

	/**
	 * InflictStatusOnNPCAction
	 *
	 * @param itemName name of item;
	 */
	@Dev
	public InflictStatusOnNPCAction(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * InflictStatusOnNPCAction
	 *
	 * @param status status to inflict
	 */
	public InflictStatusOnNPCAction(Status status) {
		this.status = status;
	}

	@Override
	public void fire(Player player, Sentence sentence, EventRaiser npc) {
		if (! (npc.getEntity() instanceof RPEntity)) {
			return;
		}

		Status myStatus = status;
		ConsumableItem item = null;
		if (itemName != null) {
			item = (ConsumableItem) SingletonRepository.getEntityManager().getItem(itemName);
			if (item.getAmount() > 0) {
				myStatus = new EatStatus(item.getAmount(), item.getFrecuency(), item.getRegen());
			} else {
				myStatus = new PoisonStatus(item.getAmount(), item.getFrecuency(), item.getRegen());
			}
		}

		((RPEntity) npc.getEntity()).getStatusList().inflictStatus(myStatus, item);
	}

	@Override
	public int hashCode() {
		if (itemName != null) {
			return 5237 * itemName.hashCode();
		}
		if (status != null) {
			return 5261 * status.hashCode();
		}
		return 5273;
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof InflictStatusOnNPCAction)) {
			return false;
		}
		InflictStatusOnNPCAction other = (InflictStatusOnNPCAction) obj;
		return Objects.equal(itemName, other.itemName)
			&& Objects.equal(status, other.status);
	}
}
