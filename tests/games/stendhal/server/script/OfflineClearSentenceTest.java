/**
 * 
 */
package games.stendhal.server.script;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;

import java.util.Arrays;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import utilities.PlayerTestHelper;

/**
 * Tests for OfflineClearSentence script
 * 
 * @author madmetzger
 */
public class OfflineClearSentenceTest {

	@BeforeClass
	public static void beforeClass() {
		PlayerTestHelper.generateNPCRPClasses();
		MockStendlRPWorld.get();
	}

	@AfterClass
	public static void afterClass() {
		MockStendlRPWorld.reset();
	}

	@Test
	public final void testValidateParametersValid() throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Arrays.asList("tester"));
		assertThat(Boolean.valueOf(validity), is(Boolean.TRUE));
	}

	@Test
	public final void testValidateParametersInvalidEmptyParameters()
			throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Collections.<String> emptyList());
		assertThat(Boolean.valueOf(validity), is(Boolean.FALSE));
	}

    @Test
    public final void testValidateParametersInvalidTooMuchParameters() throws Exception {
        Player admin = createAdmin();
        boolean validity = new OfflineClearSentence().validateParameters(admin, Arrays.asList("tester", "secondtester"));
        assertThat(Boolean.valueOf(validity), is(Boolean.FALSE));
    }

	@Test
    public final void testProcessWithoutSentence() throws Exception {
        Player admin = createAdmin();
        Player playerToModify = createPlayerToModify();
        Player playerToCompare = createPlayerToModify();
        new OfflineClearSentence().process(admin, playerToModify, Collections.<String> emptyList());
        assertThat(playerToModify, is(playerToCompare));
	}

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
