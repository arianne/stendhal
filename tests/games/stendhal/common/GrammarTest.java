package games.stendhal.common;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
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
		assertEquals("a youngster", Grammar.a_noun("youngster"));
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

	@Test
	public void testPluralItem() {
		Assert.assertEquals("sandwiches", Grammar.plural("sandwich"));
		Assert.assertEquals("knives", Grammar.plural("knife"));
		Assert.assertEquals("daggers", Grammar.plural("dagger"));
		Assert.assertEquals("short swords", Grammar.plural("short sword"));
		Assert.assertEquals("swords", Grammar.plural("sword"));
		Assert.assertEquals("scimitars", Grammar.plural("scimitar"));
		Assert.assertEquals("wikipedia says katana/katanas ok", "katanas",
				Grammar.plural("katana"));
		Assert.assertEquals("claymores", Grammar.plural("claymore"));
		Assert.assertEquals("broadswords", Grammar.plural("broadsword"));
		Assert.assertEquals("biting swords", Grammar.plural("biting sword"));
		Assert.assertEquals("fire swords", Grammar.plural("fire sword"));
		Assert.assertEquals("ice swords", Grammar.plural("ice sword"));
		Assert.assertEquals("great swords", Grammar.plural("great sword"));
		Assert.assertEquals("r hand swords", Grammar.plural("r hand sword"));
		Assert.assertEquals("l hand swords", Grammar.plural("l hand sword"));
		Assert.assertEquals("small axes", Grammar.plural("small axe"));
		Assert.assertEquals("hand axes", Grammar.plural("hand axe"));
		Assert.assertEquals("axes", Grammar.plural("axe"));
		Assert.assertEquals("battle axes", Grammar.plural("battle axe"));
		Assert.assertEquals("bardiches", Grammar.plural("bardiche"));
		Assert.assertEquals("scythes", Grammar.plural("scythe"));
		Assert.assertEquals("twoside axes", Grammar.plural("twoside axe"));
		Assert.assertEquals("halberds", Grammar.plural("halberd"));
		Assert.assertEquals("twoside axes +3", Grammar.plural("twoside axe +3"));
		Assert.assertEquals("clubs", Grammar.plural("club"));
		Assert.assertEquals("staffs", Grammar.plural("staff"));
		Assert.assertEquals("maces", Grammar.plural("mace"));
		Assert.assertEquals("flails", Grammar.plural("flail"));
		Assert.assertEquals("maces +1", Grammar.plural("mace +1"));
		Assert.assertEquals("maces +2", Grammar.plural("mace +2"));
		Assert.assertEquals("skull staffs", Grammar.plural("skull staff"));
		Assert.assertEquals("flails +2", Grammar.plural("flail +2"));
		Assert.assertEquals("hammers", Grammar.plural("hammer"));
		Assert.assertEquals("hammers +3", Grammar.plural("hammer +3"));
		Assert.assertEquals("war hammers", Grammar.plural("war hammer"));
		Assert.assertEquals("wooden bows", Grammar.plural("wooden bow"));
		Assert.assertEquals("longbows", Grammar.plural("longbow"));
		Assert.assertEquals("longbows +1", Grammar.plural("longbow +1"));
		Assert.assertEquals("crossbows", Grammar.plural("crossbow"));
		Assert.assertEquals("wooden arrows", Grammar.plural("wooden arrow"));
		Assert.assertEquals("steel arrows", Grammar.plural("steel arrow"));
		Assert.assertEquals("golden arrows", Grammar.plural("golden arrow"));
		Assert.assertEquals("bucklers", Grammar.plural("buckler"));
		Assert.assertEquals("wooden shields", Grammar.plural("wooden shield"));
		Assert.assertEquals("studded shields", Grammar.plural("studded shield"));
		Assert.assertEquals("plate shields", Grammar.plural("plate shield"));
		Assert.assertEquals("lion shields", Grammar.plural("lion shield"));
		Assert.assertEquals("unicorn shields", Grammar.plural("unicorn shield"));
		Assert.assertEquals("lion shields +1", Grammar.plural("lion shield +1"));
		Assert.assertEquals("skull shields", Grammar.plural("skull shield"));
		Assert.assertEquals("crown shields", Grammar.plural("crown shield"));
		Assert.assertEquals("golden shields", Grammar.plural("golden shield"));
		Assert.assertEquals("dresses", Grammar.plural("dress"));
		Assert.assertEquals("suits of leather armor",
				Grammar.plural("suit of leather armor"));
		Assert.assertEquals("leather cuirasses",
				Grammar.plural("leather cuirass"));
		Assert.assertEquals("suits of leather armor +1",
				Grammar.plural("suit of leather armor +1"));
		Assert.assertEquals("leather cuirasses +1",
				Grammar.plural("leather cuirass +1"));
		Assert.assertEquals("suits of studded armor",
				Grammar.plural("suit of studded armor"));
		Assert.assertEquals("suits of chain armor",
				Grammar.plural("suit of chain armor"));
		Assert.assertEquals("suits of chain armor +1",
				Grammar.plural("suit of chain armor +1"));
		Assert.assertEquals("suits of scale armor",
				Grammar.plural("suit of scale armor"));
		Assert.assertEquals("suits of scale armor +1",
				Grammar.plural("suit of scale armor +1"));
		Assert.assertEquals("suits of scale armor +112",
				Grammar.plural("suit of scale armor +112"));
		Assert.assertEquals("suits of chain armor +3",
				Grammar.plural("suit of chain armor +3"));
		Assert.assertEquals("suits of scale armor +2",
				Grammar.plural("suit of scale armor +2"));
		Assert.assertEquals("suits of plate armor",
				Grammar.plural("suit of plate armor"));
		Assert.assertEquals("suits of golden armor",
				Grammar.plural("suit of golden armor"));
		Assert.assertEquals("leather helmets", Grammar.plural("leather helmet"));
		Assert.assertEquals("robins hats", Grammar.plural("robins hat"));
		Assert.assertEquals("studded helmets", Grammar.plural("studded helmet"));
		Assert.assertEquals("chain helmets", Grammar.plural("chain helmet"));
		Assert.assertEquals("viking helmets", Grammar.plural("viking helmet"));
		Assert.assertEquals("chain helmets +2",
				Grammar.plural("chain helmet +2"));
		Assert.assertEquals("golden helmets", Grammar.plural("golden helmet"));
		Assert.assertEquals("golden helmets +3",
				Grammar.plural("golden helmet +3"));
		Assert.assertEquals("trophy helmets", Grammar.plural("trophy helmet"));
		Assert.assertEquals("pairs of leather legs",
				Grammar.plural("pair of leather legs"));
		Assert.assertEquals("pairs of studded legs",
				Grammar.plural("pair of studded legs"));
		Assert.assertEquals("pairs of chain legs",
				Grammar.plural("pair of chain legs"));
		Assert.assertEquals("pairs of golden legs",
				Grammar.plural("pair of golden legs"));
		Assert.assertEquals("pairs of leather boots",
				Grammar.plural("pair of leather boots"));
		Assert.assertEquals("pairs of studded boots",
				Grammar.plural("pair of studded boots"));
		Assert.assertEquals("pairs of chain boots",
				Grammar.plural("pair of chain boots"));
		Assert.assertEquals("pairs of steel boots",
				Grammar.plural("pair of steel boots"));
		Assert.assertEquals("pairs of golden boots",
				Grammar.plural("pair of golden boots"));
		Assert.assertEquals("cloaks", Grammar.plural("cloak"));
		Assert.assertEquals("elf cloaks", Grammar.plural("elf cloak"));
		Assert.assertEquals("dwarf cloaks", Grammar.plural("dwarf cloak"));
		Assert.assertEquals("elf cloaks +2", Grammar.plural("elf cloak +2"));
		Assert.assertEquals("green dragon cloaks",
				Grammar.plural("green dragon cloak"));
		Assert.assertEquals("lich cloaks", Grammar.plural("lich cloak"));
		Assert.assertEquals("blue dragon cloaks",
				Grammar.plural("blue dragon cloak"));
		Assert.assertEquals("black dragon cloaks",
				Grammar.plural("black dragon cloak"));
		Assert.assertEquals("golden cloaks", Grammar.plural("golden cloak"));
		Assert.assertEquals("cherries", Grammar.plural("cherry"));
		Assert.assertEquals("pieces of cheese",
				Grammar.plural("piece of cheese"));
		Assert.assertEquals("carrots", Grammar.plural("carrot"));
		Assert.assertEquals("salads", Grammar.plural("salad"));
		Assert.assertEquals("apples", Grammar.plural("apple"));
		Assert.assertEquals("loaves of bread", Grammar.plural("loaf of bread"));
		Assert.assertEquals("chunks of meat", Grammar.plural("chunk of meat"));
		Assert.assertEquals("pieces of ham", Grammar.plural("ham"));
		Assert.assertEquals("sandwiches", Grammar.plural("sandwich"));
		Assert.assertEquals("pies", Grammar.plural("pie"));
		Assert.assertEquals("button mushrooms",
				Grammar.plural("button mushroom"));
		Assert.assertEquals("porcini", Grammar.plural("porcini"));
		Assert.assertEquals("toadstools", Grammar.plural("toadstool"));
		Assert.assertEquals("bottles of beer", Grammar.plural("beer"));
		Assert.assertEquals("flasks of wine", Grammar.plural("flask of wine"));
		Assert.assertEquals("minor potions", Grammar.plural("minor potion"));
		Assert.assertEquals("bottles of antidote", Grammar.plural("antidote"));
		Assert.assertEquals("potions", Grammar.plural("potion"));
		Assert.assertEquals("greater potions", Grammar.plural("greater potion"));
		Assert.assertEquals("bottles of poison", Grammar.plural("poison"));
		Assert.assertEquals("bottles of greater poison",
				Grammar.plural("greater poison"));
		Assert.assertEquals("bottles of deadly poison",
				Grammar.plural("deadly poison"));
		Assert.assertEquals("flasks", Grammar.plural("flask"));
		Assert.assertEquals("bottles", Grammar.plural("bottle"));
		Assert.assertEquals("big bottles", Grammar.plural("big bottle"));

		Assert.assertEquals("black books", Grammar.plural("black book"));
		Assert.assertEquals("logs of wood", Grammar.plural("log of wood"));
		Assert.assertEquals("sheaves of grain",
				Grammar.plural("sheaf of grain"));
		Assert.assertEquals("bags of flour", Grammar.plural("bag of flour"));
		Assert.assertEquals("nuggets of iron ore",
				Grammar.plural("nugget of iron ore"));
		Assert.assertEquals("bars of iron", Grammar.plural("bar of iron"));
		Assert.assertEquals("golden gm tokens",
				Grammar.plural("golden gm token"));
		Assert.assertEquals("silvery gm tokens",
				Grammar.plural("silvery gm token"));
		Assert.assertEquals("bronze gm tokens",
				Grammar.plural("bronze gm token"));
		Assert.assertEquals("tokens", Grammar.plural("token"));
		Assert.assertEquals("notes", Grammar.plural("note"));
		Assert.assertEquals("coupons", Grammar.plural("coupon"));
		Assert.assertEquals("dice", Grammar.plural("dice"));
		Assert.assertEquals("teddies", Grammar.plural("teddy"));
		Assert.assertEquals("maps", Grammar.plural("map"));
		Assert.assertEquals("summon scrolls", Grammar.plural("summon scroll"));
		Assert.assertEquals("empty scrolls", Grammar.plural("empty scroll"));
		Assert.assertEquals("home scrolls", Grammar.plural("home scroll"));
		Assert.assertEquals("marked scrolls", Grammar.plural("marked scroll"));
		Assert.assertEquals("presents", Grammar.plural("present"));
		Assert.assertEquals("rods of the gm", Grammar.plural("rod of the gm"));
		Assert.assertEquals("rat keys", Grammar.plural("rat key"));
		Assert.assertEquals("sprigs of arandula", Grammar.plural("arandula"));

		// TODO: decide on name and plural of money
		Assert.assertEquals("money", Grammar.plural("money"));

	}

	/**
	 * Tests pluralisation of creatures
	 */
	@Test
	public void testPluralCreatures() {
		Assert.assertEquals("deer", Grammar.plural("deer"));
		Assert.assertEquals("crabs", Grammar.plural("crab"));
		Assert.assertEquals("rats", Grammar.plural("rat"));
		Assert.assertEquals("bats", Grammar.plural("bat"));
		Assert.assertEquals("caverats", Grammar.plural("caverat"));
		Assert.assertEquals("penguins", Grammar.plural("penguin"));
		Assert.assertEquals("monkeys", Grammar.plural("monkey"));
		Assert.assertEquals("boars", Grammar.plural("boar"));
		Assert.assertEquals("wolves", Grammar.plural("wolf"));
		Assert.assertEquals("gnomes", Grammar.plural("gnome"));
		Assert.assertEquals("mage gnomes", Grammar.plural("mage gnome"));
		Assert.assertEquals("cobras", Grammar.plural("cobra"));
		Assert.assertEquals("bears", Grammar.plural("bear"));
		Assert.assertEquals("lions", Grammar.plural("lion"));
		Assert.assertEquals("goblins", Grammar.plural("goblin"));
		Assert.assertEquals("kobolds", Grammar.plural("kobold"));
		Assert.assertEquals("elephants", Grammar.plural("elephant"));
		Assert.assertEquals("venomrats", Grammar.plural("venomrat"));
		Assert.assertEquals("tigers", Grammar.plural("tiger"));
		Assert.assertEquals("skeletons", Grammar.plural("skeleton"));
		Assert.assertEquals("gargoyles", Grammar.plural("gargoyle"));
		Assert.assertEquals("young beholders", Grammar.plural("young beholder"));
		Assert.assertEquals("zombie rats", Grammar.plural("zombie rat"));
		Assert.assertEquals("veteran goblins", Grammar.plural("veteran goblin"));
		Assert.assertEquals("soldier kobolds", Grammar.plural("soldier kobold"));
		Assert.assertEquals(
				"plural of slime is slime but this is a creature....",
				"green slimes", Grammar.plural("green slime"));
		Assert.assertEquals("archer kobolds", Grammar.plural("archer kobold"));
		Assert.assertEquals("black bears", Grammar.plural("black bear"));
		Assert.assertEquals("elder gargoyles", Grammar.plural("elder gargoyle"));
		Assert.assertEquals("razorrats", Grammar.plural("razorrat"));
		Assert.assertEquals("cyclopses", Grammar.plural("cyclops"));
		Assert.assertEquals("beholders", Grammar.plural("beholder"));
		Assert.assertEquals("soldier goblins", Grammar.plural("soldier goblin"));
		Assert.assertEquals("veteran kobolds", Grammar.plural("veteran kobold"));
		Assert.assertEquals("trolls", Grammar.plural("troll"));
		Assert.assertEquals("orcs", Grammar.plural("orc"));
		Assert.assertEquals("dark gargoyles", Grammar.plural("dark gargoyle"));
		Assert.assertEquals("ogres", Grammar.plural("ogre"));
		Assert.assertEquals("mummies", Grammar.plural("mummy"));
		Assert.assertEquals("leader kobolds", Grammar.plural("leader kobold"));
		Assert.assertEquals("orc warriors", Grammar.plural("orc warrior"));
		Assert.assertEquals("orc hunters", Grammar.plural("orc hunter"));
		Assert.assertEquals("ghosts", Grammar.plural("ghost"));
		Assert.assertEquals("giantrats", Grammar.plural("giantrat"));
		Assert.assertEquals("elves", Grammar.plural("elf"));
		Assert.assertEquals("dwarves", Grammar.plural("dwarf"));
		Assert.assertEquals("ratmen", Grammar.plural("ratman"));
		Assert.assertEquals("ratwomen", Grammar.plural("ratwoman"));
		Assert.assertEquals("elder beholders", Grammar.plural("elder beholder"));
		Assert.assertEquals(
				"plural of slime is slime but this is a creature....",
				"brown slimes", Grammar.plural("brown slime"));
		Assert.assertEquals("venom gargoyles", Grammar.plural("venom gargoyle"));
		Assert.assertEquals("elder ogres", Grammar.plural("elder ogre"));
		Assert.assertEquals("dwarf guardians", Grammar.plural("dwarf guardian"));
		Assert.assertEquals("chief is an exception to the v rule",
				"orc chiefs", Grammar.plural("orc chief"));
		Assert.assertEquals("militia elves", Grammar.plural("militia elf"));
		Assert.assertEquals("archer elves", Grammar.plural("archer elf"));
		Assert.assertEquals("zombies", Grammar.plural("zombie"));
		Assert.assertEquals("elder dwarves", Grammar.plural("elder dwarf"));
		Assert.assertEquals("soldier elves", Grammar.plural("soldier elf"));
		Assert.assertEquals("warrior skeletons",
				Grammar.plural("warrior skeleton"));
		Assert.assertEquals(
				"plural of slime is slime but this is a creature....",
				"black slimes", Grammar.plural("black slime"));
		Assert.assertEquals("wooden golems", Grammar.plural("wooden golem"));
		Assert.assertEquals("royal mummies", Grammar.plural("royal mummy"));
		Assert.assertEquals("archrats", Grammar.plural("archrat"));
		Assert.assertEquals("hero dwarves", Grammar.plural("hero dwarf"));
		Assert.assertEquals("mage elves", Grammar.plural("mage elf"));
		Assert.assertEquals("deaths", Grammar.plural("death"));
		Assert.assertEquals("commander elves", Grammar.plural("commander elf"));
		Assert.assertEquals("stone golems", Grammar.plural("stone golem"));
		Assert.assertEquals("archmage elves", Grammar.plural("archmage elf"));
		Assert.assertEquals("leader dwarves", Grammar.plural("leader dwarf"));
		Assert.assertEquals("demon skeletons", Grammar.plural("demon skeleton"));
		Assert.assertEquals("elf sacerdotists",
				Grammar.plural("elf sacerdotist"));
		Assert.assertEquals("earth elementals",
				Grammar.plural("earth elemental"));
		Assert.assertEquals("fire elementals", Grammar.plural("fire elemental"));
		Assert.assertEquals("water elementals",
				Grammar.plural("water elemental"));
		Assert.assertEquals("green dragons", Grammar.plural("green dragon"));
		Assert.assertEquals("death knights", Grammar.plural("death knight"));
		Assert.assertEquals("liches", Grammar.plural("lich"));
		Assert.assertEquals("blue dragons", Grammar.plural("blue dragon"));
		Assert.assertEquals("black dragons", Grammar.plural("black dragon"));

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

}
