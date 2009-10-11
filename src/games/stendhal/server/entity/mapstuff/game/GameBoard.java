package games.stendhal.server.entity.mapstuff.game;

import games.stendhal.server.entity.item.token.BoardToken;
import games.stendhal.server.entity.mapstuff.area.AreaEntity;
import games.stendhal.server.entity.mapstuff.game.movevalidator.MoveValidator;
import games.stendhal.server.entity.player.Player;

import java.util.List;

import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.Type;

public abstract class GameBoard extends AreaEntity {
	protected BoardToken[][] board;
	protected boolean active;
	protected List<String> players;
	protected List<String> tokenTypes;
	protected int currentPlayerIndex;
	private GameBoardTimer timer;

	/**
	 * creates a new GameBoard
	 */
	public GameBoard() {
		super();
		init();
	}

	/**
	 * creates a new GameBoard
	 *
	 * @param width  width of the board
	 * @param height height of the board
	 */
	public GameBoard(int width, int height) {
		super(width, height);
		init();
	}

	private void init() {
		setRPClass("game_board");
		put("type", "game_board");
		timer = new GameBoardTimer(this, 5 * 60);
	}


	/**
	 * is the game active?
	 *
	 * @return active
	 */
	public boolean isGameActive() {
		return active;
	}

	/**
	 * gets a list of player names participating in this game
	 *
	 * @return list of player names
	 */
	public List<String> getPlayers() {
		return players;
	}

	/**
	 * gets the name of the player who is doing the current turn.
	 *
	 * @return name of player
	 */
	public String getCurrentPlayer() {
		return players.get(currentPlayerIndex);
	}

	/**
	 * gets the name of the token type for the current turn
	 *
	 * @return name of token type
	 */
	public Object getCurrentTokenType() {
		return tokenTypes.get(currentPlayerIndex);
	}

	/**
	 * checks whether there are empty spots left
	 *
	 * @return <code>true</code> iff there are empty spots.
	 */
	public boolean areEmptyFieldsLeft() {
		for (int xIndex = 0; xIndex < board.length; xIndex++) {
			for (int yIndex = 0; yIndex < board[xIndex].length; yIndex++) {
				if (board[xIndex][yIndex] == null) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * moves to the next turn (next player)
	 */
	protected void nextTurn() {
		currentPlayerIndex++;
		if (currentPlayerIndex >= players.size()) {
			currentPlayerIndex = 0;
		}
	}

	/**
	 * handling of moved token
	 *
	 * @param player player moving the toke
	 * @param token  moved token
	 */
	public void onTokenMoved(Player player, BoardToken token) {
		int xIndex = getXIndex(token.getX());
		int yIndex = getYIndex(token.getY());
		MoveValidator validator = new TicTacToeMovementValidatorChain();
		if (!validator.validate(this, player, token, xIndex, yIndex)) {
			token.resetToHomePosition();
			return;
		}
	
		completeMove(xIndex, yIndex, token);
	}


	abstract void completeMove(int xIndex, int yIndex, BoardToken token);

	protected void startGame() {
		timer.start();
	}
	
	protected void endGame() {
		timer.stop();
		active = false;
	}

	public void timeOut() {
		// TODO: NPC say timeout
		endGame();
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

	/**
	 * generates the RP class
	 */
	public static void generateRPClass() {
		final RPClass entity = new RPClass("game_board");
		entity.isA("entity");
		entity.addAttribute("class", Type.STRING);
	}

}