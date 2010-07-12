package games.stendhal.client.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.util.EnumSet;
import java.util.HashMap;

import javax.swing.JComponent;

/**
 * A simple layout manager that does what BoxLayout fails to do,
 * and provides a predictable layout with few hidden interactions.
 * <p>
 * Minimum size is properly supported within reasonable limits.
 * 
 * Maximum size has only a soft support, i.e. maximum size works
 * as a limiting preferred size, even if the child component itself
 * would suggest a larger size.
 */
public class SBoxLayout implements LayoutManager, LayoutManager2 {
	/*
	 * Implementation considerations:
	 * 	- MaxSize is not fully supported. It could be done the same way as MinSize is now
	 * 
	 * 	- Expanding and contracting the components is done by same amount for all 
	 * 	the components that are resized (unless forbidden by minimum/maximum size 
	 * 	constraints). Should it be relative to the component size instead?
	 * 
	 *  - alignment is not supported, but is easy to add if needed (at least the
	 *  useful ones (left, right, center))
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
	public static int COMMON_PADDING = 5;
	
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
	private final HashMap<Component, EnumSet<SLayout>> constraints;
	
	/**
	 * The direction object. All the dimension calculations are
	 * delegated to this.
	 */
	private final Direction d;
	
	/** Preciously calculated dimension data, or <code>null</code> if it has been invalidated */ 
	private Dimension cachedMinimum, cachedMaximum, cachedPreferred;
	
	/** Amount of axially expandable components */
	private int expandable;
	
	/**
	 * Create a new SBoxLayout
	 * @param direction
	 */
	public SBoxLayout(boolean direction) {
		constraints = new HashMap<Component, EnumSet<SLayout>>();
		if (direction == VERTICAL) {
			d = verticalDirection;
		} else {
			d = horizontalDirection;
		}
	}

	public void addLayoutComponent(Component component, Object flags) {
		EnumSet<SLayout> constraintFlags = EnumSet.noneOf(SLayout.class);
		if (flags == null) {
			// nothing to add
		} else if (flags instanceof EnumSet<?>) {
			// Type checking within the rather poor limits of generics
			EnumSet<?> eflags = (EnumSet<?>) flags;
			// Translate to axial & perpendicular
			for (SLayout flag : SLayout.values()) {
				if (eflags.contains(flag)) {
					flag = d.translate(flag);
					constraintFlags.add(flag);
				}
			}
			
			if (constraintFlags.contains(SLayout.EXPAND_AXIAL)) {
				expandable++;
			}
		} else {
			throw new IllegalArgumentException("Invalid flags: " + flags);
		}
		constraints.put(component, constraintFlags);
	}
	
	public void addLayoutComponent(String id, Component component) {
		// Required by LayoutManager
	}
	
