package games.stendhal.server.entity.npc.condition;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import games.stendhal.server.entity.mapstuff.office.StoreableEntityList;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * Is there a storeable entity in the specified list that has name
 * of the current player as identifier? 
 */
public class PlayerHasStoreableEntityCondition extends SpeakerNPC.ChatCondition {
	private StoreableEntityList< ? > storeableEntityList;
	
	public PlayerHasStoreableEntityCondition(StoreableEntityList< ? > storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	@Override
	public boolean fire(Player player, Sentence sentence, SpeakerNPC engine) {
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
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false,
			PlayerHasStoreableEntityCondition.class);
	}
}