/* $Id$ */
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
package games.stendhal.client.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.swing.JComponent;

/**
 * A simple layout manager that does what BoxLayout fails to do,
 * and provides a predictable layout with few hidden interactions.
 * <p>
 * Minimum size is properly supported within reasonable limits.
 * <p>
 * Maximum size has only a soft support, i.e. maximum size works
 * as a limiting preferred size, even if the child component itself
 * would suggest a larger size.
 * <p>
 * Component alignment is supported in the direction perpendicular to the
 * layout direction.
 * <p>
 * SBoxLayout supports constraints of type {@link SLayout}. A single constraint
 * flag can passed as the second parameter to
 * <code>Container.add(Component c, Object constraint)</code>. Constraints can
 * be combined using the object returned by {@link #constraint(SLayout...)} as
 * the constraints object.
 */
public class SBoxLayout implements LayoutManager2 {
	/*
	 * Implementation considerations:
	 * 	- MaxSize is not fully supported. It could be done the same way as MinSize is now
	 *
	 * 	- Expanding and contracting the components is done by same amount for all
	 * 	the components that are resized (unless forbidden by minimum/maximum size
	 * 	constraints). Should it be relative to the component size instead?
	 *
	 * Further refinement:
	 * 	Layout management in swing is dumb (and not just buggy for the fundamental
	 * 	layout managers). Any change in a child component will result in revalidation
	 * 	of the whole component tree until the first validation root (which normally
	 *	is the top level window). This is a huge performance problem as changing a
	 *	single number in the StatsPanel will result in the whole window layout being
	 *	redone - and 3 changes in a second is normal.
	 *		Therefore, investigate if it's feasible to make a component that acts as
	 *	a validation root, and passes the invalidation upwards only if its size changes.
	 */

	public static final boolean VERTICAL = false;
	public static final boolean HORIZONTAL = true;

	/** Common padding width where padding or border is wanted. */
	public static final int COMMON_PADDING = 5;

	/**
	 * Create a constraints object.
	 *
	 * @param flags constraint flags
	 * @return constraints object
	 */
	public static Object constraint(SLayout ... flags) {
		EnumSet<SLayout> obj = EnumSet.noneOf(SLayout.class);
		for (SLayout flag : flags) {
			obj.add(flag);
		}
		return obj;
	}

	private static final Direction horizontalDirection = new HDirection();
	private static final Direction verticalDirection = new VDirection();

	/**
	 * Layout constraints of the child components.
	 */
	private final Map<Component, EnumSet<SLayout>> constraints;

	/**
	 * The direction object. All the dimension calculations are
	 * delegated to this.
	 */
	private final Direction d;

	/** Preciously calculated dimension data, or <code>null</code> if it has been invalidated. */
	private Dimension cachedMinimum, cachedMaximum, cachedPreferred;

	/** Amount of axially expandable components. */
	private int expandable;
	/** Amount of padding between components. */
	private int padding;

	/**
	 * Create a new SBoxLayout.
	 *
	 * @param direction layout direction
	 */
	public SBoxLayout(boolean direction) {
		constraints = new IdentityHashMap<>();
		if (direction == VERTICAL) {
			d = verticalDirection;
		} else {
			d = horizontalDirection;
		}
	}

	/**
	 * Create a new SBoxLayout with padding between components.
	 *
	 * @param direction layout direction
	 * @param padding component padding in pixels
	 */
	public SBoxLayout(boolean direction, int padding) {
		this(direction);
		setPadding(padding);
	}

	/**
	 * Set the padding between the components. Typically you should use either
	 * 0 (the default), or COMMON_PADDING for consistent look. For the padding
	 * around everything use appropriate empty border instead.
	 *
	 * @param padding pixel width of the padding
	 */
	public final void setPadding(int padding) {
		this.padding = padding;
	}

