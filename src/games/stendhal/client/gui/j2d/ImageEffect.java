/***************************************************************************
 *                   (C) Copyright 2003-2012 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.j2d;

import games.stendhal.client.entity.IEntity;
import games.stendhal.client.gui.j2d.entity.Entity2DView;
import games.stendhal.client.gui.j2d.entity.helpers.HorizontalAlignment;
import games.stendhal.client.gui.j2d.entity.helpers.VerticalAlignment;
import games.stendhal.client.sprite.SequenceSprite;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;

/**
 * A graphical effect that attachs itself to an entity view, and removes itself
 * once all the animation frames have been drawn.
 */
public class ImageEffect implements SequenceSprite.SequenceSpriteListener {
	private final static String IMAGE_LOCATION = "data/sprites/effects/";

	private final Entity2DView<? extends IEntity> view;
	private final Sprite sequence;

	/**
	 * Create a new ImageEffect.
	 *
	 * @param view the view where the effect binds itself to
	 * @param image name of the image that is used for the the animation
	 */
	public ImageEffect(Entity2DView<? extends IEntity> view, String image) {
		this.view = view;
		SpriteStore store = SpriteStore.get();
		/*
		 * TODO: get the frame delays from a configuration file. Also the file
		 * should specify whether the effect must obey zone coloring.
		 */
		Sprite base = store.getSprite(IMAGE_LOCATION + image + ".png");
		int width = base.getWidth();
		int height = base.getHeight();
		int count = width / height;
		Sprite[] frames = store.getTiles(base, 0, 0, count, height, height);
		sequence = new SequenceSprite(this, frames, 100);
		view.attachSprite(sequence, HorizontalAlignment.CENTER,
				VerticalAlignment.MIDDLE, 0, 0);
	}

	@Override
	public void endSequence() {
		view.detachSprite(sequence);
	}
}
