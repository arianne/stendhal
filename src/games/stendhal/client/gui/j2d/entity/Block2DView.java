/**
 * 
 */
package games.stendhal.client.gui.j2d.entity;

import games.stendhal.client.entity.Block;

/**
 * View of a pushable block
 * @author madmetzger
 */
public class Block2DView extends Entity2DView<Block> {

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getZIndex() {
        // blocks should be at the same z index as players
        return 8000;
    }

}
