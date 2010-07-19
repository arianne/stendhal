package games.stendhal.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import games.stendhal.server.entity.npc.parser.ConversationParser;

import java.util.Collection;
import java.util.LinkedList;

import marauroa.common.Log4J;

import org.junit.BeforeClass;
import org.junit.Test;

public class GrammarTest {
	@BeforeClass
	public static void setupClass() {
		Log4J.init();
	}

	/**
	 * Tests for itthem.
	 */
	@Test
	public void testItthem() {
		assertEquals("it", Grammar.itthem(1));
		assertEquals("them", Grammar.itthem(2));
		assertEquals("them", Grammar.itthem(0));
	}

	/**
	 * Tests for itThem.
	 */
	@Test
	public void testItThem() {
		assertEquals("It", Grammar.ItThem(1));
		assertEquals("Them", Grammar.ItThem(2));
		assertEquals("Them", Grammar.ItThem(0));
	}

	/**
	 * Tests for itthey.
	 */
	@Test
	public void testItthey() {
		assertEquals("it", Grammar.itthey(1));
		assertEquals("they", Grammar.itthey(2));
		assertEquals("they", Grammar.itthey(0));
	}

	/**
	 * Tests for itThey.
	 */
	@Test
	public void testItThey() {
		assertEquals("It", Grammar.ItThey(1));
		assertEquals("They", Grammar.ItThey(2));
		assertEquals("They", Grammar.ItThey(0));
	}

	/**
	 * Tests for orderedInt.
	 */
	@Test
	public void testOrderedInt() {
		assertEquals("first", Grammar.ordered(1));
		assertEquals("second", Grammar.ordered(2));
		assertEquals("third", Grammar.ordered(3));
		assertEquals("4", Grammar.ordered(4));
	}

	/**
	 * Tests for isare.
	 */
	@Test
	public void testIsare() {
		assertEquals("is", Grammar.isare(1));
		assertEquals("are", Grammar.isare(2));
		assertEquals("are", Grammar.isare(0));
	}

	/**
	 * Tests for article_noun.
	 */
	@Test
	public void testarticle_noun() {
		assertEquals("the test", Grammar.article_noun("test", true));
		assertEquals("a test", Grammar.article_noun("test", false));
	}

	/**
	 * Tests for isAre.
	 */
	@Test
	public void testIsAre() {
		assertEquals("Is", Grammar.IsAre(1));
		assertEquals("Are", Grammar.IsAre(2));
		assertEquals("Are", Grammar.IsAre(0));
	}
	
	@Test
	public void testHasHave() {
		assertEquals("Has", Grammar.IsAre(1));
		assertEquals("Have", Grammar.IsAre(2));
		assertEquals("Have", Grammar.IsAre(0));
	}

	/**
	 * Tests for a_noun.
	 */
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

	/**
	 * Tests for fullform.
	 */
	@Test
	public void testFullform() {
		assertEquals("piece of meat", Grammar.fullForm("Meat"));
		assertEquals("piece of ham", Grammar.fullForm("Ham"));
		assertEquals("piece of cheese", Grammar.fullForm("Cheese"));
		assertEquals("piece of wood", Grammar.fullForm("wood"));
		assertEquals("piece of paper", Grammar.fullForm("paper"));
		assertEquals("piece of iron", Grammar.fullForm("iron"));
		assertEquals("nugget of iron ore", Grammar.fullForm("iron ore"));
		assertEquals("sack of flour", Grammar.fullForm("flour"));
		assertEquals("sheaf of grain", Grammar.fullForm("grain"));
		assertEquals("loaf of bread", Grammar.fullForm("bread"));
		assertEquals("bottle of beer", Grammar.fullForm("beer"));
		assertEquals("glass of wine", Grammar.fullForm("wine"));
		assertEquals("bottle of poison", Grammar.fullForm("poison"));
		assertEquals("bottle of antidote", Grammar.fullForm("antidote"));
		assertEquals("money", Grammar.fullForm("money"));
		assertEquals("whatever book", Grammar.fullForm("book whatever"));
		assertEquals("sprig of arandula", Grammar.fullForm("arandula"));
		assertEquals("suit of iron armor", Grammar.fullForm("iron armor"));
		assertEquals("pair of iron legs", Grammar.fullForm("iron legs"));
		assertEquals("pair of iron boots", Grammar.fullForm("iron boots"));
	}

