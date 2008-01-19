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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * @author mtotz This dialog shows exceptions with its whole stacktrace
 */
public class ExceptionDialog extends JDialog {
	private static final long serialVersionUID = 1L;

	public static void showDialog(JFrame parent, Exception exception, String text) {
		new ExceptionDialog(parent, exception, text).setVisible(true);
	}

	/**
	 * 
	 */
	private ExceptionDialog(JFrame parent, Exception exception, String text) {
		super(parent, true);
		setBounds(parent.getX(), parent.getY(), 500, 300);

		setTitle("Exception occurred");

		Container contentPane = getContentPane();
		contentPane.setBackground(Color.GRAY);
		contentPane.setLayout(new BorderLayout());

		JTextArea textArea = new JTextArea(10, 50);
		textArea.setEditable(false);
		textArea.setBackground(Color.LIGHT_GRAY);
		textArea.setTabSize(4);
		Font font = textArea.getFont();
		textArea.setFont(font.deriveFont(Font.BOLD));
		String message = (text == null ? "" : text + "\n\n") + getExceptionString(exception);
		textArea.setText(message);
		contentPane.add(new JScrollPane(textArea), BorderLayout.CENTER);

		JButton okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ExceptionDialog.this.setVisible(false);
			}
		});
		contentPane.add(okButton, BorderLayout.SOUTH);
	}

	/**
	 * @param exception
	 * @return
	 */
	private String getExceptionString(Exception exception) {
		StringBuilder buf = new StringBuilder();
		buf.append(exception).append("\n");

		for (StackTraceElement element : exception.getStackTrace()) {
			buf.append("\tat ").append(element).append("\n");
		}

		Throwable cause = exception.getCause();
		while (cause != null) {
			buf.append("Caused by: ").append(cause).append("\n");

			for (StackTraceElement element : cause.getStackTrace()) {
				buf.append("\tat ").append(element).append("\n");
			}
		}

		return buf.toString();
	}

}
