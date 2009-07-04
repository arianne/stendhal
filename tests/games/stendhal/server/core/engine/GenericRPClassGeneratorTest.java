package games.stendhal.server.core.engine;

import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPClass;
import marauroa.common.game.Definition.DefinitionClass;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


public class GenericRPClassGeneratorTest {

	@Before
	public void setUp() throws Exception {
		MockStendlRPWorld.reset();
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
		assertThat(rpclass.getName(), is("EntityGenerationTestEntity"));
		assertTrue(rpclass.subclassOf("entity"));
		assertTrue(rpclass.hasDefinition(DefinitionClass.ATTRIBUTE,"name"));
		assertTrue(rpclass.hasDefinition(DefinitionClass.ATTRIBUTE,"changedname"));
		assertTrue(rpclass.hasDefinition(DefinitionClass.RPSLOT,"slot"));
		assertTrue(rpclass.hasDefinition(DefinitionClass.RPSLOT,"slotTwo"));
	}

}
