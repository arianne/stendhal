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
import games.stendhal.client.StendhalClient;
import games.stendhal.client.StendhalUI;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.entity.EntityFactory;
import games.stendhal.client.entity.User;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtTextPanel;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.Level;

import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the panel where the character can be outfittet.
 * 
 * @author mtotz
 */
public class Character extends WtPanel {

	/** Panel width */
	private static final int PANEL_WIDTH = 170;

	/** Panel height */
	private static final int PANEL_HEIGHT = 285;

	/** Height/width of slots */
	private static final int SLOT_SIZE = 39; // estimate

	/** Space between slots */
	private static final int SLOT_SPACING = 3; // estimate

	/** the stats panel */
	private WtTextPanel statsPanel;

	/** the stats panel */
	private Map<String, EntitySlot> slotPanels;

	/** cached player entity */
	private User playerEntity;

	/** the money we have */
	int money;

	/** the last player modification counter */
	private long oldPlayerModificationCount;

	/** Creates a new instance of Character */
	public Character(StendhalUI ui) {
		super("character", ui.getWidth() - PANEL_WIDTH, 0, PANEL_WIDTH,
				PANEL_HEIGHT);

		StendhalClient client = ui.getClient();

		setTitleBar(true);
		setFrame(true);
		setMovable(true);
		setMinimizeable(true);

		slotPanels = new HashMap<String, EntitySlot>();

		// now add the slots
		SpriteStore st = SpriteStore.get();

		// Offset to center the slot holders
		int xoff = (getClientWidth() - ((SLOT_SIZE * 3) + (SLOT_SPACING * 2))) / 2;

		slotPanels.put("head", new EntitySlot(client, "head",
				st.getSprite("data/gui/helmet-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff, 0));

		slotPanels.put("armor", new EntitySlot(client, "armor",
				st.getSprite("data/gui/armor-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1)));

		slotPanels.put("rhand", new EntitySlot(client, "rhand",
				st.getSprite("data/gui/weapon-slot.png"), xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10));

		slotPanels.put("lhand", new EntitySlot(client, "lhand",
				st.getSprite("data/gui/shield-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10));

		slotPanels.put("finger", new EntitySlot(client, "finger",
				st.getSprite("data/gui/ring-slot.png"), xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2) + 10));

		slotPanels.put("cloak", new EntitySlot(client, "cloak",
				st.getSprite("data/gui/cloak-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2) + 10));

		slotPanels.put("legs", new EntitySlot(client, "legs",
				st.getSprite("data/gui/legs-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2)));

		slotPanels.put("feet", new EntitySlot(client, "feet",
				st.getSprite("data/gui/boots-slot.png"),
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 3)));

		for (EntitySlot slot : slotPanels.values()) {
			addChild(slot);
		}

		statsPanel = new WtTextPanel(
				"stats",
				5,
				((SLOT_SIZE + SLOT_SPACING) * 4),
				170,
				100,
				"HP: ${hp}/${maxhp}\nATK: ${atk}+${atkitem} (${atkxp})\nDEF: ${def}+${defitem} (${defxp})\nXP:${xp}\nNext Level: ${xptonextlevel}\nMoney: $${money}");
		statsPanel.setFrame(false);
		statsPanel.setTitleBar(false);
		addChild(statsPanel);
	}

	/** we're using the window manager */
	@Override
	protected boolean useWindowManager() {
		return true;
	}

	/** sets the player entity */
	public void setPlayer(User userEntity) {
		this.playerEntity = userEntity;
	}

	/** refreshes the player stats and updates the text/slot panels */
	private void refreshPlayerStats() {
		if (playerEntity == null) {
			return;
		}

		if (!playerEntity.isModified(oldPlayerModificationCount)) {
			return;
		}

		money = 0;

		GameObjects gameObjects = GameObjects.getInstance();

		// taverse all carrying slots
		String[] slotsCarrying = { "bag", "rhand", "lhand", "head", "armor",
				"legs", "feet", "finger", "cloak", "keyring" };

		for (String slotName : slotsCarrying) {
			RPSlot slot = playerEntity.getSlot(slotName);

			if (slot == null) {
				continue;
			}

			EntitySlot entitySlot = slotPanels.get(slotName);

			if (entitySlot != null) {
				entitySlot.setParent(playerEntity);

				Iterator<RPObject> iter = slot.iterator();

				if (iter.hasNext()) {
					RPObject object = iter.next();

					Entity entity = gameObjects.get(object);

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

			// count all money
			for (RPObject content : slot) {
				if (content.get("class").equals("money")
						&& content.has("quantity")) {
					money += content.getInt("quantity");
				}
			}
		}

		int atkitem = playerEntity.getAtkItem();
		int defitem = playerEntity.getDefItem();

		setTitletext(playerEntity.getName());
		statsPanel.set("hp", playerEntity.getHP());
		statsPanel.set("maxhp", playerEntity.getBase_hp());
		statsPanel.set("atk", playerEntity.getAtk());
		statsPanel.set("def", playerEntity.getDef());
		statsPanel.set("atkitem", atkitem);
		statsPanel.set("defitem", defitem);
		statsPanel.set("atkxp", playerEntity.getAtkXp());
		statsPanel.set("defxp", playerEntity.getDefXp());
		statsPanel.set("xp", playerEntity.getXp());
		int level = Level.getLevel(playerEntity.getXp());
		statsPanel.set("xptonextlevel", Level.getXP(level + 1)
				- playerEntity.getXp());
		statsPanel.set("money", money);

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
	protected void drawContent(Graphics2D g) {
		refreshPlayerStats();

		super.drawContent(g);
	}
}
