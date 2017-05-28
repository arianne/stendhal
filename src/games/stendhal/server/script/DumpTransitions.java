/* $Id$ */
package games.stendhal.server.script;

import java.util.List;

import games.stendhal.common.parser.Expression;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.player.Player;

/**
 * Dumps the transition table of an NPC for "dot" http://www.graphviz.org/ to
 * generate a nice graph and prints the graph text to the client console.
 *
 * @author hendrik
 */
public class DumpTransitions extends ScriptImpl {

	private StringBuilder dumpedTable;

	@Override
	public void execute(final Player admin, final List<String> args) {
		if (args.size() < 1) {
			admin.sendPrivateText("/script DumpTransitions.class <npcname>");
			return;
		}

		final StringBuilder npcName = new StringBuilder();
		for (final String arg : args) {
			npcName.append(arg + " ");
		}
		final SpeakerNPC npc = SingletonRepository.getNPCList().get(npcName.toString().trim());
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
	public String getDump(final SpeakerNPC npc) {
		dump(npc);
		return dumpedTable.toString();
	}

	private void dump(final SpeakerNPC npc) {
		dumpedTable = new StringBuilder();
		dumpHeader();
		dumpNPC(npc);
		dumpFooter();
	}

	private void dumpHeader() {
		dumpedTable.append("digraph finite_state_machine {\r\n");
		dumpedTable.append("rankdir=LR\r\n");
	}

	private void dumpNPC(final SpeakerNPC npc) {
		final List<Transition> transitions = npc.getTransitions();
		for (final Transition transition : transitions) {
			dumpedTable.append(getStateName(transition.getState()) + " -> "
					+ getStateName(transition.getNextState()));

			for(Expression expr : transition.getTriggers()) {
				final String transitionName = getExtendedTransitionName(transition, expr.toString());

				dumpedTable.append(" [ label = \"" + transitionName + "\" ];\r\n");
			}
		}
	}

	private static String getExtendedTransitionName(final Transition transition, String transitionName) {
		if (transition.getCondition() != null) {
			if (!transition.isPreferred()) {
				transitionName = "-" + transitionName;
			}

			transitionName = "~ " + transitionName;
		}

		if (transition.getAction() != null) {
			transitionName = transitionName + " *";
		}

		return transitionName;
	}

	private static String getStateName(final ConversationStates number) {
		return number.toString();
//		final Integer num = Integer.valueOf(number);
//		final Field[] fields = ConversationStates.class.getFields();
//
//		for (final Field field : fields) {
//			try {
//				if (field.get(null).equals(num)) {
//					return field.getName();
//				}
//			} catch (final IllegalArgumentException e) {
//				logger.error(e, e);
//			} catch (final IllegalAccessException e) {
//				logger.error(e, e);
//			}
//		}
//
//		return Integer.toString(number);
	}

	private void dumpFooter() {
		dumpedTable.append("}");
	}

}
