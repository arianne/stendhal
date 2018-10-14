package games.stendhal.server.entity.npc.condition;

import java.util.List;

import games.stendhal.common.parser.Sentence;
import games.stendhal.server.core.config.annotations.Dev;
import games.stendhal.server.core.config.annotations.Dev.Category;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.item.GateKey;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.player.Player;

/**
 * Checks to see if the player has a gate key that matches the identifier of the
 * raiser (usually the gate but can be any entity with the "identifier" attribute)
 *
 * @author filipe
 */
@Dev(category=Category.IGNORE, label="Key?")
public class PlayerHasCorrectGateKey implements ChatCondition {

	@Override
	public boolean fire(Player player, Sentence sentence, Entity raiser) {
		// entity can't handle it without the identifier of the key
		if (!(raiser.has("identifier"))) {
			return false;
		}

		// get all gate keys and check if one matches
		List<Item> keys = player.getAllEquipped("gate key");
		for (Item key : keys) {
			if (((GateKey) key).matches(raiser.get("identifier"))) {
				return true;
			}
		}

		// no matches
		return false;
	}


	@Override
	public int hashCode() {
		return 43801;
	}

	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof PlayerHasCorrectGateKey);
	}
}
