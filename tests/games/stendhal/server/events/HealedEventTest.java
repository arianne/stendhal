package games.stendhal.server.events;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Ignore;
import org.junit.Test;

public class HealedEventTest {

	/**
	 * Tests for healedEvent.
	 */
    @Test
    public void testHealedEvent() {
        HealedEvent event = new HealedEvent(1);
        assertThat(new Integer(event.getInt("amount")), is(new Integer(1)));
    }
    
	/**
	 * Tests for healedEventTryBroken.
	 */
    @Ignore
    @Test
    public void testHealedEventTryBroken() {
        HealedEvent event = new HealedEvent(1);
        event.put("amount", "boo boo");
        assertThat(event.get("amount"), is("boo boo"));
        assertThat(new Integer(event.getInt("amount")), is(new Integer(0)));
    }

}