	/**
	 * Tests for a_noun.
	 */
	@Test
	public void testA_noun() {
		assertEquals("An eater", Grammar.A_noun("eater"));
		assertEquals("A money", Grammar.A_noun("money"));
		assertEquals("A s", Grammar.A_noun("s"));
		assertEquals("An a", Grammar.A_noun("a"));
		assertEquals("A ", Grammar.A_noun(""));

	}

	/**
	 * Tests for suffix_s.
	 */
	@Test
	public void testSuffix_s() {
		assertEquals("s'", Grammar.suffix_s("s"));
		assertEquals("a's", Grammar.suffix_s("a"));
	}

	private static void testPluralisationOfAGivenSingularWord(final String message, final String plural, final String singular) {
		assertEquals(message, plural, Grammar.plural(singular));
		assertEquals("no change expected", plural, Grammar.plural(plural));
	}

	private static void testSingularisationOfAGivenSingularWord(final String message, final String plural, final String singular) {
		assertEquals(message, Grammar.fullForm(singular), Grammar.singular(plural));
		assertEquals("no change expected", singular, Grammar.singular(singular));
	}

	private static void testPluralisationAndSingularisation(final String plural, final String singular) {
		testPluralisationOfAGivenSingularWord("building plural form", plural, singular);
		testSingularisationOfAGivenSingularWord("building singular form", plural, singular);
	}

	private static void testPluralisationAndSingularisation(final String message, final String plural, final String singular) {
		testPluralisationOfAGivenSingularWord(message, plural, singular);
		testSingularisationOfAGivenSingularWord(message, plural, singular);
	}

