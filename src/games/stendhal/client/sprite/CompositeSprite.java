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

import java.awt.Graphics;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

/**
 * A sprite that merges several {@link Sprite} objects to one, and pre-renders
 * those it can so that they do not need to be composited every time the sprite
 * is drawn. 
 */
public class CompositeSprite implements Sprite {
	/**
	 * Composition status flag. <code>true</code> it the static image layers
	 * have been merged, <code>false</code> otherwise.
	 */
	private boolean composited;
	// One Sprite to rule them all...
	/** The layer Sprites making up the composite. */
	private List<Sprite> slaves;
	/** Reference object made up from the meaningful slave references */ 
	private final CompositeRef reference;
	
	/**
	 * Get a composite of at least one {@link Sprite}. Note that the result
	 * is not necessarily a CompositeSprite, but can well be one of the slave
	 * sprites if the said sprite is enough to represent the entire composite.
	 * 
	 * @param cache Cache to look up a previously stored, and storing
	 * 	newly created composites
	 * @param slaves Sprites making up the composite. The list should be
	 *	non-null and not empty. Also the sprites themselves should be non-null.
	 * @return A Sprite representing a composite of the slave Sprites
	 */
	public static Sprite getComposite(SpriteCache cache, List<Sprite> slaves) {
		ListIterator<Sprite> iter = slaves.listIterator();
		Sprite empty = null;
		Sprite previous = null;
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
			return slaves.get(0);
		default:
			// A proper composite. Return either a previously generated one,
			// or create a new and cache that
			CompositeRef ref = new CompositeRef(slaves);
			Sprite composite = cache.get(ref);
			if (composite == null) {
				composite = new CompositeSprite(slaves, ref);
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
	 * @param reference Identifier for cache lookups
	 */
	private CompositeSprite(List<Sprite> slaves, CompositeRef reference) {
		// Get a copy. The caller can modify the list
		this.slaves = new LinkedList<Sprite>(slaves);
		this.reference = reference;
	}

	public Sprite createRegion(int x, int y, int width, int height, Object ref) {
		return new TileSprite(this, x, y, width, height, ref);
	}

	public void draw(Graphics g, int x, int y) {
		if (!composited) {
			composite();
		}
		for (Sprite sprite : slaves) {
			sprite.draw(g, x, y);
		}
	}

	public void draw(Graphics g, int destx, int desty, int x, int y, int w,
			int h) {
		if (!composited) {
			composite();
		}
		for (Sprite sprite : slaves) {
			sprite.draw(g, destx, desty, x, y, w, h);
		}
	}

	public int getHeight() {
		return slaves.get(0).getHeight();
	}

	public Object getReference() {
		return reference;
	}

	public int getWidth() {
		return slaves.get(0).getWidth();
	}

	/**
	 * Merge all ImageSprite layers.
	 */
	private void composite() {
		List<Sprite> newSlaves = new LinkedList<Sprite>();
		ImageSprite floor = null;
		ListIterator<Sprite> iter = slaves.listIterator();
		boolean copied = false;
		
		// Go through the layers and merge what can be reasonably merged.
		while (iter.hasNext()) {
			Sprite current = iter.next();
			if (current instanceof ImageSprite) {
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
					current.draw(floor.getGraphics(), 0, 0);
				} else {
					floor = (ImageSprite) current;
				}
			} else {
				if (floor != null) {
					// Add the previously composited sprites to the new image
					// stack.
					newSlaves.add(floor);
					floor = null;
				}
				newSlaves.add(current);
			}
		}
		if (floor != null) {
			newSlaves.add(floor);
		}
		slaves = newSlaves;
		composited = true;
	}
	
	/**
	 * Reference object for identifying equal composite sprite stacks.
	 */
	private static class CompositeRef {
		private final Object[] refs;
		private final int hash;
		
		/**
		 * Create a new CompositeRef.
		 * 
		 * @param slaves Meaningful sprites making up the composite,
		 * 	<em>before</em> merging them.
		 */
		public CompositeRef(List<Sprite> slaves) {
			refs = new Object[slaves.size()];
			int tmphash = 0;
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
