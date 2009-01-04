package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.office.RentedSign;
import marauroa.common.game.RPObject;

public class RentedSignTransformer implements Transformer {

	public RPObject transform(final RPObject object) {
				return new RentedSign(object);
	}

}
