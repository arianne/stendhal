package games.stendhal.server.core.rp.pvp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
@RunWith(Suite.class)
@SuiteClasses({PlayerVsPlayerChallengeTestHashCode.class, PlayerVsPlayerChallengeTestEquals.class,
	PlayerVsPlayerChallengeTestToString.class, PlayerVsPlayerChallengeManagerTest.class})
public class PlayerVsPlayerChallengeTest {

	static PlayerVsPlayerChallenge createChallenge(String challenger, String challenged, boolean accepted, long opened, long acceptTurn) {
		if(opened > acceptTurn) {
			throw new IllegalArgumentException("The turn of acceptance must be after the openening turn.");
		}

		Player challengedPlayer = PlayerTestHelper.createPlayer(challenged);
		PlayerVsPlayerChallenge challenge = new PlayerVsPlayerChallenge(opened,
				 PlayerTestHelper.createPlayer(challenger),
				 challengedPlayer);
		if(accepted) {
			challenge.accept(acceptTurn, challengedPlayer);
		}
		return challenge;
	}

	static PlayerVsPlayerChallenge createChallenge(String challenger, String challenged, boolean accepted) {
		return createChallenge(challenger, challenged, accepted, 0l, 1l);
	}

	//suite class
}
