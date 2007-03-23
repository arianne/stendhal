package games.stendhal.client.soundreview;

import java.awt.geom.Rectangle2D;

/**
 * the area around the userAvatar that the avatar can hear
 * the position should be udated by every step the avatar makes
 * @author astrid
 *
 */
public class HearingArea {

	public static final int HEARINGDIST = 20;

	private int left;

	private int right;

	private int lower;

	private int upper;

	private static HearingArea instance = new HearingArea(0, 0);

	public static HearingArea get() {
		return instance;
	}

	public HearingArea(int x, int y) {
		instance = this;
		set(x, y);

	}

	public void set(int x, int y) {
		left = x - HEARINGDIST;
		right = x + HEARINGDIST;
		upper = y - HEARINGDIST;
		lower = y + HEARINGDIST;
	}

	public boolean contains(int x, int y) {
		if (left < x ? x < right : false) {
			return (upper < y ? y < lower : false);
		}
		return false;
	}

	public void moveTo(int x, int y) {
		set(x, y);

	}

	public void set(double x, double y) {
		set((int) x, (int) y);

	}

	public Rectangle2D getAsRect() {
		return new Rectangle2D.Double(left, upper, 2 * HEARINGDIST, 2 * HEARINGDIST);
	}

}
