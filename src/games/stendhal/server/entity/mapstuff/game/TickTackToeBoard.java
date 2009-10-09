package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.item.token.Token.TokenMoveListener;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.player.Player;

import java.util.LinkedList;
import java.util.List;

/**
 * A Tick Tack Toe board.
 *
 * @author hendrik
 */
public class TickTackToeBoard extends AreaEntity implements TokenMoveListener<BoardToken> {
	private List<BoardToken> tokens = new LinkedList<BoardToken>();
	private BoardToken[][] board = new BoardToken[2][2];

	/**
	 * creates a new tick tack toe board
	 */
	public TickTackToeBoard() {
		super(3, 3);
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

	public void onTokenMoved(Player player, BoardToken token) {
		int xIndex = getXIndex(token.getX());
		int yIndex = getYIndex(token.getY());
		if (!validateMoveTarget(player, xIndex, yIndex)) {
			token.resetToHomePosition();
		}
	}

	private boolean validateMoveTarget(Player player, int xIndex, int yIndex) {
		if ((xIndex < 0) || (yIndex < 0)) {
			player.sendPrivateText("Please drop the token onto the game board.");
			return false;
		}
		return true;
	}

	/**
	 * gets the x-index of the specified x-coordinate
	 *
	 * @param x x
	 * @return x-index, or <code>-1</code> on error.
	 */
	private int getXIndex(int x) {
		int idx = x - getX();
		if (idx > 2) {
			idx = -1;
		}
		return idx;
	}


	/**
	 * gets the y-index of the specified y-coordinate
	 *
	 * @param y y
	 * @return y-index, or <code>-1</code> on error.
	 */
	private int getYIndex(int y) {
		int idx = y - getY();
		if (idx > 2) {
			idx = -1;
		}
		return idx;
	}
}
