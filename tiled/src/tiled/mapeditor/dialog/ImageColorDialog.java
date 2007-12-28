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
 *  Matthias Totz <mtotz@users.sourceforge.net>
 */

package tiled.mapeditor.dialog;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.PixelGrabber;

import javax.swing.*;

import tiled.mapeditor.widget.ImageViewPanel;
import tiled.mapeditor.widget.VerticalStaticJPanel;

public class ImageColorDialog extends JDialog implements ActionListener, MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 4104726676566529193L;

	private Image image;
	private JButton bCancel;
	private Color color;
	private JPanel colorPanel;
	private int[] pixels;

	public ImageColorDialog() {
		super();
	}

	public ImageColorDialog(Image i) {
		this();
		image = i;
		PixelGrabber pg = new PixelGrabber(i, 0, 0, -1, -1, true);

		try {
			pg.grabPixels();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		pixels = (int[]) pg.getPixels();

		init();
		pack();
		setLocationRelativeTo(getOwner());
		setModal(true);
	}

	private void init() {
		ImageViewPanel imagePanel = new ImageViewPanel(image);
		imagePanel.addMouseListener(this);
		imagePanel.addMouseMotionListener(this);

		setTitle("Color Chooser");

		color = new Color(255, 103, 139); // Evil pink
		colorPanel = new JPanel();
		colorPanel.setPreferredSize(new Dimension(25, 25));
		colorPanel.setBackground(color);

		bCancel = new JButton("Cancel");
		bCancel.addActionListener(this);

		JScrollPane imageScrollPane = new JScrollPane(imagePanel);
		imageScrollPane.setAutoscrolls(true);

		VerticalStaticJPanel mainPanel = new VerticalStaticJPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

		mainPanel.add(imageScrollPane);

		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		buttonPanel.add(colorPanel);
		c.gridx = 1;
		buttonPanel.add(Box.createRigidArea(new Dimension(25, 5)));
		c.gridx = 2;
		buttonPanel.add(bCancel);

		mainPanel.add(buttonPanel);

		setContentPane(mainPanel);
	}

	public Color showDialog() {
		setVisible(true);
		return color;
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == bCancel) {
			color = null;
			dispose();
		}
	}

	public void mouseClicked(MouseEvent e) {
		grabColor(e.getX(), e.getY());
		dispose();
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mouseDragged(MouseEvent e) {
		grabColor(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		grabColor(e.getX(), e.getY());
	}

	private void grabColor(int x, int y) {
		int w = image.getWidth(null);
		if (pixels != null) {
			int r = (pixels[y * w + x] >> 16) & 0xff;
			int g = (pixels[y * w + x] >> 8) & 0xff;
			int b = (pixels[y * w + x]) & 0xff;

			color = new Color(r, g, b);
			colorPanel.setBackground(color);
		}
	}
}
