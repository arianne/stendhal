package games.stendhal.client.gui.laf;

import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;

/**
 * Stendhal UI theme derived from the metal look and theme.
 */
public class StendhalTheme extends DefaultMetalTheme // gradient title bars: use base class OceanTheme
{

    public String getName() {return "Stendhal";}

    private static final ColorUIResource color1 = new ColorUIResource(0xb09257);   
    private static final ColorUIResource color2 = new ColorUIResource(0x936138);    // slider on chat window
    private static final ColorUIResource color3 = new ColorUIResource(0x7b5a2b);	// active title bar and selected text

    private static final ColorUIResource color4 = new ColorUIResource(0xefe5ac);
    private static final ColorUIResource color5 = new ColorUIResource(0xF0CFE5);	// buttons
    private static final ColorUIResource color6 = new ColorUIResource(0x6a3e1a);	// background

    protected ColorUIResource getPrimary1() {return color1;}
    protected ColorUIResource getPrimary2() {return color2;}
    protected ColorUIResource getPrimary3() {return color3;}

    protected ColorUIResource getSecondary1() {return color4;}
    protected ColorUIResource getSecondary2() {return color5;}
    protected ColorUIResource getSecondary3() {return color6;}

	/**
	 * Activate the Stendhal look and feel
	 */
	public static void activate() {
		try {
			MetalLookAndFeel.setCurrentTheme(new StendhalTheme());

        	UIManager.setLookAndFeel(new MetalLookAndFeel());
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}

}
