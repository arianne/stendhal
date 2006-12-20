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
		assertEquals("", Grammar.plural("knife"));
		assertEquals("", Grammar.plural("dagger"));
		assertEquals("", Grammar.plural("short sword"));
		assertEquals("", Grammar.plural("sword"));
		assertEquals("", Grammar.plural("scimitar"));
		assertEquals("", Grammar.plural("katana"));
		assertEquals("", Grammar.plural("claymore"));
		assertEquals("", Grammar.plural("broadsword"));
		assertEquals("", Grammar.plural("biting sword"));
		assertEquals("", Grammar.plural("fire sword"));
		assertEquals("", Grammar.plural("ice sword"));
		assertEquals("", Grammar.plural("great sword"));
		assertEquals("", Grammar.plural("r hand sword"));
		assertEquals("", Grammar.plural("l hand sword"));
		assertEquals("", Grammar.plural("small axe"));
		assertEquals("", Grammar.plural("hand axe"));
		assertEquals("", Grammar.plural("axe"));
		assertEquals("", Grammar.plural("battle axe"));
		assertEquals("", Grammar.plural("bardiche"));
		assertEquals("", Grammar.plural("scythe"));
		assertEquals("", Grammar.plural("twoside axe"));
		assertEquals("", Grammar.plural("halberd"));
		assertEquals("", Grammar.plural("twoside axe +3"));
		assertEquals("", Grammar.plural("club"));
		assertEquals("", Grammar.plural("staff"));
		assertEquals("", Grammar.plural("mace"));
		assertEquals("", Grammar.plural("flail"));
		assertEquals("", Grammar.plural("mace +1"));
		assertEquals("", Grammar.plural("mace +2"));
		assertEquals("", Grammar.plural("skull staff"));
		assertEquals("", Grammar.plural("flail +2"));
		assertEquals("", Grammar.plural("hammer"));
		assertEquals("", Grammar.plural("hammer +3"));
		assertEquals("", Grammar.plural("war hammer"));
		assertEquals("", Grammar.plural("wooden bow"));
		assertEquals("", Grammar.plural("longbow"));
		assertEquals("", Grammar.plural("longbow +1"));
		assertEquals("", Grammar.plural("crossbow"));
		assertEquals("", Grammar.plural("wooden arrow"));
		assertEquals("", Grammar.plural("steel arrow"));
		assertEquals("", Grammar.plural("golden arrow"));
		assertEquals("", Grammar.plural("buckler"));
		assertEquals("", Grammar.plural("wooden shield"));
		assertEquals("", Grammar.plural("studded shield"));
		assertEquals("", Grammar.plural("plate shield"));
		assertEquals("", Grammar.plural("lion shield"));
		assertEquals("", Grammar.plural("unicorn shield"));
		assertEquals("", Grammar.plural("lion shield +1"));
		assertEquals("", Grammar.plural("skull shield"));
		assertEquals("", Grammar.plural("crown shield"));
		assertEquals("", Grammar.plural("golden shield"));
		assertEquals("", Grammar.plural("dress"));
		assertEquals("", Grammar.plural("leather armor"));
		assertEquals("", Grammar.plural("leather cuirass"));
		assertEquals("", Grammar.plural("leather armor +1"));
		assertEquals("", Grammar.plural("leather cuirass +1"));
		assertEquals("", Grammar.plural("studded armor"));
		assertEquals("", Grammar.plural("chain armor"));
		assertEquals("", Grammar.plural("chain armor +1"));
		assertEquals("", Grammar.plural("scale armor"));
		assertEquals("", Grammar.plural("scale armor +1"));
		assertEquals("", Grammar.plural("chain armor +3"));
		assertEquals("", Grammar.plural("scale armor +2"));
		assertEquals("", Grammar.plural("plate armor"));
		assertEquals("", Grammar.plural("golden armor"));
		assertEquals("", Grammar.plural("leather helmet"));
		assertEquals("", Grammar.plural("robins hat"));
		assertEquals("", Grammar.plural("studded helmet"));
		assertEquals("", Grammar.plural("chain helmet"));
		assertEquals("", Grammar.plural("viking helmet"));
		assertEquals("", Grammar.plural("chain helmet +2"));
		assertEquals("", Grammar.plural("golden helmet"));
		assertEquals("", Grammar.plural("golden helmet +3"));
		assertEquals("", Grammar.plural("trophy helmet"));
		assertEquals("", Grammar.plural("leather legs"));
		assertEquals("", Grammar.plural("studded legs"));
		assertEquals("", Grammar.plural("chain legs"));
		assertEquals("", Grammar.plural("golden legs"));
		assertEquals("", Grammar.plural("leather boots"));
		assertEquals("", Grammar.plural("studded boots"));
		assertEquals("", Grammar.plural("chain boots"));
		assertEquals("", Grammar.plural("steel boots"));
		assertEquals("", Grammar.plural("golden boots"));
		assertEquals("", Grammar.plural("cloak"));
		assertEquals("", Grammar.plural("elf cloak"));
		assertEquals("", Grammar.plural("dwarf cloak"));
		assertEquals("", Grammar.plural("elf cloak +2"));
		assertEquals("", Grammar.plural("green dragon cloak"));
		assertEquals("", Grammar.plural("lich cloak"));
		assertEquals("", Grammar.plural("blue dragon cloak"));
		assertEquals("", Grammar.plural("black dragon cloak"));
		assertEquals("", Grammar.plural("golden cloak"));
		assertEquals("", Grammar.plural("cherry"));
		assertEquals("", Grammar.plural("cheese"));
		assertEquals("", Grammar.plural("carrot"));
		assertEquals("", Grammar.plural("salad"));
		assertEquals("", Grammar.plural("apple"));
		assertEquals("", Grammar.plural("bread"));
		assertEquals("", Grammar.plural("meat"));
		assertEquals("", Grammar.plural("ham"));
		assertEquals("", Grammar.plural("sandwich"));
		assertEquals("", Grammar.plural("pie"));
		assertEquals("", Grammar.plural("button mushroom"));
		assertEquals("", Grammar.plural("porcini"));
		assertEquals("", Grammar.plural("toadstool"));
		assertEquals("", Grammar.plural("beer"));
		assertEquals("", Grammar.plural("wine"));
		assertEquals("", Grammar.plural("minor potion"));
		assertEquals("", Grammar.plural("antidote"));
		assertEquals("", Grammar.plural("potion"));
		assertEquals("", Grammar.plural("greater potion"));
		assertEquals("", Grammar.plural("poison"));
		assertEquals("", Grammar.plural("greater poison"));
		assertEquals("", Grammar.plural("deadly poison"));
		assertEquals("", Grammar.plural("flask"));
		assertEquals("", Grammar.plural("bottle"));
		assertEquals("", Grammar.plural("big bottle"));
		assertEquals("", Grammar.plural("money"));
		assertEquals("", Grammar.plural("book black"));
		assertEquals("", Grammar.plural("arandula"));
		assertEquals("", Grammar.plural("wood"));
		assertEquals("", Grammar.plural("grain"));
		assertEquals("", Grammar.plural("flour"));
		assertEquals("", Grammar.plural("iron ore"));
		assertEquals("", Grammar.plural("iron"));
		assertEquals("", Grammar.plural("Golden GM Tokend"));
		assertEquals("", Grammar.plural("Silvery GM Tokend"));
		assertEquals("", Grammar.plural("Bronze GM Tokend"));
		assertEquals("", Grammar.plural("token"));
		assertEquals("", Grammar.plural("note"));
		assertEquals("", Grammar.plural("coupon"));
		assertEquals("", Grammar.plural("dice"));
		assertEquals("", Grammar.plural("teddy"));
		assertEquals("", Grammar.plural("map"));
		assertEquals("", Grammar.plural("summon scroll"));
		assertEquals("", Grammar.plural("empty scroll"));
		assertEquals("", Grammar.plural("home scroll"));
		assertEquals("", Grammar.plural("marked scroll"));
		assertEquals("", Grammar.plural("present"));
		assertEquals("", Grammar.plural("rod of the gm"));
		assertEquals("", Grammar.plural("rat key"));
	}

	/**
	 * Tests pluralisation of creatures
	 */
	public void testPluralCreatures() {
		assertEquals("", Grammar.plural("deer"));
		assertEquals("", Grammar.plural("crab"));
		assertEquals("", Grammar.plural("rat"));
		assertEquals("", Grammar.plural("bat"));
		assertEquals("", Grammar.plural("caverat"));
		assertEquals("", Grammar.plural("penguin"));
		assertEquals("", Grammar.plural("monkey"));
		assertEquals("", Grammar.plural("boar"));
		assertEquals("", Grammar.plural("wolf"));
		assertEquals("", Grammar.plural("gnome"));
		assertEquals("", Grammar.plural("mage gnome"));
		assertEquals("", Grammar.plural("cobra"));
		assertEquals("", Grammar.plural("bear"));
		assertEquals("", Grammar.plural("lion"));
		assertEquals("", Grammar.plural("goblin"));
		assertEquals("", Grammar.plural("kobold"));
		assertEquals("", Grammar.plural("elephant"));
		assertEquals("", Grammar.plural("venomrat"));
		assertEquals("", Grammar.plural("tiger"));
		assertEquals("", Grammar.plural("skeleton"));
		assertEquals("", Grammar.plural("gargoyle"));
		assertEquals("", Grammar.plural("young beholder"));
		assertEquals("", Grammar.plural("zombie rat"));
		assertEquals("", Grammar.plural("veteran goblin"));
		assertEquals("", Grammar.plural("soldier kobold"));
		assertEquals("", Grammar.plural("green slime"));
		assertEquals("", Grammar.plural("archer kobold"));
		assertEquals("", Grammar.plural("black bear"));
		assertEquals("", Grammar.plural("elder gargoyle"));
		assertEquals("", Grammar.plural("razorrat"));
		assertEquals("", Grammar.plural("cyclops"));
		assertEquals("", Grammar.plural("beholder"));
		assertEquals("", Grammar.plural("soldier goblin"));
		assertEquals("", Grammar.plural("veteran kobold"));
		assertEquals("", Grammar.plural("troll"));
		assertEquals("", Grammar.plural("orc"));
		assertEquals("", Grammar.plural("dark gargoyle"));
		assertEquals("", Grammar.plural("ogre"));
		assertEquals("", Grammar.plural("mummy"));
		assertEquals("", Grammar.plural("leader kobold"));
		assertEquals("", Grammar.plural("orc warrior"));
		assertEquals("", Grammar.plural("orc hunter"));
		assertEquals("", Grammar.plural("ghost"));
		assertEquals("", Grammar.plural("giantrat"));
		assertEquals("", Grammar.plural("elf"));
		assertEquals("", Grammar.plural("dwarf"));
		assertEquals("", Grammar.plural("ratman"));
		assertEquals("", Grammar.plural("ratwoman"));
		assertEquals("", Grammar.plural("elder beholder"));
		assertEquals("", Grammar.plural("brown slime"));
		assertEquals("", Grammar.plural("venom gargoyle"));
		assertEquals("", Grammar.plural("elder ogre"));
		assertEquals("", Grammar.plural("dwarf guardian"));
		assertEquals("", Grammar.plural("orc chief"));
		assertEquals("", Grammar.plural("militia elf"));
		assertEquals("", Grammar.plural("archer elf"));
		assertEquals("", Grammar.plural("zombie"));
		assertEquals("", Grammar.plural("elder dwarf"));
		assertEquals("", Grammar.plural("soldier elf"));
		assertEquals("", Grammar.plural("warrior skeleton"));
		assertEquals("", Grammar.plural("black slime"));
		assertEquals("", Grammar.plural("wooden golem"));
		assertEquals("", Grammar.plural("royal mummy"));
		assertEquals("", Grammar.plural("archrat"));
		assertEquals("", Grammar.plural("hero dwarf"));
		assertEquals("", Grammar.plural("mage elf"));
		assertEquals("", Grammar.plural("death"));
		assertEquals("", Grammar.plural("commander elf"));
		assertEquals("", Grammar.plural("stone golem"));
		assertEquals("", Grammar.plural("archmage elf"));
		assertEquals("", Grammar.plural("leader dwarf"));
		assertEquals("", Grammar.plural("demon skeleton"));
		assertEquals("", Grammar.plural("elf sacerdotist"));
		assertEquals("", Grammar.plural("earth elemental"));
		assertEquals("", Grammar.plural("fire elemental"));
		assertEquals("", Grammar.plural("water elemental"));
		assertEquals("", Grammar.plural("green dragon"));
		assertEquals("", Grammar.plural("death knight"));
		assertEquals("", Grammar.plural("lich"));
		assertEquals("", Grammar.plural("blue dragon"));
		assertEquals("", Grammar.plural("black dragon"));

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
