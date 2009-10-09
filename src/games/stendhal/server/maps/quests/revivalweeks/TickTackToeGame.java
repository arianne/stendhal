package games.stendhal.server.maps.quests.revivalweeks;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.mapstuff.game.TickTackToeBoard;

/**
 * Add a sign saying the tower is closed
 *
 * @author hendrik
 */
public class TickTackToeGame {

	public void addToWorld() {
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		TickTackToeBoard board = new TickTackToeBoard();
		board.setPosition(105, 120);
		zone.add(board);
		board.addToWorld();
	}
}
