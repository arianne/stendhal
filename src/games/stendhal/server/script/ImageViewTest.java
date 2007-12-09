package games.stendhal.server.script;

import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;
import java.util.List;
import marauroa.common.game.Definition;
import marauroa.common.game.Definition.DefinitionClass;
import marauroa.common.game.RPClass;
import marauroa.common.game.RPEvent;

/**
 * A simple test for the client ImageViewer.
 * @author timothyb89
 */
public class ImageViewTest extends ScriptImpl {

    public static void generateRPEvent() {
        RPClass rpclass = new RPClass("examine");
        rpclass.add(DefinitionClass.RPEVENT, "examine", Definition.STANDARD);
    }

    @Override
    public void execute(Player admin, List<String> args) {
        generateRPEvent();

        RPEvent event = new RPEvent("examine");
        event.put("path", "/data/gui/StendhalSplash.jpg");
        event.put("alt", "Test image");
        event.put("title", "Image viewer test");
        
        admin.addEvent(event);
    }
}
