package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.item.token.Token.TokenMoveListener;

import java.util.LinkedList;
import java.util.List;

/**
 * A Tick Tack Toe board.
 *
 * @author hendrik
 */
public class TickTackToeBoard extends GameBoard implements TokenMoveListener<BoardToken> {
	private List<BoardToken> tokens = new LinkedList<BoardToken>();

	/**
	 * creates a new tick tack toe board
	 */
	public TickTackToeBoard() {
		super(3, 3);
		board = new BoardToken[2][2];
	}

	public void addToWorld() {
		for (int i = 0; i < 5; i++) {
			addTokenToWorld("x_board_token", getX() - 2, getY() + 1);
			addTokenToWorld("o_board_token", getX() + (int) getWidth() + 1, getY() + 1);
		}
	}

	/**
	 * Creates a token and adds it to the world.
	 *
	 * @param x
	 *            x-position
	 * @param y
	 *            y-position
	 */
	private void addTokenToWorld(String name, int x, int y) {
		final BoardToken token = (BoardToken) SingletonRepository.getEntityManager().getItem(name);
		token.setPosition(x, y);
		token.setHomePosition(x, y);
		token.setTokenMoveListener(this);
		getZone().add(token, false);
		tokens.add(token);
	}

}
