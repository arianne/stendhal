package games.stendhal.server.core.engine;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import marauroa.common.game.RPClass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class GenericRPClassGeneratorTest {

	@Before
	public void setUp() throws Exception {
	}
	
	@After
	public void after() throws Exception {
	}
	
	@Test
	public void testRPClassGeneration() throws Exception {
		RPClass entity = new RPClass("entity");
		Class<?> clazz = new EntityGenerationTestEntity().getClass();
		assertNotNull(clazz);
		GenericRPClassGenerator genericRPClassGenerator = new GenericRPClassGenerator();
		genericRPClassGenerator.generate(clazz);
		RPClass rpclass = RPClass.getRPClass("EntityGenerationTestEntity");
		assertNotNull(rpclass);
		assertThat(rpclass.getName(), is("EntityGenerationTestEntity"));
		assertTrue(rpclass.subclassOf("entity"));
		System.out.println(entity);
		genericRPClassGenerator.generate(clazz);
	}

}
