/**
 * 
 */
package games.stendhal.server.entity.mapstuff.block;

import org.junit.BeforeClass;
import org.junit.Test;

import utilities.RPClass.BlockTestHelper;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;


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
		BlockTarget unshapedTarget = new BlockTarget(1, 1);
		BlockTarget squareTarget = new BlockTarget(1, 1, "square");
		Block unshapedBlock = new Block(1, 1, true);
		Block squareBlock = new Block(1, 1, true, "block", "square");
		assertThat(Boolean.valueOf(unshapedTarget.doesTrigger(unshapedBlock)), is(Boolean.TRUE));
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(unshapedBlock)), is(Boolean.FALSE));
		assertThat(Boolean.valueOf(squareTarget.doesTrigger(squareBlock)), is(Boolean.TRUE));
	}

}
