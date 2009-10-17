package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.trade.Market;
import marauroa.common.game.RPObject;

public class MarketTransformer implements Transformer {

	public RPObject transform(RPObject object) {
		return (RPObject)new Market(object);
	}

}
