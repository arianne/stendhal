package games.stendhal.client.gui.styled;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * TextUI using pixmap styles for JTextFields.
 */
public class StyledTextFieldUI extends BasicTextFieldUI {
	/** Pixels before the first letter and after the last */
	private static final int PADDING = 2;
	
	private final Style style;
	
	// Required by UIManager
	public static ComponentUI createUI(JComponent field) {
		// Text field UIs can not be shared
		return new StyledTextFieldUI(StyleUtil.getStyle());
	}
	
	/**
	 * Create a new StyledTextFieldUI.
	 * 
	 * @param style pixmap style for drawing the borders
	 */
	public StyledTextFieldUI(Style style) {
		this.style = style;
	}
	
	@Override
	public void installUI(JComponent button) {
		super.installUI(button);
		button.setBorder(BorderFactory.createCompoundBorder(style.getBorderDown(),
				BorderFactory.createEmptyBorder(0, PADDING, 0, PADDING)));
	}
}
