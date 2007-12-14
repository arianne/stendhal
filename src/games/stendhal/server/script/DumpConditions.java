/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.PreTransitionCondition;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.npc.fsm.TransitionList;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.scripting.ScriptImpl;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Dumps the transition table of an NPC for "dot" http://www.graphviz.org/ to
 * generate a nice graph.
 * 
 * @author hendrik
 */
public class DumpConditions extends ScriptImpl {

	private static Logger logger = Logger.getLogger(DumpConditions.class);

	private StringBuilder dumpedTable;

	@Override
	public void execute(Player admin, List<String> args) {
		dumpedTable = new StringBuilder();
		Set<String> npcs = NPCList.get().getNPCs();
		for (String npcName : npcs) {
			dump(NPCList.get().get(npcName));
		}
		System.out.println(dumpedTable.toString());
	}

	private void dump(SpeakerNPC npc) {
		dumpNPC(npc);
	}

	private void dumpNPC(SpeakerNPC npc) {
		TransitionList transitions = new TransitionList(npc.getTransitions());
		Set<Integer> states = transitions.getSourceStates();

		for (Integer stateInt : states) {
			int state = stateInt.intValue();
			Set<String> triggers = transitions.getTriggersForState(state);
			for (String trigger : triggers) {
				List<Transition> trans = transitions.getTransitionsForStateAndTrigger(
						state, trigger);
				Set<PreTransitionCondition> conditions = new HashSet<PreTransitionCondition>();
				for (Transition tran : trans) {
					PreTransitionCondition condition = tran.getCondition();
					if (condition != null) {
						conditions.add(condition);
					}
				}
				if (conditions.size() > 1) {
					dumpedTable.append(npc.getName() + "\t"
							+ getStateName(state) + "\t" + trigger + "\t"
							+ conditions + "\n");
				}
			}
		}
	}

	private static String getStateName(int number) {
		Integer num = Integer.valueOf(number);
		Field[] fields = ConversationStates.class.getFields();
		for (Field field : fields) {
			try {
				if (field.get(null).equals(num)) {
					return field.getName();
				}
			} catch (IllegalArgumentException e) {
				logger.error(e, e);
			} catch (IllegalAccessException e) {
				logger.error(e, e);
			}
		}
		return Integer.toString(number);
	}

}
