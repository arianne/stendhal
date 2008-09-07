package games.stendhal.server.events;

import marauroa.common.game.RPClass;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class TextEventTest {

	@Test
	public void testTextEvent() {
		TextEvent event = new TextEvent("text");
		assertThat(event, is(TextEvent.class));
		assertThat(event.get("text"), is("text"));
	}

	public void testGenerateRPClass() {
		assertFalse(RPClass.hasRPClass("text"));
		TextEvent.generateRPClass();
		assertTrue(RPClass.hasRPClass("text"));
	}
	
	

}
