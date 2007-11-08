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
	public void testarticle_noun(){
		assertEquals("the test", Grammar.article_noun("test",true));
		assertEquals("a test", Grammar.article_noun("test",false));
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

	// test building the plural form of the given singular word
    private static void testPluralisation(String message, String plural, String singular) {
        // test the plural() function
        Assert.assertEquals((message!=null? message: "building plural form"), plural, Grammar.plural(singular));

        /* Calling plural() with words already in their plural form does not yet work.
        Assert.assertEquals("no change expected", plural, Grammar.plural(plural)); */
    }

    // test building the plural form of the given singular word
    private static void testPluralisation(String plural, String singular) {
    	testPluralisation(null, plural, singular);
    }

    @Test
	public void testPluralItem() {

		Assert.assertEquals("hammers+3", Grammar.plural("hammer+3"));
//		 TODO: decide on name and plural of money
		testPluralisation("money", "money");
		testPluralisation("sandwiches", "sandwich");
		testPluralisation("knives", "knife");
		testPluralisation("daggers", "dagger");
		testPluralisation("short swords", "short sword");
		testPluralisation("swords", "sword");
		testPluralisation("scimitars", "scimitar");
		testPluralisation("wikipedia says katana/katanas ok", "katanas",
				"katana");
		testPluralisation("claymores", "claymore");
		testPluralisation("broadswords", "broadsword");
		testPluralisation("biting swords", "biting sword");
		testPluralisation("fire swords", "fire sword");
		testPluralisation("ice swords", "ice sword");
		testPluralisation("great swords", "great sword");
		testPluralisation("r hand swords", "r hand sword");
		testPluralisation("l hand swords", "l hand sword");
		testPluralisation("small axes", "small axe");
		testPluralisation("hand axes", "hand axe");
		testPluralisation("axes", "axe");
		testPluralisation("battle axes", "battle axe");
		testPluralisation("bardiches", "bardiche");
		testPluralisation("scythes", "scythe");
		testPluralisation("twoside axes", "twoside axe");
		testPluralisation("halberds", "halberd");
		testPluralisation("twoside axes +3", "twoside axe +3");
		testPluralisation("clubs", "club");
		testPluralisation("staffs", "staff");
		testPluralisation("maces", "mace");
		testPluralisation("flails", "flail");
		testPluralisation("maces +1", "mace +1");
		testPluralisation("maces +2", "mace +2");
		testPluralisation("skull staffs", "skull staff");
		testPluralisation("flails +2", "flail +2");
		testPluralisation("hammers", "hammer");
		testPluralisation("hammers +3", "hammer +3");
		testPluralisation("war hammers", "war hammer");
		testPluralisation("wooden bows", "wooden bow");
		testPluralisation("longbows", "longbow");
		testPluralisation("longbows +1", "longbow +1");
		testPluralisation("crossbows", "crossbow");
		testPluralisation("wooden arrows", "wooden arrow");
		testPluralisation("steel arrows", "steel arrow");
		testPluralisation("golden arrows", "golden arrow");
		testPluralisation("bucklers", "buckler");
		testPluralisation("wooden shields", "wooden shield");
		testPluralisation("studded shields", "studded shield");
		testPluralisation("plate shields", "plate shield");
		testPluralisation("lion shields", "lion shield");
		testPluralisation("unicorn shields", "unicorn shield");
		testPluralisation("lion shields +1", "lion shield +1");
		testPluralisation("skull shields", "skull shield");
		testPluralisation("crown shields", "crown shield");
		testPluralisation("golden shields", "golden shield");
		testPluralisation("dresses", "dress");
		testPluralisation("suits of leather armor",
				"suit of leather armor");
		testPluralisation("leather cuirasses",
				"leather cuirass");
		testPluralisation("suits of leather armor +1",
				"suit of leather armor +1");
		testPluralisation("leather cuirasses +1",
				"leather cuirass +1");
		testPluralisation("suits of studded armor",
				"suit of studded armor");
		testPluralisation("suits of chain armor",
				"suit of chain armor");
		testPluralisation("suits of chain armor +1",
				"suit of chain armor +1");
		testPluralisation("suits of scale armor",
				"suit of scale armor");
		testPluralisation("suits of scale armor +1",
				"suit of scale armor +1");
		testPluralisation("suits of scale armor +112",
				"suit of scale armor +112");
		testPluralisation("suits of chain armor +3",
				"suit of chain armor +3");
		testPluralisation("suits of scale armor +2",
				"suit of scale armor +2");
		testPluralisation("suits of plate armor",
				"suit of plate armor");
		testPluralisation("suits of golden armor",
				"suit of golden armor");
		testPluralisation("leather helmets", "leather helmet");
		testPluralisation("robins hats", "robins hat");
		testPluralisation("studded helmets", "studded helmet");
		testPluralisation("chain helmets", "chain helmet");
		testPluralisation("viking helmets", "viking helmet");
		testPluralisation("chain helmets +2",
				"chain helmet +2");
		testPluralisation("golden helmets", "golden helmet");
		testPluralisation("golden helmets +3",
				"golden helmet +3");
		testPluralisation("trophy helmets", "trophy helmet");
		testPluralisation("pairs of leather legs",
				"pair of leather legs");
		testPluralisation("pairs of studded legs",
				"pair of studded legs");
		testPluralisation("pairs of chain legs",
				"pair of chain legs");
		testPluralisation("pairs of golden legs",
				"pair of golden legs");
		testPluralisation("pairs of leather boots",
				"pair of leather boots");
		testPluralisation("pairs of studded boots",
				"pair of studded boots");
		testPluralisation("pairs of chain boots",
				"pair of chain boots");
		testPluralisation("pairs of steel boots",
				"pair of steel boots");
		testPluralisation("pairs of golden boots",
				"pair of golden boots");
		testPluralisation("cloaks", "cloak");
		testPluralisation("elf cloaks", "elf cloak");
		testPluralisation("dwarf cloaks", "dwarf cloak");
		testPluralisation("elf cloaks +2", "elf cloak +2");
		testPluralisation("green dragon cloaks",
				"green dragon cloak");
		testPluralisation("lich cloaks", "lich cloak");
		testPluralisation("blue dragon cloaks",
				"blue dragon cloak");
		testPluralisation("black dragon cloaks",
				"black dragon cloak");
		testPluralisation("golden cloaks", "golden cloak");
		testPluralisation("cherries", "cherry");
		testPluralisation("pieces of cheese",
				"piece of cheese");
		testPluralisation("carrots", "carrot");
		testPluralisation("salads", "salad");
		testPluralisation("apples", "apple");
		testPluralisation("loaves of bread", "loaf of bread");
		testPluralisation("chunks of meat", "chunk of meat");
		testPluralisation("pieces of ham", "ham");
		testPluralisation("sandwiches", "sandwich");
		testPluralisation("pies", "pie");
		testPluralisation("button mushrooms",
				"button mushroom");
		testPluralisation("porcini", "porcini");
		testPluralisation("toadstools", "toadstool");
		testPluralisation("bottles of beer", "beer");
		testPluralisation("flasks of wine", "flask of wine");
		testPluralisation("minor potions", "minor potion");
		testPluralisation("bottles of antidote", "antidote");
		testPluralisation("potions", "potion");
		testPluralisation("greater potions", "greater potion");
		testPluralisation("bottles of poison", "poison");
		testPluralisation("bottles of greater poison",
				"greater poison");
		testPluralisation("bottles of deadly poison",
				"deadly poison");
		testPluralisation("flasks", "flask");
		testPluralisation("bottles", "bottle");
		testPluralisation("big bottles", "big bottle");

		testPluralisation("black books", "black book");
		testPluralisation("logs of wood", "log of wood");
		testPluralisation("sheaves of grain",
				"sheaf of grain");
		testPluralisation("bags of flour", "bag of flour");
		testPluralisation("nuggets of iron ore",
				"nugget of iron ore");
		testPluralisation("bars of iron", "bar of iron");
		testPluralisation("golden gm tokens",
				"golden gm token");
		testPluralisation("silvery gm tokens",
				"silvery gm token");
		testPluralisation("bronze gm tokens",
				"bronze gm token");
		testPluralisation("tokens", "token");
		testPluralisation("notes", "note");
		testPluralisation("coupons", "coupon");
		testPluralisation("dice", "dice");
		testPluralisation("teddies", "teddy");
		testPluralisation("maps", "map");
		testPluralisation("summon scrolls", "summon scroll");
		testPluralisation("empty scrolls", "empty scroll");
		testPluralisation("home scrolls", "home scroll");
		testPluralisation("marked scrolls", "marked scroll");
		testPluralisation("presents", "present");
		testPluralisation("rods of the gm", "rod of the gm");
		testPluralisation("rat keys", "rat key");
		testPluralisation("sprigs of arandula", "arandula");

		testPluralisation("mice", "mouse");

		testPluralisation("houses", "house");
		testPluralisation("houses of sun", "house of sun");
		testPluralisation("geese", "goose");
		testPluralisation("cabooses", "caboose");
		testPluralisation("teeth", "tooth");
		testPluralisation("feet", "foot");
		testPluralisation("children", "child");

		testPluralisation("tomatoes", "tomato");
		testPluralisation("algae", "alga");
		testPluralisation("larvae", "larva");
		testPluralisation("hyphae", "hypha");
		testPluralisation("bureaux", "bureau");
		testPluralisation("a", "ium");
		testPluralisation("dei", "deus");
		testPluralisation("vortices", "vortex");


		testPluralisation("xxxxses", "xxxxsis");

		testPluralisation("matrices", "matrix");

		testPluralisation("wumpuses", "wumpus");
		testPluralisation("lotuses", "lotus");
		testPluralisation("mumakil", "mumak");

		testPluralisation("efreet", "efreeti");
		testPluralisation("djinn", "djinni");
		testPluralisation("ys", "y");
		testPluralisation("bies", "by");
		testPluralisation("fs", "f");


	}

	/**
	 * Tests pluralisation of creatures
	 */
	@Test
	public void testPluralCreatures() {
		testPluralisation("deer", "deer");
		testPluralisation("crabs", "crab");
		testPluralisation("rats", "rat");
		testPluralisation("bats", "bat");
		testPluralisation("caverats", "caverat");
		testPluralisation("penguins", "penguin");
		testPluralisation("monkeys", "monkey");
		testPluralisation("boars", "boar");
		testPluralisation("wolves", "wolf");
		testPluralisation("gnomes", "gnome");
		testPluralisation("mage gnomes", "mage gnome");
		testPluralisation("cobras", "cobra");
		testPluralisation("bears", "bear");
		testPluralisation("lions", "lion");
		testPluralisation("goblins", "goblin");
		testPluralisation("kobolds", "kobold");
		testPluralisation("elephants", "elephant");
		testPluralisation("venomrats", "venomrat");
		testPluralisation("tigers", "tiger");
		testPluralisation("skeletons", "skeleton");
		testPluralisation("gargoyles", "gargoyle");
		testPluralisation("young beholders", "young beholder");
		testPluralisation("zombie rats", "zombie rat");
		testPluralisation("veteran goblins", "veteran goblin");
		testPluralisation("soldier kobolds", "soldier kobold");
		testPluralisation(
				"plural of slime is slime but this is a creature....",
				"green slimes", "green slime");
		testPluralisation("archer kobolds", "archer kobold");
		testPluralisation("black bears", "black bear");
		testPluralisation("elder gargoyles", "elder gargoyle");
		testPluralisation("razorrats", "razorrat");
		testPluralisation("cyclopses", "cyclops");
		testPluralisation("beholders", "beholder");
		testPluralisation("soldier goblins", "soldier goblin");
		testPluralisation("veteran kobolds", "veteran kobold");
		testPluralisation("trolls", "troll");
		testPluralisation("orcs", "orc");
		testPluralisation("dark gargoyles", "dark gargoyle");
		testPluralisation("ogres", "ogre");
		testPluralisation("mummies", "mummy");
		testPluralisation("leader kobolds", "leader kobold");
		testPluralisation("orc warriors", "orc warrior");
		testPluralisation("orc hunters", "orc hunter");
		testPluralisation("ghosts", "ghost");
		testPluralisation("giantrats", "giantrat");
		testPluralisation("elves", "elf");
		testPluralisation("dwarves", "dwarf");
		testPluralisation("ratmen", "ratman");
		testPluralisation("ratwomen", "ratwoman");
		testPluralisation("elder beholders", "elder beholder");
		testPluralisation(
				"plural of slime is slime but this is a creature....",
				"brown slimes", "brown slime");
		testPluralisation("venom gargoyles", "venom gargoyle");
		testPluralisation("elder ogres", "elder ogre");
		testPluralisation("dwarf guardians", "dwarf guardian");
		testPluralisation("chief is an exception to the v rule",
				"orc chiefs", "orc chief");
		testPluralisation("militia elves", "militia elf");
		testPluralisation("archer elves", "archer elf");
		testPluralisation("zombies", "zombie");
		testPluralisation("elder dwarves", "elder dwarf");
		testPluralisation("soldier elves", "soldier elf");
		testPluralisation("warrior skeletons",
				"warrior skeleton");
		testPluralisation(
				"plural of slime is slime but this is a creature....",
				"black slimes", "black slime");
		testPluralisation("wooden golems", "wooden golem");
		testPluralisation("royal mummies", "royal mummy");
		testPluralisation("archrats", "archrat");
		testPluralisation("hero dwarves", "hero dwarf");
		testPluralisation("mage elves", "mage elf");
		testPluralisation("deaths", "death");
		testPluralisation("commander elves", "commander elf");
		testPluralisation("stone golems", "stone golem");
		testPluralisation("archmage elves", "archmage elf");
		testPluralisation("leader dwarves", "leader dwarf");
		testPluralisation("demon skeletons", "demon skeleton");
		testPluralisation("elf sacerdotists",
				"elf sacerdotist");
		testPluralisation("earth elementals",
				"earth elemental");
		testPluralisation("fire elementals", "fire elemental");
		testPluralisation("water elementals",
				"water elemental");
		testPluralisation("green dragons", "green dragon");
		testPluralisation("death knights", "death knight");
		testPluralisation("liches", "lich");
		testPluralisation("blue dragons", "blue dragon");
		testPluralisation("black dragons", "black dragon");

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
	public void testenumerateCollectionCollection() throws Exception {
		assertEquals("", Grammar.enumerateCollection(null));
		Collection<String> source = new LinkedList<String>();
		assertEquals("", Grammar.enumerateCollection(source));
		source.add("first");
		assertEquals("first", Grammar.enumerateCollection(source));
		source.add("second");
		assertEquals("first and second", Grammar.enumerateCollection(source));
		source.add("third");
		assertEquals("first, second, and third", Grammar.enumerateCollection(source));

	}

}
