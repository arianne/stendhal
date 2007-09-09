

import java.util.Random;

public class Dice {
	static Random rand = new Random();

	public static int between(int min, int max) {
		return min + rand.nextInt(1 + max - min);
	}

	public static int r1D100() {
		return rand.nextInt(100) + 1;

	}

	public static int r1D20() {
		return rand.nextInt(20) + 1;
	}

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
