/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
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

import games.stendhal.client.GameScreen;
import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;
import games.stendhal.client.sound.SoundSystem;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import marauroa.common.Log4J;
import marauroa.common.game.AttributeNotFoundException;
import marauroa.common.game.RPObject;

import org.apache.log4j.Logger;

public class NPC extends RPEntity {
	private static final Logger logger = Log4J.getLogger(NPC.class);

	private Sprite ideaImage;

	private static Sprite eat;

	private static Sprite food;

	private static Sprite walk;

	private static Sprite follow;

	private int outfit;

	static {
		SpriteStore st = SpriteStore.get();

		eat = st.getSprite("data/sprites/ideas/eat.png");
		food = st.getSprite("data/sprites/ideas/food.png");
		walk = st.getSprite("data/sprites/ideas/walk.png");
		follow = st.getSprite("data/sprites/ideas/follow.png");
	}

	public NPC( RPObject object)
			throws AttributeNotFoundException {
		super( object);

		String type = getType();

		String name = null;
		if (object.has("name")) {
			name = object.get("name");
		} else {
			name = object.get("type");
		}

		if (type.startsWith("npc")) {
			setAudibleRange(3);
			if (name.equals("Diogenes")) {
				SoundSystem.startSoundCycle(this, "Diogenes-patrol", 10000, 20,
						50, 100);
			} else if (name.equals("Carmen")) {
				SoundSystem.startSoundCycle(this, "Carmen-patrol", 60000, 20,
						50, 75);
			} else if (name.equals("Nishiya")) {
				SoundSystem.startSoundCycle(this, "Nishiya-patrol", 40000, 20,
						50, 80);
			} else if (name.equals("Margaret")) {
				SoundSystem.startSoundCycle(this, "Margaret-patrol", 30000, 10,
						30, 70);
			} else if (name.equals("Sato")) {
				SoundSystem.startSoundCycle(this, "Sato-patrol", 60000, 30, 50,
						70);
			}
		}
	}

	@Override
	protected void buildAnimations(RPObject object) {
		SpriteStore store = SpriteStore.get();

		Sprite aspect;

		try {
			if (object.has("outfit")) {
				// This NPC's outfit is built like a player's outfit,
				// i.e. from separated graphic files for dress, hair, etc. 
				if ((outfit == object.getInt("outfit")) && (outfit != 0)) {
					// We avoid creating again the outfit if it is already
					// done.
					// Save CPU cycles.
					return;
				}
				outfit = object.getInt("outfit");
				aspect = getOutfitSprite(store, object);
			} else {
				// This NPC's outfit is read from a single file.
				aspect = store
						.getSprite(translate("npc/" + object.get("class")));
			}
		} catch (Exception e) {
			logger.error("cannot build Animations", e);
			aspect = store.getSprite(translate(object.get("class")));
		}

		sprites.put("move_up", store.getAnimatedSprite(aspect, 0, 4, 1.5, 2));
		sprites.put("move_right", store
						.getAnimatedSprite(aspect, 1, 4, 1.5, 2));
		sprites.put("move_down", store.getAnimatedSprite(aspect, 2, 4, 1.5, 2));
		sprites.put("move_left", store.getAnimatedSprite(aspect, 3, 4, 1.5, 2));

		sprites.get("move_up")[3] = sprites.get("move_up")[1];
		sprites.get("move_right")[3] = sprites.get("move_right")[1];
		sprites.get("move_down")[3] = sprites.get("move_down")[1];
		sprites.get("move_left")[3] = sprites.get("move_left")[1];
	}

	@Override
	public void onChangedAdded(RPObject base, RPObject diff)
			throws AttributeNotFoundException {
		super.onChangedAdded(base, diff);

		if (diff.has("idea")) {
			String idea = diff.get("idea");
			if (idea.equals("eat")) {
				ideaImage = eat;
			} else if (idea.equals("food")) {
				ideaImage = food;
			} else if (idea.equals("walk")) {
				ideaImage = walk;
			} else if (idea.equals("follow")) {
				ideaImage = follow;
			}
		}
	}

	@Override
	public Rectangle2D getArea() {
		return new Rectangle.Double(x, y + 1, 1, 1);
	}

	@Override
	public Rectangle2D getDrawedArea() {
		return new Rectangle.Double(x, y, 1.5, 2);
	}

	@Override
	public void draw(GameScreen screen) {
		super.draw(screen);

		if (ideaImage != null) {
			Rectangle2D rect = getArea();
			double sx = rect.getMaxX();
			double sy = rect.getY();
			screen.draw(ideaImage, sx - 0.25, sy - 0.25);
		}
	}
}
