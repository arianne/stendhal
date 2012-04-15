package games.stendhal.server.entity.spell;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.junit.Assert.assertThat;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.item.Item;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.spell.exception.InsufficientManaException;
import games.stendhal.server.entity.spell.exception.InvalidSpellTargetException;
import games.stendhal.server.entity.spell.exception.LevelRequirementNotFulfilledException;
import games.stendhal.server.entity.spell.exception.SpellException;
import games.stendhal.server.entity.spell.exception.SpellNotCooledDownException;
import games.stendhal.server.maps.MockStendlRPWorld;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;
import utilities.RPClass.CreatureTestHelper;
/**
 * Tests for Spells
 * 
 * @author madmetzger
 */
public class SpellTest {
	
	@BeforeClass
	public static void setUpBeforeClass() {
		MockStendlRPWorld.get();
		CreatureTestHelper.generateRPClasses();
	}
	
	@AfterClass
	public static void tearDownAfterClass() {
		MockStendlRPWorld.reset();
	}
	
	private Player createWizard() {
		Player caster = PlayerTestHelper.createPlayer("wizard");
		caster.setLevel(10);
		caster.setBaseMana(1000);
		caster.setMana(1000);
		return caster;
	}

	@Test
	public void testIsTargetValid() {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Player target = createTarget();
		boolean targetValid = spell.isTargetValid(caster, target);
		assertThat(Boolean.valueOf(targetValid), is(Boolean.TRUE));
		Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		boolean creatureTargetValid = spell.isTargetValid(caster, creature);
		assertThat(Boolean.valueOf(creatureTargetValid), is(Boolean.FALSE));
		Item i = SingletonRepository.getEntityManager().getItem("axe");
		boolean itemIsInvalid = spell.isTargetValid(caster, i);
		assertThat(Boolean.valueOf(itemIsInvalid), is(Boolean.FALSE));
	}

	private Player createTarget() {
		Player target = PlayerTestHelper.createPlayer("target");
		return target;
	}
	
	@Test(expected=InvalidSpellTargetException.class)
	public void testIsTargetValidCreature() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Creature creature = SingletonRepository.getEntityManager().getCreature("rat");
		boolean creatureTargetValid = spell.isTargetValid(caster, creature);
		assertThat(Boolean.valueOf(creatureTargetValid), is(Boolean.FALSE));
		spell.cast(caster, creature);
	}
	
	@Test(expected=InvalidSpellTargetException.class)
	public void testIsTargetValidItem() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Item i = SingletonRepository.getEntityManager().getItem("axe");
		boolean itemIsInvalid = spell.isTargetValid(caster, i);
		assertThat(Boolean.valueOf(itemIsInvalid), is(Boolean.FALSE));
		spell.cast(caster, i);
	}
	
	@Test
	public void testCopyConstructor() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Spell copy = new HealingSpell(spell);
		assertThat(copy, is(spell));
	}
	
	@Test(expected=SpellNotCooledDownException.class)
	public void testCoolDownNegative() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		long lastCastTime = System.currentTimeMillis() + spell.getCooldown();
		spell.put("timestamp", String.valueOf(lastCastTime));
		Player target = createTarget();
		Player caster = createWizard();
		spell.cast(caster, target);
	}
	
	@Test
	public void testCoolDownPositive() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		spell.put("timestamp", String.valueOf(0L));
		assertThat(Boolean.valueOf(spell.isCooledDown()), is(Boolean.TRUE));
		Player target = createTarget();
		Player caster = createWizard();
		spell.cast(caster, target);
	}
	
	@Test
	public void testPossibleSlots() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		boolean inSpells = spell.canBeEquippedIn("spells");
		assertThat(Boolean.valueOf(inSpells), is(Boolean.TRUE));
		boolean inBag = spell.canBeEquippedIn("bag");
		assertThat(Boolean.valueOf(inBag), is(Boolean.FALSE));
	}
	
	@Test(expected=InsufficientManaException.class)
	public void testManaCheckNegative() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		caster.setMana(0);
		caster.setBaseMana(0);
		Player target = createTarget();
		spell.cast(caster, target);
	}
	
	@Test
	public void testManaCheckPositive() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Player target = createTarget();
		spell.cast(caster, target);
	}
	
	@Test(expected=LevelRequirementNotFulfilledException.class)
	public void testLevelCheckNegative() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		caster.setLevel(5);
		Player target = createTarget();
		spell.cast(caster, target);
	}
	
	@Test
	public void testLevelCheckPositive() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Player target = createTarget();
		spell.cast(caster, target);
	}

	@Test
	public void testEffectHeal() throws Exception {
		Spell spell = SingletonRepository.getEntityManager().getSpell("heal");
		Player caster = createWizard();
		Player target = createTarget();
		target.setBaseHP(500);
		target.setHP(1);
		spell.cast(caster, target);
		//healing spell's effect acts as turn listener
		int currentTurnForDebugging = TurnNotifier.get().getCurrentTurnForDebugging();
		TurnNotifier.get().logic(currentTurnForDebugging + 1);
		assertThat(Integer.valueOf(caster.getMana()), lessThanOrEqualTo(Integer.valueOf(1000)));
		assertThat(Integer.valueOf(target.getHP()), greaterThan(Integer.valueOf(1)));
	}
	
	@Test
	public void testEffectAttack() throws Exception {
		
	}

}
