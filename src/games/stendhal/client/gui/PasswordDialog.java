/*
 * PasswordDialog.java
 *
 * Created on April 14, 2007, 11:58 AM
 */

package games.stendhal.client.gui;

import javax.swing.JOptionPane;

/**
 *
 * @author  timothyb89
 */
public class PasswordDialog extends javax.swing.JFrame {
    
    /** Creates new form PasswordDialog */
    public PasswordDialog() {
	initComponents();
    }

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
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
        jLabel6 = new javax.swing.JLabel();
        passLabel = new javax.swing.JLabel();
        acceptButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        menuBar = new javax.swing.JMenuBar();
        menu = new javax.swing.JMenu();
        menu_exit = new javax.swing.JMenuItem();

        getContentPane().setLayout(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Password Management");
        setMinimumSize(new java.awt.Dimension(425, 300));
        jLabel1.setFont(new java.awt.Font("Dialog", 3, 14));
        jLabel1.setText("Password Management");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(10, 10, 166, 17);

        currrentPass.setText("Password");
        currrentPass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                currrentPassMouseClicked(evt);
            }
        });

        getContentPane().add(currrentPass);
        currrentPass.setBounds(20, 70, 240, 30);

        jLabel2.setText("Change Password");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 40, 111, 15);

        getContentPane().add(jSeparator1);
        jSeparator1.setBounds(10, 60, 390, 10);

        jLabel3.setText("Current Password");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(270, 75, 111, 20);

        newPass.setText("Password");
        newPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newPassKeyTyped(evt);
            }
        });

        getContentPane().add(newPass);
        newPass.setBounds(20, 110, 240, 30);

        jLabel4.setText("New Password");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(270, 115, 100, 20);

        newPassRepeat.setText("Password");
        newPassRepeat.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                newPassRepeatKeyTyped(evt);
            }
        });

        getContentPane().add(newPassRepeat);
        newPassRepeat.setBounds(20, 150, 240, 30);

        jLabel5.setText("Repeat New Password");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(270, 155, 137, 20);

        jLabel6.setText("Passwords:");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(210, 200, 70, 20);

        passLabel.setText("Do not match.");
        getContentPane().add(passLabel);
        passLabel.setBounds(290, 200, 90, 20);

        acceptButton.setText("Accept");
        acceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptButtonActionPerformed(evt);
            }
        });

        getContentPane().add(acceptButton);
        acceptButton.setBounds(10, 240, 75, 25);

        cancelButton.setText("Cancel");
        getContentPane().add(cancelButton);
        cancelButton.setBounds(110, 240, 75, 25);

        menu.setText("Menu");
        menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dispose(evt);
            }
        });

        menu_exit.setText("Close");
        menu_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menu_exitActionPerformed(evt);
            }
        });

        menu.add(menu_exit);

        menuBar.add(menu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void acceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_acceptButtonActionPerformed
	if (passCorrect) {
	    //check for server password and see if that is accepted
	    JOptionPane.showMessageDialog(null, "Passwords Match"); // remove this- just for testing.
	} else {
	    JOptionPane.showMessageDialog(null, "It seems the passwords you entered do not match. Please try again.");
	}
    }//GEN-LAST:event_acceptButtonActionPerformed

    private void newPassRepeatKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newPassRepeatKeyTyped
	checkPass();
    }//GEN-LAST:event_newPassRepeatKeyTyped

    private void newPassKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newPassKeyTyped
	checkPass();
    }//GEN-LAST:event_newPassKeyTyped

    private boolean clear = false;
    private void currrentPassMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_currrentPassMouseClicked
	if (!clear) {
	    currrentPass.setText("");
	    clear = true;
	}
    }//GEN-LAST:event_currrentPassMouseClicked

    private void menu_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menu_exitActionPerformed
	//  |
	//  |
	// \/
    }//GEN-LAST:event_menu_exitActionPerformed

    private void dispose(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dispose
	dispose();
    }//GEN-LAST:event_dispose
    
    
    /**
     * Checks to see if the two new passwords match
     */
    private boolean passCorrect = false;
    private void checkPass() {
	if (newPass.getPassword() == newPassRepeat.getPassword()) {
	    passCorrect = true;
	    passLabel.setText("Match");
	}
	else {
	    passLabel.setText("Do not match.");
	    passCorrect = false;
	}
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
	java.awt.EventQueue.invokeLater(new Runnable() {
	    public void run() {
		new PasswordDialog().setVisible(true);
	    }
	});
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton acceptButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPasswordField currrentPass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenu menu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem menu_exit;
    private javax.swing.JPasswordField newPass;
    private javax.swing.JPasswordField newPassRepeat;
    private javax.swing.JLabel passLabel;
    // End of variables declaration//GEN-END:variables
    
}
