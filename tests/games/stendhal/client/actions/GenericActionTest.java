package games.stendhal.client.actions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Test;

import games.stendhal.client.MockStendhalClient;
import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public class GenericActionTest {
	@After
	public void tearDown() throws Exception {
		StendhalClient.resetClient();
	}
	@Test
	public void testGenericActionExecuteUsingWho() {
		String name = "who";
		int mini = 0;
		int maxi = 0;
		Map<String, String> staticAttributes = new LinkedHashMap<>();
		staticAttributes.put("type", "who");
		Map<String, Integer> pa = new LinkedHashMap<>();
		String remainder = null;
		GenericAction action = new GenericAction(name, mini, maxi, staticAttributes, pa, remainder);
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				for (final String attrib : action) {
					assertEquals("type", attrib);
					assertEquals("who", (action.get(attrib)));
				}
			}
		};
		action.execute(null, null);
	}
	@Test
	public void testGenericActionWhereParametersInputLengthIsInValid() {
		String name = "genericAction";
		int mini = 1;
		int maxi = 2;
		Map<String, String> staticAttributes = new LinkedHashMap<>();
		Map<String, Integer> pa = new LinkedHashMap<>();
		pa.put("type", 5);
		String remainder = null;
		GenericAction action = new GenericAction(name, mini, maxi, staticAttributes, pa, remainder);
		
		assertFalse(action.execute(null, null));
	}
	@Test
	public void testGenericActionWhereRemainderIsIncorrectLength() {
		String name = "genericAction";
		int mini = 0;
		int maxi = 0;
		Map<String, String> staticAttributes = new LinkedHashMap<>();
		Map<String, Integer> pa = new LinkedHashMap<>();
		String remainder = "remainder";
		GenericAction action = new GenericAction(name, mini, maxi, staticAttributes, pa, remainder);
		
		assertFalse(action.execute(null, null));
	}
	@Test
	public void testGenericActionExecuteUsingSupport() {
		String name = "support";
		int mini = 0;
		int maxi = 0;
		Map<String, String> staticAttributes = new LinkedHashMap<>();
		staticAttributes.put("type", "support");
		Map<String, Integer> pa = new LinkedHashMap<>();
		String remainder = "text";
		GenericAction action = new GenericAction(name, mini, maxi, staticAttributes, pa, remainder);
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
			}
		};
		assertTrue(action.execute(null, "text"));
	}
	@Test
	public void testGenericActionExecuteUsingAddBuddyAction() {
		new MockStendhalClient() {
			@Override
			public void send(final RPAction action) {
				assertEquals("addbuddy", action.get("type"));
				assertEquals("schnick", action.get("target"));
			}
		};
		Map<String, String> sa = new LinkedHashMap<>();
		sa.put("type", "addbuddy");
		Map<String, Integer> pa = new LinkedHashMap<>();
		pa.put("target", 0);
		final GenericAction action = new GenericAction("addbuddy", 1,1, sa, pa, null);
		assertFalse(action.execute(null, null));
		assertTrue(action.execute(new String []{"schnick"}, null));
	}
}
