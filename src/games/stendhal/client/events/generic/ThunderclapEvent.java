/***************************************************************************
 *                    Copyright © 2024 - Faiumoni e. V.                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.events.generic;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.util.Timer;
import java.util.TimerTask;

import games.stendhal.client.ClientSingletonRepository;
import games.stendhal.client.GameScreen;
import games.stendhal.client.GameScreen.SceneCompleteRunnable;
import games.stendhal.client.entity.Entity;
import games.stendhal.client.sound.ConfiguredSounds;
import games.stendhal.client.sprite.Sprite;
import games.stendhal.client.sprite.SpriteStore;
import games.stendhal.common.constants.SoundID;
import games.stendhal.common.constants.SoundLayer;


/**
 * Creates a thunder and lightning effect.
 */
public class ThunderclapEvent extends SubEvent {

	/** Thunder image. */
	private static final Sprite image = SpriteStore.get().getSprite("data/maps/effect/lightning.png");

	/** Determines if screen should flash white. */
	private final boolean flash;
	/** Determines if lightning bolt should be drawn. */
	private final boolean lightning;
	/** Time that event execution began. */
	private long startTime;

	/** Width of viewport for drawing flash. */
	private int width;
	/** Height of viewport for drawing flash. */
	private int height;


	/**
	 * Creates a new thunderclap event.
	 *
	 * @param flags
	 *   List of enabled flags.
	 */
	public ThunderclapEvent(final String[] flags) {
		super(flags);
		flash = !flagEnabled("no-flash");
		lightning = !flagEnabled("no-lightning");
		startTime = 0;

		final GameScreen screen = GameScreen.get();
		width = screen.getWidth();
		height = screen.getHeight();
	}

	@Override
	public void execute(final Entity entity) {
		startTime = System.currentTimeMillis();
		// thunder sound
		ClientSingletonRepository.getSound().playLocalizedEffect(ConfiguredSounds.get(
				SoundID.THUNDERCLAP), (int) entity.getX(), (int) entity.getY(), SoundLayer.FIGHTING_NOISE);
		if (flash || lightning) {
			final GameScreen viewport = GameScreen.get();
			viewport.onSceneComplete = new SceneCompleteRunnable() {
				@Override
				public void run(final Graphics2D ctx, final int offsetX, final int offsetY) {
					drawLightning(ctx, offsetX, offsetY);
				}
			};
			new Timer().schedule(new TimerTask() {
				@Override
				public void run() {
					viewport.onSceneComplete = null;
				}
			}, 300L);
		}
	}

	/**
	 * Draws a lightning effect on the viewport.
	 *
	 * @param ctx
	 *   Viewport drawing canvas.
	 * @param offsetX
	 *   Canvas horizontal offset.
	 * @param offsetY
	 *   Canvas vertical offset.
	 */
	private void drawLightning(final Graphics2D ctx, final int offsetX, final int offsetY) {
		final int timeDiff = (int) (System.currentTimeMillis() - startTime);
		if (flash) {
			final Composite savedComposite = ctx.getComposite();
			if (timeDiff <= 100 || timeDiff > 200) {
				ctx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			} else {
				ctx.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
			}
			ctx.setColor(Color.WHITE);
			ctx.fillRect(offsetX, offsetY, width, height);
			// restore composite info to make lightning opaque
			ctx.setComposite(savedComposite);
		}
		if (lightning) {
			image.draw(ctx, offsetX, offsetY);
		}
	}
}
