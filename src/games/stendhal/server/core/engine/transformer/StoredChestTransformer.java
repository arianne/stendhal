package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.chest.StoredChest;
import marauroa.common.game.RPObject;

public class StoredChestTransformer implements Transformer {

	public RPObject transform(final RPObject object) {
				return new StoredChest(object);
	}

}