	/**
	 * Tests for pluralItem.
	 */
	@Test
	public void testPluralItem() {
		testPluralisationAndSingularisation("money", "money");
		testPluralisationAndSingularisation("sandwiches", "sandwich");
		testPluralisationAndSingularisation("knives", "knife");
		testPluralisationAndSingularisation("daggers", "dagger");
		testPluralisationAndSingularisation("short swords", "short sword");
		testPluralisationAndSingularisation("swords", "sword");
		testPluralisationAndSingularisation("scimitars", "scimitar");
		testPluralisationAndSingularisation(
				"wikipedia says katana/katanas ok", "katanas", "katana");
		testPluralisationAndSingularisation("claymores", "claymore");
		testPluralisationAndSingularisation("broadswords", "broadsword");
		testPluralisationAndSingularisation("biting swords", "biting sword");
		testPluralisationAndSingularisation("fire swords", "fire sword");
		testPluralisationAndSingularisation("ice swords", "ice sword");
		testPluralisationAndSingularisation("great swords", "great sword");
		testPluralisationAndSingularisation("r hand swords", "r hand sword");
		testPluralisationAndSingularisation("l hand swords", "l hand sword");
		testPluralisationAndSingularisation("small axes", "small axe");
		testPluralisationAndSingularisation("hand axes", "hand axe");
		testPluralisationAndSingularisation("axes", "axe");
		testPluralisationAndSingularisation("battle axes", "battle axe");
		testPluralisationAndSingularisation("bardiches", "bardiche");
		testPluralisationAndSingularisation("scythes", "scythe");
		testPluralisationAndSingularisation("twoside axes", "twoside axe");
		testPluralisationAndSingularisation("halberds", "halberd");
		testPluralisationAndSingularisation("twoside axes +3",
				"twoside axe +3");
		testPluralisationAndSingularisation("clubs", "club");
		testPluralisationAndSingularisation("staffs", "staff");
		testPluralisationAndSingularisation("maces", "mace");
		testPluralisationAndSingularisation("flails", "flail");
		testPluralisationAndSingularisation("maces +1", "mace +1");
		testPluralisationAndSingularisation("maces +2", "mace +2");
		testPluralisationAndSingularisation("skull staffs", "skull staff");
		testPluralisationAndSingularisation("flails +2", "flail +2");
		testPluralisationAndSingularisation("hammers", "hammer");
		testPluralisationAndSingularisation("hammers +3", "hammer +3");
		testPluralisationAndSingularisation("war hammers", "war hammer");
		testPluralisationAndSingularisation("wooden bows", "wooden bow");
		testPluralisationAndSingularisation("longbows", "longbow");
		testPluralisationAndSingularisation("longbows +1", "longbow +1");
		testPluralisationAndSingularisation("crossbows", "crossbow");
		testPluralisationAndSingularisation("wooden arrows", "wooden arrow");
		testPluralisationAndSingularisation("steel arrows", "steel arrow");
		testPluralisationAndSingularisation("golden arrows", "golden arrow");
		testPluralisationAndSingularisation("bucklers", "buckler");
		testPluralisationAndSingularisation("wooden shields", "wooden shield");
		testPluralisationAndSingularisation("studded shields",
				"studded shield");
		testPluralisationAndSingularisation("plate shields", "plate shield");
		testPluralisationAndSingularisation("lion shields", "lion shield");
		testPluralisationAndSingularisation("unicorn shields",
				"unicorn shield");
		testPluralisationAndSingularisation("lion shields +1",
				"lion shield +1");
		testPluralisationAndSingularisation("skull shields", "skull shield");
		testPluralisationAndSingularisation("crown shields", "crown shield");
		testPluralisationAndSingularisation("golden shields", "golden shield");
		testPluralisationAndSingularisation("dresses", "dress");
		testPluralisationAndSingularisation("suits of leather armor",
				"suit of leather armor");
		testPluralisationAndSingularisation("leather cuirasses",
				"leather cuirass");
		testPluralisationAndSingularisation("suits of leather armor +1",
				"suit of leather armor +1");
		testPluralisationAndSingularisation("leather cuirasses +1",
				"leather cuirass +1");
		testPluralisationAndSingularisation("suits of studded armor",
				"suit of studded armor");
		testPluralisationAndSingularisation("suits of chain armor",
				"suit of chain armor");
		testPluralisationAndSingularisation("suits of chain armor +1",
				"suit of chain armor +1");
		testPluralisationAndSingularisation("suits of scale armor",
				"suit of scale armor");
		testPluralisationAndSingularisation("suits of scale armor +1",
				"suit of scale armor +1");
		testPluralisationAndSingularisation("suits of scale armor +112",
				"suit of scale armor +112");
		testPluralisationAndSingularisation("suits of chain armor +3",
				"suit of chain armor +3");
		testPluralisationAndSingularisation("suits of scale armor +2",
				"suit of scale armor +2");
		testPluralisationAndSingularisation("suits of plate armor",
				"suit of plate armor");
		testPluralisationAndSingularisation("suits of golden armor",
				"suit of golden armor");
		testPluralisationAndSingularisation("leather helmets",
				"leather helmet");
		testPluralisationAndSingularisation("robins hats", "robins hat");
		testPluralisationAndSingularisation("studded helmets",
				"studded helmet");
		testPluralisationAndSingularisation("chain helmets", "chain helmet");
		testPluralisationAndSingularisation("viking helmets", "viking helmet");
		testPluralisationAndSingularisation("chain helmets +2",
				"chain helmet +2");
		testPluralisationAndSingularisation("golden helmets", "golden helmet");
		testPluralisationAndSingularisation("golden helmets +3",
				"golden helmet +3");
		testPluralisationAndSingularisation("trophy helmets", "trophy helmet");
		testPluralisationAndSingularisation("pairs of leather legs",
				"pair of leather legs");
		testPluralisationAndSingularisation("pairs of studded legs",
				"pair of studded legs");
		testPluralisationAndSingularisation("pairs of chain legs",
				"pair of chain legs");
		testPluralisationAndSingularisation("pairs of golden legs",
				"pair of golden legs");
		testPluralisationAndSingularisation("pairs of leather boots",
				"pair of leather boots");
		testPluralisationAndSingularisation("pairs of studded boots",
				"pair of studded boots");
		testPluralisationAndSingularisation("pairs of chain boots",
				"pair of chain boots");
		testPluralisationAndSingularisation("pairs of steel boots",
				"pair of steel boots");
		testPluralisationAndSingularisation("pairs of golden boots",
				"pair of golden boots");
		testPluralisationAndSingularisation("cloaks", "cloak");
		testPluralisationAndSingularisation("elf cloaks", "elf cloak");
		testPluralisationAndSingularisation("dwarf cloaks", "dwarf cloak");
		testPluralisationAndSingularisation("elf cloaks +2", "elf cloak +2");
		testPluralisationAndSingularisation("green dragon cloaks",
				"green dragon cloak");
		testPluralisationAndSingularisation("lich cloaks", "lich cloak");
		testPluralisationAndSingularisation("blue dragon cloaks",
				"blue dragon cloak");
		testPluralisationAndSingularisation("black dragon cloaks",
				"black dragon cloak");
		testPluralisationAndSingularisation("golden cloaks", "golden cloak");
		testPluralisationAndSingularisation("cherries", "cherry");
		testPluralisationAndSingularisation("pieces of cheese",
				"piece of cheese");
		testPluralisationAndSingularisation("carrots", "carrot");
		testPluralisationAndSingularisation("salads", "salad");
		testPluralisationAndSingularisation("apples", "apple");
		testPluralisationAndSingularisation("loaves of bread",
				"loaf of bread");
		testPluralisationAndSingularisation("chunks of meat", "chunk of meat");
		testPluralisationAndSingularisation("pieces of ham", "ham");
		testPluralisationAndSingularisation("sandwiches", "sandwich");
		testPluralisationAndSingularisation("pies", "pie");
		testPluralisationAndSingularisation("button mushrooms",
				"button mushroom");
		testPluralisationAndSingularisation("porcini", "porcini");
		testPluralisationAndSingularisation("toadstools", "toadstool");
		testPluralisationAndSingularisation("bottles of beer", "beer");
		testPluralisationAndSingularisation("flasks of wine", "flask of wine");
		testPluralisationAndSingularisation("bottles of minor potion", "minor potion");
		testPluralisationAndSingularisation("bottles of antidote", "antidote");
		testPluralisationAndSingularisation("bottles of potion", "potion");
		testPluralisationAndSingularisation("bottles of greater potion",
				"greater potion");
		testPluralisationAndSingularisation("bottles of poison", "poison");
		testPluralisationAndSingularisation("bottles of greater poison",
				"greater poison");
		testPluralisationAndSingularisation("bottles of deadly poison",
				"deadly poison");
		testPluralisationAndSingularisation("flasks", "flask");
		testPluralisationAndSingularisation("bottles", "bottle");
		testPluralisationAndSingularisation("big bottles", "big bottle");

		testPluralisationAndSingularisation("black books", "black book");
		testPluralisationAndSingularisation("logs of wood", "log of wood");
		testPluralisationAndSingularisation("sheaves of grain",
				"sheaf of grain");
		testPluralisationAndSingularisation("bags of flour", "bag of flour");
		testPluralisationAndSingularisation("nuggets of iron ore",
				"nugget of iron ore");
		testPluralisationAndSingularisation("bars of iron", "bar of iron");
		testPluralisationAndSingularisation("golden gm tokens",
				"golden gm token");
		testPluralisationAndSingularisation("silvery gm tokens",
				"silvery gm token");
		testPluralisationAndSingularisation("bronze gm tokens",
				"bronze gm token");
		testPluralisationAndSingularisation("tokens", "token");
		testPluralisationAndSingularisation("notes", "note");
		testPluralisationAndSingularisation("coupons", "coupon");
		testPluralisationAndSingularisation("dice", "dice");
		testPluralisationAndSingularisation("teddies", "teddy");
		testPluralisationAndSingularisation("maps", "map");
		testPluralisationAndSingularisation("summon scrolls", "summon scroll");
		testPluralisationAndSingularisation("empty scrolls", "empty scroll");
		testPluralisationAndSingularisation("home scrolls", "home scroll");
		testPluralisationAndSingularisation("marked scrolls", "marked scroll");
		testPluralisationAndSingularisation("presents", "present");
		testPluralisationAndSingularisation("rods of the gm", "rod of the gm");
		testPluralisationAndSingularisation("rat keys", "rat key");
		testPluralisationAndSingularisation("sprigs of arandula", "arandula");

		testPluralisationAndSingularisation("mice", "mouse");

		testPluralisationAndSingularisation("houses", "house");
		testPluralisationAndSingularisation("houses of sun", "house of sun");
		testPluralisationAndSingularisation("geese", "goose");
		testPluralisationAndSingularisation("cabooses", "caboose");
		testPluralisationAndSingularisation("teeth", "tooth");
		testPluralisationAndSingularisation("feet", "foot");
		testPluralisationAndSingularisation("children", "child");

        testPluralisationAndSingularisation("moose", "moose");
        testPluralisationAndSingularisation("nooses", "noose");
        testPluralisationAndSingularisation("helia", "helium");
        testPluralisationAndSingularisation("sodia", "sodium");
        testPluralisationAndSingularisation("men", "man");
        testPluralisationAndSingularisation("humans", "human");

        testPluralisationAndSingularisation("tomatoes", "tomato");
		testPluralisationAndSingularisation("algae", "alga");
		testPluralisationAndSingularisation("larvae", "larva");
		testPluralisationAndSingularisation("hyphae", "hypha");
		testPluralisationAndSingularisation("bureaux", "bureau");
		testPluralisationAndSingularisation("dei", "deus");
        testPluralisationAndSingularisation("indices", "index");
        testPluralisationAndSingularisation("vertices", "vertex");
		testPluralisationAndSingularisation("vortices", "vortex");

		testPluralisationAndSingularisation("matrices", "matrix");
        testPluralisationAndSingularisation("analyses", "analysis");
		testPluralisationAndSingularisation("xxxyses", "xxxysis");

		testPluralisationAndSingularisation("wumpuses", "wumpus");
		testPluralisationAndSingularisation("lotuses", "lotus");
		testPluralisationAndSingularisation("mumakil", "mumak");

		testPluralisationAndSingularisation("djinn", "djinni");
		testPluralisationAndSingularisation("efreet", "efreeti");
		testPluralisationAndSingularisation("ys", "y");
		// baby: ... + consonant + "y"
		testPluralisationAndSingularisation("abies", "aby"); 
		testPluralisationAndSingularisation("fs", "f");

        testPluralisationAndSingularisation("matches", "match");
        testPluralisationAndSingularisation("boxes", "box");
        testPluralisationAndSingularisation("bushes", "bush");
        testPluralisationAndSingularisation("boys", "boy");
        testPluralisationAndSingularisation("bosses", "boss");
        testPluralisationAndSingularisation("bodies", "body");
        testPluralisationAndSingularisation("princesses", "princess");
        testPluralisationAndSingularisation("wikipedias", "wikipedia");

        assertEquals("cheese", Grammar.singular("cheese"));
		testPluralisationAndSingularisation("pieces of cheese", "cheese");
		testPluralisationAndSingularisation("bicycles", "bicycle");
	}

