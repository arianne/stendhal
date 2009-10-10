package games.stendhal.server.entity.item.token;

import games.stendhal.server.entity.player.Player;

import java.util.Map;

/**
 * a token to be used on a game board
 *
 * @author hendrik
 */
public class BoardToken extends Token {
	private int homeX = 1;
	private int homeY = 1;
	private int moveCountSinceHome = 0;

	/**
	 * @param name
	 * @param clazz
	 * @param subclass
	 * @param attributes
	 */
	public BoardToken(String name, String clazz, String subclass,Map<String, String> attributes) {
		super(name, clazz, subclass, attributes);
	}

	/**
	 * @param item
	 */
	public BoardToken(Token item) {
		super(item);
	}

	/**
	 * was this token on its home spot before the last move?
	 *
	 * @return <code>true</code> iff the token was on the home spot before this move
	 */
	public boolean wasMovedFromHomeInLastMove() {
		return moveCountSinceHome <= 1;
	}

	@Override
	public void onPutOnGround(final Player player) {
		moveCountSinceHome++;
		super.onPutOnGround(player);
	}

	/**
	 * sets the home position for this token.
	 *
	 * @param x x
	 * @param y y
	 */
	public void setHomePosition(int x, int y) {
		homeX = x;
		homeY = y;
	}

	/**
	 * moves this token back to the home position
	 */
	public void resetToHomePosition() {
		this.setPosition(homeX, homeY);
		moveCountSinceHome = 0;
	}
}