	@Override
	public void addLayoutComponent(Component component, Object flags) {
		EnumSet<SLayout> constraintFlags = EnumSet.noneOf(SLayout.class);
		if (flags != null) {
			if (flags instanceof SLayout) {
				constraintFlags.add(d.translate((SLayout) flags));
			} else if (flags instanceof EnumSet<?>) {
				translateFlags((EnumSet<?>) flags, constraintFlags);
			} else {
				throw new IllegalArgumentException("Invalid flags: " + flags);
			}

			// Keep count of expandable items
			if (constraintFlags.contains(SLayout.EXPAND_AXIAL)) {
				expandable++;
			}
		}
		constraints.put(component, constraintFlags);
	}

	@SuppressWarnings("unlikely-arg-type")
	private void translateFlags(EnumSet<?> rawFlags, EnumSet<SLayout> constraintFlags) {
		Arrays.stream(SLayout.values()).filter(rawFlags::contains).map(d::translate).forEach(constraintFlags::add);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
	 */
	@Override
	public void addLayoutComponent(String id, Component component) {
	}

	/**
	 * Add to the primary dimension.
	 *
	 * @param result the dimension to be expanded
	 * @param length the expanding amount
	 */
	private void addToPrimary(Dimension result, int length) {
		d.setPrimary(result, d.getPrimary(result) + length);
	}

	/**
	 * Expand a <code>Dimension</code> so that it can include
	 * another <code>Dimension</code>.
	 *
	 * @param result The dimension to be expanded
	 * @param dim limiting dimension
	 */
	private void expand(Dimension result, Dimension dim) {
		result.width = Math.max(result.width, dim.width);
		result.height = Math.max(result.height, dim.height);
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
	 */
	@Override
	public float getLayoutAlignmentX(Container target) {
		// The specs don't tell what this actually should do
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
	 */
	@Override
	public float getLayoutAlignmentY(Container target) {
		// The specs don't tell what this actually should do
		return 0;
	}

	/**
	 * Get a components preferred dimensions restricted by
	 * the maximum and minimum constraints.
	 *
	 * @param comp the component to examine
	 * @return constraint adjusted preferred dimensions
	 */
	private Dimension getPreferred(Component comp) {
		Dimension dim = comp.getPreferredSize();

		expand(dim, comp.getMinimumSize());
		shrink(dim, comp.getMaximumSize());

		return dim;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
	 */
	@Override
	public void invalidateLayout(Container target) {
		cachedMinimum = null;
		cachedMaximum = null;
		cachedPreferred = null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	@Override
	public void layoutContainer(Container parent) {
		// Maximum dimensions available for use
		Dimension realDim = parent.getSize();
		Insets insets = parent.getInsets();

		Dimension preferred = preferredLayoutSize(parent);

		final int stretch = d.getPrimary(realDim) - d.getPrimary(preferred);

		// remove the insets for the actual area we have in use
		realDim.width -= insets.left + insets.right;
		realDim.height -= insets.top + insets.bottom;

		Dimension position = new Dimension(insets.left, insets.top);

		// Check the conditions, and pass the task to the proper layout method
		if (stretch >= 0) {
			layoutSufficientSpace(parent, realDim, position, stretch);
		} else {
			Dimension minDim = minimumLayoutSize(parent);
			// remove the insets for the actual area we have in use
			minDim.width -= insets.left + insets.right;
			minDim.height -= insets.top + insets.bottom;
			final int squeeze = d.getPrimary(realDim) - d.getPrimary(minDim);
			if (squeeze < 0) {
				layoutUnderMinimumSpace(parent, realDim, position);
			} else {
				layoutWithSqueeze(parent, realDim, position, stretch);
			}
		}
	}

	/**
	 * Lay out the components, when we have sufficient space to do so.
	 *
	 * @param parent the parent container of the components
	 * @param realDim the dimensions of the space in use
	 * @param startPosition top left corner
	 * @param stretch the total amount to stretch the components
	 */
	private void layoutSufficientSpace(Container parent, Dimension realDim,
			Dimension startPosition, int stretch) {
		int remainingStretch = stretch;
		int remainingExpandable = expandable;

		// Easy - we got at least the dimensions we asked for
		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				Dimension cPref = getPreferred(c);
				shrink(cPref, realDim);
				int xAlign = 0;
				int yAlign = 0;

				EnumSet<SLayout> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(cPref, d.getSecondary(realDim));
				} else {
					xAlign = getXAlignment(c, realDim);
					yAlign = getYAlignment(c, realDim);
				}

				if ((remainingStretch > 0) && flags.contains(SLayout.EXPAND_AXIAL)) {
					// Stretch the components that allow it, if needed
					int add = Math.max(1, remainingStretch / remainingExpandable);
					addToPrimary(cPref, add);
					remainingStretch -= add;
					remainingExpandable--;
				}
				c.setBounds(startPosition.width + xAlign, startPosition.height + yAlign, cPref.width, cPref.height);

				// Move the coordinates of the next component by the size of the
				// previous + padding
				shiftByPrimary(startPosition, cPref);
				addToPrimary(startPosition, padding);
			}
		}
	}

	/**
	 * Lay out the components in a smaller space than the specified minimum.
	 * Just gives the components their required minimum, until the space runs out.
	 *
	 * @param parent the parent container of the components
	 * @param realDim the dimensions of the space in use
	 * @param startPosition top left corner
	 */
	private void layoutUnderMinimumSpace(Container parent, Dimension realDim, Dimension startPosition) {
		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				Dimension compSize = c.getMinimumSize();
				shrink(compSize, realDim);
				int xAlign = 0;
				int yAlign = 0;

				EnumSet<SLayout> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(compSize, d.getSecondary(realDim));
				} else {
					xAlign = getXAlignment(c, realDim);
					yAlign = getYAlignment(c, realDim);
				}

				int shrink = d.getPrimary(realDim) - d.getPrimary(compSize) - d.getPrimary(startPosition);
				if (shrink < 0) {
					// avoid < 0 sizes
					shrink = Math.max(-d.getPrimary(compSize), shrink);
					addToPrimary(compSize, shrink);
				}
				c.setBounds(startPosition.width + xAlign, startPosition.height + yAlign, compSize.width, compSize.height);

				// Move the coordinates of the next component by the size of the
				// previous + padding
				shiftByPrimary(startPosition, compSize);
				addToPrimary(startPosition, padding);
			}
		}
	}

	/**
	 * Lay out in sufficient, but smaller space than preferred.
	 * Respects the minimum dimensions, and squeezes only the components
	 * that do not break that constraint.
	 *
	 * @param parent the parent container of the components
	 * @param realDim the dimensions of the space in use
	 * @param startPosition top left corner
	 * @param stretch the total amount to stretch the components (negative)
	 */
	private void layoutWithSqueeze(Container parent, Dimension realDim,
			Dimension startPosition, int stretch) {
		/*
		 * We can squeeze the components without violating the constraints,
		 * but calculations take a bit of effort.
		 */
		int numComponents = parent.getComponents().length;
		int[] dim;
		boolean[] violations = new boolean[numComponents];
		int numViolations = 0;

		// Only visible components can be squeezed
		int numVisible = 0;
		for (Component c : parent.getComponents()) {
			if (c.isVisible()) {
				numVisible++;
			}
		}

		int numSqueezable;
		/*
		 * Start by trying to squeeze all, and then mark as
		 * incompressible the components whose size would become
		 * too small. Repeat until only those components that can
		 * take it, get squeezed.
		 */
		do {
			dim = new int[numComponents];

			numSqueezable = numVisible;
			for (boolean b : violations) {
				if (b) {
					numSqueezable--;
				}
			}
			int remainingSqueeze = -stretch;
			numViolations = 0;

			for (int i = 0; i < numComponents; i++) {
				Component c = parent.getComponents()[i];

				if (c.isVisible()) {
					Dimension cPref = getPreferred(c);

					int adjust = 0;
					if (remainingSqueeze > 0 && !violations[i]) {
						adjust = Math.max(1, remainingSqueeze / numSqueezable);
						remainingSqueeze -= adjust;
						numSqueezable--;
					}
					dim[i] = d.getPrimary(cPref) - adjust;
					if (dim[i] < d.getPrimary(c.getMinimumSize())) {
						violations[i] = true;
						numViolations++;
					}
				} else {
					dim[i] = 0;
				}
			}
		} while (numViolations != 0);

		// Done with the dimensions, now lay it out
		for (int i = 0; i < numComponents; i++) {
			Component c = parent.getComponents()[i];
			// skip hidden components
			if (c.isVisible()) {
				Dimension cPref = getPreferred(c);
				shrink(cPref, realDim);
				int xAlign = 0;
				int yAlign = 0;

				EnumSet<SLayout> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(cPref, d.getSecondary(realDim));
				} else {
					xAlign = getXAlignment(c, realDim);
					yAlign = getYAlignment(c, realDim);
				}
				d.setPrimary(cPref, dim[i]);
				c.setBounds(startPosition.width + xAlign, startPosition.height + yAlign, cPref.width, cPref.height);

				// Move the coordinates of the next component by the size of the
				// previous + padding
				shiftByPrimary(startPosition, cPref);
				addToPrimary(startPosition, padding);
			}
		}
	}

	/**
	 * Get the x alignment of a child component.
	 *
	 * @param c component
	 * @param available available space
	 * @return x alignment in pixels
	 */
	private int getXAlignment(Component c, Dimension available) {
		if (d == horizontalDirection) {
			return 0;
		} else {
			return getPerpendicularAlignment(c, available);
		}
	}

	/**
	 * Get the y alignment of a child component.
	 *
	 * @param c component
	 * @param available available space
	 * @return y alignment in pixels
	 */
	private int getYAlignment(Component c, Dimension available) {
		if (d == horizontalDirection) {
			return getPerpendicularAlignment(c, available);
		} else {
			return 0;
		}
	}

	/**
	 * Get the pixel alignment of a component in the perpendicular direction.
	 *
	 * @param c component
	 * @param available size of the container of the component
	 * @return pixel alignment
	 */
	private int getPerpendicularAlignment(Component c, Dimension available) {
		int align = 0;
		int extra = d.getSecondary(available) - d.getSecondary(c.getPreferredSize());
		if (extra > 0) {
			align = (int) (extra * d.getComponentAlignment(c));
		}

		return align;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension maximumLayoutSize(Container parent) {
		/*
		 * The specs are *very* vague about what this should do (and
		 * helpfully name the parameter "target", sigh), but returning
		 * the max size of the whole layout seems to be what's wanted,
		 * and most other tries crash in a way or another.
		 */
		if (cachedMaximum != null) {
			return new Dimension(cachedMaximum);
		}

		Dimension result = new Dimension();

		int numVisible = 0;
		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				numVisible++;
				d.addComponentDimensions(result, c.getMaximumSize());
			}
		}

		// Take padding in account
		if (numVisible > 1) {
			d.setPrimary(result, safeAdd(d.getPrimary(result), (numVisible - 1) * padding));
		}

		// Expand by the insets
		Insets insets = parent.getInsets();
		result.width = safeAdd(result.width, insets.left + insets.right);
		result.height = safeAdd(result.height, insets.top + insets.bottom);

		cachedMaximum = result;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension minimumLayoutSize(Container parent) {
		if (cachedMinimum != null) {
			return new Dimension(cachedMinimum);
		}
		Dimension result = new Dimension();

		int numVisible = 0;
		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				numVisible++;
				d.addComponentDimensions(result, c.getMinimumSize());
			}
		}

		// Take padding in account
		if (numVisible > 1) {
			d.setPrimary(result, safeAdd(d.getPrimary(result), (numVisible - 1) * padding));
		}

		// Expand by the insets
		Insets insets = parent.getInsets();
		result.width += insets.left + insets.right;
		result.height += insets.top + insets.bottom;

		cachedMinimum = result;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	@Override
	public Dimension preferredLayoutSize(Container parent) {
		if (cachedPreferred != null) {
			return new Dimension(cachedPreferred);
		}
		Dimension result = new Dimension();

		int numVisible = 0;
		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				numVisible++;
				d.addComponentDimensions(result, getPreferred(c));
			}
		}

		// Take padding in account
		if (numVisible > 1) {
			d.setPrimary(result, safeAdd(d.getPrimary(result), (numVisible - 1) * padding));
		}

		// Expand by the insets
		Insets insets = parent.getInsets();
		result.width = safeAdd(result.width, insets.left + insets.right);
		result.height = safeAdd(result.height, insets.top + insets.bottom);

		/*
		 * Check the constraints of the parent. Unlike the standard layout
		 * managers we should never suggest sizes outside the range that
		 * the parent should do.
		 */
		Dimension maxDim = parent.getMaximumSize();
		Dimension minDim = parent.getMinimumSize();

		/*
		 *  Despite what said above, the return value can still be smaller
		 *  than the specified minimum values, if the user has set
		 *  inconsistent minimum and maximum constraints.
		 */
		expand(result, minDim);
		shrink(result, maxDim);

		cachedPreferred = result;
		return result;
	}

	/*
	 * (non-Javadoc)
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	@Override
	public void removeLayoutComponent(Component component) {
		EnumSet<SLayout> constr = constraints.get(component);
		if (constr.contains(SLayout.EXPAND_AXIAL)) {
			expandable--;
		}

		constraints.remove(component);
	}

	/**
	 * Expand a dimension by the primary dimension of another.
	 *
	 * @param result the dimension to be expanded
	 * @param dim the expanding dimension
	 */
	private void shiftByPrimary(Dimension result, Dimension dim) {
		addToPrimary(result, d.getPrimary(dim));
	}

	/**
	 * Shrink a <code>Dimension</code> so that it does not exceed
	 * the limits of another.
	 *
	 * @param result The dimension to be shrunk
	 * @param dim limiting dimension
	 */
	private void shrink(Dimension result, Dimension dim) {
		result.width = Math.min(result.width, dim.width);
		result.height = Math.min(result.height, dim.height);
	}

	/**
	 * A safe addition for dimensions. Returns Integer.MAX_VALUE if the
	 * addition would overflow. Some components do set that to their maximum
	 * size so they'd overflow if there are other components or insets.
	 *
	 * @param a
	 * @param b
	 * @return sum of a and b, or Integer.MAX_VALUE
	 */
	private static int safeAdd(int a, int b) {
		int tmp = a + b;
		if (tmp >= 0) {
			return tmp;
		} else {
			return Integer.MAX_VALUE;
		}
	}

	/**
	 * An abstraction for various direction dependent operations.
	 */
	private interface Direction {
		/**
		 * Translate X and Y to axial and perpendicular.
		 * @param dir
		 * @return SLayout
		 */
		SLayout translate(SLayout dir);

		/**
		 * Get the alignment of the component perpendicular to the layout axis.
		 *
		 * @param component component to examine
		 * @return component alignment
		 */
		float getComponentAlignment(Component component);

		/**
		 * Expand a dimension by a component's dimensions.
		 *
		 * @param result the dimension to be expanded
		 * @param dim the dimensions to be added
		 */
		void addComponentDimensions(Dimension result, Dimension dim);

		/**
		 * Set primary dimension.
		 *
		 * @param result the dimension to be modified
		 * @param length
		 */
		void setPrimary(Dimension result, int length);

		/**
		 * Set secondary dimension.
		 *
		 * @param result the dimension to be modified
		 * @param length
		 */
		void setSecondary(Dimension result, int length);

		/**
		 * Get the dimension along the layout axis.
		 *
		 * @param dim
		 * @return primary dimension
		 */
		int getPrimary(Dimension dim);

		/**
		 * Get the dimension perpendicular to the layout axis.
		 *
		 * @param dim
		 * @return secondary dimension
		 */
		int getSecondary(Dimension dim);
	}

	/**
	 * Horizontal direction calculations.
	 */
	private static class HDirection implements Direction {
		@Override
		public SLayout translate(SLayout dir) {
			if (dir == SLayout.EXPAND_X) {
				dir = SLayout.EXPAND_AXIAL;
			} else if (dir == SLayout.EXPAND_Y) {
				dir = SLayout.EXPAND_PERPENDICULAR;
			}

			return dir;
		}

		@Override
		public void addComponentDimensions(Dimension result, Dimension dim) {
			// Avoid integer overflows
			result.width = safeAdd(result.width, dim.width);
			result.height = Math.max(result.height, dim.height);
		}

		@Override
		public int getPrimary(Dimension dim) {
			return dim.width;
		}

		@Override
		public int getSecondary(Dimension dim) {
			return dim.height;
		}

		@Override
		public void setPrimary(Dimension result, int length) {
			result.width = length;
		}

		@Override
		public void setSecondary(Dimension result, int length) {
			result.height = length;
		}

		@Override
		public float getComponentAlignment(Component component) {
			return component.getAlignmentY();
		}
	}

	/**
	 * Vertical dimension calculations.
	 */
	private static class VDirection implements Direction  {
		@Override
		public SLayout translate(SLayout dir) {
			if (dir == SLayout.EXPAND_X) {
				dir = SLayout.EXPAND_PERPENDICULAR;
			} else if (dir == SLayout.EXPAND_Y) {
				dir = SLayout.EXPAND_AXIAL;
			}

			return dir;
		}

		@Override
		public void addComponentDimensions(Dimension result, Dimension dim) {
			result.width = Math.max(result.width, dim.width);
			// Avoid integer overflows
			result.height = safeAdd(result.height, dim.height);
		}

		@Override
		public int getPrimary(Dimension dim) {
			return dim.height;
		}

		@Override
		public int getSecondary(Dimension dim) {
			return dim.width;
		}

		@Override
		public void setPrimary(Dimension result, int length) {
			result.height = length;
		}

		@Override
		public void setSecondary(Dimension result, int length) {
			result.width = length;
		}

		@Override
		public float getComponentAlignment(Component component) {
			return component.getAlignmentX();
		}
	}

	/**
	 * An utility component for layout.
	 */
	private static class Spring extends JComponent {
	}

	/**
	 * Add a utility component that expands by default, to a container using
	 * SBoxLayout. Adding it rather than just creating the component is a
	 * workaround for components not passing information about new subcomponents
	 * if the user explicitly specifies the constraints.
	 *
	 * @param target the container where to add a string to
	 * @return A spring with preferred dimensions 0, 0.
	 */
	public static JComponent addSpring(Container target) {
		JComponent spring = new Spring();
		target.add(spring, SLayout.EXPAND_AXIAL);

		return spring;
	}

	/**
	 * A convenience method for creating a container using SBoxLayout.
	 *
	 * @param direction layout direction
	 * @return A component using SBoxLayout
	 */
	public static JComponent createContainer(boolean direction) {
		JComponent container = new Spring();
		container.setLayout(new SBoxLayout(direction));

		return container;
	}

	/**
	 * A convenience method for creating a container using SBoxLayout with
	 * padding between the components.
	 *
	 * @param direction layout direction
	 * @param padding padding in pixels between the components
	 * @return A component using SBoxLayout
	 */
	public static JComponent createContainer(boolean direction, int padding) {
		JComponent container = new Spring();
		container.setLayout(new SBoxLayout(direction, padding));

		return container;
	}
}
