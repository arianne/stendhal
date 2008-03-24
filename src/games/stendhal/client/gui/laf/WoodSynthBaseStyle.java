package games.stendhal.client.gui.laf;

import java.awt.Color;
import java.awt.Font;

import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.synth.ColorType;
import javax.swing.plaf.synth.SynthContext;
import javax.swing.plaf.synth.SynthPainter;
import javax.swing.plaf.synth.SynthStyle;

public class WoodSynthBaseStyle extends SynthStyle {

	private Font defaultFont = new FontUIResource("Dialog", Font.PLAIN, 12);

	private final SynthPainter painter;

	public WoodSynthBaseStyle() {
		this.painter = null;
	}

	public WoodSynthBaseStyle(SynthPainter painter) {
		this.painter = painter;
	}

    /**
     * Returns the color for the specified state. This should NOT call any
     * methods on the <code>JComponent</code>.
     *
     * @param context SynthContext identifying requester
     * @param type Type of color being requested.
     * @return Color to render with
     */
	@Override
	protected Color getColorForState(SynthContext context, ColorType type) {
		if (type == ColorType.FOREGROUND) {
			return Color.LIGHT_GRAY;
		} else {
			return null;
		}
	}

    /**
     * Returns the font for the specified state. This should NOT call any
     * method on the <code>JComponent</code>.
     *
     * @param context SynthContext identifying requester
     * @return Font to render with
     */
	@Override
	protected Font getFontForState(SynthContext context) {
		return defaultFont;
	}

	/**
     * Returns the <code>SynthPainter</code> that will be used for painting.
     * This may return null.
     *
     * @param context SynthContext identifying requester
     * @return SynthPainter to use 
     */
	@Override
    public SynthPainter getPainter(SynthContext context) {
        return painter;
    }

}
