package games.stendhal.server.maps.deathmatch;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.events.LoginListener;
import games.stendhal.server.events.LoginNotifier;
import games.stendhal.server.util.Area;

public class DeathmatchArea implements LoginListener {
	private Area area;

	private Spot cowardSpot;

	DeathmatchArea(Area area) {
		super();
		this.area = area;
		initialize();
	}

	protected void initialize() {

		LoginNotifier.get().addListener(this);

	}

	public void onLoggedIn(Player player) {

		if (area.contains(player)) {
			teleportToCowardPlace(player);
		}

	}

	private void teleportToCowardPlace(Player player) {
		
		if (cowardSpot == null) {
			cowardSpot = new Spot(StendhalRPWorld.get().getZone(
			"0_semos_mountain_n2_w"), 104, 123);
		}
		player.teleport(cowardSpot.getZone(), cowardSpot.getX(), cowardSpot.getY(), Direction.DOWN, player);
		player.sendPrivateText("You wake up far away from the city in the mountains. But you don't know what happened.");
	}

	public Area getArea() {

		return area;
	}

	public boolean contains(Player player) {

		return area.contains(player);
	}

}
