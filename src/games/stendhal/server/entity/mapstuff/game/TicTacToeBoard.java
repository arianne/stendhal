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
public class TicTacToeBoard extends GameBoard implements TokenMoveListener<BoardToken> {
	private List<BoardToken> tokens = new LinkedList<BoardToken>();

	/**
	 * creates a new tick tack toe board
	 */
	public TicTacToeBoard() {
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

	@Override
	void completeMove(int xIndex, int yIndex, BoardToken token) {
		board[xIndex][yIndex] = token;
		checkBoardStatus();
	}

	private void checkBoardStatus() {
		// TODO: check for win
		// TODO: check for no empty squares
		nextTurn();
	}


	/**
	 * prepares a new game
	 */
	public void startNewGame() {

		// clear board state
		for (int xIndex = 0; xIndex < board.length; xIndex++) {
			for (int yIndex = 0; yIndex < board[xIndex].length; yIndex++) {
				board[xIndex][yIndex] = null;
			}
		}

		// reset tokens to home
		for (BoardToken token : tokens) {
			token.resetToHomePosition();
		}
	}
}
