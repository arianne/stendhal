package games.stendhal.client.gui.laf;

import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.synth.SynthLookAndFeel;

import data.gui.LFRessourceBase;

/**
 * Look and feel for the Wood style, configured from XML.
 *
 * @author Martin Fuchs
 */
@SuppressWarnings("serial")
public class WoodLookAndFeel extends SynthLookAndFeel {

	/**
	 * Activate the Wood look and feel
	 */
	public static void activate() {
		try {
        	UIManager.setLookAndFeel(new WoodLookAndFeel());
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}


	public WoodLookAndFeel() {
		try {
	        load(WoodLookAndFeel.class.getClassLoader().getResourceAsStream("data/gui/woodskin.xml"), LFRessourceBase.class);
        } catch(Exception e) {
	        e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return "Wood";
    }

    @Override
    public String getID() {
        return "Wood";
    }

    /**
     * Load the SystemColors into the defaults table.  The keys
     * for SystemColor defaults are the same as the names of
     * the public fields in SystemColor.
     */
    protected void initSystemColorDefaults(UIDefaults table)
    {
    	super.initSystemColorDefaults(table);

//        table.put("window", WoodStyle.getInstance().getForeground());
    }

    @Override
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

//        table.put("ButtonUI", "games.stendhal.client.gui.laf.WoodButtonUI");
//        table.put("InternalFrameUI", "games.stendhal.client.gui.laf.WoodInternalFrameUI");
    }

}
