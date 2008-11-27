package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.office.ArrestWarrant;
import marauroa.common.game.RPObject;

public class ArrestWarrantTransformer implements Transformer {

	public RPObject transform(RPObject object) {
		return new ArrestWarrant(object);
	}

}
