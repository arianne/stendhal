/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.sprite;

import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.Logger;

/**
 * A sprite that merges several {@link Sprite} objects to one, and pre-renders
 * those it can so that they do not need to be composited every time the sprite
 * is drawn.
 */
public class CompositeSprite implements Sprite {
	private static final Logger logger = Logger.getLogger(CompositeSprite.class);

	/**
	 * Composition status flag. <code>true</code> it the static image layers
	 * have been merged, <code>false</code> otherwise.
	 */
	private boolean composited;
	// One Sprite to rule them all...
	/** The layer Sprites making up the composite. */
	private List<Sprite> slaves;
	private Composite blend;
	private Sprite adjSprite;
	/** Reference object made up from the meaningful slave references */
	private final CompositeRef reference;

	/**
	 * Get a composite of at least one {@link Sprite}. Note that the result
	 * is not necessarily a CompositeSprite, but can well be one of the slave
	 * sprites if the said sprite is enough to represent the entire composite.
	 * The composite can have also one sprite, which is composited above the
	 * others using a special blend mode.
	 *
	 * @param cache Cache to look up a previously stored, and storing
	 * 	newly created composites
	 * @param slaves Sprites making up the composite. The list should be
	 *	non-null and not empty. Also the sprites themselves should be non-null.
	 * @param blend Blend mode for the special adjustment sprite, or
	 * 	<code>null</code>, if no adjustments are wanted
	 * @param adj adjustment sprite, or <code>null</code>
	 * @return A Sprite representing a composite of the slave Sprites
	 */
	public static Sprite getComposite(SpriteCache cache, List<Sprite> slaves,
			Composite blend, Sprite adj) {
		ListIterator<Sprite> iter = slaves.listIterator();
		Sprite empty = null;
		Sprite previous = null;
		if ((blend == null) || (adj == null) || (adj instanceof EmptySprite)) {
			blend = null;
		}
		while (iter.hasNext()) {
			Sprite sprite = iter.next();
			if (sprite instanceof EmptySprite) {
				// Weed out empty sprites...
				iter.remove();
				// ...but keep one just in case we need to return an empty
				// sprite.
				if (empty == null) {
					empty = sprite;
				}
			} else if (sprite == previous) {
				// Remove duplicates as well. This works as long as we do not
				// have translucent images
				iter.remove();
			} else {
				previous = sprite;
			}
		}

		int size = slaves.size();
		switch (size) {
		case 0:
			// No contents. Return the previously saved empty sprite
			return empty;
		case 1:
			// Composite of one non empty sprite. Just return that one.
			Sprite loner = slaves.get(0);
			if (blend == null) {
				return loner;
			}
			// Needs composition for the blending anyway. Fall through!
		default:
			// A proper composite. Return either a previously generated one,
			// or create a new and cache that
			CompositeRef ref = new CompositeRef(slaves, blend, adj);
			Sprite composite = cache.get(ref);
			if (composite == null) {
				composite = new CompositeSprite(slaves, blend, adj, ref);
				cache.add(composite);
			}
			return composite;
		}
	}

	/**
	 * Create a new CompositeSprite. Meaningless slave Sprites (ie.
	 * {@link EmptySprite}s must be dropped before this to have well behaving
	 * references for all equal composites.
	 *
	 * @param slaves Non empty slave Sprites. The first is the bottom of the
	 * 	Sprite stack
	 * @param blend blending mode for the blend layer
	 * @param adj sprite for the blend layer
	 * @param reference Identifier for cache lookups
	 */
	private CompositeSprite(List<Sprite> slaves, Composite blend, Sprite adj,
			CompositeRef reference) {
		// Get a copy. The caller can modify the list
		this.slaves = new ArrayList<>(slaves);
		this.reference = reference;
		if (blend != null) {
			this.adjSprite = adj;
			this.blend = blend;
		}
	}

	@Override
	public Sprite createRegion(int x, int y, int width, int height, Object ref) {
		return new TileSprite(this, x, y, width, height, ref);
	}

