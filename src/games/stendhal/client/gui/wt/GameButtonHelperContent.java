/*
 * PurchaseHelperContent.java
 *
 * Created on April 22, 2007, 8:58 PM
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.gui.j2DClient;
import games.stendhal.client.gui.HelpDialog;
import games.stendhal.client.gui.styled.WoodStyle;
import games.stendhal.client.gui.styled.swing.StyledJButton;

/**
 * 
 * @author timothyb89
 */
class GameButtonHelperContent extends javax.swing.JPanel {
	private static final long serialVersionUID = -1607102841664745919L;

	

	/** Creates new form PurchaseHelperContent. 
	 * @param sp 
	 * @param gbh 
	 * @param ui */
	protected GameButtonHelperContent(final SettingsPanel sp, final GameButtonHelper gbh,
			final j2DClient ui) { 
		// settings panel for the setVisable stuff
		initComponents();
		

	}

	private void initComponents() {
		final WoodStyle style = new WoodStyle();
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
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				ghActionPerformed(evt);
			}
		});

		add(gh);
		gh.setBounds(0, 0, 150, 25);

		jButton1.setText("Purchase Helper");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(final java.awt.event.ActionEvent evt) {
				jButton1ActionPerformed(evt);
			}
		});

		add(jButton1);
		jButton1.setBounds(0, 30, 150, 25);

	} 

	private void jButton1ActionPerformed(final java.awt.event.ActionEvent evt) { 
	// if (sp.buywindow.isVisible()) {
	// sp.buywindow.setVisible(false); // so very useful...
	// } else {
	// sp.buywindow.setVisible(true);
	// }
	} 

	private void ghActionPerformed(final java.awt.event.ActionEvent evt) { 
		new HelpDialog().display();
	} 

	private StyledJButton gh;

	private StyledJButton jButton1;

}
