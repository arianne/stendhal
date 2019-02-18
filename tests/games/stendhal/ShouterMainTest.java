package games.stendhal;

import games.stendhal.bot.shouter.ShouterMain;
import org.junit.Test;

public class ShouterMainTest {

    /**
     * Tests for equals.
     */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testMain() {
        ShouterMain.main(new String[0]);
    }
}
