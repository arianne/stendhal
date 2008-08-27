package games.stendhal.server.core.reflectiondebugger;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * tests for the field tester
 *
 * @author hendrik
 */
public class FieldListerTest {

	/** fails when run with eclemma coverage 
	 * 
	 */
	@Test
	public void testListAttributesIncludingPrivateAndParents() {
		FieldLister fl = new FieldLister(new MockChildClass());
		fl.scan();
		System.out.println(fl.getResult());
		assertThat(fl.getResult().size(), is(4));
		
	}
}
