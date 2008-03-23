package games.stendhal.client.gui.laf;

import java.awt.Font;

import javax.swing.JComponent;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;

import sun.swing.plaf.synth.DefaultSynthStyle;

/**
 * Because of bugs in the Synth Look&Feel implementation of Java 1.5,
 * we have to use a style factory to plg in our own L&F routines.
 *
 * In Java 1.6 it would be possible to use WoodLookAndFeel and the file
 * "woodskin.xml" to achieve the wood look and feel using XML configuration.
 *
 * @author Martin Fuchs
 */
public class WoodStyleFactory extends SynthStyleFactory {

    /**
     * Wood style objects.
     */
    private final DefaultSynthStyle woodStyle;
    private final DefaultSynthStyle woodTitleStyle;

    private final DefaultSynthStyle woodButtonStyle;

    private final DefaultSynthStyle defaultStyle;

	public WoodStyleFactory() {
		woodStyle = new DefaultSynthStyle();
		woodStyle.setFont(new FontUIResource("Dialog", Font.PLAIN, 12));
		woodStyle.setPainter(new WoodStylePainter("data/gui/panelwood003.jpg"));

		woodButtonStyle = new DefaultSynthStyle();
		woodButtonStyle.setFont(new FontUIResource("Dialog", Font.PLAIN, 12));
		woodButtonStyle.setPainter(new WoodStyleButtonPainter("data/gui/panelwood006.jpg", 2));

		woodTitleStyle = new DefaultSynthStyle();
		woodTitleStyle.setFont(new FontUIResource("Dialog", Font.PLAIN, 12));
		woodTitleStyle.setPainter(new WoodStylePainter("data/gui/panelwood119.jpg"));

		defaultStyle = new DefaultSynthStyle();
		woodTitleStyle.setFont(new FontUIResource("Dialog", Font.PLAIN, 12));
	}

	@Override
    public SynthStyle getStyle(JComponent c, Region id) {
    	if (id == Region.PANEL || id == Region.INTERNAL_FRAME) {
			return woodStyle;
    	} else if (id == Region.INTERNAL_FRAME_TITLE_PANE) {
    		return woodTitleStyle;
    	} else if (id == Region.BUTTON || id.toString().endsWith("Button")) {
			return woodButtonStyle;
        } else {
        	return defaultStyle;
        }
    }

}
