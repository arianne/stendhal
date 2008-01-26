package games.stendhal.common;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test the ErrorBuffer class.
 * 
 * @author Martin Fuchs
 */
public class ErrorBufferTest {

	@Test
	public final void test() {
		ErrorDrain errors = new ErrorBuffer();

		assertEquals(false, errors.hasError());

		errors.setError("error 1 occured");
		assertEquals(true, errors.hasError());
		assertEquals("error 1 occured", errors.getErrorString());

		errors.setError("error 2 occured");
		assertEquals(true, errors.hasError());
		assertEquals("error 1 occured\nerror 2 occured", errors.getErrorString());
	}

}
