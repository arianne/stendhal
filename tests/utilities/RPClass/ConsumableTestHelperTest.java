package utilities.RPClass;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import games.stendhal.server.entity.item.ConsumableItem;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConsumableTestHelperTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreate() throws Exception {
		
		ConsumableItem eater = ConsumableTestHelper.createEater("consume");
		assertThat(eater, is(ConsumableItem.class));
		
		ConsumableItem createImmunizer = ConsumableTestHelper.createImmunizer("consume");
		assertThat(createImmunizer, is(ConsumableItem.class));
	}
}
