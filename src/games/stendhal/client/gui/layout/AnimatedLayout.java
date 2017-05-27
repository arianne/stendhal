/***************************************************************************
 *                (C) Copyright 2003-2015 - Faiumoni e.V.                  *
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
 * Inspired by MadProgrammer's example at
 * https://stackoverflow.com/a/14541651/2471439
 */
package games.stendhal.client.gui.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.Timer;

/**
 * A forwarding layout manager that uses smooth animations for layout changes.
 */
public class AnimatedLayout implements LayoutManager2 {
	/** The layout manager used to determine the desired final layout. */
	private LayoutManager2 proxy;
	/** Mapping of animations managed by this layout. */
	private Map<Container, Animator> animations;
	/** Flag for showing or suppressing animation. */
	private boolean animated = true;

	/**
	 * Create a new AnimatedLayout.
	 *
	 * @param proxy layout manager to be used for determining the desired layout
	 */
	public AnimatedLayout(LayoutManager2 proxy) {
		this.proxy = proxy;
		animations = new WeakHashMap<Container, Animator>(5);
	}

	/**
	 * Turn animations on or off.
	 *
	 * @param animate <code>true</code> if subsequent layout changes are
	 * animated, otherwise <code>false</code>
	 */
	public void setAnimated(boolean animate) {
		if (animated != animate) {
			animated = animate;
		}
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
		proxy.addLayoutComponent(name, comp);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		proxy.removeLayoutComponent(comp);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return proxy.preferredLayoutSize(parent);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return proxy.minimumLayoutSize(parent);
	}

