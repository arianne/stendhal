/***************************************************************************
 *                   (C) Copyright 2003-2017 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package data.sprites.monsters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionListener;

public class CreatureAnimationPreview {
	private static final int NUMBER_OF_ROWS = 4;
	private static final int NUMBER_OF_FRAMES = 3;

	private final JFrame jFrame = new JFrame();
	private final JLabel[] animationPanel = new JLabel[NUMBER_OF_ROWS];
	private final JLabel mainPanel = new JLabel();
	private final AnimationRunner[] animations = new AnimationRunner[NUMBER_OF_ROWS];
	private final JSplitPane split = new JSplitPane();

	private CreatureAnimationPreview() {
		jFrame.setSize(new Dimension(818, 470));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(createJTree());
		split.setLeftComponent(scroll);

		JPanel rightPanel = new JPanel();
		JComponent row = new JComponent() {};
		row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

		for (int i = 0; i < NUMBER_OF_ROWS; i++) {
			JLabel l = new JLabel();
			animationPanel[i] = l;
			animations[i] = new AnimationRunner(l);
			row.add(l);
		}

		rightPanel.add(row);
		rightPanel.add(mainPanel);

		split.setRightComponent(rightPanel);
		jFrame.setContentPane(split);
		jFrame.setTitle("animated Monsters test");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jFrame.setVisible(true);
	}

	public static void main(final String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new CreatureAnimationPreview();
			}
		});
	}

	/**
	 * This method initializes jTree.
	 *
	 * @return data.sprites.monsters.FileTree
	 */
	private FileTree createJTree() {
		FileTree jTree = null;
		try {
			final Preferences pref = Preferences.userNodeForPackage(AnimationRunner.class);
			File lastDirectory = null;
			if (pref.get("lastpath", null) != null) {
				lastDirectory = new File(pref.get("lastpath", null));
			}

			final JFileChooser fc = new JFileChooser(lastDirectory);

			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			final int returnVal = fc.showOpenDialog(jFrame);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				final File file = fc.getSelectedFile();
				Preferences.userNodeForPackage(AnimationRunner.class).put("lastpath", file.getAbsolutePath());

				jTree = new FileTree(file.getPath());
				jTree.addTreeSelectionListener(new TreeSelector());
			} else {
				System.exit(0);
			}

		} catch (final FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (final SecurityException e1) {
			e1.printStackTrace();
		}

		return jTree;
	}

	private BufferedImage[] buffersCreate(final BufferedImage buf, final int row) {
		final BufferedImage[] buffers = new BufferedImage[NUMBER_OF_FRAMES];
		final int framewidth = buf.getWidth() / CreatureAnimationPreview.NUMBER_OF_FRAMES;

		final int frameheight = buf.getHeight() / CreatureAnimationPreview.NUMBER_OF_ROWS;
		for (int i = 0; i < NUMBER_OF_FRAMES; i++) {
			buffers[i] = buf.getSubimage(i * framewidth, row * frameheight, framewidth, frameheight);
		}
		return buffers;
	}

	/**
	 * Handler for tree selection changes.
	 */
	private class TreeSelector implements TreeSelectionListener {
		@Override
		public void valueChanged(final javax.swing.event.TreeSelectionEvent e) {
			jFrame.setTitle(e.getNewLeadSelectionPath().getLastPathComponent().toString());

			BufferedImage buf = null;
			try {
				final File file = new File(e.getNewLeadSelectionPath().getLastPathComponent().toString());
				if (file.isDirectory()) {
					return;
				}

				buf = ImageIO.read(file);
				if (buf == null) {
					return;
				}
			} catch (final IOException e1) {
				// should never happen;
				e1.printStackTrace();
				return;
			}

			mainPanel.setIcon(new ImageIcon(buf));
			for (int i = 0; i < NUMBER_OF_ROWS; i++) {
				animations[i].startAnimation(buffersCreate(buf, i));
			}
			split.resetToPreferredSizes();
		}
	}
}
