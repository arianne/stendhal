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
public class PlayerVsPlayerChallengeTestHashCode {

	@Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		Boolean.TRUE},
        	{PlayerVsPlayerChallengeTest.createChallenge("a", "b", false),
        		PlayerVsPlayerChallengeTest.createChallenge("a", "b", true),
        		Boolean.TRUE}
        });
    }

	@Parameter(0)
	public PlayerVsPlayerChallenge one;

	@Parameter(1)
	public PlayerVsPlayerChallenge two;

	@Parameter(2)
	public Boolean expected;

	@Test
	public void testHashCode() {
		Integer actualHashCodeOne = one.hashCode();
		Integer actualHashCodeTwo = two.hashCode();
		assertThat(actualHashCodeOne.equals(actualHashCodeTwo), is(expected));
	}

}
