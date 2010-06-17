package games.stendhal.client.gui.styled;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JProgressBar;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicProgressBarUI;

public class StyledProgressBarUI extends BasicProgressBarUI {
	private final Style style;
	
	// Required by UIManager
	/**
	 * Create a new StyledProgressBarUI using the current Stendhal style.
	 * 
	 * @param bar a JProgressBar
	 * @return ComponentUI for <code>bar</code>
	 */
	public static ComponentUI createUI(JComponent bar) {
		return new StyledProgressBarUI(StyleUtil.getStyle());
	}
	
	/**
	 * Create a new StyledProgressBarUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledProgressBarUI(Style style) {
		this.style = style;
	}
	
	@Override
	protected void paintDeterminate(Graphics g, JComponent component) {
		// OK, whose bright idea was to have JComponent as a parameter here
		JProgressBar bar = (JProgressBar) component;
		
		// Background color
		g.setColor(style.getHighLightColor());
		g.fillRect(0, 0, bar.getWidth(), bar.getHeight());
		
		// Wood and border
		Insets insets = bar.getInsets();
		int width = getAmountFull(insets, bar.getWidth(), bar.getHeight());
		StyleUtil.fillBackground(style, g, 0, 0, width, bar.getHeight());
		style.getBorder().paintBorder(component, g, insets.left, insets.top, 
				width, bar.getHeight() - insets.left - insets.right);
	}
	
	@Override
	public void installUI(JComponent bar) {
		super.installUI(bar);
		bar.setBorder(style.getBorderDown());
	}
}
