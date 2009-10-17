package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.trade.Earning;
import marauroa.common.game.RPObject;

public class EarningTransformer implements Transformer {

	public RPObject transform(RPObject object) {
		return new Earning(object);
	}

}
