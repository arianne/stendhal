package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import games.stendhal.common.Line.Action;

import org.junit.Test;

public class LineTest {

	@Test
	public void testLine() {
		String expected = "10,10;11,10;12,11;13,11;14,12;15,12;16,13;17,13;18,14;19,14;20,14;"
				+ "21,15;22,15;23,16;24,16;25,17;26,17;27,18;28,18;29,18;30,19;31,19;"
				+ "32,20;33,20;34,21;35,21;36,22;37,22;38,22;39,23;40,23;41,24;42,24;"
				+ "43,25;44,25;45,26;46,26;47,26;48,27;49,27;50,28;51,28;52,29;53,29;"
				+ "54,30;55,30;56,30;57,31;58,31;59,32;60,32;61,33;62,33;63,34;64,34;"
				+ "65,34;66,35;67,35;68,36;69,36;70,37;71,37;72,38;73,38;74,38;75,39;"
				+ "76,39;77,40;78,40;79,41;80,41;81,42;82,42;83,42;84,43;85,43;86,44;"
				+ "87,44;88,45;89,45;90,46;91,46;92,46;93,47;94,47;95,48;96,48;97,49;"
				+ "98,49;99,50;100,50;";
		final StringBuilder sb = new StringBuilder();
		Line.renderLine(10, 10, 100, 50, new Action() {

			@Override
			public void fire(int x, int y) {
				sb.append(x + "," + y + ";");
			}
		});

		assertEquals("Current path finding. Note if you improve the "
				+ "pathfinder, you need to adjust this test.", expected,
				sb.toString());
	}
}