	@Override
	public void draw(Graphics g, int x, int y) {
		if (!composited) {
			composite();
		}
		// Saves allocating the iterator. This gets called a lot.
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).draw(g, x, y);
		}
	}

	@Override
	public void draw(Graphics g, int destx, int desty, int x, int y, int w,
			int h) {
		if (!composited) {
			composite();
		}
		// Saves allocating the iterator. This gets called a lot.
		for (int i = 0; i < slaves.size(); i++) {
			slaves.get(i).draw(g, destx, desty, x, y, w, h);
		}
	}

	@Override
	public int getHeight() {
		return slaves.get(0).getHeight();
	}

	@Override
	public Object getReference() {
		return reference;
	}

	@Override
	public int getWidth() {
		return slaves.get(0).getWidth();
	}

	/**
	 * Merge all ImageSprite layers.
	 */
	private void composite() {
		ArrayList<Sprite> newSlaves = new ArrayList<>(slaves.size());
		ImageSprite floor = null;
		boolean copied = false;

		// Go through the layers and merge what can be reasonably merged.
		for (Sprite slave : slaves) {
			if (slave.isConstant()) {
				if (floor != null) {
					if (!copied) {
						/*
						 * Copy the image to avoid messing with the actual
						 * tiles. Deferred to this stage to avoid unnecessary
						 * copying in the case the floor does not have a static
						 * image on top of it.
						 */
						floor = new ImageSprite(floor);
						copied = true;
					}
					Graphics g = floor.getGraphics();
					slave.draw(g, 0, 0);
					g.dispose();
				} else {
					floor = new ImageSprite(slave);
					// Stacks with blends will always need a copy
					if (blend != null) {
						floor = new ImageSprite(floor);
						copied = true;
					}
				}
			} else {
				if (floor != null) {
					// Add the previously composited sprites to the new image
					// stack.
					newSlaves.add(floor);
					floor = null;
				}
				newSlaves.add(slave);
			}
		}
		if (floor != null) {
			newSlaves.add(floor);
		}
		newSlaves.trimToSize();
		slaves = newSlaves;

		// Adjustment effect
		if (blend != null) {
			// Blend individual stacks
			applyBlend(newSlaves);
			blend = null;
			adjSprite = null;
		}
		composited = true;
	}

	/**
	 * Apply blend to individual sprites of the otherwise merged sprite stack.
	 *
	 * @param stack
	 */
	private void applyBlend(List<Sprite> stack) {
		ListIterator<Sprite> iter = stack.listIterator();
		while (iter.hasNext()) {
			Sprite sprite = iter.next();
			if (sprite instanceof ImageSprite) {
				Graphics g = ((ImageSprite) sprite).getGraphics();
				if (g instanceof Graphics2D) {
					((Graphics2D) g).setComposite(blend);
					adjSprite.draw(g, 0, 0);
					g.dispose();
					/*
					 * Some JVMs with some drawing back ends treat an image that
					 * has been drawn to using special composites ever after as
					 * a pariah, and trying to draw the said image is apparently
					 * done using the slowest possible method. That the image
					 * differs in no way from others, makes no difference (A
					 * clean copy of all the data results in an image that is
					 * treated normally). That is a java bug, and the
					 * workaround is ugly waste of resources, but there seems to
					 * be no other way. There's no way to tell java that we have
					 * stopped modifying the image, and it's safe to tuck it in
					 * VRAM.
					 *
					 * Using ImageSprite for the copying is the simplest method.
					 */
					iter.set(new ImageSprite(sprite));
				}
			} else if (sprite instanceof AnimatedSprite) {
				/*
				 * Create an animated sprite made of composite sprites. The
				 * individual frames will use the usual mechanism to apply the
				 * blend only once.
				 */
				AnimatedSprite parent = ((AnimatedSprite) sprite);
				Sprite[] frames = parent.frames;
				Sprite[] newFrames = new Sprite[frames.length];
				List<Sprite> tmp = new ArrayList<>(1);
				for (int i = 0; i < frames.length; i++) {
					tmp.add(frames[i]);
					/*
					 * The frames of an animated sprite can have the same
					 * reference as the animated sprite itself, so we can not
					 * use getComposite() to obtain shared instances. Just make
					 * a new instance to avoid problems, even though it would
					 * be nice to render once the partial stacks that appear in
					 * more than one composite.
					 */
					newFrames[i] = new CompositeSprite(tmp, blend, adjSprite, null);
					tmp.clear();
				}
				iter.set(new AnimatedSprite(newFrames, parent.delays, true, null));
			} else {
				logger.error("Unhandled sprite with a blend: " + sprite);
			}
		}
	}

	/**
	 * Reference object for identifying equal composite sprite stacks.
	 */
	private static class CompositeRef {
		/** References of the slave and adjustment sprites */
		private final Object[] refs;
		/** hash code */
		private final int hash;

		/**
		 * Create a new CompositeRef.
		 *
		 * @param slaves Meaningful sprites making up the composite,
		 * 	<em>before</em> merging them.
		 * @param blend composite mode of the adjustment sprite, or
		 * 	<code>null</code>
		 * @param adj adjustment sprite, or <code>null</code>
		 */
		CompositeRef(List<Sprite> slaves, Composite blend, Sprite adj) {
			int tmphash = 0;
			if (blend == null) {
				refs = new Object[slaves.size()];
			} else {
				refs = new Object[slaves.size() + 1];
				Object ref = adj.getReference();
				refs[slaves.size()] = ref + "@" + blend.toString();
				tmphash = (ref == null) ? 42 : ref.hashCode();
			}
			for (int i = 0; i < slaves.size(); i++) {
				Object ref = slaves.get(i).getReference();
				refs[i] = ref;
				if (ref != null) {
					tmphash ^= ref.hashCode();
				}
			}
			hash = tmphash;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof CompositeRef) {
				return Arrays.equals(refs, ((CompositeRef) obj).refs);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return hash;
		}
	}
}
