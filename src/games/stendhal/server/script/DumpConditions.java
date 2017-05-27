/* $Id$ */
package games.stendhal.server.script;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import games.stendhal.common.parser.Expression;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.fsm.PreTransitionCondition;
import games.stendhal.server.entity.npc.fsm.Transition;
import games.stendhal.server.entity.npc.fsm.TransitionList;
import games.stendhal.server.entity.player.Player;

/**
 * Dumps the transition table of an NPC for "dot" http://www.graphviz.org/ to
 * generate a nice graph.
 *
 * @author hendrik
 */
public class DumpConditions extends ScriptImpl {

	private StringBuilder dumpedTable;

	@Override
	public void execute(final Player admin, final List<String> args) {
		dumpedTable = new StringBuilder();
		final Set<String> npcs = SingletonRepository.getNPCList().getNPCs();
		for (final String npcName : npcs) {
			dump(SingletonRepository.getNPCList().get(npcName));
		}
		System.out.println(dumpedTable.toString());
	}

	private void dump(final SpeakerNPC npc) {
		dumpNPC(npc);
	}

	private void dumpNPC(final SpeakerNPC npc) {
		final TransitionList transitions = new TransitionList(npc.getTransitions());
		final Set<ConversationStates> states = transitions.getSourceStates();

		for (final ConversationStates state : states) {
			final Collection<Expression> triggers = transitions.getTriggersForState(state);
			for (final Expression trigger : triggers) {
				final List<Transition> trans = transitions.getTransitionsForStateAndTrigger(
						state, trigger);
				final Set<PreTransitionCondition> conditions = new HashSet<PreTransitionCondition>();
				for (final Transition tran : trans) {
					final PreTransitionCondition condition = tran.getCondition();
					if (condition != null) {
						conditions.add(condition);
					}
				}
				if (conditions.size() > 1) {
					dumpedTable.append(npc.getName() + "\t"
							+ state.toString() + "\t" + trigger + "\t"
							+ conditions + "\n");
				}
			}
		}
	}
}
