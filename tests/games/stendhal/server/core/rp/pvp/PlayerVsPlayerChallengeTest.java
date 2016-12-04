package games.stendhal.server.core.rp.pvp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import games.stendhal.server.entity.player.Player;
import utilities.PlayerTestHelper;
@RunWith(Suite.class)
@SuiteClasses({PlayerVsPlayerChallengeTestHashCode.class, PlayerVsPlayerChallengeTestEquals.class, PlayerVsPlayerChallengeTestToString.class})
public class PlayerVsPlayerChallengeTest {

	static PlayerVsPlayerChallenge createChallenge(String challenger, String challenged, boolean accepted) {
		Player challengedPlayer = PlayerTestHelper.createPlayer(challenged);
		PlayerVsPlayerChallenge challenge = new PlayerVsPlayerChallenge(0l, 
				 PlayerTestHelper.createPlayer(challenger), 
				 challengedPlayer);
		if(accepted) {
			challenge.accept(0l, challengedPlayer);
		}
		return challenge;
	}
	//suite class
}
