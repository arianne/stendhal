/* $Id$ */
package games.stendhal.server.script;

import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.NPCList;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.player.Player;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Dumps the transition table of an NPC for "dot" http://www.graphviz.org/ to
 * generate a nice graph.
 * 
 * @author hendrik
 */
public class DumpTransitions extends ScriptImpl {

	private static Logger logger = Logger.getLogger(DumpTransitions.class);

	private StringBuilder dumpedTable;

	@Override
	public void execute(Player admin, List<String> args) {
		if (args.size() < 1) {
			admin.sendPrivateText("/script DumpTransitions.class <npcname>");
			return;
		}

		StringBuilder npcName = new StringBuilder();
		for (String arg : args) {
			npcName.append(arg + " ");
		}
		SpeakerNPC npc = NPCList.get().get(npcName.toString().trim());
		if (npc == null) {
			admin.sendPrivateText("There is no NPC called " + npcName);
			return;
		}
		dump(npc);
		admin.sendPrivateText("Transition table of " + npcName + "\r\n"
				+ dumpedTable.toString());
	}

	/**
	 * Returns the transition diagram as string.
	 * 
	 * @param npc
	 *            SpeakerNPC
	 * @return transition diagram
	 */
	public String getDump(SpeakerNPC npc) {
		dump(npc);
		return dumpedTable.toString();
	}

	private void dump(SpeakerNPC npc) {
		dumpedTable = new StringBuilder();
		dumpHeader();
		dumpNPC(npc);
		dumpFooter();
	}

	private void dumpHeader() {
		dumpedTable.append("digraph finite_state_machine {\r\n");
		dumpedTable.append("rankdir=LR\r\n");
	}

	private void dumpNPC(SpeakerNPC npc) {
		List<Transition> transitions = npc.getTransitions();
		for (Transition transition : transitions) {
			dumpedTable.append(getStateName(transition.getState()) + " -> "
					+ getStateName(transition.getNextState()));
			String transitionName = getExtendedTransitionName(transition);
			dumpedTable.append(" [ label = \"" + transitionName + "\" ];\r\n");
		}
	}

	private static String getExtendedTransitionName(Transition transition) {
		String transitionName = transition.getTrigger().getNormalized();
		if (transition.getCondition() != null) {
			transitionName = "~ " + transitionName;
		}
		if (transition.getAction() != null) {
			transitionName = transitionName + " *";
		}
		return transitionName;
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

	private void dumpFooter() {
		dumpedTable.append("}");
	}

}
