/**
 * 
 */
package games.stendhal.test.gui;

import games.stendhal.client.Sprite;
import games.stendhal.client.SpriteStore;

/**
 * @author mtotz
 * 
 */
public class SwingUtils {
	private static final String BACKGROUND_IMAGE = "data/gui/paneldrock009.jpg";

	/** the normal sprite */
	private Sprite normalSprite;

	/** the dark wood sprite */
	private Sprite darkSprite;

	/** the bright wood sprite */
	private Sprite brightSprite;

	private static SwingUtils instance;

	/** no public constructor */
	private SwingUtils() {
		normalSprite = SpriteStore.get().getSprite(BACKGROUND_IMAGE);
		darkSprite = normalSprite.darker();
		brightSprite = normalSprite.brighter();
	}

	/** returns the shared instance */
	public static synchronized SwingUtils getInstance() {
		if (instance == null) {
			instance = new SwingUtils();
		}
		return instance;
	}

	/**
	 * @return Returns the brightSprite.
	 */
	public Sprite getBrightSprite() {
		return brightSprite;
	}

	/**
	 * @return Returns the darkSprite.
	 */
	public Sprite getDarkSprite() {
		return darkSprite;
	}

	/**
	 * @return Returns the normalSprite.
	 */
	public Sprite getNormalSprite() {
		return normalSprite;
	}
}
