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
package games.stendhal.client.gui;

/**
 * A {@link ScalingModel} that implements a linearly displayed value with a
 * known maximum. The representation will have value 0 at value 0, and the
 * maximum representation when the value is the specified maximum.
 */
public class LinearScalingModel extends AbstractScalingModel {
	private double maxValue;
	private int maxRepresentation;
	private double scale;
	private double value;

	/**
	 * Create a LinearScalingModel with maximum value of 1.0, and maximum
	 * representation 1.
	 */
	public LinearScalingModel() {
		this(1.0, 1);
	}

	/**
	 * Create a LinearScalingModel.
	 *
	 * @param maxValue maximum value of the variable
	 * @param maxRepresentation the maximum of the presentation of the value
	 */
	public LinearScalingModel(double maxValue, int maxRepresentation) {
		this.maxValue = maxValue;
		this.maxRepresentation = maxRepresentation;
		calculateScale();
	}

	@Override
	public void setValue(double value) {
		this.value = value;
		setRepresentation(Math.min((int) Math.round(this.value * scale), maxRepresentation));
	}

	/**
	 * Get the internal value.
	 *
	 * @return value
	 */
	public double getValue() {
		return value;
	}

	@Override
	public void setMaxRepresentation(int max) {
		if (max != maxRepresentation) {
			maxRepresentation = max;
			calculateScale();
			setValue(value);
		}
	}

	/**
	 * Set the maximum value for the model, ie. the value that corresponds to
	 * the maximum representation.
	 *
	 * @param maxValue new maximum value
	 */
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		calculateScale();
		// Fires changed event, if needs be
		setValue(value);
	}

	/**
	 * Calculate the scaling factor.
	 */
	private void calculateScale() {
		scale = maxRepresentation / maxValue;
	}
}
