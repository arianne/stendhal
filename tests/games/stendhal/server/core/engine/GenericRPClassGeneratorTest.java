package games.stendhal.server.core.engine;

import marauroa.common.game.RPClass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import sun.reflect.ReflectionFactory;


public class GenericRPClassGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void after() throws Exception {
	}
	
	@Test
	public void testRPClassGeneration() throws Exception {
		Class<?> clazz = new EntityGenerationTestEntity().getClass();
		assertNotNull(clazz);
		GenericRPClassGenerator genericRPClassGenerator = new GenericRPClassGenerator();
		genericRPClassGenerator.generate(clazz);
		RPClass rpclass = RPClass.getRPClass("EntityGenerationTestEntity");
		assertNotNull(rpclass);
		assertThat(rpclass.getName(),is("EntityGenerationTestEntity"));
	}

}
