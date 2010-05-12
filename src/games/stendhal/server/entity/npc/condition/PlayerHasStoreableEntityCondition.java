package games.stendhal.server.entity.npc.condition;

import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.office.StoreableEntityList;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.parser.Sentence;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * Is there a storeable entity in the specified list that has name
 * of the current player as identifier? 
 */
public class PlayerHasStoreableEntityCondition implements ChatCondition {
	private final StoreableEntityList< ? > storeableEntityList;
	
	public PlayerHasStoreableEntityCondition(final StoreableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	public boolean fire(final Player player, final Sentence sentence, final Entity entity) {
		return storeableEntityList.getByName(player.getName()) != null;
	}

	@Override
	public String toString() {
		return "in list <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
			PlayerHasStoreableEntityCondition.class);
	}
}
