package games.stendhal.server.maps.quests.maze;

import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.mapstuff.sign.Sign;
import games.stendhal.server.entity.mapstuff.sign.SignFromHallOfFameLoader;

public class MazeSign extends Sign {
	private static final int SIGN_LENGTH = 10;

	/**
	 * creates a new maze sign.
	 */
	public MazeSign() {
		updatePlayers();
		put("class", "book_blue");
	}

	/**
	 * Update the player list written on the sign.
	 */
	public void updatePlayers() {
		String introduction = "The best maze runners:\n";
		SignFromHallOfFameLoader loader = new SignFromHallOfFameLoader(this, introduction, "M", SIGN_LENGTH, false, true);
		TurnNotifier.get().notifyInTurns(0, loader);
	}
}
