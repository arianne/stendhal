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

package tiled.mapeditor.dialog;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import tiled.mapeditor.MapEditor;

/**
 * The about dialog.
 */
public class AboutDialog extends JFrame {
	private static final long serialVersionUID = 7565310866809925372L;

	JFrame parent;

	public AboutDialog(JFrame parent) {
		super(MapEditor.TITLE + " v" + MapEditor.VERSION);

		this.parent = parent;
		ImageIcon icon;

		try {
			icon = new ImageIcon(ImageIO.read(getClass().getResourceAsStream("/tiled/mapeditor/resources/logo.png")));

			JPanel content = new JPanel();
			JLabel label = new JLabel(icon);
			content.add(new JLabel("<html>Stendhal Mapeditor is<br>based on Tiled 0.5.2"));
			content.add(label);

			setContentPane(content);
			setResizable(false);
			setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			pack();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			setLocationRelativeTo(parent);
		}
		super.setVisible(visible);
	}
}
