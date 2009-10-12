package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.item.token.Token.TokenMoveListener;

import java.util.Arrays;
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
		put("class", "tictactoe");
		board = new BoardToken[3][3];
		tokenTypes = Arrays.asList("x_board_token", "o_board_token");
		setDescription("You see a game board for Tic Tac Toe.");
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
		if (checkForWin()) {
			npc.say("Congratulations! " + players.get(currentPlayerIndex) + " won his game.");
			endGame();
			return;
		}

		if (checkForTie()) {
			npc.say("I am sorry, it looks nobody won this round.");
			endGame();
			return;
		}

		nextTurn();
	}


	private boolean checkForWin() {
		for (int i = 0; i < 3; i++) {
			if ((board[i][0] != null) && (board[i][1] != null) && (board[i][2] != null)) {
				if (board[i][0].getItemSubclass().equals(board[i][1].getItemSubclass()) && board[i][0].getItemSubclass().equals(board[i][2].getItemSubclass())) {
					return true;
				}
			}
			if ((board[0][i] != null) && (board[1][i] != null) && (board[2][i] != null)) {
				if (board[0][i].getItemSubclass().equals(board[1][i].getItemSubclass()) && board[0][i].getItemSubclass().equals(board[2][i].getItemSubclass())) {
					return true;
				}
			}
		}
		if ((board[0][0] != null) && (board[1][1] != null) && (board[2][2] != null)) {
			if (board[0][0].getItemSubclass().equals(board[1][1].getItemSubclass()) && board[0][0].getItemSubclass().equals(board[2][2].getItemSubclass())) {
				return true;
			}
		}
		if ((board[0][2] != null) && (board[1][1] != null) && (board[2][0] != null)) {
			if (board[0][2].getItemSubclass().equals(board[1][1].getItemSubclass()) && board[0][2].getItemSubclass().equals(board[2][0].getItemSubclass())) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForTie() {
		for (int xIndex = 0; xIndex < board.length; xIndex++) {
			for (int yIndex = 0; yIndex < board[xIndex].length; yIndex++) {
				if (board[xIndex][yIndex] != null) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * prepares a new game
	 */
	public void startGame() {
		super.startGame();

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
