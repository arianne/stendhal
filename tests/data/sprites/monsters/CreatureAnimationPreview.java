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
package data.sprites.monsters;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class CreatureAnimationPreview {
	private static final int NUMBER_OF_ROWS = 4;

	private static final int NUMBER_OF_FRAMES = 3;

	/**
	 * This method initializes jPanel.
	 * 
	 * @return javax.swing.JPanel
	 */
	@SuppressWarnings("serial")
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			JComponent row = new JComponent() {};
			row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
			jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

			row.add(getImageViewerSwing());
			row.add(getImageViewerSwing2());
			row.add(getImageViewerSwing3());
			row.add(getImageViewerSwing4());
			jPanel.add(row);
			jPanel.add(getImageViewerSwing1());
		}
		return jPanel;
	}

	/**
	 * This method initializes imageViewerSwing.
	 * 
	 * @return data.sprites.monsters.ImageViewerSwing
	 */
	private ImageViewerSwing getImageViewerSwing() {
		if (imageViewerSwing == null) {
			imageViewerSwing = new ImageViewerSwing();
			imageViewerSwing.setName("imageViewerSwing");
		}
		return imageViewerSwing;
	}

	/**
	 * This method initializes imageViewerSwing1.
	 * 
	 * @return data.sprites.monsters.ImageViewerSwing
	 */
	private ImageViewerSwing getImageViewerSwing1() {
		if (imageViewerSwing1 == null) {
			imageViewerSwing1 = new ImageViewerSwing();
			imageViewerSwing1.setName("imageViewerSwing1");
		}
		return imageViewerSwing1;
	}

	/**
	 * This method initializes imageViewerSwing2.
	 * 
	 * @return data.sprites.monsters.ImageViewerSwing
	 */
	private ImageViewerSwing getImageViewerSwing2() {
		if (imageViewerSwing2 == null) {
			imageViewerSwing2 = new ImageViewerSwing();
			imageViewerSwing2.setName("imageViewerSwing2");
		}
		return imageViewerSwing2;
	}

	/**
	 * This method initializes imageViewerSwing3.
	 * 
	 * @return data.sprites.monsters.ImageViewerSwing
	 */
	private ImageViewerSwing getImageViewerSwing3() {
		if (imageViewerSwing3 == null) {
			imageViewerSwing3 = new ImageViewerSwing();
			imageViewerSwing3.setName("imageViewerSwing3");
		}
		return imageViewerSwing3;
	}

	/**
	 * This method initializes imageViewerSwing4.
	 * 
	 * @return data.sprites.monsters.ImageViewerSwing
	 */
	private ImageViewerSwing getImageViewerSwing4() {
		if (imageViewerSwing4 == null) {
			imageViewerSwing4 = new ImageViewerSwing();
			imageViewerSwing4.setName("imageViewerSwing4");
		}
		return imageViewerSwing4;
	}

	public static void main(final String[] args) {

		(new CreatureAnimationPreview()).getJFrame().setVisible(true);
	}

	private JFrame jFrame;

	private JSplitPane jSplitPane;

	private JScrollPane jScrollPane;

	private FileTree jTree;

	private JPanel jPanel;

	private ImageViewerSwing imageViewerSwing;

	private ImageViewerSwing imageViewerSwing1;

	private ImageViewerSwing imageViewerSwing2;

	private ImageViewerSwing imageViewerSwing3;

	private ImageViewerSwing imageViewerSwing4;

	private AnimationRunner[] animations;

	/**
	 * This method initializes jFrame.
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();

			jFrame.setSize(new Dimension(818, 470));

			jFrame.setContentPane(getJSplitPane());
			jFrame.setTitle("animated Monsters test");
			jFrame.addWindowListener(new java.awt.event.WindowAdapter() {

				@Override
				public void windowClosing(final java.awt.event.WindowEvent e) {
					if (animations != null) {
						for (int i = 0; i < animations.length; i++) {
							if (animations[i] != null) {
								animations[i].stopAnimation();
								animations[i].tearDown();
							}
						}
					}

					jSplitPane = null;
					jScrollPane = null;
					jTree = null;
					jPanel = null;
					imageViewerSwing = null;
					imageViewerSwing1 = null;
					imageViewerSwing2 = null;
					imageViewerSwing3 = null;
					imageViewerSwing4 = null;
					super.windowClosing(e);
					System.exit(0);
				}
			});
		}

		return jFrame;
	}

	/**
	 * This method initializes jSplitPane.
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getJSplitPane() {
		if (jSplitPane == null) {
			jSplitPane = new JSplitPane();
			jSplitPane.setLeftComponent(getJScrollPane());
			// jSplitPane.setRightComponent(getImageViewer());
			jSplitPane.setRightComponent(getJPanel());
		}
		return jSplitPane;
	}

	/**
	 * This method initializes jScrollPane.
	 * 
	 * @return javax.swing.JScrollPane
	 */
	private JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setViewportView(getJTree());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTree.
	 * 
	 * @return data.sprites.monsters.FileTree
	 */
	private FileTree getJTree() {
		if (jTree == null) {
			try {
				final Preferences pref = Preferences.userNodeForPackage(AnimationRunner.class);
				File lastDirectory = null;
				if (pref.get("lastpath", null) != null) {
					lastDirectory = new File(pref.get("lastpath", null));

				}

				final JFileChooser fc = new JFileChooser(lastDirectory);

				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				final int returnVal = fc.showOpenDialog(jScrollPane);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					final File file = fc.getSelectedFile();
					Preferences.userNodeForPackage(AnimationRunner.class).put("lastpath", file.getAbsolutePath());

					jTree = new FileTree(file.getPath());
				} else {
					System.exit(0);
				}

			} catch (final FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (final SecurityException e1) {
				e1.printStackTrace();
			}
			if (jTree != null) {
				jTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
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

						getImageViewerSwing1().setImage(buf);
						if (animations == null) {
							animations = new AnimationRunner[4];
							animations[0] = AnimationCreate(buf, 0, getImageViewerSwing());
							animations[1] = AnimationCreate(buf, 1, getImageViewerSwing2());
							animations[2] = AnimationCreate(buf, 2, getImageViewerSwing3());
							animations[3] = AnimationCreate(buf, 3, getImageViewerSwing4());

						} else {
							animations[0].stopAnimation();
							animations[1].stopAnimation();
							animations[2].stopAnimation();
							animations[3].stopAnimation();
						}

						animations[0].startAnimation(buffersCreate(buf, 0));
						animations[1].startAnimation(buffersCreate(buf, 1));
						animations[2].startAnimation(buffersCreate(buf, 2));
						animations[3].startAnimation(buffersCreate(buf, 3));

					}
				});
			}
		}
		return jTree;
	}

	AnimationRunner AnimationCreate(final BufferedImage buf, final int row, final ImageViewerSwing imageViewer) {
		return new AnimationRunner(imageViewer);
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

}
