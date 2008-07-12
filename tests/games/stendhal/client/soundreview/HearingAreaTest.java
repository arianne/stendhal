package games.stendhal.client.soundreview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import games.stendhal.client.entity.User;

import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

public class HearingAreaTest {

	@Before
	public void setUp() throws Exception {

		HearingArea.set(0, 0);
		User.setNull();
	}

	@Test
	public void contains() {
		runContainTests();
		new User();
		runContainTests();
	}

	private void runContainTests() {
		assertTrue("both in ", HearingArea.contains(0, 0));
		assertFalse("both out", HearingArea.contains(100, 100));
		assertFalse("edge does not belong", HearingArea.contains(
				-HearingArea.HEARINGDIST, -HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", HearingArea.contains(
				HearingArea.HEARINGDIST, HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", HearingArea.contains(
				-HearingArea.HEARINGDIST, HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", HearingArea.contains(
				HearingArea.HEARINGDIST, -HearingArea.HEARINGDIST));
		assertTrue("inner edge belongs", HearingArea.contains(
				-(HearingArea.HEARINGDIST - 1), -(HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", HearingArea.contains(
				(HearingArea.HEARINGDIST - 1), (HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", HearingArea.contains(
				-(HearingArea.HEARINGDIST - 1), (HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", HearingArea.contains(
				(HearingArea.HEARINGDIST - 1), -(HearingArea.HEARINGDIST - 1)));
		assertFalse("x in , y out", HearingArea.contains(0, 100));
		assertFalse("x out y in", HearingArea.contains(100, 10));
	}

	@Test
	public void move() {
		assertTrue("both in ", HearingArea.contains(0, 0));
		HearingArea.moveTo(20, 20);
		assertFalse(" edge does not belong", HearingArea.contains(0, 0));
		assertFalse(" edge does not belong", HearingArea.contains(40, 40));
		assertFalse(" edge does not belong", HearingArea.contains(0, 40));
		assertFalse(" edge does not belong", HearingArea.contains(40, 0));
		assertFalse("both out", HearingArea.contains(100, 100));
		assertTrue("both in", HearingArea.contains(20, 20));
	}

	@Test
	public void getAsRect() {
		final int centerX = 1;
		final int centerY = 5;
		HearingArea.set(centerX, centerY);
		Rectangle2D rect = HearingArea.getAsRect();
		assertEquals(-HearingArea.HEARINGDIST + centerX, (int) rect.getMinX());
		assertEquals(-HearingArea.HEARINGDIST + centerY, (int) rect.getMinY());
		assertEquals(HearingArea.HEARINGDIST + centerX, (int) rect.getMaxX());
		assertEquals(HearingArea.HEARINGDIST + centerY, (int) rect.getMaxY());
		new User();
		rect = HearingArea.getAsRect();
		assertEquals(-HearingArea.HEARINGDIST + User.get().getX(),
				rect.getMinX(), 0.001);
		assertEquals(-HearingArea.HEARINGDIST + User.get().getY(),
				rect.getMinY(), 0.001);
		assertEquals(HearingArea.HEARINGDIST + User.get().getX(),
				rect.getMaxX(), 0.001);
		assertEquals(HearingArea.HEARINGDIST + User.get().getY(),
				rect.getMaxY(), 0.001);
	}

	@Test
	public void getAsRectSetDouble() {
		final double centerX = 1;
		final double centerY = 5;
		HearingArea.set(centerX, centerY);

		final Rectangle2D rect = HearingArea.getAsRect();
		assertEquals(-HearingArea.HEARINGDIST + centerX, rect.getMinX(), 0.001);
		assertEquals(-HearingArea.HEARINGDIST + centerY, rect.getMinY(), 0.001);
		assertEquals(HearingArea.HEARINGDIST + centerX, rect.getMaxX(), 0.001);
		assertEquals(HearingArea.HEARINGDIST + centerY, rect.getMaxY(), 0.001);
	}
}
