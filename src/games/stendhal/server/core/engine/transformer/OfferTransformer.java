package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.trade.Offer;
import marauroa.common.game.RPObject;

public class OfferTransformer implements Transformer {

	@Override
	public RPObject transform(RPObject object) {
		return new Offer(object);
	}

}