	/**
	 * Tests pluralization of creatures.
	 */
	@Test
	public void testPluralCreatures() {
		testPluralisationAndSingularisation("deer", "deer");
		testPluralisationAndSingularisation("crabs", "crab");
		testPluralisationAndSingularisation("rats", "rat");
		testPluralisationAndSingularisation("bats", "bat");
		testPluralisationAndSingularisation("caverats", "caverat");
		testPluralisationAndSingularisation("penguins", "penguin");
		testPluralisationAndSingularisation("monkeys", "monkey");
		testPluralisationAndSingularisation("boars", "boar");
		testPluralisationAndSingularisation("wolves", "wolf");
		testPluralisationAndSingularisation("gnomes", "gnome");
		testPluralisationAndSingularisation("mage gnomes", "mage gnome");
		testPluralisationAndSingularisation("cobras", "cobra");
		testPluralisationAndSingularisation("bears", "bear");
		testPluralisationAndSingularisation("lions", "lion");
		testPluralisationAndSingularisation("goblins", "goblin");
		testPluralisationAndSingularisation("kobolds", "kobold");
		testPluralisationAndSingularisation("elephants", "elephant");
		testPluralisationAndSingularisation("venomrats", "venomrat");
		testPluralisationAndSingularisation("tigers", "tiger");
		testPluralisationAndSingularisation("skeletons", "skeleton");
		testPluralisationAndSingularisation("gargoyles", "gargoyle");
		testPluralisationAndSingularisation("young beholders",
				"young beholder");
		testPluralisationAndSingularisation("zombie rats", "zombie rat");
		testPluralisationAndSingularisation("veteran goblins",
				"veteran goblin");
		testPluralisationAndSingularisation("soldier kobolds",
				"soldier kobold");
		testPluralisationAndSingularisation(
				"plural of slime is slime but this is a creature....",
				"green slimes", "green slime");
		testPluralisationAndSingularisation("archer kobolds", "archer kobold");
		testPluralisationAndSingularisation("black bears", "black bear");
		testPluralisationAndSingularisation("elder gargoyles",
				"elder gargoyle");
		testPluralisationAndSingularisation("razorrats", "razorrat");
		testPluralisationAndSingularisation("cyclopses", "cyclops");
		testPluralisationAndSingularisation("erinyes", "erinys");
		testPluralisationAndSingularisation("beholders", "beholder");
		testPluralisationAndSingularisation("soldier goblins",
				"soldier goblin");
		testPluralisationAndSingularisation("veteran kobolds",
				"veteran kobold");
		testPluralisationAndSingularisation("trolls", "troll");
		testPluralisationAndSingularisation("orcs", "orc");
		testPluralisationAndSingularisation("dark gargoyles", "dark gargoyle");
		testPluralisationAndSingularisation("ogres", "ogre");
		testPluralisationAndSingularisation("mummies", "mummy");
		testPluralisationAndSingularisation("leader kobolds", "leader kobold");
		testPluralisationAndSingularisation("orc warriors", "orc warrior");
		testPluralisationAndSingularisation("orc hunters", "orc hunter");
		testPluralisationAndSingularisation("ghosts", "ghost");
		testPluralisationAndSingularisation("giantrats", "giantrat");
		testPluralisationAndSingularisation("elves", "elf");
		testPluralisationAndSingularisation("dwarves", "dwarf");
		testPluralisationAndSingularisation("ratmen", "ratman");
		testPluralisationAndSingularisation("ratwomen", "ratwoman");
		testPluralisationAndSingularisation("elder beholders",
				"elder beholder");
		testPluralisationAndSingularisation(
				"plural of slime is slime but this is a creature....",
				"brown slimes", "brown slime");
		testPluralisationAndSingularisation("venom gargoyles",
				"venom gargoyle");
		testPluralisationAndSingularisation("elder ogres", "elder ogre");
		testPluralisationAndSingularisation("dwarf guardians",
				"dwarf guardian");
		testPluralisationAndSingularisation(
				"chief is an exception to the v rule", "orc chiefs",
				"orc chief");
		testPluralisationAndSingularisation("militia elves", "militia elf");
		testPluralisationAndSingularisation("archer elves", "archer elf");
		testPluralisationAndSingularisation("zombies", "zombie");
		testPluralisationAndSingularisation("elder dwarves", "elder dwarf");
		testPluralisationAndSingularisation("soldier elves", "soldier elf");
		testPluralisationAndSingularisation("warrior skeletons",
				"warrior skeleton");
		testPluralisationAndSingularisation(
				"plural of slime is slime but this is a creature....",
				"black slimes", "black slime");
		testPluralisationAndSingularisation("wooden golems", "wooden golem");
		testPluralisationAndSingularisation("royal mummies", "royal mummy");
		testPluralisationAndSingularisation("archrats", "archrat");
		testPluralisationAndSingularisation("hero dwarves", "hero dwarf");
		testPluralisationAndSingularisation("mage elves", "mage elf");
		testPluralisationAndSingularisation("deaths", "death");
		testPluralisationAndSingularisation("commander elves",
				"commander elf");
		testPluralisationAndSingularisation("stone golems", "stone golem");
		testPluralisationAndSingularisation("archmage elves", "archmage elf");
		testPluralisationAndSingularisation("leader dwarves", "leader dwarf");
		testPluralisationAndSingularisation("demon skeletons",
				"demon skeleton");
		testPluralisationAndSingularisation("elf sacerdotists",
				"elf sacerdotist");
		testPluralisationAndSingularisation("earth elementals",
				"earth elemental");
		testPluralisationAndSingularisation("fire elementals",
				"fire elemental");
		testPluralisationAndSingularisation("water elementals",
				"water elemental");
		testPluralisationAndSingularisation("green dragons", "green dragon");
		testPluralisationAndSingularisation("death knights", "death knight");
		testPluralisationAndSingularisation("liches", "lich");
		testPluralisationAndSingularisation("blue dragons", "blue dragon");
		testPluralisationAndSingularisation("black dragons", "black dragon");
	}

