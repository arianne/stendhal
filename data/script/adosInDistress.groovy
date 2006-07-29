import games.stendhal.server.entity.*
import games.stendhal.server.entity.creature.*
import games.stendhal.server.entity.item.*
import games.stendhal.server.scripting.*
import games.stendhal.server.entity.npc.*;
import games.stendhal.server.pathfinder.Path
import games.stendhal.common.Direction;

public class Friends {
	private StendhalGroovyScript game;

	public Friends(StendhalGroovyScript game) {
		this.game = game;
	}

	public void createSoldier(String name, int x, int y) {
		ScriptingNPC npc = new ScriptingNPC(name);		
		npc.setClass("youngsoldiernpc");
		npc.set(x, y);
		npc.setDirection(Direction.DOWN);
		game.add(npc)
	}

	public void createSoldiers() {
		createSoldier("Soldier", 55, 47);
		createSoldier("Soldier", 56, 47);
		createSoldier("Soldier", 57, 47);
	}
	
	public void createSheep() {
		Creature creature = new Sheep();
		creature.setx(56);
		creature.sety(46);
		game.add(creature);
	}
}

game.setZone("0_ados_outside_nw");
Friends soldiers = new Friends(game);
soldiers.createSoldiers();
soldiers.createSheep();
