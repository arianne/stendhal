/*
 * PurchaseHelperContent.java
 *
 * Created on April 22, 2007, 8:58 PM
 */

package games.stendhal.client.gui.wt;

import games.stendhal.client.StendhalUI;
import games.stendhal.client.gui.HelpDialog;

import games.stendhal.client.gui.styled.swing.StyledJButton;
import games.stendhal.client.gui.styled.WoodStyle;

/**
 *
 * @author  timothyb89
 */
public class GameButtonHelperContent extends javax.swing.JPanel {
	private static final long serialVersionUID = -1607102841664745919L;

    SettingsPanel sp;
    
    StendhalUI ui;
    
    /** Creates new form PurchaseHelperContent */
    public GameButtonHelperContent(SettingsPanel sp, GameButtonHelper gbh, StendhalUI ui) {//settings panel for the setVisable stuff
	initComponents();
	this.sp = sp;
	this.ui = ui;
    }
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {

	WoodStyle style = new WoodStyle();

        gh = new StyledJButton();
        ph = new javax.swing.JToggleButton();

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
        gh.setBounds(-10, 0, 160, 25);

        ph.setText("Purchase Helper");
        ph.setOpaque(false);
        ph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phActionPerformed(evt);
            }
        });

        add(ph);
        ph.setBounds(-20, 30, 170, 25);

    }// </editor-fold>//GEN-END:initComponents

    private void phActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phActionPerformed
	if (sp.buywindow.isVisible()) {
	    sp.buywindow.setVisible(false); //so very useful...
	} else {
	    sp.buywindow.setVisible(true);
	}
    }//GEN-LAST:event_phActionPerformed

    private void ghActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ghActionPerformed
    	new HelpDialog().display();
    }//GEN-LAST:event_ghActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private StyledJButton gh;
    private javax.swing.JToggleButton ph;
    // End of variables declaration//GEN-END:variables
    
}
