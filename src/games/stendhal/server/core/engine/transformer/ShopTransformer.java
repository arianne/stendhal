package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.trade.Shop;
import marauroa.common.game.RPObject;

public class ShopTransformer implements Transformer {

	public RPObject transform(RPObject object) {
		return (RPObject)new Shop(object);
	}

}
