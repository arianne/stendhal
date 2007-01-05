package games.stendhal.test.common;

import games.stendhal.common.Grammar;

/**
 * Tests the grammar string modifications
 *
 * @author kymara / hendrik
 */
public class TestGrammar extends TestCase {

	/**
	 * Tests pluralisation of items
	 */
	public void testPluralItem() {
		assertEquals("knives", Grammar.plural("knife"));
		assertEquals("daggers", Grammar.plural("dagger"));
		assertEquals("short swords", Grammar.plural("short sword"));
		assertEquals("swords", Grammar.plural("sword"));
		assertEquals("scimitars", Grammar.plural("scimitar"));
		assertEquals("katanas", Grammar.plural("katana")); // wikipedia says katana/katanas ok
		assertEquals("claymores", Grammar.plural("claymore"));
		assertEquals("broadswords", Grammar.plural("broadsword"));
		assertEquals("biting swords", Grammar.plural("biting sword"));
		assertEquals("fire swords", Grammar.plural("fire sword"));
		assertEquals("ice swords", Grammar.plural("ice sword"));
		assertEquals("great swords", Grammar.plural("great sword"));
		assertEquals("r hand swords", Grammar.plural("r hand sword"));
		assertEquals("l hand swords", Grammar.plural("l hand sword"));
		assertEquals("small axes", Grammar.plural("small axe"));
		assertEquals("hand axes", Grammar.plural("hand axe"));
		assertEquals("axes", Grammar.plural("axe"));
		assertEquals("battle axes", Grammar.plural("battle axe"));
		assertEquals("bardiches", Grammar.plural("bardiche"));
		assertEquals("scythes", Grammar.plural("scythe"));
		assertEquals("twoside axes", Grammar.plural("twoside axe"));
		assertEquals("halberds", Grammar.plural("halberd"));
		assertEquals("twoside axes +3", Grammar.plural("twoside axe +3")); // i will make a set of names for these +s
		assertEquals("clubs", Grammar.plural("club"));
		assertEquals("staffs", Grammar.plural("staff"));
		assertEquals("maces", Grammar.plural("mace"));
		assertEquals("flails", Grammar.plural("flail"));
		assertEquals("maces +1", Grammar.plural("mace +1"));
		assertEquals("maces +2", Grammar.plural("mace +2"));
		assertEquals("skull staffs", Grammar.plural("skull staff"));
		assertEquals("flails +2", Grammar.plural("flail +2"));
		assertEquals("hammers", Grammar.plural("hammer"));
		assertEquals("hammers +3", Grammar.plural("hammer +3"));
		assertEquals("war hammers", Grammar.plural("war hammer"));
		assertEquals("wooden bows", Grammar.plural("wooden bow"));
		assertEquals("longbows", Grammar.plural("longbow"));
		assertEquals("longbows +1", Grammar.plural("longbow +1"));
		assertEquals("crossbows", Grammar.plural("crossbow"));
		assertEquals("wooden arrows", Grammar.plural("wooden arrow"));
		assertEquals("steel arrows", Grammar.plural("steel arrow"));
		assertEquals("golden arrows", Grammar.plural("golden arrow"));
		assertEquals("bucklers", Grammar.plural("buckler"));
		assertEquals("wooden shields", Grammar.plural("wooden shield"));
		assertEquals("studded shields", Grammar.plural("studded shield"));
		assertEquals("plate shields", Grammar.plural("plate shield"));
		assertEquals("lion shields", Grammar.plural("lion shield"));
		assertEquals("unicorn shields", Grammar.plural("unicorn shield"));
		assertEquals("lion shields +1", Grammar.plural("lion shield +1"));
		assertEquals("skull shields", Grammar.plural("skull shield"));
		assertEquals("crown shields", Grammar.plural("crown shield"));
		assertEquals("golden shields", Grammar.plural("golden shield"));
		assertEquals("dresses", Grammar.plural("dress"));
		assertEquals("suits of leather armor", Grammar.plural("suit of leather armor")); //
		assertEquals("leather cuirasses", Grammar.plural("leather cuirass"));
		assertEquals("suits of leather armor +1", Grammar.plural("suit of leather armor +1")); //
		assertEquals("leather cuirasses +1", Grammar.plural("leather cuirass +1"));
		assertEquals("suits of studded armor", Grammar.plural("suit of studded armor")); //
		assertEquals("suits of chain armor", Grammar.plural("suit of chain armor")); //
		assertEquals("suits of chain armor +1", Grammar.plural("suit of chain armor +1")); //
		assertEquals("suits of scale armor", Grammar.plural("suit of scale armor")); //
		assertEquals("suits of scale armor +1", Grammar.plural("suit of scale armor +1")); //
		assertEquals("suits of chain armor +3", Grammar.plural("suit of chain armor +3")); //
		assertEquals("suits of scale armor +2", Grammar.plural("suit of scale armor +2")); //
		assertEquals("suits of plate armor", Grammar.plural("suit of plate armor")); //
		assertEquals("suits of golden armor", Grammar.plural("suit of golden armor")); //
		assertEquals("leather helmets", Grammar.plural("leather helmet"));
		assertEquals("robins hats", Grammar.plural("robins hat"));
		assertEquals("studded helmets", Grammar.plural("studded helmet"));
		assertEquals("chain helmets", Grammar.plural("chain helmet"));
		assertEquals("viking helmets", Grammar.plural("viking helmet"));
		assertEquals("chain helmets +2", Grammar.plural("chain helmet +2"));
		assertEquals("golden helmets", Grammar.plural("golden helmet"));
		assertEquals("golden helmets +3", Grammar.plural("golden helmet +3"));
		assertEquals("trophy helmets", Grammar.plural("trophy helmet"));
		assertEquals("pairs of leather legs", Grammar.plural("pair of leather legs")); //
		assertEquals("pairs of studded legs", Grammar.plural("pair of studded legs")); //
		assertEquals("pairs of chain legs", Grammar.plural("pair of chain legs")); //
		assertEquals("pairs of golden legs", Grammar.plural("pair of golden legs")); //
		assertEquals("pairs of leather boots", Grammar.plural("pair of leather boots")); //
		assertEquals("pairs of studded boots", Grammar.plural("pair of studded boots")); //
		assertEquals("pairs of chain boots", Grammar.plural("pair of chain boots")); //
		assertEquals("pairs of steel boots", Grammar.plural("pair of steel boots")); //
		assertEquals("pairs of golden boots", Grammar.plural("pair of golden boots")); //
		assertEquals("cloaks", Grammar.plural("cloak"));
		assertEquals("elf cloaks", Grammar.plural("elf cloak"));
		assertEquals("dwarf cloaks", Grammar.plural("dwarf cloak"));
		assertEquals("elf cloaks +2", Grammar.plural("elf cloak +2"));
		assertEquals("green dragon cloaks", Grammar.plural("green dragon cloak"));
		assertEquals("lich cloaks", Grammar.plural("lich cloak"));
		assertEquals("blue dragon cloaks", Grammar.plural("blue dragon cloak"));
		assertEquals("black dragon cloaks", Grammar.plural("black dragon cloak"));
		assertEquals("golden cloaks", Grammar.plural("golden cloak"));
		assertEquals("cherries", Grammar.plural("cherry"));
		assertEquals("pieces of cheese", Grammar.plural("piece of cheese")); //
		assertEquals("carrots", Grammar.plural("carrot"));
		assertEquals("salads", Grammar.plural("salad"));
		assertEquals("apples", Grammar.plural("apple"));
		assertEquals("loaves of bread", Grammar.plural("loaf of bread")); //
		assertEquals("chunks of meat", Grammar.plural("chunk of meat")); //
// 		assertEquals("hams", Grammar.plural("ham"));
		assertEquals("sandwiches", Grammar.plural("sandwich"));
		assertEquals("pies", Grammar.plural("pie"));
		assertEquals("button mushrooms", Grammar.plural("button mushroom"));
		assertEquals("porcini", Grammar.plural("porcini"));
		assertEquals("toadstools", Grammar.plural("toadstool"));
//		assertEquals("beers", Grammar.plural("beer"));
		assertEquals("flasks of wine", Grammar.plural("flask of wine")); //
		assertEquals("minor potions", Grammar.plural("minor potion"));
//		assertEquals("antidotes", Grammar.plural("antidote"));
//		assertEquals("potions", Grammar.plural("potion"));
		assertEquals("greater potions", Grammar.plural("greater potion"));
//		assertEquals("poisons", Grammar.plural("poison"));
//		assertEquals("greater poisons", Grammar.plural("greater poison"));
//		assertEquals("deadly poisons", Grammar.plural("deadly poison"));
		assertEquals("flasks", Grammar.plural("flask"));
		assertEquals("bottles", Grammar.plural("bottle"));
		assertEquals("big bottles", Grammar.plural("big bottle"));
		assertEquals("money", Grammar.plural("money")); // See my bug report! don't like this!
		assertEquals("black books", Grammar.plural("black book")); //
// TODO:		assertEquals("arandula", Grammar.plural("arandula"));
		assertEquals("logs of wood", Grammar.plural("log of wood")); //
		assertEquals("sheaves of grain", Grammar.plural("sheaf of grain"));
		assertEquals("bags of flour", Grammar.plural("bag of flour")); //
		assertEquals("nuggets of iron ore", Grammar.plural("nugget of iron ore")); //
		assertEquals("bars of iron", Grammar.plural("bar of iron")); //
		assertEquals("golden gm tokens", Grammar.plural("golden gm token")); // Token spelt wrong 
		assertEquals("silvery gm tokens", Grammar.plural("silvery gm token")); // Token spelt wrong 
		assertEquals("bronze gm tokens", Grammar.plural("bronze gm token")); // Token spelt wrong 
		assertEquals("tokens", Grammar.plural("token"));
		assertEquals("notes", Grammar.plural("note"));
		assertEquals("coupons", Grammar.plural("coupon"));
		assertEquals("dice", Grammar.plural("dice"));
		assertEquals("teddies", Grammar.plural("teddy"));
		assertEquals("maps", Grammar.plural("map"));
		assertEquals("summon scrolls", Grammar.plural("summon scroll"));
		assertEquals("empty scrolls", Grammar.plural("empty scroll"));
		assertEquals("home scrolls", Grammar.plural("home scroll"));
		assertEquals("marked scrolls", Grammar.plural("marked scroll"));
		assertEquals("presents", Grammar.plural("present"));
		assertEquals("rods of the gm", Grammar.plural("rod of the gm"));
		assertEquals("rat keys", Grammar.plural("rat key"));
	}

