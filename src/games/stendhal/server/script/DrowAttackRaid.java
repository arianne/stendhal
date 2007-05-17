package games.stendhal.server.script;

import games.stendhal.common.MathHelper;
import games.stendhal.server.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hendrik
 */
public class DrowAttackRaid extends ScriptImpl {
	private static Map<String,Integer> attackArmy;
	
	static {
		attackArmy=new HashMap<String, Integer>();
		attackArmy.put("dark_elf",30);
		attackArmy.put("dark_elf_archer",10);
		attackArmy.put("dark_elf_elite_archer",5);
		attackArmy.put("dark_elf_captain",7);
		attackArmy.put("dark_elf_knight",3);
		attackArmy.put("dark_elf_general",1);
		attackArmy.put("dark_elf_wizard",2);
		attackArmy.put("dark_elf_viceroy",1);
		attackArmy.put("dark_elf_sacerdotist",3);
		attackArmy.put("dark_elf_matronmother",1);
	}

	@Override
	public void execute(Player admin, List<String> args) {

		// help
		if (args.size() == 0) {
			admin.sendPrivateText("/script DrowAttackRaid <dificulty=[0-10]>");
			return;
		}

		// extract position of admin
		StendhalRPZone myZone = sandbox.getZone(admin);
		int x = admin.getX();
		int y = admin.getY();
		sandbox.setZone(myZone);

		for(Map.Entry<String, Integer> entry: attackArmy.entrySet()) {
			Creature creature = sandbox.getCreature(entry.getKey());
			
			for(int i=0;i<entry.getValue();i++) {
				sandbox.add(creature,x+games.stendhal.common.Rand.rand(0, 20),y+games.stendhal.common.Rand.rand(0, 20));
			}			
		}
	}
}
