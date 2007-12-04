package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.LinkedList;

import marauroa.common.Log4J;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarTest {
	@BeforeClass
	public static void setupClass() {
		Log4J.init();

	}

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
	public void testOrderedInt() {
		assertEquals("first", Grammar.ordered(1));
		assertEquals("second", Grammar.ordered(2));
		assertEquals("third", Grammar.ordered(3));
		assertEquals("4", Grammar.ordered(4));
	}

	@Test
	public void testIsare() {
		assertEquals("is", Grammar.isare(1));
		assertEquals("are", Grammar.isare(2));
		assertEquals("are", Grammar.isare(0));
	}

	@Test
	public void testarticle_noun() {
		assertEquals("the test", Grammar.article_noun("test", true));
		assertEquals("a test", Grammar.article_noun("test", false));
	}

	@Test
	public void testIsAre() {
		assertEquals("Is", Grammar.IsAre(1));
		assertEquals("Are", Grammar.IsAre(2));
		assertEquals("Are", Grammar.IsAre(0));
	}

	@Test
	public void testa_noun() {
		assertNull(Grammar.a_noun(null));
		assertEquals("an eater", Grammar.a_noun("eater"));
		assertEquals("a money", Grammar.a_noun("money"));
		assertEquals("a youngster", Grammar.a_noun("youngster"));
		assertEquals("an yclept", Grammar.a_noun("yclept"));
		assertEquals("a s", Grammar.a_noun("s"));
		assertEquals("an a", Grammar.a_noun("a"));
		assertEquals("a ", Grammar.a_noun(""));
		assertEquals("a eupepsia", Grammar.a_noun("eupepsia"));

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
		assertEquals("A s", Grammar.A_noun("s"));
		assertEquals("An a", Grammar.A_noun("a"));
		assertEquals("A ", Grammar.A_noun(""));

	}

	@Test
	public void testSuffix_s() {
		assertEquals("s'", Grammar.suffix_s("s"));
		assertEquals("a's", Grammar.suffix_s("a"));
	}

	private static void testPluralisationOfAGivenSingularWord(String message,
			String plural, String singular) {
		Assert.assertEquals(message, plural, Grammar.plural(singular));

		/*
		 * Calling plural() with words already in their plural form does not yet
		 * work. Assert.assertEquals("no change expected", plural,
		 * Grammar.plural(plural));
		 */
	}

	private static void testPluralisationOfAGivenSingularWord(String plural,
			String singular) {
		testPluralisationOfAGivenSingularWord("building plural form", plural,
				singular);
	}

	@Test
	public void testPluralItem() {

		testPluralisationOfAGivenSingularWord("hammers+3", "hammer+3");
		// TODO: decide on name and plural of money
		testPluralisationOfAGivenSingularWord("money", "money");
		testPluralisationOfAGivenSingularWord("sandwiches", "sandwich");
		testPluralisationOfAGivenSingularWord("knives", "knife");
		testPluralisationOfAGivenSingularWord("daggers", "dagger");
		testPluralisationOfAGivenSingularWord("short swords", "short sword");
		testPluralisationOfAGivenSingularWord("swords", "sword");
		testPluralisationOfAGivenSingularWord("scimitars", "scimitar");
		testPluralisationOfAGivenSingularWord(
				"wikipedia says katana/katanas ok", "katanas", "katana");
		testPluralisationOfAGivenSingularWord("claymores", "claymore");
		testPluralisationOfAGivenSingularWord("broadswords", "broadsword");
		testPluralisationOfAGivenSingularWord("biting swords", "biting sword");
		testPluralisationOfAGivenSingularWord("fire swords", "fire sword");
		testPluralisationOfAGivenSingularWord("ice swords", "ice sword");
		testPluralisationOfAGivenSingularWord("great swords", "great sword");
		testPluralisationOfAGivenSingularWord("r hand swords", "r hand sword");
		testPluralisationOfAGivenSingularWord("l hand swords", "l hand sword");
		testPluralisationOfAGivenSingularWord("small axes", "small axe");
		testPluralisationOfAGivenSingularWord("hand axes", "hand axe");
		testPluralisationOfAGivenSingularWord("axes", "axe");
		testPluralisationOfAGivenSingularWord("battle axes", "battle axe");
		testPluralisationOfAGivenSingularWord("bardiches", "bardiche");
		testPluralisationOfAGivenSingularWord("scythes", "scythe");
		testPluralisationOfAGivenSingularWord("twoside axes", "twoside axe");
		testPluralisationOfAGivenSingularWord("halberds", "halberd");
		testPluralisationOfAGivenSingularWord("twoside axes +3",
				"twoside axe +3");
		testPluralisationOfAGivenSingularWord("clubs", "club");
		testPluralisationOfAGivenSingularWord("staffs", "staff");
		testPluralisationOfAGivenSingularWord("maces", "mace");
		testPluralisationOfAGivenSingularWord("flails", "flail");
		testPluralisationOfAGivenSingularWord("maces +1", "mace +1");
		testPluralisationOfAGivenSingularWord("maces +2", "mace +2");
		testPluralisationOfAGivenSingularWord("skull staffs", "skull staff");
		testPluralisationOfAGivenSingularWord("flails +2", "flail +2");
		testPluralisationOfAGivenSingularWord("hammers", "hammer");
		testPluralisationOfAGivenSingularWord("hammers +3", "hammer +3");
		testPluralisationOfAGivenSingularWord("war hammers", "war hammer");
		testPluralisationOfAGivenSingularWord("wooden bows", "wooden bow");
		testPluralisationOfAGivenSingularWord("longbows", "longbow");
		testPluralisationOfAGivenSingularWord("longbows +1", "longbow +1");
		testPluralisationOfAGivenSingularWord("crossbows", "crossbow");
		testPluralisationOfAGivenSingularWord("wooden arrows", "wooden arrow");
		testPluralisationOfAGivenSingularWord("steel arrows", "steel arrow");
		testPluralisationOfAGivenSingularWord("golden arrows", "golden arrow");
		testPluralisationOfAGivenSingularWord("bucklers", "buckler");
		testPluralisationOfAGivenSingularWord("wooden shields", "wooden shield");
		testPluralisationOfAGivenSingularWord("studded shields",
				"studded shield");
		testPluralisationOfAGivenSingularWord("plate shields", "plate shield");
		testPluralisationOfAGivenSingularWord("lion shields", "lion shield");
		testPluralisationOfAGivenSingularWord("unicorn shields",
				"unicorn shield");
		testPluralisationOfAGivenSingularWord("lion shields +1",
				"lion shield +1");
		testPluralisationOfAGivenSingularWord("skull shields", "skull shield");
		testPluralisationOfAGivenSingularWord("crown shields", "crown shield");
		testPluralisationOfAGivenSingularWord("golden shields", "golden shield");
		testPluralisationOfAGivenSingularWord("dresses", "dress");
		testPluralisationOfAGivenSingularWord("suits of leather armor",
				"suit of leather armor");
		testPluralisationOfAGivenSingularWord("leather cuirasses",
				"leather cuirass");
		testPluralisationOfAGivenSingularWord("suits of leather armor +1",
				"suit of leather armor +1");
		testPluralisationOfAGivenSingularWord("leather cuirasses +1",
				"leather cuirass +1");
		testPluralisationOfAGivenSingularWord("suits of studded armor",
				"suit of studded armor");
		testPluralisationOfAGivenSingularWord("suits of chain armor",
				"suit of chain armor");
		testPluralisationOfAGivenSingularWord("suits of chain armor +1",
				"suit of chain armor +1");
		testPluralisationOfAGivenSingularWord("suits of scale armor",
				"suit of scale armor");
		testPluralisationOfAGivenSingularWord("suits of scale armor +1",
				"suit of scale armor +1");
		testPluralisationOfAGivenSingularWord("suits of scale armor +112",
				"suit of scale armor +112");
		testPluralisationOfAGivenSingularWord("suits of chain armor +3",
				"suit of chain armor +3");
		testPluralisationOfAGivenSingularWord("suits of scale armor +2",
				"suit of scale armor +2");
		testPluralisationOfAGivenSingularWord("suits of plate armor",
				"suit of plate armor");
		testPluralisationOfAGivenSingularWord("suits of golden armor",
				"suit of golden armor");
		testPluralisationOfAGivenSingularWord("leather helmets",
				"leather helmet");
		testPluralisationOfAGivenSingularWord("robins hats", "robins hat");
		testPluralisationOfAGivenSingularWord("studded helmets",
				"studded helmet");
		testPluralisationOfAGivenSingularWord("chain helmets", "chain helmet");
		testPluralisationOfAGivenSingularWord("viking helmets", "viking helmet");
		testPluralisationOfAGivenSingularWord("chain helmets +2",
				"chain helmet +2");
		testPluralisationOfAGivenSingularWord("golden helmets", "golden helmet");
		testPluralisationOfAGivenSingularWord("golden helmets +3",
				"golden helmet +3");
		testPluralisationOfAGivenSingularWord("trophy helmets", "trophy helmet");
		testPluralisationOfAGivenSingularWord("pairs of leather legs",
				"pair of leather legs");
		testPluralisationOfAGivenSingularWord("pairs of studded legs",
				"pair of studded legs");
		testPluralisationOfAGivenSingularWord("pairs of chain legs",
				"pair of chain legs");
		testPluralisationOfAGivenSingularWord("pairs of golden legs",
				"pair of golden legs");
		testPluralisationOfAGivenSingularWord("pairs of leather boots",
				"pair of leather boots");
		testPluralisationOfAGivenSingularWord("pairs of studded boots",
				"pair of studded boots");
		testPluralisationOfAGivenSingularWord("pairs of chain boots",
				"pair of chain boots");
		testPluralisationOfAGivenSingularWord("pairs of steel boots",
				"pair of steel boots");
		testPluralisationOfAGivenSingularWord("pairs of golden boots",
				"pair of golden boots");
		testPluralisationOfAGivenSingularWord("cloaks", "cloak");
		testPluralisationOfAGivenSingularWord("elf cloaks", "elf cloak");
		testPluralisationOfAGivenSingularWord("dwarf cloaks", "dwarf cloak");
		testPluralisationOfAGivenSingularWord("elf cloaks +2", "elf cloak +2");
		testPluralisationOfAGivenSingularWord("green dragon cloaks",
				"green dragon cloak");
		testPluralisationOfAGivenSingularWord("lich cloaks", "lich cloak");
		testPluralisationOfAGivenSingularWord("blue dragon cloaks",
				"blue dragon cloak");
		testPluralisationOfAGivenSingularWord("black dragon cloaks",
				"black dragon cloak");
		testPluralisationOfAGivenSingularWord("golden cloaks", "golden cloak");
		testPluralisationOfAGivenSingularWord("cherries", "cherry");
		testPluralisationOfAGivenSingularWord("pieces of cheese",
				"piece of cheese");
		testPluralisationOfAGivenSingularWord("carrots", "carrot");
		testPluralisationOfAGivenSingularWord("salads", "salad");
		testPluralisationOfAGivenSingularWord("apples", "apple");
		testPluralisationOfAGivenSingularWord("loaves of bread",
				"loaf of bread");
		testPluralisationOfAGivenSingularWord("chunks of meat", "chunk of meat");
		testPluralisationOfAGivenSingularWord("pieces of ham", "ham");
		testPluralisationOfAGivenSingularWord("sandwiches", "sandwich");
		testPluralisationOfAGivenSingularWord("pies", "pie");
		testPluralisationOfAGivenSingularWord("button mushrooms",
				"button mushroom");
		testPluralisationOfAGivenSingularWord("porcini", "porcini");
		testPluralisationOfAGivenSingularWord("toadstools", "toadstool");
		testPluralisationOfAGivenSingularWord("bottles of beer", "beer");
		testPluralisationOfAGivenSingularWord("flasks of wine", "flask of wine");
		testPluralisationOfAGivenSingularWord("minor potions", "minor potion");
		testPluralisationOfAGivenSingularWord("bottles of antidote", "antidote");
		testPluralisationOfAGivenSingularWord("potions", "potion");
		testPluralisationOfAGivenSingularWord("greater potions",
				"greater potion");
		testPluralisationOfAGivenSingularWord("bottles of poison", "poison");
		testPluralisationOfAGivenSingularWord("bottles of greater poison",
				"greater poison");
		testPluralisationOfAGivenSingularWord("bottles of deadly poison",
				"deadly poison");
		testPluralisationOfAGivenSingularWord("flasks", "flask");
		testPluralisationOfAGivenSingularWord("bottles", "bottle");
		testPluralisationOfAGivenSingularWord("big bottles", "big bottle");

		testPluralisationOfAGivenSingularWord("black books", "black book");
		testPluralisationOfAGivenSingularWord("logs of wood", "log of wood");
		testPluralisationOfAGivenSingularWord("sheaves of grain",
				"sheaf of grain");
		testPluralisationOfAGivenSingularWord("bags of flour", "bag of flour");
		testPluralisationOfAGivenSingularWord("nuggets of iron ore",
				"nugget of iron ore");
		testPluralisationOfAGivenSingularWord("bars of iron", "bar of iron");
		testPluralisationOfAGivenSingularWord("golden gm tokens",
				"golden gm token");
		testPluralisationOfAGivenSingularWord("silvery gm tokens",
				"silvery gm token");
		testPluralisationOfAGivenSingularWord("bronze gm tokens",
				"bronze gm token");
		testPluralisationOfAGivenSingularWord("tokens", "token");
		testPluralisationOfAGivenSingularWord("notes", "note");
		testPluralisationOfAGivenSingularWord("coupons", "coupon");
		testPluralisationOfAGivenSingularWord("dice", "dice");
		testPluralisationOfAGivenSingularWord("teddies", "teddy");
		testPluralisationOfAGivenSingularWord("maps", "map");
		testPluralisationOfAGivenSingularWord("summon scrolls", "summon scroll");
		testPluralisationOfAGivenSingularWord("empty scrolls", "empty scroll");
		testPluralisationOfAGivenSingularWord("home scrolls", "home scroll");
		testPluralisationOfAGivenSingularWord("marked scrolls", "marked scroll");
		testPluralisationOfAGivenSingularWord("presents", "present");
		testPluralisationOfAGivenSingularWord("rods of the gm", "rod of the gm");
		testPluralisationOfAGivenSingularWord("rat keys", "rat key");
		testPluralisationOfAGivenSingularWord("sprigs of arandula", "arandula");

		testPluralisationOfAGivenSingularWord("mice", "mouse");

		testPluralisationOfAGivenSingularWord("houses", "house");
		testPluralisationOfAGivenSingularWord("houses of sun", "house of sun");
		testPluralisationOfAGivenSingularWord("geese", "goose");
		testPluralisationOfAGivenSingularWord("cabooses", "caboose");
		testPluralisationOfAGivenSingularWord("teeth", "tooth");
		testPluralisationOfAGivenSingularWord("feet", "foot");
		testPluralisationOfAGivenSingularWord("children", "child");

		testPluralisationOfAGivenSingularWord("tomatoes", "tomato");
		testPluralisationOfAGivenSingularWord("algae", "alga");
		testPluralisationOfAGivenSingularWord("larvae", "larva");
		testPluralisationOfAGivenSingularWord("hyphae", "hypha");
		testPluralisationOfAGivenSingularWord("bureaux", "bureau");
		testPluralisationOfAGivenSingularWord("a", "ium");
		testPluralisationOfAGivenSingularWord("dei", "deus");
		testPluralisationOfAGivenSingularWord("vortices", "vortex");

		testPluralisationOfAGivenSingularWord("xxxxses", "xxxxsis");

		testPluralisationOfAGivenSingularWord("matrices", "matrix");

		testPluralisationOfAGivenSingularWord("wumpuses", "wumpus");
		testPluralisationOfAGivenSingularWord("lotuses", "lotus");
		testPluralisationOfAGivenSingularWord("mumakil", "mumak");

		testPluralisationOfAGivenSingularWord("efreet", "efreeti");
		testPluralisationOfAGivenSingularWord("djinn", "djinni");
		testPluralisationOfAGivenSingularWord("ys", "y");
		testPluralisationOfAGivenSingularWord("bies", "by");
		testPluralisationOfAGivenSingularWord("fs", "f");

	}

	/**
	 * Tests pluralisation of creatures
	 */
	@Test
	public void testPluralCreatures() {
		testPluralisationOfAGivenSingularWord("deer", "deer");
		testPluralisationOfAGivenSingularWord("crabs", "crab");
		testPluralisationOfAGivenSingularWord("rats", "rat");
		testPluralisationOfAGivenSingularWord("bats", "bat");
		testPluralisationOfAGivenSingularWord("caverats", "caverat");
		testPluralisationOfAGivenSingularWord("penguins", "penguin");
		testPluralisationOfAGivenSingularWord("monkeys", "monkey");
		testPluralisationOfAGivenSingularWord("boars", "boar");
		testPluralisationOfAGivenSingularWord("wolves", "wolf");
		testPluralisationOfAGivenSingularWord("gnomes", "gnome");
		testPluralisationOfAGivenSingularWord("mage gnomes", "mage gnome");
		testPluralisationOfAGivenSingularWord("cobras", "cobra");
		testPluralisationOfAGivenSingularWord("bears", "bear");
		testPluralisationOfAGivenSingularWord("lions", "lion");
		testPluralisationOfAGivenSingularWord("goblins", "goblin");
		testPluralisationOfAGivenSingularWord("kobolds", "kobold");
		testPluralisationOfAGivenSingularWord("elephants", "elephant");
		testPluralisationOfAGivenSingularWord("venomrats", "venomrat");
		testPluralisationOfAGivenSingularWord("tigers", "tiger");
		testPluralisationOfAGivenSingularWord("skeletons", "skeleton");
		testPluralisationOfAGivenSingularWord("gargoyles", "gargoyle");
		testPluralisationOfAGivenSingularWord("young beholders",
				"young beholder");
		testPluralisationOfAGivenSingularWord("zombie rats", "zombie rat");
		testPluralisationOfAGivenSingularWord("veteran goblins",
				"veteran goblin");
		testPluralisationOfAGivenSingularWord("soldier kobolds",
				"soldier kobold");
		testPluralisationOfAGivenSingularWord(
				"plural of slime is slime but this is a creature....",
				"green slimes", "green slime");
		testPluralisationOfAGivenSingularWord("archer kobolds", "archer kobold");
		testPluralisationOfAGivenSingularWord("black bears", "black bear");
		testPluralisationOfAGivenSingularWord("elder gargoyles",
				"elder gargoyle");
		testPluralisationOfAGivenSingularWord("razorrats", "razorrat");
		testPluralisationOfAGivenSingularWord("cyclopses", "cyclops");
		testPluralisationOfAGivenSingularWord("beholders", "beholder");
		testPluralisationOfAGivenSingularWord("soldier goblins",
				"soldier goblin");
		testPluralisationOfAGivenSingularWord("veteran kobolds",
				"veteran kobold");
		testPluralisationOfAGivenSingularWord("trolls", "troll");
		testPluralisationOfAGivenSingularWord("orcs", "orc");
		testPluralisationOfAGivenSingularWord("dark gargoyles", "dark gargoyle");
		testPluralisationOfAGivenSingularWord("ogres", "ogre");
		testPluralisationOfAGivenSingularWord("mummies", "mummy");
		testPluralisationOfAGivenSingularWord("leader kobolds", "leader kobold");
		testPluralisationOfAGivenSingularWord("orc warriors", "orc warrior");
		testPluralisationOfAGivenSingularWord("orc hunters", "orc hunter");
		testPluralisationOfAGivenSingularWord("ghosts", "ghost");
		testPluralisationOfAGivenSingularWord("giantrats", "giantrat");
		testPluralisationOfAGivenSingularWord("elves", "elf");
		testPluralisationOfAGivenSingularWord("dwarves", "dwarf");
		testPluralisationOfAGivenSingularWord("ratmen", "ratman");
		testPluralisationOfAGivenSingularWord("ratwomen", "ratwoman");
		testPluralisationOfAGivenSingularWord("elder beholders",
				"elder beholder");
		testPluralisationOfAGivenSingularWord(
				"plural of slime is slime but this is a creature....",
				"brown slimes", "brown slime");
		testPluralisationOfAGivenSingularWord("venom gargoyles",
				"venom gargoyle");
		testPluralisationOfAGivenSingularWord("elder ogres", "elder ogre");
		testPluralisationOfAGivenSingularWord("dwarf guardians",
				"dwarf guardian");
		testPluralisationOfAGivenSingularWord(
				"chief is an exception to the v rule", "orc chiefs",
				"orc chief");
		testPluralisationOfAGivenSingularWord("militia elves", "militia elf");
		testPluralisationOfAGivenSingularWord("archer elves", "archer elf");
		testPluralisationOfAGivenSingularWord("zombies", "zombie");
		testPluralisationOfAGivenSingularWord("elder dwarves", "elder dwarf");
		testPluralisationOfAGivenSingularWord("soldier elves", "soldier elf");
		testPluralisationOfAGivenSingularWord("warrior skeletons",
				"warrior skeleton");
		testPluralisationOfAGivenSingularWord(
				"plural of slime is slime but this is a creature....",
				"black slimes", "black slime");
		testPluralisationOfAGivenSingularWord("wooden golems", "wooden golem");
		testPluralisationOfAGivenSingularWord("royal mummies", "royal mummy");
		testPluralisationOfAGivenSingularWord("archrats", "archrat");
		testPluralisationOfAGivenSingularWord("hero dwarves", "hero dwarf");
		testPluralisationOfAGivenSingularWord("mage elves", "mage elf");
		testPluralisationOfAGivenSingularWord("deaths", "death");
		testPluralisationOfAGivenSingularWord("commander elves",
				"commander elf");
		testPluralisationOfAGivenSingularWord("stone golems", "stone golem");
		testPluralisationOfAGivenSingularWord("archmage elves", "archmage elf");
		testPluralisationOfAGivenSingularWord("leader dwarves", "leader dwarf");
		testPluralisationOfAGivenSingularWord("demon skeletons",
				"demon skeleton");
		testPluralisationOfAGivenSingularWord("elf sacerdotists",
				"elf sacerdotist");
		testPluralisationOfAGivenSingularWord("earth elementals",
				"earth elemental");
		testPluralisationOfAGivenSingularWord("fire elementals",
				"fire elemental");
		testPluralisationOfAGivenSingularWord("water elementals",
				"water elemental");
		testPluralisationOfAGivenSingularWord("green dragons", "green dragon");
		testPluralisationOfAGivenSingularWord("death knights", "death knight");
		testPluralisationOfAGivenSingularWord("liches", "lich");
		testPluralisationOfAGivenSingularWord("blue dragons", "blue dragon");
		testPluralisationOfAGivenSingularWord("black dragons", "black dragon");

	}

	/**
	 * Tests pluralisation of creatures
	 */
	@Test
	public void testFullForm() {
		Assert.assertEquals("1 sandwich", Grammar.quantityplnoun(1, "sandwich"));
		Assert.assertEquals("2 sandwiches", Grammar.quantityplnoun(2,
				"sandwich"));
		Assert.assertEquals("1 piece of ham", Grammar.quantityplnoun(1, "ham"));
		Assert.assertEquals("2 pieces of ham", Grammar.quantityplnoun(2, "ham"));
		Assert.assertEquals("1 piece of cheese", Grammar.quantityplnoun(1,
				"cheese"));
		Assert.assertEquals("2 pieces of cheese", Grammar.quantityplnoun(2,
				"cheese"));
		Assert.assertEquals("1 loaf of bread", Grammar.quantityplnoun(1,
				"bread"));
		Assert.assertEquals("2 loaves of bread", Grammar.quantityplnoun(2,
				"bread"));
		Assert.assertEquals("1 #sandwich", Grammar.quantityplnoun(1,
				"#sandwich"));
		Assert.assertEquals("2 #sandwiches", Grammar.quantityplnoun(2,
				"#sandwich"));
		Assert.assertEquals("1 piece of #ham",
				Grammar.quantityplnoun(1, "#ham"));
		Assert.assertEquals("2 pieces of #ham", Grammar.quantityplnoun(2,
				"#ham"));
		Assert.assertEquals("1 piece of #cheese", Grammar.quantityplnoun(1,
				"#cheese"));
		Assert.assertEquals("2 pieces of #cheese", Grammar.quantityplnoun(2,
				"#cheese"));
		Assert.assertEquals("1 loaf of #bread", Grammar.quantityplnoun(1,
				"#bread"));
		Assert.assertEquals("2 loaves of #bread", Grammar.quantityplnoun(2,
				"#bread"));

	}

	@Test
	public void testEnumerateCollectionCollection() throws Exception {
		assertEquals("", Grammar.enumerateCollection(null));
		Collection<String> source = new LinkedList<String>();
		assertEquals("", Grammar.enumerateCollection(source));
		source.add("first");
		assertEquals("first", Grammar.enumerateCollection(source));
		source.add("second");
		assertEquals("first and second", Grammar.enumerateCollection(source));
		source.add("third");
		assertEquals("first, second, and third",
				Grammar.enumerateCollection(source));
	}

	private static void checkNumberString(int n, String string) {
		assertEquals(string, Grammar.numberString(n));
		assertEquals(n, Grammar.number(string));
	}

	@Test
	public void testNumberStrings() throws Exception {
		assertEquals(0, Grammar.number("zero"));
		checkNumberString(0, "no");
		checkNumberString(1, "one");
		checkNumberString(2, "two");
		checkNumberString(3, "three");
		checkNumberString(4, "four");
		checkNumberString(5, "five");
		checkNumberString(6, "six");
		checkNumberString(7, "seven");
		checkNumberString(8, "eight");
		checkNumberString(9, "nine");
		checkNumberString(10, "ten");
		checkNumberString(11, "eleven");
		checkNumberString(12, "twelve");
	}

}
