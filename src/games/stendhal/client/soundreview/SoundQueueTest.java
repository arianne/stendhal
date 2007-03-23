package games.stendhal.client.soundreview;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class SoundQueueTest {

	SoundQueue<String> sq;

	@Before
	public void setUp() throws Exception {
		sq = new SoundQueue<String>();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsEmpty() {
		assertTrue(sq.isEmpty());
		sq.offer(new String("hug"));
		assertFalse(sq.isEmpty());
	}

	@Test
	public void testOfferPeekPoll() {
		assertTrue("should be empty at start", sq.isEmpty());
		String hug = new String("hug");
		sq.offer(hug);
		assertFalse("should not be empty after offer", sq.isEmpty());
		String st = (String) sq.peek();
		assertEquals("hug", st);
		assertTrue("identical object should be retrieved", hug == st);
		assertFalse("should not be empty after peek", sq.isEmpty());
		st = (String) sq.poll();
		assertEquals("hug", st);
		assertTrue("identical object should be retrieved", hug == st);
		assertTrue("should be empty after poll", sq.isEmpty());
	}

	@Test
	public void testPeek() {
		String hug = new String("hug");
		sq.offer(hug);
		assertFalse("should not be empty after offer", sq.isEmpty());
		String st = (String) sq.peek();
		assertEquals("hug", st);
		String boom = new String("boom");
		sq.offer(boom);
		st = (String) sq.peek();
		assertEquals("thw frist should be returned by peek", hug, st);

	}

	@Test
	public void testPoll() {
		String hug = new String("hug");
		sq.offer(hug);
		assertFalse("should not be empty after offer", sq.isEmpty());
		String boom = new String("boom");
		sq.offer(boom);
		String st = (String) sq.poll();
		assertEquals("the frist should be returned by first poll", hug, st);
		st = (String) sq.poll();
		assertEquals("the second  should be returned by second poll", boom, st);
	}

	@Test
	public void testClear() {
		assertTrue(sq.isEmpty());
		for (int i = 0; i < 5; i++) {
			sq.offer(new String());
		}
		assertFalse(sq.isEmpty());
		sq.clear();
		assertTrue(sq.isEmpty());

	}

}
