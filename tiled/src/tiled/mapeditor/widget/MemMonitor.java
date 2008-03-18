/*
 *  Tiled Map Editor, (c) 2004
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  Adam Turk <aturk@biggeruniverse.com>
 *  Bjorn Lindeijer <b.lindeijer@xs4all.nl>
 *  
 *  modified for Stendhal, an Arianne powered RPG 
 *  (http://arianne.sf.net)
 *
 *  Matthias Totz &lt;mtotz@users.sourceforge.net&gt;
 */

package tiled.mapeditor.widget;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JProgressBar;

/**
 * A simple ProgressBar showing the current memory consumption.
 * 
 * @author mtotz
 */
public class MemMonitor extends JPanel {
	private static final long serialVersionUID = 1L;

	private JProgressBar progressBar;

	/** creates a new memory monitor. */
	public MemMonitor() {
		setOpaque(false);
		progressBar = new JProgressBar();
		progressBar.setMinimum(0);
		progressBar.setMaximum(100);
		this.add(progressBar);
		progressBar.setStringPainted(true);

		Thread updaterThread = new Thread(new Updater());
		updaterThread.setDaemon(true);
		updaterThread.start();
	}

	@Override
	public Dimension getPreferredSize() {
		return progressBar.getPreferredSize();
	}

	@Override
	public Dimension getMinimumSize() {
		return progressBar.getMinimumSize();
	}

	private class Updater implements Runnable {
		public void run() {
			while (true) {
				Runtime runtime = Runtime.getRuntime();
				int maxMem = (int) (runtime.totalMemory() / (1024));
				int freeMem = (int) (runtime.freeMemory() / (1024));
				progressBar.setMaximum(maxMem);
				progressBar.setValue(maxMem - freeMem);
				progressBar.setString((maxMem - freeMem) + "/" + maxMem);
				progressBar.repaint();
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// ignore Exception
				}
			}
		}
	}

}
