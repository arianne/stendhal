package games.stendhal.server.entity.item;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import utilities.RPClass.ItemTestHelper;

public class StackableItemTest {

	

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		MockStendlRPWorld.get();
		ItemTestHelper.generateRPClasses();
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

	@Ignore
	@Test
	public void testUpdate() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetQuantity() {
		StackableItem stack = new StackableItem("item","clazz","subclass",null);
		assertThat(stack.getQuantity(), is(1));
		
	}

	@Test
	public void testRemoveOne() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		stack.put("id", 0);
		assertThat(stack.getQuantity(), is(1));
		stack.removeOne();
		assertThat(stack.getQuantity(), is(0));
	}

	@Ignore
	@Test
	public void testStackableItemStringStringStringMapOfStringString() {
		fail("Not yet implemented");
	}

	@Ignore
	@Test
	public void testStackableItemStackableItem() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetQuantity() {
		StackableItem stack = new StackableItem("item","clazz","subclass",null);
		assertThat(stack.getQuantity(), is(1));
		stack.setQuantity(0);
		assertThat(stack.getQuantity(), is(1));
		stack.setQuantity(-1);
		assertThat(stack.getQuantity(), is(1));
		stack.setQuantity(100);
		assertThat(stack.getQuantity(), is(100));
	}

	@Test
	public void testSub() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		stack.put("id", 0);
		assertThat(stack.getQuantity(), is(1));
		stack.sub(0);
		assertThat(stack.getQuantity(), is(1));
		
		
		stack.setQuantity(1);
		assertThat(stack.getQuantity(), is(1));
		stack.sub(1);
		assertThat(stack.getQuantity(), is(0));
		
		stack.setQuantity(100); 
		assertThat(stack.getQuantity(), is(100));
		stack.sub(1);
		assertThat(stack.getQuantity(), is(99));
		
	}
	
	@Test
	public void testSubNegativeNumber() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		stack.put("id", 0);
		assertThat(stack.getQuantity(), is(1));
		stack.sub(-1);
		assertThat("similar to splitOff()", stack.getQuantity(), is(1));
	}
	
	
	@Test
	public void testSubNegativeNumberSimilarToSplitOff() {
		final StackableItem subStack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		subStack.put("id", 0);
		assertThat(subStack.getQuantity(), is(1));
		final StackableItem splitStack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		splitStack.put("id", 0);
		assertThat(splitStack.getQuantity(), is(1));
		subStack.sub(-1);
		splitStack.splitOff(-1);
		assertThat(subStack.getQuantity(), is(splitStack.getQuantity()));
	}
	
	@Test
	public void testAddStackable() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		assertThat(stack.add(stack), is(1));
		assertThat(stack.getQuantity(), is(1));
		final StackableItem stackToAdd = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		assertThat(stack.add(stackToAdd), is(2));
		assertThat(stack.getQuantity(), is(2));
	}

	@Test
	public void testSplitOff() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		stack.put("id", 0);
		assertThat(stack.getQuantity(), is(1));
		stack.splitOff(0);
		assertThat(stack.getQuantity(), is(1));
		
		
		stack.setQuantity(1);
		assertThat(stack.getQuantity(), is(1));
		stack.splitOff(1);
		assertThat(stack.getQuantity(), is(0));
		
		stack.setQuantity(100); 
		assertThat(stack.getQuantity(), is(100));
		stack.splitOff(1);
		assertThat(stack.getQuantity(), is(99));
	}

	@Test
	public void testSplitOffNegativeNumber() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		stack.put("id", 0);
		assertThat(stack.getQuantity(), is(1));
		stack.splitOff(-1);
		assertThat("similar to sub()", stack.getQuantity(), is(1));
	}
	
	@Test
	public void testIsStackableMoney() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		final StackableItem stackOnTop = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		assertTrue(stack.isStackable(stackOnTop));
		assertTrue(stackOnTop.isStackable(stack));
		assertFalse(stack.isStackable(stack));
	}
	
	@Test
	public void testIsStackableBaloon() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("balloon");
		final StackableItem stackOnTop = (StackableItem) SingletonRepository.getEntityManager().getItem("balloon");
		assertTrue(stack.isStackable(stackOnTop));
		assertTrue(stackOnTop.isStackable(stack));
		assertFalse(stack.isStackable(stack));
	}
	
	@Test
	public void testIsStackableBaloonOnMoney() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("balloon");
		final StackableItem stackOnTop = (StackableItem) SingletonRepository.getEntityManager().getItem("money");
		assertFalse(stack.isStackable(stackOnTop));
		assertFalse(stackOnTop.isStackable(stack));
		assertFalse(stack.isStackable(stack));
	}
	
	@Test
	public void testIsStackableSummonScrolls() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("summon scroll");
		final StackableItem stackOnTop = (StackableItem) SingletonRepository.getEntityManager().getItem("summon scroll");
		assertTrue(stack.isStackable(stackOnTop));
		assertTrue(stackOnTop.isStackable(stack));
		assertFalse(stack.isStackable(stack));
	}
	
	@Test
	public void testIsStackableInvitationScrolls() {
		final StackableItem stack = (StackableItem) SingletonRepository.getEntityManager().getItem("invitation scroll");
		final StackableItem stackOnTop = (StackableItem) SingletonRepository.getEntityManager().getItem("invitation scroll");
		assertTrue(stack.isStackable(stackOnTop));
		assertTrue(stackOnTop.isStackable(stack));
		assertFalse(stack.isStackable(stack));
		final StackableItem summonStack = (StackableItem) SingletonRepository.getEntityManager().getItem("summon scroll");
		assertFalse(stack.isStackable(summonStack));
	}

}