	@Override
	public void layoutContainer(Container parent) {
		if (!animated) {
			proxy.layoutContainer(parent);
			return;
		}

		Map<Component, Rectangle> startPositions = new HashMap<Component, Rectangle>(parent.getComponentCount());
		for (Component comp : parent.getComponents()) {
			startPositions.put(comp, new Rectangle(comp.getBounds()));
		}

		proxy.layoutContainer(parent);

		Collection<BoundData> changes = new ArrayList<BoundData>();
		for (Component comp : parent.getComponents()) {
			Rectangle bounds = comp.getBounds();
			Rectangle startBounds = startPositions.get(comp);
			if (!startBounds.equals(bounds)) {
				comp.setBounds(startBounds);
				changes.add(new BoundData(comp, startBounds, bounds));
			}
		}

		if (!changes.isEmpty()) {
			Animator animator = animations.get(parent);
			if (animator == null) {
				animator = new Animator(parent, changes);
				animations.put(parent, animator);
			} else {
				animator.setBounds(changes);
			}
			animator.restart();
		} else {
			Animator animator = animations.get(parent);
			if (animator != null) {
				animator.stop();
				animations.remove(parent);
			}
		}
	}


	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
		proxy.addLayoutComponent(comp, constraints);
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return proxy.maximumLayoutSize(target);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return proxy.getLayoutAlignmentX(target);
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return proxy.getLayoutAlignmentY(target);
	}

	@Override
	public void invalidateLayout(Container target) {
		proxy.invalidateLayout(target);
	}

	/**
	 * Class for holding Components' initial and final bounds.
	 */
	private static class BoundData {
		/** The component whose bounds are stored. */
		private final Component component;
		/** Initial bounds. */
		private final Rectangle startBounds;
		/** Final bounds. */
		private final Rectangle finalBounds;

		/**
		 * Create new BoundData.
		 *
		 * @param component component whose bounds are stored
		 * @param startBounds bounds of the component at the start of the animation
		 * @param finalBounds bounds of the component at the end of the animation,
		 */
		BoundData(Component component, Rectangle startBounds, Rectangle finalBounds) {
			this.component = component;
			this.startBounds = startBounds;
			this.finalBounds = finalBounds;
		}

		/**
		 * Get the component to which the bounds belong to.
		 *
		 * @return component
		 */
		Component getComponent() {
			return component;
		}

		/**
		 * Get the maximum coordinate change.
		 *
		 * @return maximum change
		 */
		int getMaxDistance() {
			int maxDist = Math.abs(startBounds.x - finalBounds.x);
			maxDist = Math.max(maxDist, Math.abs(startBounds.x + startBounds.width - finalBounds.x - finalBounds.width));
			maxDist = Math.max(maxDist, Math.abs(startBounds.y - finalBounds.y));
			return Math.max(maxDist, Math.abs(startBounds.y + startBounds.height - finalBounds.y - finalBounds.height));
		}

		/**
		 * Get the bounds at specified progress state.
		 * @param progress state of progress
		 *
		 * @return bounds at specified progress state
		 */
		Rectangle getBounds(double progress) {
			return interpolate(startBounds, finalBounds, progress);
		}

		/**
		 * Calculate interpolated bounds based on the initial and final bounds,
		 * and the state of progress.
		 *
		 * @param startBounds initial bounds
		 * @param finalBounds final bounds
		 * @param progress state of progress
		 * @return interpolated bounds
		 */
		private Rectangle interpolate(Rectangle startBounds, Rectangle finalBounds, double progress) {
			Rectangle bounds = new Rectangle();
			bounds.setLocation(interpolate(startBounds.getLocation(), finalBounds.getLocation(), progress));
			bounds.setSize(interpolate(startBounds.getSize(), finalBounds.getSize(), progress));

			return bounds;
		}

		/**
		 * Calculate interpolated dimensions based on the initial and final
		 * dimensions, and the state of progress.
		 *
		 * @param startSize initial size
		 * @param finalSize final size
		 * @param progress state of progress
		 * @return interpolated size
		 */
		private Dimension interpolate(Dimension startSize, Dimension finalSize, double progress) {
			Dimension size = new Dimension();
			size.width = interpolate(startSize.width, finalSize.width, progress);
			size.height = interpolate(startSize.height, finalSize.height, progress);

			return size;
		}

		/**
		 * Calculate interpolated location based on the initial and final
		 * location, and the state of progress.
		 *
		 * @param startPoint initial location
		 * @param finalPoint final location
		 * @param progress state of progress
		 * @return interpolated location
		 */
		private Point interpolate(Point startPoint, Point finalPoint, double progress) {
			Point point = new Point();
			point.x = interpolate(startPoint.x, finalPoint.x, progress);
			point.y = interpolate(startPoint.y, finalPoint.y, progress);

			return point;
		}

		/**
		 * Calculate interpolated value based on the initial and final values,
		 * and the state of progress.
		 *
		 * @param startValue initial value
		 * @param endValue final value
		 * @param progress state of progress
		 * @return interpolated value
		 */
		private int interpolate(int startValue, int endValue, double progress) {
			int distance = endValue - startValue;
			double distanceDone;
			// quadratic ease in and out
			if (progress <= 0.5) {
				distanceDone = 2 * progress * progress;
			} else {
				distanceDone = -2 * progress * progress + 4 * progress - 1;
			}

			return (int) (distance * distanceDone) + startValue;
		}
	}

	/**
	 * Object for managing the animations.
	 */
	private static class Animator implements ActionListener {
		/** Minimum animation speed. Larger is faster. */
		private static final double MINIMUM_SPEED = 0.032;

		/** Timer used for the animation steps. */
		private final Timer timer;
		/** The container of the animated components. */
		private Container parent;
		/** Bound data of the animated components. */
		private Collection<BoundData> boundList;
		/** Current animation state. */
		private double progress;
		/** Current animation speed. */
		private double progressRate;

		/**
		 * Create a new Animator.
		 *
		 * @param parent The container of the animated components
		 * @param bounds Bound data of the animated components
		 */
		Animator(Container parent, Collection<BoundData> bounds) {
			setBounds(bounds);
			timer = new Timer(16, this);
			this.parent = parent;
		}

		/**
		 * Set bound data for the animations.
		 * @param bounds bound data
		 */
		final void setBounds(Collection<BoundData> bounds) {
			// Base the animation speed on the longest animated distance
			int maxDist = 0;
			for (BoundData ab : bounds) {
				maxDist = Math.max(maxDist, ab.getMaxDistance());
			}
			progressRate = Math.max(MINIMUM_SPEED, 1.0 / maxDist);
			this.boundList = bounds;
		}

		/**
		 * Start, or restart the animation.
		 */
		void restart() {
			progress = 0;
			timer.restart();
		}

		/**
		 * Stop the animation.
		 */
		void stop() {
			timer.stop();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			progress += progressRate;
			if (progress >= 1) {
				progress = 1;
				timer.stop();
			}

			for (BoundData bounds : boundList) {
				Component comp = bounds.getComponent();
				comp.setBounds(bounds.getBounds(progress));
			}

			parent.repaint();
		}
	}
}
