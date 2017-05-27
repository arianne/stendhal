/***************************************************************************
 *                  (C) Copyright 2003 - 2015 Faiumoni e.V.                *
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

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import org.apache.log4j.Logger;

import games.stendhal.client.StendhalClient;
import games.stendhal.client.stendhal;
import games.stendhal.client.gui.NumberDocumentFilter;
import games.stendhal.client.gui.ProgressBar;
import games.stendhal.client.gui.WindowUtils;
import games.stendhal.client.gui.layout.SBoxLayout;
import games.stendhal.client.gui.layout.SLayout;
import games.stendhal.client.update.ClientGameConfiguration;
import marauroa.client.BannedAddressException;
import marauroa.client.LoginFailedException;
import marauroa.client.TimeoutException;
import marauroa.common.game.AccountResult;
import marauroa.common.net.InvalidVersionException;

/**
 * The account creation dialog. For requesting account name, password, and all
 * other needed data.
 */
public class CreateAccountDialog extends JDialog {
	/** Logger instance. */
	private static final Logger LOGGER = Logger.getLogger(CreateAccountDialog.class);

	/** User name input field. */
	private JTextField usernameField;
	/** Password input field. */
	private JPasswordField passwordField;
	/** Password verification field. */
	private JPasswordField passwordretypeField;
	/** Email input field. */
	private JTextField emailField;
	/** Server name input field. */
	private JTextField serverField;
	/** Server port input field. */
	private JTextField serverPortField;

	/** The client used for login. */
	private StendhalClient client;
	/** Descriptions of error conditions. */
	private String badEmailTitle, badEmailReason, badPasswordReason;

	/**
	 * Create an CreateAccountDialog for a parent window, and specified client.
	 *
	 * @param owner parent frame
	 * @param client client used for login
	 */
	public CreateAccountDialog(final Frame owner, final StendhalClient client) {
		super(owner, true);
		this.client = client;
		initializeComponent(owner);

		WindowUtils.closeOnEscape(this);
		this.setVisible(true);
	}

	/**
	 * A dumb constructor used only for tests.
	 */
	CreateAccountDialog() {
		super();
		initializeComponent(null);
	}

