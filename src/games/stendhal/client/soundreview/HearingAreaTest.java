package games.stendhal.client.soundreview;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.geom.Rectangle2D;

import org.junit.Before;
import org.junit.Test;

public class HearingAreaTest {

	HearingArea area;

	@Before
	public void setUp() throws Exception {
		area = new HearingArea(0, 0);
	}

	@Test
	public void contains() {
		assertTrue("both in ", area.contains(0, 0));
		assertFalse("both out", area.contains(100, 100));
		assertFalse("edge does not belong", area.contains(-HearingArea.HEARINGDIST,
		        -HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", area.contains(HearingArea.HEARINGDIST,
		        HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", area.contains(-HearingArea.HEARINGDIST,
		        HearingArea.HEARINGDIST));
		assertFalse("edge does not belong", area.contains(HearingArea.HEARINGDIST,
		        -HearingArea.HEARINGDIST));
		assertTrue("inner edge belongs", area.contains(-(HearingArea.HEARINGDIST - 1),
		        -(HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", area.contains((HearingArea.HEARINGDIST - 1),
		        (HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", area.contains(-(HearingArea.HEARINGDIST - 1),
		        (HearingArea.HEARINGDIST - 1)));
		assertTrue("inner edge belongs", area.contains((HearingArea.HEARINGDIST - 1),
		        -(HearingArea.HEARINGDIST - 1)));
		assertFalse("x in , y out", area.contains(0, 100));
		assertFalse("x out y in", area.contains(100, 10));
	}

	@Test
	public void move() {
		assertTrue("both in ", area.contains(0, 0));
		area.moveTo(20, 20);
		assertFalse(" edge does not belong", area.contains(0, 0));
		assertFalse(" edge does not belong", area.contains(40, 40));
		assertFalse(" edge does not belong", area.contains(0, 40));
		assertFalse(" edge does not belong", area.contains(40, 0));
		assertFalse("both out", area.contains(100, 100));
		assertTrue("both in", area.contains(20, 20));
	}

	@Test
	public void get() {
		assertTrue("instance is object", area == HearingArea.get());
	}

	@Test
	public void getAsArea() {
		int centerX = 1;
		int centerY = 5;
		area.set(centerX, centerY);
		Rectangle2D rect = area.getAsRect();
		assertEquals(-HearingArea.HEARINGDIST + centerX, (int) rect.getMinX());
		assertEquals(-HearingArea.HEARINGDIST + centerY, (int) rect.getMinY());
		assertEquals(HearingArea.HEARINGDIST + centerX, (int) rect.getMaxX());
		assertEquals(HearingArea.HEARINGDIST + centerY, (int) rect.getMaxY());
	}
}
