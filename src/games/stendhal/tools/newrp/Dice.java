package games.stendhal.tools.newrp;

import java.util.Random;

/**
 * This class represent a Dice. It provide methods for rolling a 1D100, 1D20 and
 * 1D6.
 *
 * Also it has methods for rolling N 1D6 and N 1D20.
 *
 * A finally it is also able to return a random number between min and max both
 * inclusive.
 *
 * @author miguel
 *
 */
public class Dice {
	static Random rand = new Random();

	/**
	 * Returns a number between min and max both included.
	 *
	 * @param min
	 * @param max
	 * @return
	 */
	public static int between(int min, int max) {
		return min + rand.nextInt(1 + max - min);
	}

	/**
	 * Roll a dice of 100 sides.
	 *
	 * @return
	 */
	public static int r1D100() {
		return rand.nextInt(100) + 1;

	}

	/**
	 * Roll a dice of 20 sides.
	 *
	 * @return
	 */
	public static int r1D20() {
		return rand.nextInt(20) + 1;
	}

	/**
	 * Roll a dice of 6 sides.
	 *
	 * @return
	 */
	public static int r1D6() {
		return rand.nextInt(6) + 1;
	}

	public static int rND20(int n) {
		int total = 0;
		for (int i = 0; i < n; i++) {
			total += r1D20();
		}

		return total;
	}

	public static int rND6(int n) {
		int total = 0;
		for (int i = 0; i < n; i++) {
			total += r1D6();
		}

		return total;
	}
}

/**
 * Result of the roll of the Dice.
 *
 * @author miguel
 *
 */
enum RollResult {
	SUCCESS, CRITICAL_SUCCESS, FAILURE, CRITICAL_FAILURE;

	public boolean success() {
		return this == SUCCESS || this == CRITICAL_SUCCESS;
	}
}