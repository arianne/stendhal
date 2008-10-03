package games.stendhal.server.core.reflectiondebugger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Map;

import marauroa.common.Pair;

import org.junit.Test;

/**
 * tests for the field tester
 *
 * @author hendrik
 */
public class FieldListerTest {

	@Test
	public void testListAttributesIncludingPrivateAndParents() {
		FieldLister fl = new FieldLister(new MockChildClass());
		fl.scan();
		Map<String, Pair<String, String>> fields = fl.getResult();

		// field created by ecl-emma to track coverage data
		fields.remove("$VRc");

		System.out.println(fields);
		assertThat(fields.size(), is(5));
		
	}
}
