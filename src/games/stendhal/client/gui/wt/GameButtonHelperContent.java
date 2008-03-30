/*
 * PurchaseHelperContent.java
 *
 * Created on April 22, 2007, 8:58 PM
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.ClientPanel;
import games.stendhal.client.gui.HelpDialog;

import javax.swing.JButton;

/**
 * 
 * @author timothyb89
 */
@SuppressWarnings("serial")
public class GameButtonHelperContent extends ClientPanel {

	/** Creates new form PurchaseHelperContent. */
	public GameButtonHelperContent(SettingsPanel sp, StendhalUI ui) { 
		super("gametools", 100, 100);

		// settings panel for the setVisible() stuff
		initComponents();
	}

	private void initComponents() {
//		WoodStyle style = new WoodStyle();
		gh = new JButton();
		jButton1 = new JButton();

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

	}

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
	private JButton gh;

	private JButton jButton1;
	// End of variables declaration//GEN-END:variables

}
