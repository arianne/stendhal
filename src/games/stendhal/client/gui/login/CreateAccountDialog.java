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

		JLabel serverLabel = new JLabel("服务器名称");
		serverField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_SERVER"));
		serverField.setEditable(true);
		JLabel serverPortLabel = new JLabel("服务器端口");
		serverPortField = new JTextField(
				ClientGameConfiguration.get("DEFAULT_PORT"));
		((AbstractDocument) serverPortField.getDocument()).setDocumentFilter(new NumberDocumentFilter(serverPortField, false));

		JLabel usernameLabel = new JLabel("用户名");
		usernameField = new JTextField();
		usernameField.setDocument(new LowerCaseLetterDocument());

		JLabel passwordLabel = new JLabel("登陆密码");
		passwordField = new JPasswordField();

		JLabel passwordretypeLabel = new JLabel("确认密码");
		passwordretypeField = new JPasswordField();

		JLabel emailLabel = new JLabel("邮箱地圵 (可填)");
		emailField = new JTextField();

		// createAccountButton
		//
		JButton createAccountButton = new JButton();
		createAccountButton.setText("注册新用户");
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
		JCheckBox showPWToggle = new JCheckBox("显示密码");
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
		JLabel logLabel = new JLabel("<html><body><p>通过网络发送的登陆信息会被记录（比如一些猜测密码等一些黑客行为引发的事故）<br>另外，游戏中全部的事件和行为（比如打怪和做任务）都会被记录<br>此消息也用于记录不常见的游戏错误</p></body></html>");
		// Add a bit more empty space around it
		logLabel.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
		logLabel.setAlignmentX(CENTER_ALIGNMENT);
		contentPane.add(logLabel, SBoxLayout.constraint(SLayout.EXPAND_X, SLayout.EXPAND_Y));

		// Button row
		JComponent buttonRow = SBoxLayout.createContainer(SBoxLayout.HORIZONTAL, SBoxLayout.COMMON_PADDING);
		buttonRow.setAlignmentX(RIGHT_ALIGNMENT);
		JButton cancelButton = new JButton("退出");
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
		this.setTitle("创建新用户");
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
					"端口号不可用，请重新填写后再试",
					"Invalid Port", JOptionPane.WARNING_MESSAGE);
			return;
		}
		finalPort = port;

		// standalone check
		if (client == null) {
			JOptionPane.showMessageDialog(this,
					"帐户创建失败 (running standalone)!");
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
							"连接服务器失败，创建用户失败，服务器可能己关闭, " +
							"也可能你填入的服务器地圵或端口不正确");

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
								"创建用户失败",
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
							"创建用户失败，可能服务器已关闭，或是你填入的服务器地址或端口不正确。",
							"Error Creating Account", JOptionPane.ERROR_MESSAGE);
				} catch (final InvalidVersionException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"Stendhal版本不匹配. 请升级客户端",
							"Invalid version", JOptionPane.ERROR_MESSAGE);
				} catch (final BannedAddressException e) {
					progressBar.cancel();
					setEnabled(true);
					JOptionPane.showMessageDialog(
							owner,
							"你的IP被拒绝。",
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
					"密码不正确，请重新输入。",
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
			final String warning = badEmailReason + "邮件地圵只用于管理员与你联系，\n如果你没有正确填入邮件，一些安全功能将不可用! 比如:\n- 你忘记密码.\n- 其他用户盗用了你的密码并修改, 密码将无法找回\n你确定不输入邮件地圵?";
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
			badEmailTitle = "Email为空";
			badEmailReason = "没有输入邮件地圵.\n";
			return false;
		} else {
			if (!email.contains("@") || !email.contains(".") || (email.length() <= 5)) {
				badEmailTitle =  "email邮件拼写错误?";
				badEmailReason = "你输入邮件地圵不可用\n";
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
				badPasswordReason = "只使用数字做为密码，将非常不安全\n"
						+ "确定使用这个密码?";
			}

			// check for username
			boolean hasUsername = false;
			if (password.contains(username)) {
				hasUsername = true;
			}

			if (!hasUsername) {
				// now we'll do some more checks to see if the password
				// contains more than three letters of the username
				debug("密码部分与用户名重复，会导致密码泄漏");
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
						debug("密码包含用户名信息!");
						break;
					}
				}

				if (!hasUsername) {
					// now from the end of the password..
					debug("检查密码包含用户名字符，trimming from the front...");
					for (int i = 0; i < username.length(); i++) {
						final String subuser = username.substring(i);
						debug("\tchecking for \"" + subuser + "\"...");
						if (subuser.length() <= minUserLength) {
							break;
						}
						if (password.contains(subuser)) {
							hasUsername = true;
							debug("密码包含用户名信息!");
							break;
						}
					}
				}
			}

			if (hasUsername) {
				badPasswordReason = "你使用了包含用户名信息的密码，这不是安全的做法\n"
						+ " 确定这样设置密码?";
				return false;
			}

		} else {
			final String text = "密码长度太短，最少应为6个字符";
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
