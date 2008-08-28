package games.stendhal.server.events;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertThat;

public class TextEventTest {

	@Test
	public void testTextEvent () {
		TextEvent event = new TextEvent("text");
		assertThat(event,is(TextEvent.class));
		assertThat(event.get("text"),is("text"));
	}

}