	/**
	 * Create the dialog contents.
	 *
	 * @param owner parent window
	 */
	private void initializeComponent(final Frame owner) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		if (owner != null) {
			addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					owner.setEnabled(true);
				}
			});
		}

		JLabel serverLabel = new JLabel("Server name");
		serverField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_SERVER"));
		serverField.setEditable(true);
		JLabel serverPortLabel = new JLabel("Server port");
		serverPortField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_PORT"));
		((AbstractDocument) serverPortField.getDocument()).setDocumentFilter(new NumberDocumentFilter(serverPortField, false));

		JLabel usernameLabel = new JLabel("Choose a username");
		usernameField = new JTextField();
		usernameField.setDocument(new LowerCaseLetterDocument());

		JLabel passwordLabel = new JLabel("Choose a password");
		passwordField = new JPasswordField();

		JLabel passwordretypeLabel = new JLabel("Retype password");
		passwordretypeField = new JPasswordField();

		JLabel emailLabel = new JLabel("E-mail address (optional)");
		emailField = new JTextField();

		// createAccountButton
		//
		JButton createAccountButton = new JButton();
		createAccountButton.setText("Create Account");
		createAccountButton.setMnemonic(KeyEvent.VK_A);
		this.rootPane.setDefaultButton(createAccountButton);
		createAccountButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				onCreateAccount();
			}
		});

		//
		// contentPane
		//
		int padding = SBoxLayout.COMMON_PADDING;
		JPanel contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(new SBoxLayout(SBoxLayout.VERTICAL, padding));
		contentPane.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));

		JComponent grid = new JComponent() {};
		grid.setLayout(new GridLayout(0, 2, padding, padding));
		contentPane.add(grid, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		// row 0
		grid.add(serverLabel);
		grid.add(serverField);

		// row 1
		grid.add(serverPortLabel);
		grid.add(serverPortField);

		// row 2
		grid.add(usernameLabel);
		grid.add(usernameField);

		// row 3
		grid.add(passwordLabel);
		grid.add(passwordField);

		// row 4
		grid.add(passwordretypeLabel);
		grid.add(passwordretypeField);

		// row 5
		grid.add(emailLabel);
		grid.add(emailField);

		// A toggle for showing the contents of the password fields
		grid.add(new JComponent(){});
		JCheckBox showPWToggle = new JCheckBox("Show password");
		showPWToggle.setHorizontalAlignment(SwingConstants.RIGHT);
		final char normalEchoChar = passwordField.getEchoChar();
		showPWToggle.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				char echoChar;
				if (e.getStateChange() == ItemEvent.SELECTED) {
					echoChar = (char) 0;
				} else {
					echoChar = normalEchoChar;
				}
				passwordField.setEchoChar(echoChar);
				passwordretypeField.setEchoChar(echoChar);
			}
		});
		grid.add(showPWToggle);

		// Warning label
		JLabel logLabel = new JLabel("<html><body><p><font size=\"-2\">On login information which identifies your computer on <br>the internet will be logged to prevent abuse (like many <br>attempts to guess a password in order to hack an <br>account or creation of many accounts to cause trouble). <br>Furthermore all events and actions that happen within <br>the game-world (like solving quests, attacking monsters) <br>are logged. This information is used to analyse bugs and <br>in rare cases for abuse handling.</font></p></body></html>");
		// Add a bit more empty space around it
		logLabel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
		logLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(logLabel, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		// Button row
		JComponent buttonRow = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		buttonRow.setAlignmentX(RIGHT_ALIGNMENT);
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispatchEvent(new WindowEvent(CreateAccountDialog.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		buttonRow.add(cancelButton);
		buttonRow.add(createAccountButton);
		contentPane.add(buttonRow);

		// CreateAccountDialog
		this.setTitle("Create New Account");
		this.setResizable(false);
		// required on Compiz
		this.pack();

		usernameField.requestFocusInWindow();
		if (owner != null) {
			owner.setEnabled(false);
			this.setLocationRelativeTo(owner);
		}
	}

	/**
	 * Run when the "Create account" button is activated.
	 */
	private void onCreateAccount() {
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

		// port couldn't be accessed from inner class
		final int finalPort;
		final ProgressBar progressBar = new ProgressBar(this);

		try {
			port = Integer.parseInt(serverPortField.getText());
		} catch (final NumberFormatException ex) {
			JOptionPane.showMessageDialog(getOwner(),
					"That is not a valid port number. Please try again.",
					"Invalid Port", JOptionPane.WARNING_MESSAGE);
			return;
		}
		finalPort = port;

		// standalone check
		if (client == null) {
			JOptionPane.showMessageDialog(this,
					"Account not created (running standalone)!");
			return;
		}

		/* separate thread for connection process added by TheGeneral */
		// run the connection process in separate thread
		final Thread connectionThread = new Thread() {

			@Override
			public void run() {
				// initialize progress bar
				progressBar.start();
				// disable this screen when attempting to connect
				setEnabled(false);


				try {
					client.connect(server, finalPort);
					// for each major connection milestone call step()
					progressBar.step();
				} catch (final Exception ex) {
					// if something goes horribly just cancel the progress bar
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							getOwner(),
							"Unable to connect to server to create your account. The server may be down or, if you are using a custom server, " +
							"you may have entered its name and port number incorrectly.");

					LOGGER.error(ex, ex);

					return;
				}
				final Window owner = getOwner();
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
						client.setCharacter(accountUsername);

						/*
						 * Once the account is created, login into server.
						 */
						client.login(accountUsername, password);
						progressBar.step();
						progressBar.finish();

						setEnabled(false);
						if (owner != null) {
							owner.setVisible(false);
							owner.dispose();
						}

						stendhal.setDoLogin();
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
							"Your IP is banned.",
							"IP Banned", JOptionPane.ERROR_MESSAGE);
				} catch (final LoginFailedException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(owner, e.getMessage(),
							"Login failed", JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		connectionThread.start();
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
		final Window owner = getOwner();
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
		final String email = (emailField.getText()).trim();
		if  (!validateEmail(email)) {
			final String warning = badEmailReason + "An email address is the only means for administrators to contact with the legitimate owner of an account.\nIf you don't provide one then you won't be able to get a new password for this account if, for example:\n- You forget your password.\n- Another player somehow gets your password and changes it.\nDo you want to continue anyway?";
			final int i = JOptionPane.showOptionDialog(owner, warning, badEmailTitle,
					JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
					null, null, 1);
			if (i != 0) {
				// no, let me type a valid email
				return false;
			}
			// yes, continue anyway
		}
		return true;
	}

	/**
	 * Validate email field format.
	 *
	 * @param email address to be validate
	 * @return <code>true</code> if the email looks good enough, otherwise
	 *	<code>false</code>
	 */
	private boolean validateEmail(final String email) {
		if  (email.isEmpty()) {
			badEmailTitle = "Email address is empty";
			badEmailReason = "You didn't enter an email address.\n";
			return false;
		} else {
			if (!email.contains("@") || !email.contains(".") || (email.length() <= 5)) {
				badEmailTitle =  "Misspelled email address?";
				badEmailReason = "The email address you entered is probably misspelled.\n";
				return false;
			}
		}
		return true;
	}


	/**
	 * Prints text only when running stand-alone.
	 * @param text text to be printed
	 */
	private void debug(final String text) {
		if (client == null) {
			LOGGER.debug(text);
		}
	}

	/**
	 * Used to preview the CreateAccountDialog.
	 * @param args ignored
	 */
	public static void main(final String[] args) {
		new CreateAccountDialog(null, null);
	}

	/**
	 * Do some sanity checks for the password.
	 *
	 * @param username user name
	 * @param password checked password
	 * @return <code>true</code> if the password seems reasonable,
	 *	<code>false</code> if the password should be rejected
	 */
	boolean validatePassword(final String username, final String password) {
		if (password.length() > 5) {

			// check for all numbers
			boolean allNumbers = true;
			try {
				Integer.parseInt(password);
			} catch (final NumberFormatException e) {
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
				final int minUserLength = 3;
				for (int i = 1; i < username.length(); i++) {
					final String subuser = username.substring(0, username.length()
							- i);
					debug("\tchecking for \"" + subuser + "\"...");
					if (subuser.length() <= minUserLength) {
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
						if (subuser.length() <= minUserLength) {
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
				JOptionPane.showMessageDialog(getOwner(), text);
			} else {
				LOGGER.warn(text);
			}
			return false;
		}

		return true;
	}

	/**
	 * A document that can contain only lower case characters.
	 */
	private static class LowerCaseLetterDocument extends PlainDocument {
		@Override
		public void insertString(final int offs, final String str, final AttributeSet a)
				throws BadLocationException {
			final String lower = str.toLowerCase(Locale.ENGLISH);
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
}
