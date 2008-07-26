package games.stendhal.server.maps.deathmatch;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.LoginListener;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.util.Area;

public class DeathmatchArea implements LoginListener {
	private final Area area;

	private Spot cowardSpot;

	DeathmatchArea(final Area area) {
		super();
		this.area = area;
		initialize();
	}

	protected void initialize() {

		SingletonRepository.getLoginNotifier().addListener(this);

	}

	public void onLoggedIn(final Player player) {

		if (area.contains(player)) {
			teleportToCowardPlace(player);
		}

	}

	private void teleportToCowardPlace(final Player player) {
		
		if (cowardSpot == null) {
			cowardSpot = new Spot(SingletonRepository.getRPWorld().getZone(
			"0_semos_mountain_n2_w"), 104, 123);
		}
		player.teleport(cowardSpot.getZone(), cowardSpot.getX(), cowardSpot.getY(), Direction.DOWN, player);
		player.sendPrivateText("You wake up far away from the city in the mountains. But you don't know what happened.");
	}

	public Area getArea() {
		return area;
	}

	public boolean contains(final Player player) {

		return area.contains(player);
	}

}
