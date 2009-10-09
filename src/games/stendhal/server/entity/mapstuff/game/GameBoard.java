package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MoveValidator;
import games.stendhal.server.entity.player.Player;

public abstract class GameBoard extends AreaEntity {
	protected BoardToken[][] board;

	public GameBoard() {
		super();
	}

	public GameBoard(int width, int height) {
		super(width, height);
	}

	public void onTokenMoved(Player player, BoardToken token) {
		int xIndex = getXIndex(token.getX());
		int yIndex = getYIndex(token.getY());
		MoveValidator validator = new TickTackToeMovementValidatorChain();
		if (!validator.validate(this, player, token, xIndex, yIndex)) {
			token.resetToHomePosition();
			return;
		}
	
		completeMove(xIndex, yIndex, token);
	}


	void completeMove(int xIndex, int yIndex, BoardToken token) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * gets the x-index of the specified x-coordinate
	 *
	 * @param x x
	 * @return x-index, or <code>-1</code> on error.
	 */
	int getXIndex(int x) {
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
	int getYIndex(int y) {
		int idx = y - getY();
		if (idx > 2) {
			idx = -1;
		}
		return idx;
	}

	/**
	 * returns the token at the specified index
	 *
	 * @param xIndex target x-index
	 * @param yIndex target y-index
	 * @return token or <code>null</code>
	 */
	public BoardToken getTokenAt(int xIndex, int yIndex) {
		return board[xIndex][yIndex];
	}

}