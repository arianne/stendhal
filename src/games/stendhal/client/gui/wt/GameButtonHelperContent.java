/*
 * PurchaseHelperContent.java
 *
 * Created on April 22, 2007, 8:58 PM
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.HelpDialog;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJButton;

/**
 * 
 * @author timothyb89
 */
public class GameButtonHelperContent extends javax.swing.JPanel {
	private static final long serialVersionUID = -1607102841664745919L;

	

	/** Creates new form PurchaseHelperContent. */
	public GameButtonHelperContent(SettingsPanel sp, GameButtonHelper gbh,
			StendhalUI ui) { // settings panel for the setVisable stuff
		initComponents();
		

	}

	// <editor-fold defaultstate="collapsed" desc=" Generated Code
	// ">//GEN-BEGIN:initComponents
	private void initComponents() {
		WoodStyle style = new WoodStyle();
		gh = new StyledJButton(style);
		jButton1 = new StyledJButton(style);

		setLayout(null);

		setMaximumSize(new java.awt.Dimension(100, 150));
		setMinimumSize(new java.awt.Dimension(100, 150));
		setOpaque(false);
		setPreferredSize(new java.awt.Dimension(100, 150));
		gh.setText("Game Help");
		gh.setOpaque(false);
		gh.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				ghActionPerformed(evt);
			}
		});

		add(gh);
		gh.setBounds(0, 0, 150, 25);

		jButton1.setText("Purchase Helper");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		add(jButton1);
		jButton1.setBounds(0, 30, 150, 25);

	} // </editor-fold>//GEN-END:initComponents

	private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_jButton1ActionPerformed
	// TODO: Do right
	// if (sp.buywindow.isVisible()) {
	// sp.buywindow.setVisible(false); // so very useful...
	// } else {
	// sp.buywindow.setVisible(true);
	// }
	} // GEN-LAST:event_jButton1ActionPerformed

	private void ghActionPerformed(java.awt.event.ActionEvent evt) { // GEN-FIRST:event_ghActionPerformed
		new HelpDialog().display();
	} // GEN-LAST:event_ghActionPerformed

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private StyledJButton gh;

	private StyledJButton jButton1;
	// End of variables declaration//GEN-END:variables

}
