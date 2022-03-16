/***************************************************************************
 *                (C) Copyright 2003-2018 - Faiumoni e.V.                  *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameObjects;
import games.stendhal.client.gui.UserInterface;
import games.stendhal.client.gui.chatlog.EventLine;
import games.stendhal.client.sound.facade.SoundSystemFacade;
import games.stendhal.client.sound.nosound.NoSoundFacade;
import games.stendhal.common.NotificationType;
import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

public class UserTest {
	private static final String CARMEN = "Carmen";
	private EventLine eventLine;
	private User user;

	@BeforeClass
	public static void setupWorld() {
		GameObjects.createInstance(null);
	}

	@After
	public void cleanUp() {
		User.setNull();
		User.updateGroupStatus(null, null);
	}

	@Before
	public void setupSystem() {
		final SoundSystemFacade soundFacade = new NoSoundFacade();

		ClientSingletonRepository.setUserInterface(new UserInterface() {
			@Override
			public void addAchievementBox(String title, String description, String category) {
			}

			@Override
			public void addEventLine(EventLine line) {
				eventLine = line;
			}

			@Override
			public void addGameScreenText(double x, double y, String text,
				NotificationType type, boolean isTalking) {}

			@Override
			public void addGameScreenText(final Entity entity, final String text,
				final NotificationType type, final boolean isTalking) {}

			@Override
			public SoundSystemFacade getSoundSystemFacade() {
				return soundFacade;
			}
		});
	}

	private void setupUser() {
		user = new User();
		RPObject rpObject = new RPObject();
		rpObject.put("name", "test user");
		rpObject.put("level", 42);
		rpObject.put("x", 0);
		rpObject.put("y", 0);
		user.initialize(rpObject);
	}

	@Test
	public void testBuddyMessages() {
		setupUser();

		RPObject changes = new RPObject();
		changes.put("offline", CARMEN);
		user.onChangedAdded(user.getRPObject(), changes);
		assertEquals("Carmen has left Stendhal.", eventLine.getText());

		changes.remove("offline");
		changes.put("online", CARMEN);
		user.onChangedAdded(user.getRPObject(), changes);

		assertEquals("Carmen has joined Stendhal.", eventLine.getText());
	}

	@Test
	public void testGetCharacterName() {
		assertNull(User.getCharacterName());
		setupUser();
		assertEquals("test user", User.getCharacterName());
	}

	@Test
	public void testGetPlayerLevel() {
		assertEquals(0, User.getPlayerLevel());
		setupUser();
		assertEquals(42, User.getPlayerLevel());
	}

	@Test
	public void testGetServerRelease() {
		assertNull(User.getServerRelease());
		setupUser();
		user.getRPObject().put("release", "9.9.9");
		assertEquals("9.9.9", User.getServerRelease());
	}

	@Test
	public void testIgnoring() {
		assertFalse(User.isIgnoring(CARMEN));

		setupUser();
		assertFalse(User.isIgnoring(CARMEN));
		RPObject changes = new RPObject();
		changes.addSlot(new RPSlot("!ignore"));
		RPObject carmen = new RPObject();
		carmen.put("_" + CARMEN, "");
		changes.getSlot("!ignore").add(carmen);
		user.onChangedAdded(user.getRPObject(), changes);
		assertTrue(User.isIgnoring(CARMEN));

		user.onChangedRemoved(user.getRPObject(), changes);
		assertFalse(User.isIgnoring(CARMEN));
	}

	@Test
	public void testIsAdmin() {
		assertFalse(User.isAdmin());

		setupUser();
		assertFalse(User.isAdmin());
		user.getRPObject().put("adminlevel", 0);
		assertFalse(User.isAdmin());
		user.getRPObject().put("adminlevel", 599);
		assertFalse(User.isAdmin());
		user.getRPObject().put("adminlevel", 600);
		assertTrue(User.isAdmin());
		user.getRPObject().put("adminlevel", 1000);
		assertTrue(User.isAdmin());
	}

	@Test
	public void testIsGroupSharingLoot() {
		assertFalse(User.isGroupSharingLoot());
		User.updateGroupStatus(Arrays.asList("Carmen"), "false");
		assertFalse(User.isGroupSharingLoot());
		User.updateGroupStatus(Arrays.asList("Carmen"), "shared");
		assertTrue(User.isGroupSharingLoot());
	}

	@Test
	public void testIsPlayerInGroup() {
		assertFalse(User.isPlayerInGroup(CARMEN));
		User.updateGroupStatus(Arrays.asList(CARMEN), "shared");
		assertTrue(User.isPlayerInGroup(CARMEN));
	}

	@Test
	public void testNoUser() {
		assertTrue(User.isNull());
		assertNull(User.get());
	}

	@Test
	public void testOnAway() {
		setupUser();
		user.onAway("away message");
		assertEquals("You have been marked as being away.", eventLine.getText());
		user.onAway(null);
		assertEquals("You are no longer marked as being away.", eventLine.getText());
	}

	@Test
	public void testOnHealed() {
		setupUser();
		user.onHealed(5);
		assertEquals("test user heals 5 health points.", eventLine.getText());
	}

	@Test
	public void testPet() {
		setupUser();
		assertFalse(user.hasPet());
		try {
			user.getPetID();
			fail();
		} catch (IllegalArgumentException e) {
		}
		user.getRPObject().put("pet", 1);
		assertTrue(user.hasPet());
		assertEquals(1, user.getPetID());
	}

	@Test
	public void testSheep() {
		setupUser();
		assertFalse(user.hasSheep());
		try {
			user.getSheepID();
			fail();
		} catch (IllegalArgumentException e) {
		}
		user.getRPObject().put("sheep", 1);
		assertTrue(user.hasSheep());
		assertEquals(1, user.getSheepID());
	}

	@Test
	public void testSquareDistance() {
		assertEquals(Double.POSITIVE_INFINITY, User.squaredDistanceTo(3, 4), 0.001);
		setupUser();
		assertEquals(25.0, User.squaredDistanceTo(3, 4), 0.001);
	}

	@Test
	public void testUser() {
		setupUser();
		assertFalse(User.isNull());
		assertSame(user, User.get());
		assertTrue(user.isUser());
	}
}
