/**
 *
 */
package games.stendhal.server.entity.mapstuff.block;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.npc.ChatCondition;
import games.stendhal.server.entity.npc.action.IncreaseXPAction;
import games.stendhal.server.entity.npc.condition.LevelGreaterThanCondition;
import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
import utilities.RPClass.BlockTestHelper;


/**
 * Tests for BlockTargets
 *
 * @author madmetzger
 */
public class BlockTargetTest {

	/**
	 * Initialize needed RPClasses
	 */
	@BeforeClass
	public static void beforeClass() {
		BlockTestHelper.generateRPClasses();
	}

	/**
	 * Tests for doesTrigger
	 */
	@Test
	public void testDoesTrigger() {
		Player player = PlayerTestHelper.createPlayer("pusher");
		BlockTarget unshapedTarget = new BlockTarget();
		unshapedTarget.setPosition(1, 1);
		BlockTarget squareTarget = new BlockTarget("square");
		squareTarget.setPosition(1, 1);
		Block unshapedBlock = new Block(true);
		unshapedBlock.setPosition(1, 1);
		Block squareBlock = new Block(true, "block", "square");
		squareBlock.setPosition(1, 1);

		assertThat(Boolean.valueOf(unshapedTarget.doesTrigger(unshapedBlock, player)), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(unshapedBlock, player)), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(squareBlock, player)), is(Boolean.TRUE));
	}

	/**
	 * Tests for does trigger with involved conditions
	 */
	@Test
	public void testDoesTriggerWithCondition() {
		Player player = PlayerTestHelper.createPlayer("pusher");
		BlockTarget unshapedTarget = new BlockTarget();
		unshapedTarget.setPosition(1, 1);
		BlockTarget squareTarget = new BlockTarget("square");
		squareTarget.setPosition(1, 1);
		Block unshapedBlock = new Block(true);
		unshapedBlock.setPosition(1, 1);
		Block squareBlock = new Block(true, "block", "square");
		squareBlock.setPosition(1, 1);
		ChatCondition condition = new LevelGreaterThanCondition(1);

		unshapedTarget.setCondition(condition);
		assertThat(Boolean.valueOf(unshapedTarget.doesTrigger(unshapedBlock, player)),
				is(Boolean.valueOf(condition.fire(player, null, null))));

		squareTarget.setCondition(condition);
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(squareBlock, player)),
				is(Boolean.valueOf(condition.fire(player, null, null))));

		player.addXP(1000);
		assertThat(Boolean.valueOf(unshapedTarget.doesTrigger(unshapedBlock, player)),
				is(Boolean.valueOf(condition.fire(player, null, null))));
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(squareBlock, player)),
				is(Boolean.valueOf(condition.fire(player, null, null))));
	}

	/**
	 * Tests for trigger
	 */
	@Test
	public void testTrigger() {
		Block unshapedBlock = new Block(true);
		unshapedBlock.setPosition(1, 1);
		BlockTarget unshapedTarget = new BlockTarget();
		unshapedTarget.setPosition(1, 1);
		Player player = PlayerTestHelper.createPlayer("pusher");
		assertThat(Integer.valueOf(player.getXP()), is(Integer.valueOf(0)));
		unshapedTarget.trigger(unshapedBlock, player);

		unshapedTarget.setAction(new IncreaseXPAction(5));

		assertThat(Integer.valueOf(player.getXP()), is(Integer.valueOf(0)));
		unshapedTarget.trigger(unshapedBlock, player);
		assertThat(Integer.valueOf(player.getXP()), is(Integer.valueOf(5)));
	}

}
