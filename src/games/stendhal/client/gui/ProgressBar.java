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
package games.stendhal.client.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class ProgressBar extends JDialog {
	private static final long serialVersionUID = 6241161656154797719L;
	/** Default delay between updating */
	private static final int SLEEP_TIME = 200;
	/** Maximum value for the progress bar */
	private static final int MAX_VALUE = 100;
	/** Default step size */
	private static final int STEP_SIZE = MAX_VALUE / 50;

	private JProgressBar progressBar;

	/** Speed factor for updating the bar */
	private int stepSizeMultiplier = 1;
	/**
	 * Keeps track of how many times it has looped with a multiplier greater
	 * than 0
	 */
	private int stepCounter;

	private final Timer timer = new Timer(SLEEP_TIME, new Updater());

	/**
	 * Create a new ProgressBar.
	 *
	 * @param w parent dialog
	 */
	public ProgressBar(final JDialog w) {
		super(w, "Connecting...", true);
		initializeComponents();
		this.pack();
		setLocationRelativeTo(w);
	}

	private void initializeComponents() {
		JPanel contentPane = (JPanel) this.getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		contentPane.add(new JLabel("Connecting..."));
		contentPane.add(Box.createVerticalStrut(5));

		progressBar = new JProgressBar(0, MAX_VALUE);
		progressBar.setStringPainted(false);
		contentPane.add(progressBar);
	}

	/**
	 * Timer task that updates the progress bar.
	 */
	private class Updater implements ActionListener {
		private int counter = 0;

		@Override
		public void actionPerformed(ActionEvent arg0) {
			counter += STEP_SIZE * stepSizeMultiplier;
			progressBar.setValue(counter);
			if (stepCounter >= 0) {
				if (stepCounter == 0) {
					stepSizeMultiplier = 1;
				}
				stepCounter--;
			}
			if (counter > 100) {
				cancel();
			}
		}
	}

	/** Start updating the progress bar */
	public void start() {
		timer.start();
		setVisible(true);
	}

	/**
	 * Temporarily speeds up the bar.
	 */
	public void step() {
		stepCounter = 3;
		stepSizeMultiplier = 3;
	}

	/**
	 *  Speeds up to quickly finish.
	 */
	public void finish() {
		stepCounter = 20;
		stepSizeMultiplier = 3;
		timer.setDelay(15);
	}

	/**
	 * Exits quickly.
	 */
	public void cancel() {
		timer.stop();
		// workaround near failures in AWT at openjdk (tested on openjdk-1.6.0.0)
		try {
		    this.dispose();
		} catch(NullPointerException npe) {
			return;
		}
	}
}
