/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2005 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
/*
 * Character.java
 *
 * Created on 19. Oktober 2005, 21:06
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.GameObjects;
import games.stendhal.client.IGameScreen;
import games.stendhal.client.entity.IEntity;
import games.stendhal.client.entity.User;
import games.stendhal.client.entity.factory.EntityFactory;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.sound.SoundSystemFacade;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Constants;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the panel where the character equipment and stats can be seen.
 * 
 * @author mtotz
 */
public class Character extends WtPanel {
	/** Height/width of slots. */
	private static final int SLOT_SIZE = 39; 

	/** Space between slots. */
	private static final int SLOT_SPACING = 3;
	
	/** Panel width. */
	private static final int PANEL_WIDTH = 3 * SLOT_SIZE + 4 * SLOT_SPACING + 4;

	/** Panel height. */
	private static final int PANEL_HEIGHT = 4 * SLOT_SIZE + 5 * SLOT_SPACING + 19;

	private final Map<String, EntitySlot> slotPanels;

	/** cached player entity. */
	private User playerEntity;

	/** the last player modification counter. */
	private long oldPlayerModificationCount;

	/**
	 * Creates a new instance of Character.
	 * 
	 * @param ui
	 *
	 * @param gameScreen 
	 * 			 The gameScreen to paint on.
	 */
	public Character(final j2DClient ui, final IGameScreen gameScreen) {
		super("character", ui.getWidth() - PANEL_WIDTH, 0, PANEL_WIDTH,
				PANEL_HEIGHT, gameScreen);

		setTitleBar(true);
		setFrame(true);
		setMovable(true);
		setMinimizeable(true);

		slotPanels = new HashMap<String, EntitySlot>();

		// now add the slots
		final SpriteStore st = SpriteStore.get();

		// Offset to center the slot holders
		final int xoff = (getClientWidth() - ((SLOT_SIZE * 3) + (SLOT_SPACING * 2))) / 2;

		slotPanels.put("head", new EntitySlot("head", st
				.getSprite("data/gui/helmet-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff, 0, gameScreen));

		slotPanels.put("armor", new EntitySlot("armor", st
				.getSprite("data/gui/armor-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1), gameScreen));

		slotPanels.put("rhand", new EntitySlot("rhand", st
				.getSprite("data/gui/weapon-slot.png"), xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10, gameScreen));

		slotPanels.put("lhand", new EntitySlot("lhand", st
				.getSprite("data/gui/shield-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10, gameScreen));

		slotPanels.put("finger", new EntitySlot("finger", st
				.getSprite("data/gui/ring-slot.png"), xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2) + 10, gameScreen));

		slotPanels.put("cloak", new EntitySlot("cloak", st
				.getSprite("data/gui/cloak-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2) + 10, gameScreen));

		slotPanels.put("legs", new EntitySlot("legs", st
				.getSprite("data/gui/legs-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2), gameScreen));

		slotPanels.put("feet", new EntitySlot("feet", st
				.getSprite("data/gui/boots-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 3), gameScreen));

		for (final EntitySlot slot : slotPanels.values()) {
			addChild(slot);
		}
	}

	/** we're using the window manager. */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/**
	 * sets the player entity.
	 * 
	 * @param userEntity
	 */
	public void setPlayer(final User userEntity) {
		this.playerEntity = userEntity;
	}

	/**
	 * refreshes the player stats and updates the text/slot panels.
	 * 
	 * @param gameScreen
	 */
	private void refreshPlayerStats(final IGameScreen gameScreen) {
		if (playerEntity == null) {
			return;
		}

		if (!playerEntity.isModified(oldPlayerModificationCount)) {
			return;
		}

		final GameObjects gameObjects = GameObjects.getInstance();

		// traverse all carrying slots

		for (final String slotName : Constants.CARRYING_SLOTS) {
			final RPSlot slot = playerEntity.getSlot(slotName);

			if (slot == null) {
				continue;
			}

			final EntitySlot entitySlot = slotPanels.get(slotName);

			if (entitySlot != null) {
				entitySlot.setParent(playerEntity);

				final Iterator<RPObject> iter = slot.iterator();

				if (iter.hasNext()) {
					final RPObject object = iter.next();

					IEntity entity = gameObjects.get(object);

					/*
					 * TODO: Remove once object mapping verified to work in all
					 * cases.
					 */
					if (entity == null) {
						entity = EntityFactory.createEntity(object);
					}

					entitySlot.setEntity(entity);
				} else {
					entitySlot.setEntity(null);
				}
			}
		}

		setTitletext(playerEntity.getName());

		oldPlayerModificationCount = playerEntity.getModificationCount();
	}

	/**
	 * Draw the panel contents. This is only called while open and not
	 * minimized.
	 * 
	 * @param g
	 *            The graphics context to draw with.
	 */
	@Override
	protected void drawContent(final Graphics2D g, final IGameScreen gameScreen) {
		refreshPlayerStats(gameScreen);

		super.drawContent(g, gameScreen);
	}

	@Override
	protected void playOpenSound() {
		SoundSystemFacade.get().getGroup("gui").play("click-6", 0, null, null, false, true);
	}
}
