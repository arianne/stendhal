import games.stendhal.server.entity.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

public class Soldiers {
	private StendhalGroovyScript game;

	public Soldiers(StendhalGroovyScript game) {
		this.game = game;
	}

	public void createSoldier(String name, int x, int y) {
		ScriptingNPC npc = new ScriptingNPC(name);		
		npc.setClass("youngsoldiernpc");
		npc.set(x, y);
		npc.setDirection(Direction.DOWN);
		game.add(npc)
	}
}


game.setZone("0_ados_outside_nw");
Soldiers soldiers = new Soldiers(game);
soldiers.createSoldier("Soldier", 55, 47);
soldiers.createSoldier("Soldier", 56, 47);
soldiers.createSoldier("Soldier", 57, 47);
