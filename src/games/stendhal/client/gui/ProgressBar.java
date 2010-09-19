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

import games.stendhal.client.update.ClientGameConfiguration;

import java.awt.Window;
import java.net.URL;
import java.security.AccessControlException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class ProgressBar extends JFrame {

	private static final long serialVersionUID = 6241161656154797719L;

	private final Window frame;

	private JPanel contentPane;

	private JProgressBar m_progressBar;

	private  Thread m_run;

	private int m_sleepTime = 210;
	
	// makes for 10 normal steps. 100/10
	private int m_stepSize = 2; 

	private int m_stepSizeMultiplier = 1;
	// keeps track of how many times it has lookp with a multiplier greater then 0
	private int m_stepCounter; 

	// continue while true
	private boolean m_con = true; 

	public ProgressBar(final Window w) {
		super("Connecting...");
		final URL url = this.getClass().getClassLoader().getResource(
				ClientGameConfiguration.get("GAME_ICON"));
		setIconImage(new ImageIcon(url).getImage());
		this.frame = w;

		initializeComponents();

		this.pack();
		this.setLocationRelativeTo(frame);
		try {
			this.setAlwaysOnTop(true);
		} catch (final AccessControlException e) {
			// ignore it
		}
	}

	private void initializeComponents() {
		contentPane = (JPanel) this.getContentPane();

		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		contentPane.add(new JLabel("Connecting..."));
		contentPane.add(Box.createVerticalStrut(5));

		m_progressBar = new JProgressBar(0, 100);
		m_progressBar.setStringPainted(false);
		m_progressBar.setValue(0);
		contentPane.add(m_progressBar);
	}

	public void setTotalTimeEstimate(final int time) {
		m_stepSize = time / 5250;
	}

	public void start() {
		m_run = new Thread("LoginProgressBar") {

			private int counter;

			@Override
			public void run() {
				while (m_con && (counter < 100)) {
					try {
						Thread.sleep(m_sleepTime);
						counter += m_stepSize * m_stepSizeMultiplier;

						final Runnable updateRunner = new Runnable() {

							public void run() {
								m_progressBar.setValue(counter);
							}
						};
						SwingUtilities.invokeLater(updateRunner);

						if (m_stepCounter <= 0) {
							m_stepCounter = 0;
							m_stepSizeMultiplier = 1;
						}
						m_stepCounter--;
					} catch (final InterruptedException ie) {
					}
				}
				ProgressBar.this.dispose();
			}
		};

		this.setVisible(true);
		m_run.start();
	}
	/** 
	 * Temporarily speeds up bar.
	 */
	public void step() { 
		m_stepCounter = 3;
		m_stepSizeMultiplier = 2;
	}
	
	/**
	 *  Speeds up to quickly finish.
	 */
	public void finish() {
		m_stepCounter = 20; 
		m_stepSizeMultiplier = 2;
		m_sleepTime = 15;
	}
	/**
	 * Exits quickly.
	 */
	public void cancel() { 
		m_con = false;
		this.dispose();
	}

}
