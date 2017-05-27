package games.stendhal.server.entity.spell;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.exception.InsufficientManaException;
import games.stendhal.server.entity.spell.exception.InvalidSpellTargetException;
import games.stendhal.server.entity.spell.exception.LevelRequirementNotFulfilledException;
import games.stendhal.server.entity.spell.exception.SpellNotCooledDownException;
import games.stendhal.server.maps.MockStendhalRPRuleProcessor;
import games.stendhal.server.maps.MockStendlRPWorld;
import marauroa.common.game.RPObject.ID;
import utilities.PlayerTestHelper;
/**
 * Tests for Spells
 *
 * @author madmetzger
 */
public class SpellTest {

	private Spell healingSpell;
	private Spell attackSpell;

	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		MockStendhalRPRuleProcessor.get();
	}

	@AfterClass
	public static void tearDownAfterClass() {
		MockStendlRPWorld.reset();
	}

	@Before
	public void setUp() {
		this.healingSpell = SingletonRepository.getEntityManager().getSpell("heal");
		this.attackSpell = SingletonRepository.getEntityManager().getSpell("fireball");
	}

	private Player createWizard() {
		Player caster = PlayerTestHelper.createPlayer("wizard");
		caster.setLevel(10);
		caster.setBaseMana(1000);
		caster.setMana(1000);
		return caster;
	}

	private Player createTarget() {
		Player target = PlayerTestHelper.createPlayer("target");
		return target;
	}

	@Test
	public void testIsTargetValid() {
		Player caster = createWizard();
		Player target = createTarget();
		boolean targetValid = healingSpell.isTargetValid(caster, target);
		assertThat(Boolean.valueOf(targetValid), is(Boolean.TRUE));
		Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		boolean creatureTargetValid = healingSpell.isTargetValid(caster, creature);
		assertThat(Boolean.valueOf(creatureTargetValid), is(Boolean.FALSE));
		Item i = SingletonRepository.getEntityManager().getItem("axe");
		boolean itemIsInvalid = healingSpell.isTargetValid(caster, i);
		assertThat(Boolean.valueOf(itemIsInvalid), is(Boolean.FALSE));
	}

	@Test(expected=InvalidSpellTargetException.class)
	public void testIsTargetValidCreature() throws Exception {
		Player caster = createWizard();
		Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		boolean creatureTargetValid = healingSpell.isTargetValid(caster, creature);
		assertThat(Boolean.valueOf(creatureTargetValid), is(Boolean.FALSE));
		healingSpell.cast(caster, creature);
	}

	@Test(expected=InvalidSpellTargetException.class)
	public void testIsTargetValidItem() throws Exception {
		Player caster = createWizard();
		Item i = SingletonRepository.getEntityManager().getItem("axe");
		boolean itemIsInvalid = healingSpell.isTargetValid(caster, i);
		assertThat(Boolean.valueOf(itemIsInvalid), is(Boolean.FALSE));
		healingSpell.cast(caster, i);
	}

	@Test
	public void testCopyConstructor() throws Exception {
		Spell copy = new HealingSpell(healingSpell);
		assertThat(copy, is(healingSpell));
	}

	@Test(expected=SpellNotCooledDownException.class)
	public void testCoolDownNegative() throws Exception {
		long lastCastTime = System.currentTimeMillis() + healingSpell.getCooldown();
		healingSpell.put("timestamp", String.valueOf(lastCastTime));
		Player target = createTarget();
		Player caster = createWizard();
		healingSpell.cast(caster, target);
	}

	@Test
	public void testCoolDownPositive() throws Exception {
		healingSpell.put("timestamp", String.valueOf(0L));
		assertThat(Boolean.valueOf(healingSpell.isCooledDown()), is(Boolean.TRUE));
		Player target = createTarget();
		Player caster = createWizard();
		healingSpell.cast(caster, target);
	}

	@Test
	public void testPossibleSlots() throws Exception {
		boolean inSpells = healingSpell.canBeEquippedIn("spells");
		assertThat(Boolean.valueOf(inSpells), is(Boolean.TRUE));
		boolean inBag = healingSpell.canBeEquippedIn("bag");
		assertThat(Boolean.valueOf(inBag), is(Boolean.FALSE));
	}

	@Test(expected=InsufficientManaException.class)
	public void testManaCheckNegative() throws Exception {
		Player caster = createWizard();
		caster.setMana(0);
		caster.setBaseMana(0);
		Player target = createTarget();
		healingSpell.cast(caster, target);
	}

	@Test
	public void testManaCheckPositive() throws Exception {
		Player caster = createWizard();
		Player target = createTarget();
		healingSpell.cast(caster, target);
	}

	@Test(expected=LevelRequirementNotFulfilledException.class)
	public void testLevelCheckNegative() throws Exception {
		Player caster = createWizard();
		caster.setLevel(1);
		Player target = createTarget();
		healingSpell.cast(caster, target);
	}

	@Test
	public void testLevelCheckPositive() throws Exception {
		Player caster = createWizard();
		Player target = createTarget();
		healingSpell.cast(caster, target);
	}

	@Test
	public void testEffectHeal() throws Exception {
		Player caster = createWizard();
		Player target = createTarget();
		target.setBaseHP(500);
		target.setHP(1);
		healingSpell.cast(caster, target);
		//healing spell's effect acts as turn listener
		int currentTurnForDebugging = TurnNotifier.get().getCurrentTurnForDebugging();
		TurnNotifier.get().logic(currentTurnForDebugging + 1);
		assertThat(Integer.valueOf(caster.getMana()), lessThanOrEqualTo(Integer.valueOf(1000)));
		assertThat(Integer.valueOf(target.getHP()), greaterThan(Integer.valueOf(1)));
		assertThat(caster.getMagicSkillXp(healingSpell.getNature()), is(1));
	}

	@Test
	public void testAttackTargetPositive() throws Exception {
		Player caster = createWizard();
		Player target = createTarget();
		Creature targetCreature = SingletonRepository.getEntityManager().getCreature("rat");
		assertThat(Boolean.valueOf(attackSpell.isTargetValid(caster, target)), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(attackSpell.isTargetValid(caster, targetCreature)), is(Boolean.TRUE));
	}

	@Test
	public void testAttackTargetNegative() throws Exception {
		Player caster = createWizard();
		Item target = SingletonRepository.getEntityManager().getItem("axe");
		assertThat(Boolean.valueOf(attackSpell.isTargetValid(caster, target)), is(Boolean.FALSE));
	}

	@Test
	public void testAttackCastPositive() throws Exception {
		StendhalRPZone zone = new StendhalRPZone("test");
		Player caster = createWizard();
		caster.setID(new ID(99, "test"));
		caster.setAtk(10);
		Creature targetCreature = SingletonRepository.getEntityManager().getCreature("rat");
		targetCreature.setID(new ID(999, "test"));
		Integer startingValue = Integer.valueOf(targetCreature.getHP());
		zone.add(targetCreature);
		zone.add(caster);
		attackSpell.cast(caster, targetCreature);
		int currentTurnForDebugging = TurnNotifier.get().getCurrentTurnForDebugging();
		for (int i = 0; i <= attackSpell.getAmount() * attackSpell.getRate(); i++) {
			TurnNotifier.get().logic(currentTurnForDebugging + i);
		}
		assertThat(Integer.valueOf(targetCreature.getHP()), lessThan(startingValue));
		assertThat(caster.getMagicSkillXp(attackSpell.getNature()), is(1));
	}

}
