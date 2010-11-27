/** Stendhal Mana/Magic Extenstion
 *  @author timothyb89
 *  Adds a magic skills system to a Stendhal server.
 */

package games.stendhal.server.extension;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.entity.player.Player;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

/**
 *  # load StendhalServerExtension(s).
 * groovy=games.stendhal.server.scripting.StendhalGroovyRunner
 * http=games.stendhal.server.StendhalHttpServer
 * magic=games.stendhal.server.extension.MagicExtn
 * server_extension=groovy,http,magic
 */
public class MagicExtn extends StendhalServerExtension implements
		ActionListener {

	private static final Logger logger = Logger.getLogger(MagicExtn.class);

	/**
	 * 
	 */
	public MagicExtn() {
		super();
		logger.info("MagicExtn starting...");
		CommandCenter.register("spell", this);

		// StendhalRPRuleProcessor.register("listspells", this); //not ready yet
	}

	/*
	 * @see games.stendhal.server.StendhalServerExtension#init()
	 */
	@Override
	public void init() {
		// this extension has no specific init code, everything is
		// implemented as /commands that are handled onAction
	}

	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals("spell")) {
			onSpell(player, action);
		}
	}

	private void onSpell(final Player player, final RPAction action) {
		final String usage = "Usage: #/spell <spellname>";
		String text = "";

		boolean canCastSpell = false;

		String castSpell = null;

		if (action.has("target")) {
			castSpell = action.get("target");
			if (castSpell.length() == 0) {
				castSpell = null;
			}
			if (castSpell == null) {
				player.sendPrivateText("You did not enter a spell to cast.");
				logger.error("User did not enter a spell.");
			} else {
				player.sendPrivateText("Trying to cast a spell...");
			}
		} else {
			text = usage;
		}

		if (castSpell != null) {
			// the list of spells
			final String availableSpells = player.getQuest("spells");

			if (availableSpells == null) {
				player.sendPrivateText("You can not cast this spell.");
				return;
			}
	
			// Checks to see if the list of spells available to the player contains
			// the spell they tried to cast
			if (availableSpells.contains(castSpell)) {
				canCastSpell = true;
			} else {
				player.sendPrivateText("You can not cast this spell.");
			}
		}

		if (canCastSpell) {
			castSpell(player, castSpell);
		}

		if (text.length() > 0) {
			player.sendPrivateText(text);
		}
	}

	private void castSpell(final Player player, String spell) {
		// put spells and actions here
		if (spell.contains("heal")) {
			if (player.getMana() > 15) {
				player.heal();

				final int mana = player.getMana();
				final int newmana = mana - 15;
				player.setMana(newmana);
				player.sendPrivateText("You have been healed.");
				player.update();
				player.notifyWorldAboutChanges();
			} else {
				player.sendPrivateText("You do not have enough available mana to use this spell.");
			}
		} else if (spell.contains("raise stats")) {
			if (player.getMana() >= 110) {
				/**
				 * Raises the level of a player along with the atk/def
				 */

				// gets old stats
				int oldLevel = player.getLevel();
				final int oldXP = player.getXP();
				final int oldDefXP = player.getDEFXP();
				int oldDef = player.getDEF();
				int oldAtk = player.getATK();
				final int oldAtkXP = player.getATKXP();

				// gets new stats
				final int newLevel = oldLevel + 1;
				final int newXP = oldXP + 44900;
				final int newDefXP = oldDefXP + 24700;
				final int newDef = oldDef + 1;
				final int newAtkXP = oldAtkXP + 24700;
				final int newAtk = oldAtk + 1;

				// sets new stats
				player.setXP(newXP);
				player.setLevel(newLevel);	// if not directly, the level will automatically be increased in updateLevel()
				player.setDEFXP(newDefXP);
				player.setDEF(newDef);		// if not directly set, the DEF value will automatically be increased in setDEFXP()
				player.setATK(newAtk);
				player.setATKXP(newAtkXP);	// if not directly set, the ATK value will automatically be increased in setATKXP()

				// saves changes
				player.update();
				player.notifyWorldAboutChanges();

				// takes away mana
				final int mana = player.getMana();
				final int newmana = mana - 110;
				player.setMana(newmana);

				player.sendPrivateText("Your stats have been raised.");
			} else {
				player.sendPrivateText("You do not have enough mana to cast this spell.");
			}
		} else {
			player.sendPrivateText("The spell you tried to cast doesn't exist!");
		}
	}

}