	/**
	 * Tests pluralisation of creatures
	 */
	public void testPluralCreatures() {
		assertEquals("deer", Grammar.plural("deer"));
		assertEquals("crabs", Grammar.plural("crab"));
		assertEquals("rats", Grammar.plural("rat"));
		assertEquals("bats", Grammar.plural("bat"));
		assertEquals("caverats", Grammar.plural("caverat"));
		assertEquals("penguins", Grammar.plural("penguin"));
		assertEquals("monkeys", Grammar.plural("monkey"));
		assertEquals("boars", Grammar.plural("boar"));
		assertEquals("wolves", Grammar.plural("wolf"));
		assertEquals("gnomes", Grammar.plural("gnome"));
		assertEquals("mage gnomes", Grammar.plural("mage gnome"));
		assertEquals("cobras", Grammar.plural("cobra"));
		assertEquals("bears", Grammar.plural("bear"));
		assertEquals("lions", Grammar.plural("lion"));
		assertEquals("goblins", Grammar.plural("goblin"));
		assertEquals("kobolds", Grammar.plural("kobold"));
		assertEquals("elephants", Grammar.plural("elephant"));
		assertEquals("venomrats", Grammar.plural("venomrat"));
		assertEquals("tigers", Grammar.plural("tiger"));
		assertEquals("skeletons", Grammar.plural("skeleton"));
		assertEquals("gargoyles", Grammar.plural("gargoyle"));
		assertEquals("young beholders", Grammar.plural("young beholder"));
		assertEquals("zombie rats", Grammar.plural("zombie rat"));
		assertEquals("veteran goblins", Grammar.plural("veteran goblin"));
		assertEquals("soldier kobolds", Grammar.plural("soldier kobold"));
		assertEquals("green slimes", Grammar.plural("green slime")); //plural of slime is slime but this is a creature....
		assertEquals("archer kobolds", Grammar.plural("archer kobold"));
		assertEquals("black bears", Grammar.plural("black bear"));
		assertEquals("elder gargoyles", Grammar.plural("elder gargoyle"));
		assertEquals("razorrats", Grammar.plural("razorrat"));
		assertEquals("cyclopses", Grammar.plural("cyclops"));
		assertEquals("beholders", Grammar.plural("beholder"));
		assertEquals("soldier goblins", Grammar.plural("soldier goblin"));
		assertEquals("veteran kobolds", Grammar.plural("veteran kobold"));
		assertEquals("trolls", Grammar.plural("troll"));
		assertEquals("orcs", Grammar.plural("orc"));
		assertEquals("dark gargoyles", Grammar.plural("dark gargoyle"));
		assertEquals("ogres", Grammar.plural("ogre"));
		assertEquals("mummies", Grammar.plural("mummy"));
		assertEquals("leader kobolds", Grammar.plural("leader kobold"));
		assertEquals("orc warriors", Grammar.plural("orc warrior"));
		assertEquals("orc hunters", Grammar.plural("orc hunter"));
		assertEquals("ghosts", Grammar.plural("ghost"));
		assertEquals("giantrats", Grammar.plural("giantrat"));
		assertEquals("elves", Grammar.plural("elf"));
		assertEquals("dwarves", Grammar.plural("dwarf"));
		assertEquals("ratmen", Grammar.plural("ratman"));
		assertEquals("ratwomen", Grammar.plural("ratwoman"));
		assertEquals("elder beholders", Grammar.plural("elder beholder"));
		assertEquals("brown slimes", Grammar.plural("brown slime")); //plural of slime is slime but this is a creature....
		assertEquals("venom gargoyles", Grammar.plural("venom gargoyle"));
		assertEquals("elder ogres", Grammar.plural("elder ogre"));
		assertEquals("dwarf guardians", Grammar.plural("dwarf guardian"));
		assertEquals("orc chiefs", Grammar.plural("orc chief")); //chief is an exception to the v rule
		assertEquals("militia elves", Grammar.plural("militia elf"));
		assertEquals("archer elves", Grammar.plural("archer elf"));
		assertEquals("zombies", Grammar.plural("zombie"));
		assertEquals("elder dwarves", Grammar.plural("elder dwarf"));
		assertEquals("soldier elves", Grammar.plural("soldier elf"));
		assertEquals("warrior skeletons", Grammar.plural("warrior skeleton"));
		assertEquals("black slimes", Grammar.plural("black slime")); //plural of slime is slime but this is a creature....
		assertEquals("wooden golems", Grammar.plural("wooden golem"));
		assertEquals("royal mummies", Grammar.plural("royal mummy"));
		assertEquals("archrats", Grammar.plural("archrat"));
		assertEquals("hero dwarves", Grammar.plural("hero dwarf"));
		assertEquals("mage elves", Grammar.plural("mage elf"));
		assertEquals("deaths", Grammar.plural("death"));
		assertEquals("commander elves", Grammar.plural("commander elf"));
		assertEquals("stone golems", Grammar.plural("stone golem"));
		assertEquals("archmage elves", Grammar.plural("archmage elf"));
		assertEquals("leader dwarves", Grammar.plural("leader dwarf"));
		assertEquals("demon skeletons", Grammar.plural("demon skeleton"));
		assertEquals("elf sacerdotists", Grammar.plural("elf sacerdotist"));
		assertEquals("earth elementals", Grammar.plural("earth elemental"));
		assertEquals("fire elementals", Grammar.plural("fire elemental"));
		assertEquals("water elementals", Grammar.plural("water elemental"));
		assertEquals("green dragons", Grammar.plural("green dragon"));
		assertEquals("death knights", Grammar.plural("death knight"));
		assertEquals("liches", Grammar.plural("lich"));
		assertEquals("blue dragons", Grammar.plural("blue dragon"));
		assertEquals("black dragons", Grammar.plural("black dragon"));

	}

	/**
	 * entry point
	 * @param args ignored
	 */
	public static void main(String[] args) {
		TestGrammar testCase = new TestGrammar();

		testCase.runTestCase(TestGrammar.class);
	}
}