package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.condition.LevelLessThanCondition;
import games.stendhal.server.entity.player.Player;

import java.util.List;

public class MinlevelForHarold extends ScriptImpl {

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		NPCList.get().get("Harold").
			add(ConversationStates.ATTENDING, "sell", 
				new LevelLessThanCondition(6), 
				ConversationStates.ATTENDING, 
				"I am sorry, I currently only accepting offers from people who have a good reputation. You can obtain acceptance by gaining experiance for example by helping people with their tasks or defending the city from evil creatures.", null);

	}

}
