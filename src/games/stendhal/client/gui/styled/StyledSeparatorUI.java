package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicSeparatorUI;

/**
 * Stendhal look and feel for JSeparators.
 */
public class StyledSeparatorUI extends BasicSeparatorUI {
	/** Shared UI instance */
	private static StyledSeparatorUI instance;
	/** Used style */
	private final Style style;
	
	/**
	 * Create StyledSeparatorUI for a separator.
	 * 
	 * @param separator <code>JSeparator</code> to create an UI for
	 * @return a ComponentUI instance
	 */
	// required by UIManager. Not necessarily called from on thread
	public static synchronized ComponentUI createUI(JComponent separator) {
		// Separator UIs can be shared
		if (instance == null) {
			instance = new StyledSeparatorUI(StyleUtil.getStyle());
		}
		
		return instance;
	}
	
	/**
	 * Create a new StyledSeparatorUI.
	 * 
	 * @param style pixmap style
	 */
	public StyledSeparatorUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void installUI(JComponent separator) {
		super.installUI(separator);
		separator.setBackground(style.getHighLightColor());
		separator.setForeground(style.getShadowColor());
	}
}
