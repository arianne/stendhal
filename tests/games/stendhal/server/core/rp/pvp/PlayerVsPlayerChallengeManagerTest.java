package games.stendhal.server.core.rp.pvp;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.maps.MockStendlRPWorld;
import utilities.PlayerTestHelper;

/**
 * Tests for the challenge manager
 *
 * @author markus
 */
public class PlayerVsPlayerChallengeManagerTest {

	private PlayerVsPlayerChallengeManager manager;
	private Player challenger;
	private Player challenged;

	@Before
	public void before() {
		MockStendlRPWorld.reset();
		manager = PlayerVsPlayerChallengeManager.create();
		challenger = PlayerTestHelper.createPlayer("challenger");
		challenged = PlayerTestHelper.createPlayer("challenged");
	}

	@Test
	public void testCreateChallenge() {
		manager.createChallenge(challenger, challenged, 0);
		PlayerVsPlayerChallenge challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, notNullValue());
		assertThat(challenge.isAccepted(), is(Boolean.FALSE));
		assertThat(challenge.getOpened(), is(Long.valueOf(0)));
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.FALSE));
	}

	@Test
	public void testCreateChallengeAndAccept() throws Exception {
		manager.createChallenge(challenger, challenged, 0);
		PlayerVsPlayerChallenge challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, notNullValue());
		assertThat(challenge.isAccepted(), is(Boolean.FALSE));
		assertThat(challenge.getOpened(), is(Long.valueOf(0)));
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.FALSE));
		manager.accpetChallenge(challenger, challenged, 1);
		challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, nullValue());
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.TRUE));
	}

	@Test
	public void testCreateChallengeAndTimeout() throws Exception {
		manager.createChallenge(challenger, challenged, 0);
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.FALSE));

		PlayerVsPlayerChallenge challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, notNullValue());
		assertThat(challenge.isAccepted(), is(Boolean.FALSE));
		assertThat(challenge.getOpened(), is(Long.valueOf(0)));

		manager.timeOutCurrentChallenges(PlayerVsPlayerChallengeManager.TIMEOUT_FOR_ACCEPTANCE + 1);
		challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, nullValue());
	}

	@Test
	public void testCreateChallengeAndLogout() throws Exception {
		manager.createChallenge(challenger, challenged, 0);

		PlayerVsPlayerChallenge challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, notNullValue());
		assertThat(challenge.isAccepted(), is(Boolean.FALSE));
		assertThat(challenge.getOpened(), is(Long.valueOf(0)));
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.FALSE));

		manager.onLoggedOut(challenger);
		challenge = manager.getOpenChallengeForPlayers(challenger, challenged);
		assertThat(challenge, nullValue());
		assertThat(Boolean.valueOf(manager.playersHaveActiveChallenge(challenger, challenged)), is(Boolean.FALSE));
	}

}
