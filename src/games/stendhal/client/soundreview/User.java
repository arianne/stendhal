package games.stendhal.client.soundreview;

import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.Player;

public class User {

	public static Player me;

	public static void setMe(Player user) {
		me = user;
	}

	public static Player getMe() {
		return me;
	}

	public static void setMe(Entity entity) {

		me = (Player) entity;
		HearingArea.get().set(me.getX(), me.getY());
	}
}
