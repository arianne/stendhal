package games.stendhal.client.gui.bag;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ItemImageTest {

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

	/**
	 * Tests for init.
	 */
	@Test
	public final void testInit() {
		ItemImage img = new ItemImage();
		BufferedImage buf = new BufferedImage(128, 64, BufferedImage.TYPE_INT_RGB);
		CircledCollection<BufferedImage>[] result = img.init(buf);
		assertEquals(2, result.length);
		assertEquals(4, result[0].size());
		
	}

}
