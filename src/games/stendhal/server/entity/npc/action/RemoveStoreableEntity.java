package games.stendhal.server.entity.npc.action;

import games.stendhal.server.entity.mapstuff.office.StoreableEntityList;
import games.stendhal.server.entity.npc.Sentence;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * removes all storeable entities from the specified list that
 * has the players name as identifier.
 */
public class RemoveStoreableEntity extends SpeakerNPC.ChatAction {

	private StoreableEntityList<?> storeableEntityList;

	/**
	 * creates a new RemoveStoreableEntity
	 *
	 * @param storeableEntityList the list to removed entities from
	 */
	public RemoveStoreableEntity(StoreableEntityList<?> storeableEntityList) {
		this.storeableEntityList = storeableEntityList;
	}

	@Override
	public void fire(Player player, Sentence sentence, SpeakerNPC engine) {
		storeableEntityList.removeByName(player.getName());
	}

	@Override
	public String toString() {
		return "remove entity <" + storeableEntityList + ">";
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj, false, 
			RemoveStoreableEntity.class);
	}
}