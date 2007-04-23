package games.stendhal.common;

import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Test;


public class GrammarTest {

	@Test
	public void testItthem() {
		assertEquals("it", Grammar.itthem(1));
		assertEquals("them", Grammar.itthem(2));
		assertEquals("them", Grammar.itthem(0));
	}

	@Test
	public void testItThem() {
		assertEquals("It", Grammar.ItThem(1));
		assertEquals("Them", Grammar.ItThem(2));
		assertEquals("Them", Grammar.ItThem(0));
	}

	@Test
	public void testItthey() {
		assertEquals("it", Grammar.itthey(1));
		assertEquals("they", Grammar.itthey(2));
		assertEquals("they", Grammar.itthey(0));
}

	@Test
	public void testItThey() {
		assertEquals("It", Grammar.ItThey(1));
		assertEquals("They", Grammar.ItThey(2));
		assertEquals("They", Grammar.ItThey(0));
	}

	@Test
	public void testIsare() {
		assertEquals("is", Grammar.isare(1));
		assertEquals("are", Grammar.isare(2));
		assertEquals("are", Grammar.isare(0));
	}

	@Test
	public void testIsAre() {
		assertEquals("Is", Grammar.IsAre(1));
		assertEquals("Are", Grammar.IsAre(2));
		assertEquals("Are", Grammar.IsAre(0));
	}

	@Test
	public void testa_noun() {
		assertEquals("an eater", Grammar.a_noun("eater"));
		assertEquals("a money", Grammar.a_noun("money"));
		assertEquals("a youngster",Grammar.a_noun("youngster"));
	}

	@Test
	public void testFullform() {
		assertEquals("piece of meat", Grammar.fullform("Meat"));
		assertEquals("piece of ham", Grammar.fullform("Ham"));
		assertEquals("piece of cheese", Grammar.fullform("Cheese"));
		assertEquals("piece of wood", Grammar.fullform("wood"));
		assertEquals("piece of paper", Grammar.fullform("paper"));
		assertEquals("piece of iron", Grammar.fullform("iron"));
		assertEquals("nugget of iron ore", Grammar.fullform("iron ore"));
		assertEquals("sack of flour", Grammar.fullform("flour"));
		assertEquals("sheaf of grain", Grammar.fullform("grain"));
		assertEquals("loaf of bread", Grammar.fullform("bread"));
		assertEquals("bottle of beer", Grammar.fullform("beer"));
		assertEquals("bottle of wine", Grammar.fullform("wine"));
		assertEquals("bottle of poison", Grammar.fullform("poison"));
		assertEquals("bottle of antidote", Grammar.fullform("antidote"));
		assertEquals("money", Grammar.fullform("money"));
		assertEquals("whatever book", Grammar.fullform("book_whatever"));
		assertEquals("whatever book", Grammar.fullform("book whatever"));
		assertEquals("sprig of arandula", Grammar.fullform("arandula"));
		assertEquals("suit of iron_armor", Grammar.fullform("iron_armor"));
		assertEquals("suit of iron armor", Grammar.fullform("iron armor"));
		assertEquals("pair of iron_legs", Grammar.fullform("iron_legs"));
		assertEquals("pair of iron legs", Grammar.fullform("iron legs"));
		assertEquals("pair of iron boots", Grammar.fullform("iron boots"));
	}

	@Test
	public void testA_noun() {
		assertEquals("An eater", Grammar.A_noun("eater"));
		assertEquals("A money", Grammar.A_noun("money"));
		
	}

	@Test
	public void testSuffix_s() {
		assertEquals("s'", Grammar.suffix_s("s"));
		assertEquals("a's", Grammar.suffix_s("a"));
	}

}
