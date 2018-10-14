/***************************************************************************
 *                 (C) Copyright 2003-2013 - Stendhal team                 *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.entity;

import games.stendhal.client.gui.wt.core.WtWindowManager;
import games.stendhal.common.MathHelper;

/**
 * Object that keeps track of lag between user initiated movement and server
 * responses to them, and calculates appropriate speed based on the lag for the
 * user to be used for movement prediction.
 */
class SpeedPredictor {
	/** Server turn length. */
	private static final double TURN_LENGTH = 300;
	/**
	 * Initial guess for the starting speed.
	 */
	private static final double INITIAL_PREDICTED_SPEED = 0.3;
	/**
	 * Time threshold in milliseconds, above which measured lag events are
	 * assumed to be caused by temporary network glitches, or other
	 * anomalous conditions (such as user being unable to move to the
	 * predicted direction, and the next move being initiated by path
	 * finding instead of keyboard).
	 */
	private static final double DISCARD_THRESHOLD = 2000;
	/**
	 * Very low starting speeds can look odd, so we treat those as 0, and only
	 * turn the player in the prediction code if that's the case.
	 */
	private static final double DISABLING_THRESHDOLD = 0.3;
	/**
	 * Length of the lag time accumulation vector. The amount of history
	 * retained for the prediction purposes. Smaller value means faster
	 * adaption to current situation, but higher instability of the
	 * predicted lag.
	 */
	private static final int VECTOR_LENGTH = 4;
	/** WM property name for saving the predicted speed. */
	private static final String SPEED_PROPERTY = "predictor.speed";
	/** WM property name for saving the recent jitter. */
	private static final String JITTER_PROPERTY = "predictor.jitter";

	/**
	 * Stored time averages.
	 */
	final double[] times;
	/**
	 * Current predicted speed.
	 */
	double prediction;
	/**
	 * Time of the previous start of prediction, or 0 if the user has moved
	 * after that.
	 */
	private long timeStamp;
	/**
	 * A measure of instability in latency. Not really any exact value, but
	 * corresponds to milliseconds.
	 */
	private double jitter;

	/**
	 * Create a new SpeedPredictor with default initial prediction and
	 * history corresponding to that.
	 */
	SpeedPredictor() {
		times = new double[VECTOR_LENGTH];
		WtWindowManager wm = WtWindowManager.getInstance();
		prediction = MathHelper.parseDoubleDefault(wm.getProperty(SPEED_PROPERTY,
				Double.toString(INITIAL_PREDICTED_SPEED)), INITIAL_PREDICTED_SPEED);
		jitter = MathHelper.parseDouble(wm.getProperty(JITTER_PROPERTY, "0.0"));

		// Fill the history with data corresponding to the stored, or default
		// prediction.
		double average = TURN_LENGTH / prediction;
		for (int i = 0; i < VECTOR_LENGTH; i++) {
			times[i] = average;
		}
	}

	/**
	 * Create a new SpeedPredictor based on the data collected by another
	 * predictor.
	 *
	 * @param old predictor to be used as the template
	 */
	SpeedPredictor(SpeedPredictor old) {
		times = old.times;
		prediction = old.prediction;
		jitter = old.jitter;
	}

	/**
	 * Check if the predictor is expecting a move event to end a time
	 * measurement.
	 *
	 * @return <code>true</code> if the predictor should be notified by
	 * 	the next user move event.
	 */
	boolean isActive() {
		return timeStamp != 0;
	}

	/**
	 * Notify the predictor that the user initiated movement using the
	 * keyboard.
	 */
	void startPrediction() {
		timeStamp = System.currentTimeMillis();
	}

	/**
	 * Called when the user moves. Marks the end of a timing instance.
	 */
	void onMoved() {
		double diff = System.currentTimeMillis() - timeStamp;
		timeStamp = 0;
		if (diff > DISCARD_THRESHOLD) {
			return;
		}

		double recentJitter = 0;
		double sum = 0.0;
		for (int i = 0; i < VECTOR_LENGTH; i++) {
			double old = times[i];
			double avg = (diff + old) / 2;
			recentJitter += Math.abs(old - diff);
			times[i] = avg;
			diff = old;
			sum += avg;
		}
		recentJitter /= 8;
		jitter = (jitter + 2.0 * Math.max(jitter, recentJitter) + recentJitter) / 4.0;
		prediction = TURN_LENGTH / (sum / VECTOR_LENGTH + jitter);

		// Save for future
		WtWindowManager wm = WtWindowManager.getInstance();
		wm.setProperty(SPEED_PROPERTY, Double.toString(prediction));
		wm.setProperty(JITTER_PROPERTY, Double.toString(jitter));
	}

	/**
	 * Get the current predicted speed.
	 *
	 * @return predicted speed
	 */
	double getSpeed() {
		if (prediction > DISABLING_THRESHDOLD) {
			return prediction;
		}

		return 0.0;
	}
}
