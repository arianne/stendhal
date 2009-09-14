/* $Id$ */
/***************************************************************************
 *                      (C) Copyright 2003 - Marauroa                      *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package games.stendhal.client.gui.login;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.ProgressBar;
import games.stendhal.client.update.ClientGameConfiguration;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.common.game.AccountResult;
import marauroa.common.net.InvalidVersionException;

import org.apache.log4j.Logger;


public class CreateAccountDialog extends JDialog {

	private static final long serialVersionUID = 4436228792112530975L;

	private static final Logger logger = Logger.getLogger(CreateAccountDialog.class);

	// Variables declaration
	private JLabel usernameLabel;
	private JLabel serverLabel;
	private JLabel serverPortLabel;
	private JLabel passwordLabel;
	private JLabel passwordretypeLabel;
	private JLabel emailLabel;
	private JTextField usernameField;
	private JPasswordField passwordField;
	private JPasswordField passwordretypeField;
	private JTextField emailField;
	private JTextField serverField;
	private JTextField serverPortField;
	private JButton createAccountButton;
	private JPanel contentPane;

	// End of variables declaration
	private StendhalClient client;
	private Frame owner;
	private String badPasswordReason;

	public CreateAccountDialog(final Frame owner, final StendhalClient client) {
		super(owner, true);
		this.client = client;
		this.owner = owner;
		initializeComponent();

		this.setVisible(true);
	}

	public CreateAccountDialog() {
		super();
		initializeComponent();
	}

	private void initializeComponent() {
		serverLabel = new JLabel("Server name");
		serverField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_SERVER"));
		serverField.setEditable(true);
		serverPortLabel = new JLabel("Server port");
		serverPortField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_PORT"));

		usernameLabel = new JLabel("Choose a username");
		usernameField = new JTextField();
		usernameField.setDocument(new LowerCaseLetterDocument());

		passwordLabel = new JLabel("Choose a password");
		passwordField = new JPasswordField();

		passwordretypeLabel = new JLabel("Retype password");
		passwordretypeField = new JPasswordField();

		emailLabel = new JLabel("E-mail address");
		emailField = new JTextField();

		// createAccountButton
		//
		createAccountButton = new JButton();
		createAccountButton.setText("Create Account");
		createAccountButton.setMnemonic(KeyEvent.VK_C);
		this.rootPane.setDefaultButton(createAccountButton);
		createAccountButton.addActionListener(new ActionListener() {

			public void actionPerformed(final ActionEvent e) {
				createAccountButton_actionPerformed(e, false);
			}
		});

		//
		// contentPane
		//
		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new GridBagLayout());
		contentPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createEtchedBorder(),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		final GridBagConstraints c = new GridBagConstraints();

		// row 0
		c.anchor = GridBagConstraints.LINE_START;
		c.insets = new Insets(4, 4, 4, 4);
		// column
		c.gridx = 0; 
		// row
		c.gridy = 0; 
		c.fill = GridBagConstraints.NONE;
		contentPane.add(serverLabel, c);
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverField, c);

		// row 1
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(serverPortLabel, c);
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(4, 4, 4, 4);
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(serverPortField, c);

		// row 2
		c.insets = new Insets(4, 4, 4, 4);
		c.gridx = 0;
		c.gridy = 2;
		contentPane.add(usernameLabel, c);
		c.gridx = 1;
		c.gridy = 2;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(usernameField, c);
	
		// row 3
		c.gridx = 0;
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(passwordLabel, c);
		c.gridx = 1;
		c.gridy = 3;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(passwordField, c);

		// row 4
		c.gridx = 0;
		c.gridy = 4;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(passwordretypeLabel, c);
		c.gridx = 1;
		c.gridy = 4;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(passwordretypeField, c);

		// row 5
		c.gridx = 0;
		c.gridy = 5;
		c.fill = GridBagConstraints.NONE;
		contentPane.add(emailLabel, c);
		c.gridx = 1;
		c.gridy = 5;
		c.fill = GridBagConstraints.BOTH;
		contentPane.add(emailField, c);

		// row 6
		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(15, 4, 4, 4);
		contentPane.add(createAccountButton, c);

		// row 7
		JLabel logLabel = new JLabel("<html><body><p><font size=\"-2\">On login information which identifies your computer on <br>the internet will be logged to prevent abuse (like many <br>attempts to guess a password in order to hack an <br>account or creation of many accounts to cause trouble). <br>Furthermore all events and actions that happen within <br>the game-world (like solving quests, attacking monsters) <br>are logged. This information is used to analyse bugs and <br>in rare cases for abuse handling.</font></p></body></html>");
		c.gridx = 0;
		c.gridy = 7;
		c.fill = GridBagConstraints.VERTICAL;
		c.anchor = GridBagConstraints.CENTER;
		c.gridwidth = 2;
		contentPane.add(logLabel, c);
		
		// CreateAccountDialog
		this.setTitle("Create New Account");
		this.setResizable(false);
		this.setSize(new Dimension(350, 350));
		// required on Compiz
		this.pack(); 
		this.setLocationRelativeTo(owner);
		usernameField.requestFocusInWindow();

	}

	private void createAccountButton_actionPerformed(final ActionEvent e,
			final boolean saveLoginBoxStatus) {
		final String accountUsername = usernameField.getText();
		final String password = new String(passwordField.getPassword());

		// If this window isn't enabled, we shouldn't act.
		if (!this.isEnabled()) {
			return;
		}

		final boolean ok = checkFields();

		if (!ok) {
			return;
		}

		final String email = emailField.getText();
		final String server = serverField.getText();
		int port = 32160;

		// standalone check
		if (client == null) {
			JOptionPane.showMessageDialog(this,
					"Account not created (running standalone)!");
			return;
		}
		// port couldnt be accessed from inner class
		final int finalPort; 
		final ProgressBar progressBar = new ProgressBar(owner);

		try {
			port = Integer.parseInt(serverPortField.getText());
		} catch (final Exception ex) {
			JOptionPane.showMessageDialog(owner,
					"That is not a valid port number. Please try again.",
					"Invalid Port", JOptionPane.WARNING_MESSAGE);
			return;
		}
		finalPort = port;

		/* seprate thread for connection proccess added by TheGeneral */
		// run the connection procces in separate thread
		final Thread m_connectionThread = new Thread() {

			@Override
			public void run() {
				// intialize progress bar
				progressBar.start(); 
				// disable this screen when attempting to connect
				setEnabled(false); 
	

				try {
					client.connect(server, finalPort);
					// for each major connection milestone call step()
					progressBar.step(); 
				} catch (final Exception ex) {
					// if something goes horribly just cancel the progressbar
					progressBar.cancel(); 
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"Stendhal cannot connect to the Internet. Please check that your connection is set up and active, then try again.");

					logger.error(ex, ex);

					return;
				}

				try {
					final AccountResult result = client.createAccount(
							accountUsername, password, email);
					if (result.failed()) {
						/*
						 * If the account can't be created, show an error
						 * message and don't continue.
						 */
						progressBar.cancel();
						setEnabled(true);
						JOptionPane.showMessageDialog(owner,
								result.getResult().getText(),
								"Create account failed",
								JOptionPane.ERROR_MESSAGE);
					} else {

						/*
						 * Print username returned by server, as server can
						 * modify it at will to match account names rules.
						 */

						progressBar.step();
						progressBar.finish();

						client.setAccountUsername(accountUsername);

						/*
						 * Once the account is created, login into server.
						 */
						client.login(accountUsername, password);
						progressBar.step();
						progressBar.finish();

						setEnabled(false);
						owner.setVisible(false);

						stendhal.doLogin = true;
					}
				} catch (final TimeoutException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"Unable to connect to server to create your account. The server may be down or, if you are using a custom server, you may have entered its name and port number incorrectly.",
							"Error Creating Account", JOptionPane.ERROR_MESSAGE);
				} catch (final InvalidVersionException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"You are running an incompatible version of Stendhal. Please update",
							"Invalid version", JOptionPane.ERROR_MESSAGE);
				} catch (final BannedAddressException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"Your IP is banned. If you think this is not right, please send a Support Request to http://sourceforge.net/tracker/?func=add&group_id=1111&atid=201111",
							"IP Banned", JOptionPane.ERROR_MESSAGE);
				} catch (final LoginFailedException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(owner, e.getMessage(),
							"Login failed", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		m_connectionThread.start();
	}

	/**
	 * Runs field checks, to, ex. confirm the passwords correct, etc.
	 * @return if no error found
	 */
	private boolean checkFields() {
		//
		// Check the password
		//
		final String password = new String(passwordField.getPassword());
		final String passwordretype = new String(
				passwordretypeField.getPassword());
		if (!password.equals(passwordretype)) {
			JOptionPane.showMessageDialog(owner,
					"The passwords do not match. Please retype both.",
					"Password Mismatch", JOptionPane.WARNING_MESSAGE);
			return false;
		}

		//
		// Password strength
		//
		final boolean valPass = validatePassword(usernameField.getText(), password);
		if (!valPass) {
			if (badPasswordReason != null) {
				// didn't like the password for some reason, show a dialog and
				// try again
				final int i = JOptionPane.showOptionDialog(owner, badPasswordReason,
						"Bad Password", JOptionPane.YES_NO_OPTION,
						JOptionPane.WARNING_MESSAGE, null, null, 1);

				if (i == JOptionPane.NO_OPTION) {
					return false;
				}
			} else {
				return false;
			}
		}

		//
		// Check the email
		//
		final String email = emailField.getText();
		if (isInvalid(email)) {
			final String text = "The email you entered appears to be invalid.\n"
					+ "You must provide a valid email address to recover a lost password. Are you sure this email is correct? ";
			final int i = JOptionPane.showOptionDialog(owner, text, "Invalid Email",
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
					null, null, 1);

			if (i != 0) {
				// no
				return false;
			} 
		}

		return true;
	}

	private boolean isInvalid(final String email) {
		return !email.contains("@") || !email.contains(".") || (email.length() <= 5);
	}

	/**
	 * Used to preview the CreateAccountDialog.
	 * @param args 
	 */
	public static void main(final String[] args) {
		new CreateAccountDialog(null, null);
	}

	private static class LowerCaseLetterDocument extends PlainDocument {
		private static final long serialVersionUID = -5123268875802709841L;

		@Override
		public void insertString(final int offs, final String str, final AttributeSet a)
				throws BadLocationException {
			final String lower = str.toLowerCase();
			boolean ok = true;
			for (int i = lower.length() - 1; i >= 0; i--) {
				final char chr = lower.charAt(i);
				if ((chr < 'a') || (chr > 'z')) {
					ok = false;
					break;
				}
			}
			if (ok) {
				super.insertString(offs, lower, a);
			} else {
				Toolkit.getDefaultToolkit().beep();
			}
		}
	}

	public boolean validatePassword(final String username, final String password) {
		if (password.length() > 5) {

			// check for all numbers
			boolean allNumbers = true;
			try {
				Integer.parseInt(password);
			} catch (final Exception e) {
				allNumbers = false;
			}
			if (allNumbers) {
				badPasswordReason = "You have used only numbers in your password. This is not a good security practice.\n"
						+ " Are you sure that you want to use this password?";
			}

			// check for username
			boolean hasUsername = false;
			if (password.contains(username)) {
				hasUsername = true;
			}

			if (!hasUsername) {
				// now we'll do some more checks to see if the password
				// contains more than three letters of the username
				debug("Checking if password contains a derivative of the username, trimming from the back...");
				final int min_user_length = 3;
				for (int i = 1; i < username.length(); i++) {
					final String subuser = username.substring(0, username.length()
							- i);
					debug("\tchecking for \"" + subuser + "\"...");
					if (subuser.length() <= min_user_length) {
						break;
					}

					if (password.contains(subuser)) {
						hasUsername = true;
						debug("Password contains username!");
						break;
					}
				}

				if (!hasUsername) {
					// now from the end of the password..
					debug("Checking if password contains a derivative of the username, trimming from the front...");
					for (int i = 0; i < username.length(); i++) {
						final String subuser = username.substring(i);
						debug("\tchecking for \"" + subuser + "\"...");
						if (subuser.length() <= min_user_length) {
							break;
						}
						if (password.contains(subuser)) {
							hasUsername = true;
							debug("Password contains username!");
							break;
						}
					}
				}
			}

			if (hasUsername) {
				badPasswordReason = "You have used your username or a derivative of your username in your password. This is a bad security practice.\n"
						+ " Are you sure that you want to use this password?";
				return false;
			}

		} else {
			final String text = "The password you provided is too short. It must be at least 6 characters long.";
			if (isVisible()) {
				JOptionPane.showMessageDialog(owner, text);
			} else {
				logger.warn(text);
			}
			return false;
		}

		return true;
	}

	/**
	 * Prints text only when running stand-alone.
	 * @param text 
	 */
	public void debug(final String text) {

		if (client == null) {
			logger.debug(text);
		}
	}
}
