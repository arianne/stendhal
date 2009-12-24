package games.stendhal.common;


import static org.hamcrest.Matchers.isIn;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RandTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * Tests for randUniform.
	 */
	@Test
	public void testRandUniform() throws Exception {
		assertEquals(0, Rand.randUniform(0, 0));
	}
	
	/**
	 * Tests for randUniform2.
	 */
	@Test
	public void testRandUniform2() throws Exception {
		for (int i = 0; i < 10; i++) {
			assertThat(Rand.randUniform(-1, 0), isIn(Arrays.asList(0, -1)));
			assertThat(Rand.randUniform(0, -1), isIn(Arrays.asList(0, -1)));
			assertThat(Rand.randUniform(1, -1), isIn(Arrays.asList(1, 0, -1)));
			assertThat(Rand.randUniform(-1, 1), isIn(Arrays.asList(1, 0, -1)));
			assertThat(Rand.randUniform(100, 102), isIn(Arrays.asList(100, 101, 102)));
		}
	}


}