	/**
	 * Add to the primary dimension.
	 * 
	 * @param result the dimension to be expanded
	 * @param amount the expanding amount
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
	
	public float getLayoutAlignmentX(Container target) {
		// The specs don't tell what this actually should do
		return 0;
	}
	
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
	
	public void invalidateLayout(Container target) {
		cachedMinimum = null;
		cachedMaximum = null;
		cachedPreferred = null;
	}

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
				EnumSet<SLayout> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(cPref, d.getSecondary(realDim));
				}
				if ((remainingStretch > 0) && flags.contains(SLayout.EXPAND_AXIAL)) {
					// Stretch the components that allow it, if needed
					int add = Math.max(1, remainingStretch / remainingExpandable);
					addToPrimary(cPref, add);
					remainingStretch -= add;
					remainingExpandable--;
				}
				c.setBounds(startPosition.width, startPosition.height, cPref.width, cPref.height);
				shiftByPrimary(startPosition, cPref);
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
				EnumSet<SLayout> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(compSize, d.getSecondary(realDim));
				}
				
				int shrink = d.getPrimary(realDim) - d.getPrimary(compSize) - d.getPrimary(startPosition);
				if (shrink < 0) {
					// avoid < 0 sizes
					shrink = Math.max(-d.getPrimary(compSize), shrink);
					addToPrimary(compSize, shrink);
				}
				c.setBounds(startPosition.width, startPosition.height, compSize.width, compSize.height);
				shiftByPrimary(startPosition, compSize);
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
		int numSqueezable = numComponents;
		
		/*
		 * Start by trying to squeeze all, and then mark as
		 * incompressible the components whose size would become
		 * too small. Repeat until only those components that can 
		 * take it, get squeezed.
		 */
		do {
			dim = new int[numComponents];

			numSqueezable = numComponents;
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
		int sum = 0;
		for (int i = 0; i < numComponents; i++) {
			Component c = parent.getComponents()[i];
			// skip hidden components
			if (c.isVisible()) {
				Dimension cPref = getPreferred(c);
				shrink(cPref, realDim);
				EnumSet<?> flags = constraints.get(c);
				if (flags.contains(SLayout.EXPAND_PERPENDICULAR)) {
					d.setSecondary(cPref, d.getSecondary(realDim));
				}
				d.setPrimary(cPref, dim[i]);
				sum += dim[i];
				c.setBounds(startPosition.width, startPosition.height, cPref.width, cPref.height);
				shiftByPrimary(startPosition, cPref);
			}
		}
	}
	
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

		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				d.addComponentDimensions(result, c.getMaximumSize());
			}
		}
		
		// Expand by the insets
		Insets insets = parent.getInsets();
		result.width = safeAdd(result.width, insets.left + insets.right);
		result.height = safeAdd(result.height, insets.top + insets.bottom);
		
		cachedMaximum = result;
		return result;
	}
	
	public Dimension minimumLayoutSize(Container parent) {
		if (cachedMinimum != null) {
			return new Dimension(cachedMinimum);
		}
		Dimension result = new Dimension();

		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				d.addComponentDimensions(result, c.getMinimumSize());
			}
		}
		
		// Expand by the insets
		Insets insets = parent.getInsets();
		result.width += insets.left + insets.right;
		result.height += insets.top + insets.bottom;
		
		cachedMinimum = result;
		return result;
	}

	public Dimension preferredLayoutSize(Container parent) {
		if (cachedPreferred != null) {
			return new Dimension(cachedPreferred);
		}
		Dimension result = new Dimension();

		for (Component c : parent.getComponents()) {
			// Skip hidden components
			if (c.isVisible()) {
				d.addComponentDimensions(result, getPreferred(c));
			}
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
	
	public void removeLayoutComponent(Component component) {
		EnumSet<?> constr = constraints.get(component);
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
	
	private interface Direction {
		/**
		 * Translate X and Y to axial and perpendicular
		 * @param dir
		 * @return
		 */
		SLayout translate(SLayout dir);
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
	 * Horizontal direction calculations
	 */
	private static class HDirection implements Direction {
		public SLayout translate(SLayout dir) {
			if (dir == SLayout.EXPAND_X) {
				dir = SLayout.EXPAND_AXIAL;
			} else if (dir == SLayout.EXPAND_Y) {
				dir = SLayout.EXPAND_PERPENDICULAR;
			}
			
			return dir;
		}
		
		public void addComponentDimensions(Dimension result, Dimension dim) {
			// Avoid integer overflows
			result.width = safeAdd(result.width, dim.width);
			result.height = Math.max(result.height, dim.height);
		}

		public int getPrimary(Dimension dim) {
			return dim.width;
		}

		public int getSecondary(Dimension dim) {
			return dim.height;
		}

		public void setPrimary(Dimension result, int length) {
			result.width = length;		
		}

		public void setSecondary(Dimension result, int length) {
			result.height = length;
		}
	}
	
	/**
	 * Vertical dimension calculations.
	 */
	private static class VDirection implements Direction  {
		public SLayout translate(SLayout dir) {
			if (dir == SLayout.EXPAND_X) {
				dir = SLayout.EXPAND_PERPENDICULAR;
			} else if (dir == SLayout.EXPAND_Y) {
				dir = SLayout.EXPAND_AXIAL;
			}
			
			return dir;
		}
		
		public void addComponentDimensions(Dimension result, Dimension dim) {
			result.width = Math.max(result.width, dim.width);
			// Avoid integer overflows
			result.height = safeAdd(result.height, dim.height);
		}

		public int getPrimary(Dimension dim) {
			return dim.height;
		}

		public int getSecondary(Dimension dim) {
			return dim.width;
		}
		
		public void setPrimary(Dimension result, int length) {
			result.height = length;
		}

		public void setSecondary(Dimension result, int length) {
			result.width = length;
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
	 * @return A spring with preferred dimensions 0, 0.
	 */
	public static JComponent addSpring(Container target) {
		JComponent spring = new Spring();
		target.add(spring, constraint(SLayout.EXPAND_AXIAL));
		
		return spring;
	}
	
	/**
	 * A convenience method for creating a container using SBoxLayout 
	 * @return
	 */
	public static JComponent createContainer(boolean direction) {
		JComponent container = new Spring();
		container.setLayout(new SBoxLayout(direction));
		
		return container;
	}
}
