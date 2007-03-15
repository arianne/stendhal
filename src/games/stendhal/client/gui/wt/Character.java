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
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.StendhalClient;
import games.stendhal.client.entity.Player;
import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.wt.core.WtPanel;
import games.stendhal.client.gui.wt.core.WtTextPanel;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Arrays;

import marauroa.common.game.RPObject;
import marauroa.common.game.RPSlot;

/**
 * This is the panel where the character can be outfittet.
 * 
 * @author mtotz
 */
public class Character extends WtPanel {
	/** Panel width */
	private static final int PANEL_WIDTH	= 144;

	/** Panel height */
	private static final int PANEL_HEIGHT	= 280;

	/** Height/width of slots */
	private static final int SLOT_SIZE	= 39;	// estimate

	/** Space between slots */
	private static final int SLOT_SPACING	= 3;	// estimate

	/** the stats panel */
	private WtTextPanel statsPanel;

	/** the stats panel */
	private Map<String, EntitySlot> slotPanels;

	/** cached player entity */
	private Player playerEntity;

	/** the money we have */
	int money;

	/** the last player modification counter */
	private long oldPlayerModificationCount;

	/** TODO: remove after next release */
	private boolean usedCompatibilityCode = false;

	/** Creates a new instance of Character */
	public Character(GameObjects gameObjects) {
		super("character", j2DClient.SCREEN_WIDTH - PANEL_WIDTH, 0, PANEL_WIDTH, PANEL_HEIGHT);
		setTitleBar(true);
		setFrame(true);
		setMoveable(true);
		setMinimizeable(true);

		slotPanels = new HashMap<String, EntitySlot>();

		// now add the slots
		SpriteStore st = SpriteStore.get();
		Sprite slotSprite = st.getSprite("data/gui/slot.png");

		// Offset to center the slot holders
		int xoff = (getClientWidth() - ((SLOT_SIZE * 3) + (SLOT_SPACING * 2))) / 2;

		slotPanels.put("head",
			new EntitySlot("head", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				0,
				gameObjects));

		slotPanels.put("armor",
			new EntitySlot("armor", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1),
				gameObjects));

		slotPanels.put("rhand",
			new EntitySlot("rhand", slotSprite,
				xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10,
				gameObjects));

		slotPanels.put("lhand",
			new EntitySlot("lhand", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 1) + 10,
				gameObjects));

		slotPanels.put("cloak",
			new EntitySlot("cloak", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 2) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2) + 10,
				gameObjects));

		slotPanels.put("legs",
			new EntitySlot("legs", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 2),
				gameObjects));

		slotPanels.put("feet",
			new EntitySlot("feet", slotSprite,
				((SLOT_SIZE + SLOT_SPACING) * 1) + xoff,
				((SLOT_SIZE + SLOT_SPACING) * 3),
				gameObjects));

		for (EntitySlot slot : slotPanels.values()) {
			addChild(slot);
		}

		statsPanel = new WtTextPanel(
				"stats",
				5,
				((SLOT_SIZE + SLOT_SPACING) * 4),
				170,
				100,
				"HP: ${hp}/${maxhp}\nMana: ${mana}/${basemana}\nATK: ${atk}(+${atkitem}) (${atkxp})\nDEF: ${def}(+${defitem}) (${defxp})\nXP:${xp}\nMoney: $${money}");
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
	public void setPlayer(Player playerEntity) {
		this.playerEntity = playerEntity;
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

		List<String> checkedItems = new LinkedList<String>();

		// taverse all carrying slots
		String [] slotsCarrying = {
			"bag", "rhand", "lhand", "head", "armor",
			"legs", "feet", "cloak"
		};

		for(String slotName : slotsCarrying) {
			RPSlot slot = playerEntity.getSlot(slotName);

			if(slot == null) {
				continue;
			}

                        EntitySlot entitySlot = slotPanels.get(slotName);

			if (entitySlot != null) {
				entitySlot.clear();
				entitySlot.setParent(playerEntity);
				// found a gui element for this slot
				for (RPObject content : slot) {
					entitySlot.add(content);
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

		// TODO: Remove this code after next release
		if ((atkitem < 0) || (defitem < 0)) {
			atkitem = 0;
			defitem = 0;
			for (RPSlot slot : playerEntity.getSlots()) {
				for (RPObject content : slot) {
					if (!slot.getName().equals("bag") && !slot.getName().startsWith("!")) {
						final List<String> weapons = Arrays.asList("sword", "axe",
								"club", "ranged", "projectiles");
						final List<String> defense = Arrays.asList("shield",
								"armor", "helmet", "legs", "boots", "cloak");
	
						if (weapons.contains(content.get("class"))
								&& !checkedItems.contains(content.get("class"))) {
							atkitem += content.getInt("atk");
							checkedItems.add(content.get("class"));
						}
						if (defense.contains(content.get("class"))
								&& !checkedItems.contains(content.get("class"))) {
							defitem += content.getInt("def");
							checkedItems.add(content.get("class"));
						}
					}
				}
			}
			if (!usedCompatibilityCode ) {
				StendhalClient.get().addEventLine(
					"Client is newer than Server: Using compatibility code for atkitem and defitem calculation.", Color.RED);
				usedCompatibilityCode = true;
			}
		}
		// TODO: Remove-Me End
		
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
		statsPanel.set("money", money);
                //statsPanel.set("mana", "Not Available"); 
                statsPanel.set("mana", Integer.toString(playerEntity.getMana()));
                statsPanel.set("basemana", playerEntity.getBaseMana());
                

		oldPlayerModificationCount = playerEntity.getModificationCount();
	}

	/** refreshes the player stats and draws them */
	@Override
	public Graphics draw(Graphics g) {
		if(isClosed()) {
			return g;
		}

		refreshPlayerStats();
		return super.draw(g);
	}
}
