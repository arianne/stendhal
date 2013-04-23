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
package games.stendhal.client.gui;

import javax.swing.JOptionPane;

/**
 * 
 * @author timothyb89
 */
class PasswordDialog extends javax.swing.JFrame {
	private static final long serialVersionUID = -1607102841664745919L;

	/** Creates new form PasswordDialog. */
	public PasswordDialog() {
		initComponents();
	}

	
	private void initComponents() {
		jLabel1 = new javax.swing.JLabel();
		currrentPass = new javax.swing.JPasswordField();
		jLabel2 = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		jLabel3 = new javax.swing.JLabel();
		newPass = new javax.swing.JPasswordField();
		jLabel4 = new javax.swing.JLabel();
		newPassRepeat = new javax.swing.JPasswordField();
		jLabel5 = new javax.swing.JLabel();
		acceptButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();

		getContentPane().setLayout(null);

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Password Management");
		setMinimumSize(new java.awt.Dimension(425, 300));
		jLabel1.setFont(new java.awt.Font("Dialog", 3, 14));
		jLabel1.setText("Password Management");
		getContentPane().add(jLabel1);
		jLabel1.setBounds(10, 10, 166, 17);

		currrentPass.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseClicked(final java.awt.event.MouseEvent evt) {
				currrentPassMouseClicked(evt);
			}
		});

		getContentPane().add(currrentPass);
		currrentPass.setBounds(140, 70, 240, 30);

		jLabel2.setText("Change Password");
		getContentPane().add(jLabel2);
		jLabel2.setBounds(10, 40, 111, 15);

		getContentPane().add(jSeparator1);
		jSeparator1.setBounds(10, 60, 390, 10);

		jLabel3.setText("Current Password");
		getContentPane().add(jLabel3);
		jLabel3.setBounds(10, 70, 111, 30);

		newPass.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(final java.awt.event.KeyEvent evt) {
				newPassKeyTyped(evt);
			}
		});

		getContentPane().add(newPass);
		newPass.setBounds(120, 110, 240, 30);

		jLabel4.setText("New Password");
		getContentPane().add(jLabel4);
		jLabel4.setBounds(10, 110, 100, 30);

		newPassRepeat.addKeyListener(new java.awt.event.KeyAdapter() {
			@Override
			public void keyTyped(final java.awt.event.KeyEvent evt) {
				newPassRepeatKeyTyped(evt);
			}
		});

		getContentPane().add(newPassRepeat);
		newPassRepeat.setBounds(160, 150, 240, 30);

		jLabel5.setText("Repeat New Password");
		getContentPane().add(jLabel5);
		jLabel5.setBounds(10, 150, 137, 30);

		acceptButton.setText("Accept");
		acceptButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				acceptButtonActionPerformed(evt);
			}
		});

		getContentPane().add(acceptButton);
		acceptButton.setBounds(10, 210, 75, 25);

		cancelButton.setText("Cancel");
		getContentPane().add(cancelButton);
		cancelButton.setBounds(100, 210, 75, 25);

		pack();
	} 

	private void acceptButtonActionPerformed(final java.awt.event.ActionEvent evt) { 
		if (checkPass(newPass.getPassword(), newPassRepeat.getPassword())) {
			// check for server password and see if that is accepted
			// remove this- just for testing.
			JOptionPane.showMessageDialog(null, "Passwords Match"); 
		} else {
			JOptionPane.showMessageDialog(null,
					"It seems the passwords you entered do not match. Please try again.");
		}
	} 

	private void newPassRepeatKeyTyped(final java.awt.event.KeyEvent evt) { 
		checkPass(newPass.getPassword(), newPassRepeat.getPassword()); 
	} 

	private void newPassKeyTyped(final java.awt.event.KeyEvent evt) { 
		checkPass(newPass.getPassword(), newPassRepeat.getPassword()); 
	} 

	private boolean clear;

	private void currrentPassMouseClicked(final java.awt.event.MouseEvent evt) { 
		if (!clear) {
			currrentPass.setText("");
			clear = true;
		}
	} 

	/**
	 * Checks to see if the two new passwords match.
	 * @param pwField_One 
	 * @param pwField_Two 
	 * @return true if passwords are equal
	 */
	boolean checkPass(final char[] pwField_One, final char[] pwField_Two) {
		return new String(pwField_One).equals(new String(pwField_Two));
	}

	/**
	 * @param args
	 *            the command line arguments
	 */
	public static void main(final String[] args) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				new PasswordDialog().setVisible(true);
			}
		});
	}


	private javax.swing.JButton acceptButton;

	private javax.swing.JButton cancelButton;

	private javax.swing.JPasswordField currrentPass;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JLabel jLabel2;

	private javax.swing.JLabel jLabel3;

	private javax.swing.JLabel jLabel4;

	private javax.swing.JLabel jLabel5;

	private javax.swing.JSeparator jSeparator1;

	private javax.swing.JPasswordField newPass;

	private javax.swing.JPasswordField newPassRepeat;


}
