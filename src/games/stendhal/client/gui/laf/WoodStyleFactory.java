package games.stendhal.client.gui.laf;

import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.plaf.synth.Region;
import javax.swing.plaf.synth.SynthLookAndFeel;
import javax.swing.plaf.synth.SynthStyle;
import javax.swing.plaf.synth.SynthStyleFactory;

/**
 * Because of bugs in the Synth Look&Feel implementation of Java 1.5,
 * we have to use a style factory to plug in our own L&F routines.
 *
 * In Java 1.6 it would be possible to use WoodLookAndFeel and the file
 * "woodskin.xml" to achieve the wood look and feel using XML configuration.
 *
 * @author Martin Fuchs
 */
public class WoodStyleFactory extends SynthStyleFactory {

	/**
	 * Activate the Wood look and feel
	 */
	public static void activate() {
		try {
        	UIManager.setLookAndFeel(new SynthLookAndFeel());

        	SynthLookAndFeel.setStyleFactory(new WoodStyleFactory());
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}


	// Wood style objects
	private final SynthStyle woodStyle;
	private final SynthStyle woodTitleStyle;
	private final SynthStyle woodButtonStyle;
//	private final SynthStyle woodMenuStyle;

    // default Synth style for all other controls
    private final SynthStyle defaultStyle;

	public WoodStyleFactory() {
		woodStyle = new WoodSynthStyle("data/gui/panelwood003.jpg");
		woodButtonStyle = new WoodSynthButtonStyle("data/gui/panelwood006.jpg", 2);
		woodTitleStyle = new WoodSynthStyle("data/gui/panelwood119.jpg");
//		woodMenuStyle = new WoodSynthStyle("data/gui/panelwood003.jpg");

		defaultStyle = new WoodSynthBaseStyle(null);
	}

	@Override
    public SynthStyle getStyle(JComponent c, Region id) {
    	if (id == Region.PANEL || id == Region.INTERNAL_FRAME) {
			return woodStyle;
    	} else if (id == Region.INTERNAL_FRAME_TITLE_PANE) {
    		return woodTitleStyle;
    	} else if (id == Region.BUTTON || id.toString().endsWith("Button")) {
			return woodButtonStyle;
//		} else if (id == Region.MENU || id == Region.MENU_BAR || id == Region.MENU_ITEM_ACCELERATOR) {
//			return woodMenuStyle;
        } else {
        	return defaultStyle;
        }
    }

}
