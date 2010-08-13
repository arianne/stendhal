package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Can the player equip the specified item?
 */
public class PlayerCanEquipItemCondition implements ChatCondition {

	private final String itemName;

	/**
	 * Creates a new PlayerCanEquipItemCondition
	 * 
	 * @param itemName
	 *            name of item
	 */
	public PlayerCanEquipItemCondition(final String itemName) {
		this.itemName = itemName;
	}


	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		final Item item = SingletonRepository.getEntityManager().getItem(itemName);
		return player.getSlotNameToEquip(item)!=null;
	}

	@Override
	public String toString() {
		return "player can equip item <" + itemName + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
				PlayerCanEquipItemCondition.class);
	}
}
