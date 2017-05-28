package games.stendhal.server.core.engine.transformer;

import games.stendhal.server.entity.mapstuff.ExpirationTracker;
import marauroa.common.game.RPObject;

public class ExpirationTrackerTransformer implements Transformer {
	@Override
	public RPObject transform(RPObject object) {
		ExpirationTracker entity = new ExpirationTracker();

		entity.setPosition(object.getInt("x"), object.getInt("y"));
		entity.setIdentifier(object.get("identifier"));
		entity.setPlayerName(object.get("player_name"));
		entity.setExpirationTime(Long.parseLong(object.get("expires")));

		return entity;
	}
}
