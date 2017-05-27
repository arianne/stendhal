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
public class PlayerVsPlayerChallengeTestEquals {

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		Boolean.TRUE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("a", "b", true),
        		Boolean.TRUE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("a", "c", false),
        		Boolean.FALSE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("c", "b", false),
        		Boolean.FALSE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
            		PlayerVsPlayerChallengeTest.createChallenge("a", "b", false, 1, 2),
            		Boolean.FALSE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false, 1, 2),
                		PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
                		Boolean.FALSE}
        });
    }

	@Parameter(0)
	public PlayerVsPlayerChallenge one;

	@Parameter(1)
	public PlayerVsPlayerChallenge two;

	@Parameter(2)
	public Boolean expected;

	@Test
	public void testEqualsObject() {
		assertThat(one.equals(two), is(expected));
		assertThat(two.equals(one), is(expected));
	}

}
