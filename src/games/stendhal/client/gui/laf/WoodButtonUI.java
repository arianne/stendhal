package games.stendhal.client.gui.laf;

import games.stendhal.client.gui.styled.Style;
import games.stendhal.client.gui.styled.WoodStyle;

import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.metal.MetalButtonUI;

/**
 * Look and feel button UI class for the Wood style.
 *
 * @author Martin Fuchs
 */
public class WoodButtonUI extends MetalButtonUI {

	private static final Style style = WoodStyle.getInstance();

	public WoodButtonUI() {
		
	}

	@Override
    public void update(Graphics g, JComponent c) {
        AbstractButton button = (AbstractButton) c;

        if ((c.getBackground() instanceof UIResource)
        		&& button.isContentAreaFilled() && c.isEnabled()) {
            ButtonModel model = button.getModel();

            if (!(c.getParent() instanceof JToolBar)) {
                if (!model.isArmed() && !model.isPressed()) {
               		style.getBackground().draw(g, 0, 0, 0, 0, c.getWidth(), c.getHeight());
                    paint(g, c);
                    return;
                }
            } else if (model.isRollover()) {
            	style.getBackground().draw(g, 0, 0, 0, 0, c.getWidth(), c.getHeight());
                paint(g, c);
                return;
            }
        }

        super.update(g, c);
    }

}
