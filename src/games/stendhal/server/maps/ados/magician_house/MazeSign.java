package games.stendhal.server.maps.ados.magician_house;

import java.util.List;
import java.util.Iterator;

import marauroa.server.game.db.DAORegister;
import games.stendhal.server.core.engine.db.StendhalHallOfFameDAO;
import games.stendhal.server.entity.mapstuff.sign.Sign;

public class MazeSign extends Sign {
	private static final int SIGN_LENGTH = 10; 
	public MazeSign() {
		updatePlayers();
		put("class", "book_blue");
	}

	/**
	 * Update the player list written on the sign.
	 */
	public void updatePlayers() {
		List<String> players = DAORegister.get().get(StendhalHallOfFameDAO.class).getCharactersByFametype("M", SIGN_LENGTH, false);
		StringBuilder builder = new StringBuilder();
		Iterator<String> it = players.iterator();
		
		while (it.hasNext()) {
			builder.append(it.next());
			if (it.hasNext()) {
				builder.append("\n");
			}
		}
		setText("The best maze runners:\n" + builder.toString());
		notifyWorldAboutChanges();
	}
}
