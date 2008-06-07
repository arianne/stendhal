package games.stendhal.server.util;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.easymock.internal.matchers.And;
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

				 System.out.println(result);

				assertThat("There should be named the french novelist for the topic Stendhal.", result, allOf(
						containsString("Marie"),
								containsString("Henri"), containsString("Beyle")));
			} else {
				fail("Sorry, could not find information on this topic in Wikipedia.");
			}
		}
	}

}
