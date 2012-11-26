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
package games.stendhal.client.gui.stats;

import games.stendhal.client.gui.AbstractScalingModel;
import games.stendhal.client.gui.StatusDisplayBar;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * A bar indicator component for karma.
 */
public final class KarmaIndicator extends StatusDisplayBar implements PropertyChangeListener {
	private static final long serialVersionUID = 3462088641737184898L;

	private static KarmaIndicator instance;
	
	/**
	 * Create the KarmaIndicator instance.
	 * 
	 * @return instance
	 */
	static synchronized KarmaIndicator create() {
		if (instance == null) {
			instance = new KarmaIndicator();
		} else {
			throw new IllegalStateException("Instance already created");
		}
		
		return instance;
	}
	
	/**
	 * Create a new karma indicator.
	 */
	private KarmaIndicator() {
		super(new KarmaScalingModel());
		setPainter(new KarmaBarPainter());
		setVisible(false);
	}

	/**
	 * Gets the instance. It is invalid to call this method before the indicator
	 * instance has been initialized with create()
	 *
	 * @return KarmaIndicator
	 */
	public static KarmaIndicator get() {
		if (instance == null) {
			throw new IllegalStateException("KarmaIndicator not initialized");
		}
		return instance;
	}

	/**
	 * Set the karma value. This method may be called outside the event dispatch
	 * thread.
	 * 
	 * @param karma new karma
	 */
	void setValue(double karma) {
		setToolTipText(describeKarma(karma));
		getModel().setValue(karma);
	}
	
	/**
	 * Get textual description of karma value.
	 * 
	 * @param karma karma value
	 * @return karma description
	 */
	private String describeKarma(double karma) {
		if (karma > 499) {
			return "You have unusually good karma";
		} else if (karma > 99) {
			return "You have great karma";
		} else if (karma > 5) {
			return "You have good karma";
		} else if (karma > -5) {
			return "You have average karma";
		} else if (karma > -99) {
			return "You have bad karma";
		} else if (karma > -499) {
			return "You have terrible karma";
		}
		return "You have disastrously bad karma";
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt == null) {
			return;
		}

		// disable
		Map<?, ?> oldMap = (Map<?, ?>) evt.getOldValue();
		if ((oldMap != null) && oldMap.containsKey("karma_indicator")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(false);
				}
			});
		}

		// enable
		Map<?, ?> newMap = (Map<?, ?>) evt.getNewValue();
		if ((newMap != null) && newMap.containsKey("karma_indicator")) {
			// Feature changes are triggered from outside the EDT.
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					setVisible(true);
				}
			});
		}
	}
	
	/**
	 * Painter for the karma bar gradient.
	 */
	private static class KarmaBarPainter implements BarPainter {
		@Override
		public void paint(Graphics2D g, int width, int height) {
			Point2D start = new Point2D.Float(0f, 0f);
			Point2D end = new Point2D.Float(width, 0f);
			Paint paint = new LinearGradientPaint(start, end,
					new float[] {0.0f, 0.5f, 1.0f},
					new Color[] {Color.RED, Color.WHITE, Color.BLUE});
			g.setPaint(paint);
			g.fillRect(0, 0, width, height);
		}
	}
	
	/**
	 * Scaling model for karma.
	 */
	private static class KarmaScalingModel extends AbstractScalingModel {
		/** 
		 * Scaling factor for interpreting karma to bar length. Smaller means
		 * smaller change in karma bar for a karma change. 
		 */
		private static final double SCALING = 0.02;
		private int maxRepresentation = 1;
		private double value;

		@Override
		public void setValue(double value) {
			this.value = value;
			calculateRepresentation();
		}

		/**
		 * Calculate the representation value.
		 */
		private void calculateRepresentation() {
			// Scale to ]0, 1[
			double normalized = 0.5 + Math.atan(SCALING * value) / Math.PI;
			// ...and then to ]0, maxRepresentation[
			setRepresentation((int) Math.round(normalized * maxRepresentation));
		}

		@Override
		public void setMaxRepresentation(int max) {
			if (maxRepresentation != max) {
				maxRepresentation = max;
				setValue(value);
			}
		}
	}
}
