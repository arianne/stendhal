package games.stendhal.client.gui.styled;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTextFieldUI;

/**
 * TextUI using pixmap styles for JTextFields.
 */
public class StyledTextFieldUI extends BasicTextFieldUI {
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
		button.setBorder(style.getBorderDown());
	}
}
