package games.stendhal.client.actions;

import static org.junit.Assert.assertEquals;

import games.stendhal.server.entity.npc.action.EquipRandomAmountOfItemAction;
import org.junit.Test;

public class EquipRandomAmountOfItemActionTest {

    /**
     * Tests for equals.
     */
    @Test
    public void testEquals() {
        EquipRandomAmountOfItemAction x = new EquipRandomAmountOfItemAction("Hello", 0, 1);
        assertEquals(x, x);
    }
}


