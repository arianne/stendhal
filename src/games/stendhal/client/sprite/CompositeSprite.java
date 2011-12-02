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
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
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
	private Composite blend;
	private Sprite adjSprite;
	/** Reference object made up from the meaningful slave references */ 
	private final CompositeRef reference;
	private BufferedImage buffer;
	
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
			if ((blend == null) || !(loner instanceof ImageSprite)) {
				return loner;
			}
			// Needs composition for the blending anyway. Fall through! Avoid
			// modifying the original sprite by making a copy of it.
			slaves.clear();
			// Reference needs to be kept to get the composite reference right
			slaves.add(new ImageSprite(loner, loner.getReference()));
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
	 * @param reference Identifier for cache lookups
	 */
	private CompositeSprite(List<Sprite> slaves, Composite blend, Sprite adj, 
			CompositeRef reference) {
		// Get a copy. The caller can modify the list
		this.slaves = new LinkedList<Sprite>(slaves);
		this.reference = reference;
		if (blend != null) {
			this.adjSprite = adj;
			this.blend = blend;
		}
	}

	public Sprite createRegion(int x, int y, int width, int height, Object ref) {
		return new TileSprite(this, x, y, width, height, ref);
	}

	public void draw(Graphics g, int x, int y) {
		if (!composited) {
			composite();
		}
		
		if (blend == null) {
			for (Sprite sprite : slaves) {
				sprite.draw(g, x, y);
			}
		} else {
			if (buffer == null) {
				buffer = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getDefaultScreenDevice().getDefaultConfiguration()
				.createCompatibleImage(getWidth(), getHeight(), Transparency.BITMASK);
			}
			Graphics2D g2d = buffer.createGraphics();
			for (Sprite sprite : slaves) {
				sprite.draw(g2d, 0, 0);
			}
			g2d.setComposite(blend);
			adjSprite.draw(g2d, 0, 0);
			g2d.dispose();
			g.drawImage(buffer, x, y, null);
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
					Graphics g = floor.getGraphics();
					current.draw(g, 0, 0);
					g.dispose();
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
		
		// Adjustment effect
		if (blend != null) {
			// Can handle only mergeable stacks
			if (slaves.size() == 1) {
				Sprite tmp = slaves.get(0);
				if (tmp instanceof ImageSprite) {
					Graphics g = ((ImageSprite) tmp).getGraphics();
					if (g instanceof Graphics2D) {
						((Graphics2D) g).setComposite(blend);
						adjSprite.draw(g, 0, 0);
						g.dispose();
					}
					blend = null;
					adjSprite = null;
				}
			}
		}
		composited = true;
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
