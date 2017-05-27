package games.stendhal.server.core.rp.pvp;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
@RunWith(Parameterized.class)
public class PlayerVsPlayerChallengeTestToString {

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                 { PlayerVsPlayerChallengeTest.createChallenge("challenger", "challenged", false),
                	 "PlayerVsPlayerChallenge{challenger=challenger, challenged=challenged, accepted=false}" },
                 { PlayerVsPlayerChallengeTest.createChallenge("challenger", "challenged", true),
            	 "PlayerVsPlayerChallenge{challenger=challenger, challenged=challenged, accepted=true}" },
                 { PlayerVsPlayerChallengeTest.createChallenge("bob", "barney", false),
                	 "PlayerVsPlayerChallenge{challenger=bob, challenged=barney, accepted=false}" },
                 { PlayerVsPlayerChallengeTest.createChallenge("bob", "barney", true),
            	 "PlayerVsPlayerChallenge{challenger=bob, challenged=barney, accepted=true}" }

           });
    }

	@Parameter(0)
	public PlayerVsPlayerChallenge toTest;

	@Parameter(1)
	public String expectedToString;

	@Test
	public void testToString() {
		assertThat(toTest.toString(), is(this.expectedToString));
	}

}
