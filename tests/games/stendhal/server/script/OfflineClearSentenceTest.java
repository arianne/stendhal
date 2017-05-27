/**
 *
 */
package games.stendhal.server.script;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

/**
 * Tests for OfflineClearSentence script
 *
 * @author madmetzger
 */
public class OfflineClearSentenceTest {

    /**
     * Setup before running tests in this test class
     */
	@BeforeClass
	public static void beforeClass() {
		PlayerTestHelper.generateNPCRPClasses();
		MockStendlRPWorld.get();
	}

    /**
     * Clean up environment after running all tests
     */
	@AfterClass
	public static void afterClass() {
		MockStendlRPWorld.reset();
	}

    /**
     * Test for parameter validation with a valid set of parameters
     *
     * @throws Exception
     */
	@Test
	public final void testValidateParametersValid() throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Arrays.asList("tester"));
		assertThat(Boolean.valueOf(validity), is(Boolean.TRUE));
	}

    /**
     * Test for parameter validation with an invalid empty set of parameters
     *
     * @throws Exception
     */
	@Test
	public final void testValidateParametersInvalidEmptyParameters()
			throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Collections.<String> emptyList());
		assertThat(Boolean.valueOf(validity), is(Boolean.FALSE));
	}

    /**
     * Test for parameter validation with too many parameters
     *
     * @throws Exception
     */
    @Test
    public final void testValidateParametersInvalidTooMuchParameters() throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Arrays.asList("tester", "secondtester"));
        assertThat(Boolean.valueOf(validity), is(Boolean.FALSE));
    }

    /**
     * Test processing a player object without a sentence
     *
     * @throws Exception
     */
	@Test
    public final void testProcessWithoutSentence() throws Exception {
        Player admin = createAdmin();
        Player playerToModify = createPlayerToModify();
        Player playerToCompare = createPlayerToModify();
        new OfflineClearSentence().process(admin, playerToModify, Collections.<String> emptyList());
        assertThat(playerToModify, is(playerToCompare));
	}

    /**
     * Test processing a player with a sentence
     *
     * @throws Exception
     */
    @Test
    public final void testProcessWithSentence() throws Exception {
        Player admin = createAdmin();
        Player playerToModify = createPlayerToModify();
        playerToModify.setSentence("I have a dumb Sentence");
        Player playerToCompare = createPlayerToModify();
        new OfflineClearSentence().process(admin, playerToModify, Collections.<String> emptyList());
        assertThat(playerToModify, is(playerToCompare));
    }

    private Player createAdmin() {
        return PlayerTestHelper.createPlayer("someadmin");
    }

    private Player createPlayerToModify() {
        return PlayerTestHelper.createPlayer("sentenceman");
    }

}