	/**
	 * Tests creating the full form.
	 */
	@Test
	public void testFullForm() {
		assertEquals("a sandwich", Grammar.quantityplnoun(1, "sandwich", "a"));
		assertEquals("2 sandwiches", 
			Grammar.quantityplnoun(2, "sandwich"));
		assertEquals("a piece of ham", Grammar.quantityplnoun(1, "ham", "a"));
		assertEquals("2 pieces of ham", Grammar.quantityplnoun(2, "ham", "a"));
		assertEquals("a piece of cheese", 
			Grammar.quantityplnoun(1, "cheese", "a"));
		assertEquals("2 pieces of cheese", 
			Grammar.quantityplnoun(2, "cheese"));
		assertEquals("a loaf of bread", 
			Grammar.quantityplnoun(1, "bread", "a"));
		assertEquals("2 loaves of bread", 
			Grammar.quantityplnoun(2, "bread"));
		assertEquals("1 #sandwich", 
			Grammar.quantityplnoun(1, "#sandwich"));
		assertEquals("a #sandwich", 
			Grammar.quantityplnoun(1, "#sandwich", "a"));
		assertEquals("A #sandwich", 
			Grammar.quantityplnoun(1, "#sandwich", "A"));
		assertEquals("one #sandwich", 
			Grammar.quantityplnoun(1, "#sandwich", "one"));
		assertEquals("#sandwich", 
			Grammar.quantityplnoun(1, "#sandwich", ""));
		assertEquals("2 #sandwiches", 
			Grammar.quantityplnoun(2, "#sandwich"));
		assertEquals("a piece of #ham",
			Grammar.quantityplnoun(1, "#ham", "a"));
		assertEquals("2 pieces of #ham", 
			Grammar.quantityplnoun(2, "#ham", "a"));
		assertEquals("a piece of #cheese", 
			Grammar.quantityplnoun(1, "#cheese", "a"));
		assertEquals("2 pieces of #cheese", 
			Grammar.quantityplnoun(2, "#cheese", "a"));
		assertEquals("a loaf of #bread", 
			Grammar.quantityplnoun(1, "#bread", "a"));
		assertEquals("2 loaves of #bread", 
			Grammar.quantityplnoun(2, "#bread", "a"));
	}

