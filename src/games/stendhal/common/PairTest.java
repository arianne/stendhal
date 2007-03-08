package games.stendhal.common;

import static org.junit.Assert.*;

import org.junit.Test;

public class PairTest {

	
	@Test
	public final void testFirst() {
		Pair <String, Integer> p1 ;
		String hugostr=new String("hugo");
		p1 = new Pair<String, Integer>(hugostr,new Integer(1));
		
		assertTrue(hugostr== p1.first());
		assertEquals(hugostr, p1.first());
		assertEquals("hugo", p1.first());
		assertEquals(new String("hugo"),p1.first());
		assertFalse(new String("hugo")== p1.first());
	}

	
	@Test
	public final void testEquals() {
		Pair<String,Integer>p1,p2,p3;
		Integer intOne= new Integer(1);
		p1 = new Pair<String,Integer>(new String("hugo"),new Integer(1));
		p2 = new Pair<String,Integer>(new String("hugo"),new Integer(1));
		p3 = new Pair<String,Integer>(new String("hugo"),new Integer(1));
		assertTrue(p1.first().equals(p2.first()));
		assertTrue(p2.first().equals(p3.first()));
		assertTrue(p1.first().equals(p3.first()));
		assertTrue(p1.second().equals(p2.second()));
		assertTrue(p2.second().equals(p3.second()));
		assertTrue(p1.second().equals(p3.second()));
		assertTrue(p1.equals(p1));
		assertTrue(p1.equals(p2));
		
		assertTrue(p2.equals(p3));
		assertTrue(p1.equals(p3));
		assertTrue(p2.equals(p1));
		Pair<String,Integer> pfirstnull1= new Pair<String,Integer>(null,intOne);
		Pair<String,Integer> pfirstnull2= new Pair<String,Integer>(null,intOne);
		Pair<String,Integer> pbothnull1= new Pair<String,Integer>(null,null);
		Pair<String,Integer> pbothnull2= new Pair<String,Integer>(null,null);
		assertTrue(pfirstnull1.equals(pfirstnull2));
		assertTrue(pbothnull1.equals(pbothnull2));
		
		
	}

}
