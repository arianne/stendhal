package games.stendhal.client.gui.styled;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicLabelUI;

public class StyledLabelUI extends BasicLabelUI {
	private static StyledLabelUI instance;
	
	private final Style style;
	
	/**
	 * Create StyledLabelUI for a label.
	 * 
	 * @param label <code>JLabel</code> to create an UI for
	 * @return a ComponentUI instance
	 */
	// required by UIManager. Not necessarily called from on thread
	public static synchronized ComponentUI createUI(JComponent label) {
		// Label UIs can be shared
		if (instance == null) {
			instance = new StyledLabelUI(StyleUtil.getStyle());
		}
		
		return instance;
	}
	
	/**
	 * Create a new StyledLabelUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledLabelUI(Style style) {
		this.style = style;
	}
	
	@Override
	protected void paintDisabledText(JLabel label, Graphics graphics, String text, int x, int y) {
		StyleUtil.paintDisabledText(style, graphics, text, x, y);
	}
	
	@Override
	public void installUI(JComponent label) {
		super.installUI(label);
		label.setForeground(style.getForeground());
		label.setOpaque(false);
		label.setFont(style.getFont().deriveFont(Font.BOLD));
	}
}
