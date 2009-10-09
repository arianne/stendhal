package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
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
		if (!validiateGameActive(player)
			|| !validatePlayerIsParticipating(player)
			|| !validateItsPlayersTurn(player)
			|| !validateSourceIsStock(player)
			|| !validateMoveTargetOnBoard(player, xIndex, yIndex)
			|| !validateMoveTargetEmpty(player, xIndex, yIndex)) {
			token.resetToHomePosition();
			return;
		}
	
		completeMove(xIndex, yIndex, token);
	}


	boolean validiateGameActive(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean validatePlayerIsParticipating(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean validateItsPlayersTurn(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean validateSourceIsStock(Player player) {
		// TODO Auto-generated method stub
		return false;
	}

	boolean validateMoveTargetOnBoard(Player player, int xIndex, int yIndex) {
		if ((xIndex < 0) || (yIndex < 0)) {
			player.sendPrivateText("Please drop the token onto the game board.");
			return false;
		}
		return true;
	}

	boolean validateMoveTargetEmpty(Player player, int xIndex,	int yIndex) {
		if (board[xIndex][yIndex] != null) {
			player.sendPrivateText("Please drop the token onto an empty spot.");
			return false;
		}
		return true;
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

}