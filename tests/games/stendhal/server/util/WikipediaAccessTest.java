package games.stendhal.server.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * Tests the WikipediaAccess class.
 * 
 * @author Martin Fuchs
 */
public class WikipediaAccessTest {

	@Test
	public void test() {
		WikipediaAccess access = new WikipediaAccess("Stendhal");

		access.run();

		if (access.getError() != null) {
			fail("Wikipedia access was not successful: " + access.getError());
		} else if (access.isFinished()) {
			if (access.getText() != null && access.getText().length() > 0) {
				String result = access.getProcessedText();

				// System.out.println(result);

				assertTrue(
						"There should be named the french novelist for the topic Stendhal.",
						result.contains("Marie-Henri Beyle"));
			} else {
				fail("Sorry, could not find information on this topic in Wikipedia.");
			}
		}
	}

}