	/**
	 * Tests for enumerateCollectionCollection.
	 */
	@Test
	public void testEnumerateCollectionCollection() throws Exception {
		assertEquals("", Grammar.enumerateCollection(null));
		final Collection<String> source = new LinkedList<String>();
		assertEquals("", Grammar.enumerateCollection(source));
		source.add("first");
		assertEquals("first", Grammar.enumerateCollection(source));
		source.add("second");
		assertEquals("first and second", Grammar.enumerateCollection(source));
		source.add("third");
		assertEquals("first, second, and third",
				Grammar.enumerateCollection(source));
	}

	private static void checkNumberString(final int n, final String string) {
		assertEquals(string, Grammar.numberString(n));
		assertEquals(Integer.valueOf(n), Grammar.number(string));
	}

	/**
	 * Tests for numberStrings.
	 */
	@Test
	public void testNumberStrings() {
		assertEquals(Integer.valueOf(0), Grammar.number("zero"));
		assertEquals(Integer.valueOf(1), Grammar.number("a"));
		assertEquals(Integer.valueOf(1), Grammar.number("an"));

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

	/**
	 * Tests for isPreposition.
	 */
	@Test
	public void testIsPreposition() {
		assertNotNull(ConversationParser.parse("on").getPreposition(0));
		assertNotNull(ConversationParser.parse("of").getPreposition(0));
		assertNotNull(ConversationParser.parse("under").getPreposition(0));
		assertNotNull(ConversationParser.parse("with").getPreposition(0));

		assertNull(ConversationParser.parse("if").getPreposition(0));
		assertNull(ConversationParser.parse("house").getPreposition(0));
		assertNull(ConversationParser.parse("dog").getPreposition(0));
		assertNull(ConversationParser.parse("player").getPreposition(0));
		assertNull(ConversationParser.parse("kymara").getPreposition(0));
	}

	/**
	 * Tests for extractNoun.
	 */
	@Test
	public void testExtractNoun() {
		assertEquals("bread", Grammar.extractNoun("loaf of bread"));
		assertEquals("trousers", Grammar.extractNoun("pair of trousers"));
		assertEquals("grain", Grammar.extractNoun("sacks of grain"));
		assertEquals("grain", Grammar.extractNoun("sheaves of grain"));
		assertEquals("water", Grammar.extractNoun("bottle of water"));
		assertEquals("arundula", Grammar.extractNoun("sprigs of arundula"));
		assertEquals("armor", Grammar.extractNoun("suit of armor"));
	}

	/**
	 * Tests for normalizeVerbs.
	 */
	@Test
	public void testNormalizeVerbs() {
		assertNull(Grammar.normalizeRegularVerb("open"));
		assertEquals("open", Grammar.normalizeRegularVerb("opened"));
		assertEquals("open", Grammar.normalizeRegularVerb("opens"));
		assertEquals("open", Grammar.normalizeRegularVerb("opening"));

		assertNull(Grammar.normalizeRegularVerb("close"));
		assertEquals("clos", Grammar.normalizeRegularVerb("closed"));
		assertEquals("clos", Grammar.normalizeRegularVerb("closes"));
		assertEquals("clos", Grammar.normalizeRegularVerb("closing"));

		assertNull(Grammar.normalizeRegularVerb("to fish"));
		assertEquals("fish", Grammar.normalizeRegularVerb("fished"));
		assertEquals("fish", Grammar.normalizeRegularVerb("fishes"));
		assertEquals("fish", Grammar.normalizeRegularVerb("fishing"));
	}

	/**
	 * Tests for gerund.
	 */
	@Test
	public void testGerund() {
		assertTrue(Grammar.isGerund("doing"));
		assertFalse(Grammar.isGerund("do"));
		assertTrue(Grammar.isGerund("working"));
		assertFalse(Grammar.isGerund("work"));
		assertTrue(Grammar.isGerund("swimming"));
		assertFalse(Grammar.isGerund("swim"));
		assertFalse(Grammar.isGerund("thing"));
		assertFalse(Grammar.isGerund("spring"));
		assertTrue(Grammar.isGerund("acting"));
	}

	/**
	 * Tests for normalizeAdjectives.
	 */
	@Test
	public void testNormalizeAdjectives() {
		assertTrue(Grammar.isDerivedAdjective("nomadic"));
		assertFalse(Grammar.isDerivedAdjective("nomad"));
		assertFalse(Grammar.isDerivedAdjective("thing"));
		assertFalse(Grammar.isDerivedAdjective("swim"));

		assertNull(Grammar.normalizeDerivedAdjective("word"));
		assertEquals("magic", Grammar.normalizeDerivedAdjective("magical"));
		assertEquals("nomad", Grammar.normalizeDerivedAdjective("nomadic"));
	}
	
	
	/**
	 * Tests for leatherLegs.
	 */
	@Test
	public void testLeatherLegs() throws Exception {
		assertEquals("leather legs", Grammar.singular("leather legs"));
	}
}
