package games.stendhal.client.gui;


import games.stendhal.client.StendhalClient;
import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJButton;
import games.stendhal.client.gui.wt.InternalManagedDialog;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class QuitDialog {
	Component quitDialog;
	
	Component getQuitDialog() {
		return quitDialog;
	}
	public QuitDialog() {
		quitDialog = buildQuitDialog();
		quitDialog.setVisible(false);
	}
	/**
	 * Build the in-window quit dialog [panel].
	 * @return the quitdialog
	 * 
	 * 
	 */
	protected Component buildQuitDialog() {
		InternalManagedDialog imd;
		Style style;
		JPanel panel;
		JButton b;

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setLayout(null);
		panel.setPreferredSize(new Dimension(150, 75));

		style = WoodStyle.getInstance();

		b = new StyledJButton(style);
		b.setText("Yes");
		b.setBounds(30, 25, 40, 25);
		b.addActionListener(new QuitConfirmCB());

		panel.add(b);

		b = new StyledJButton(style);
		b.setText("No");
		b.setBounds(80, 25, 40, 25);
		b.addActionListener(new QuitCancelCB());

		panel.add(b);

		imd = new InternalManagedDialog("quit", "Quit");
		imd.setContent(panel);
		imd.setMinimizable(false);
		imd.setMovable(false);

		return imd.getDialog();
	}
	
	protected class QuitCancelCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			quitDialog.setVisible(false);
		}
	}

	protected class QuitConfirmCB implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
		
				j2DClient.get().shutdown();
		
		}
	}
	
	/**
	 * Request quit confirmation from the user. This stops all player actions
	 * and shows a dialog in which the player can confirm that they really wants
	 * to quit the program. If so it flags the client for termination.
	 */
	
	public void requestQuit() {
		/*
		 * Stop the player
		 */
		StendhalClient.get().stop();

		/*
		 * Center dialog
		 */
		Dimension psize = quitDialog.getPreferredSize();

		quitDialog.setBounds((j2DClient.get().getWidth() - psize.width) / 2,
				(j2DClient.get().getHeight() - psize.height) / 2, psize.width, psize.height);

		quitDialog.validate();
		quitDialog.setVisible(true);
	}
}
