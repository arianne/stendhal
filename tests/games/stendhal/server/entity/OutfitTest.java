package games.stendhal.server.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class OutfitTest {

	@Test
	public void testOutfit() {
		final Outfit ou = new Outfit();
		assertEquals(Integer.valueOf(0), ou.getHair());
		assertEquals(Integer.valueOf(0), ou.getHead());
		assertEquals(Integer.valueOf(0), ou.getDress());
		assertEquals(Integer.valueOf(0), ou.getBase());

	}

	@Test
	public void testOutfitIntegerIntegerIntegerInteger() {
		final Outfit ou = new Outfit(1, 2, 3, 4);
		assertEquals(Integer.valueOf(1), ou.getHair());
		assertEquals(Integer.valueOf(2), ou.getHead());
		assertEquals(Integer.valueOf(3), ou.getDress());
		assertEquals(Integer.valueOf(4), ou.getBase());
	}

	@Test
	public void testOutfitInt() {
		Outfit ou = new Outfit(0);
		assertEquals(Integer.valueOf(0), ou.getHair());
		assertEquals(Integer.valueOf(0), ou.getHead());
		assertEquals(Integer.valueOf(0), ou.getDress());
		assertEquals(Integer.valueOf(0), ou.getBase());
		ou = new Outfit(1020304);
		assertEquals(Integer.valueOf(1), ou.getHair());
		assertEquals(Integer.valueOf(2), ou.getHead());
		assertEquals(Integer.valueOf(3), ou.getDress());
		assertEquals(Integer.valueOf(4), ou.getBase());

		final String outfitnumber = "01020304";
		ou = new Outfit(Integer.parseInt(outfitnumber));
		assertEquals(Integer.valueOf(1), ou.getHair());
		assertEquals(Integer.valueOf(2), ou.getHead());
		assertEquals(Integer.valueOf(3), ou.getDress());
		assertEquals(Integer.valueOf(4), ou.getBase());
		// coded octal
		ou = new Outfit(01020304); 
		assertEquals(Integer.valueOf(0), ou.getHair());
		assertEquals(Integer.valueOf(27), ou.getHead());
		assertEquals(Integer.valueOf(5), ou.getDress());
		assertEquals(Integer.valueOf(32), ou.getBase());

	}

	@Test
	public void testSetGetBase() {
		final Outfit of = new Outfit();
		of.setBase(0);
		assertEquals(Integer.valueOf(0), of.getBase());
		of.setBase(100);
		assertEquals(Integer.valueOf(100), of.getBase());
		of.setBase(123);
		assertEquals(Integer.valueOf(123), of.getBase());

	}

	@Test
	public void testSetGetDress() {
		final Outfit of = new Outfit();
		of.setDress(0);
		assertEquals(Integer.valueOf(0), of.getDress());
		of.setDress(-1);
		assertEquals(Integer.valueOf(-1), of.getDress());
		of.setDress(123);
		assertEquals(Integer.valueOf(123), of.getDress());
	}

	@Test
	public void testSetGetHair() {
		final Outfit of = new Outfit();
		of.setHair(0);
		assertEquals(Integer.valueOf(0), of.getHair());
		of.setHair(-1);
		assertEquals(Integer.valueOf(-1), of.getHair());
		of.setHair(123);
		assertEquals(Integer.valueOf(123), of.getHair());
	}

	@Test
	public void testSetGetHead() {
		final Outfit of = new Outfit();
		of.setHead(0);
		assertEquals(Integer.valueOf(0), of.getHead());
		of.setHead(-1);
		assertEquals(Integer.valueOf(-1), of.getHead());
		of.setHead(123);
		assertEquals(Integer.valueOf(123), of.getHead());
	}

	@Test
	public void testGetCode() {
		assertEquals(12345678, new Outfit(12345678).getCode());
	}

	@Test
	public void testPutOver() {
		final Outfit orig = new Outfit(12345678);
		final Outfit pullover = new Outfit();
		assertEquals(12345678, orig.getCode());

		Outfit result = orig.putOver(pullover);
		assertEquals(12345678, result.getCode());
		orig.setBase(null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(34), result.getHead());
		assertEquals(Integer.valueOf(56), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBase());
		orig.setDress(null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(34), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBase());
		orig.setHead(null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(12), result.getHair());
		assertEquals(Integer.valueOf(0), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBase());
		orig.setHair(null);
		result = orig.putOver(pullover);
		assertEquals(Integer.valueOf(0), result.getHair());
		assertEquals(Integer.valueOf(0), result.getHead());
		assertEquals(Integer.valueOf(0), result.getDress());
		assertEquals(Integer.valueOf(0), result.getBase());

	}

	@Test
	public void testIsPartOf() {
		final Outfit of = new Outfit();
		assertTrue(of.isPartOf(of));
		Outfit part = new Outfit(null, null, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(0, null, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, 0, null, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, 0, null);
		assertTrue(part.isPartOf(of));
		part = new Outfit(null, null, null, 0);
		assertTrue(part.isPartOf(of));
		part = new Outfit(0, null, null, 0);
		assertTrue(part.isPartOf(of));
		part = new Outfit(1, null, null, 0);
		assertFalse(part.isPartOf(of));
		part = new Outfit(1, 5, 5, 0);
		assertFalse(part.isPartOf(of));

	}

	@Test
	public void testIsChoosableByPlayers() {
		Outfit of = new Outfit();
		assertTrue(of.isChoosableByPlayers());
		of.setHair(50);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit();
		of.setHead(50);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit();
		of.setBase(50);
		assertFalse(of.isChoosableByPlayers());
		of = new Outfit();
		of.setDress(50);
		assertFalse(of.isChoosableByPlayers());
		// fail("wont work for any part of outfit == null");

	}

	@Test
	public void testIsNaked() {
		final Outfit of = new Outfit();
		assertTrue(of.isNaked());
		of.setDress(1);
		assertFalse(of.isNaked());
		of.setDress(null);
		assertTrue(of.isNaked());
	}

}
