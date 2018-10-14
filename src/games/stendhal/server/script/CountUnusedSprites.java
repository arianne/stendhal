/* $Id$ */
package games.stendhal.server.script;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;
//import java.util.Iterator;
import java.util.List;

/*

import games.stendhal.server.core.engine.StendhalRPWorld;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.creature.Creature;

import games.stendhal.server.entity.spell.Spell;
import marauroa.common.game.IRPZone;
import marauroa.common.game.RPObject;
*/

import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.rule.defaultruleset.DefaultCreature;
import games.stendhal.server.core.rule.defaultruleset.DefaultItem;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.player.Player;

/**
 * Counts the number of unused sprites on the world.
 *
 * @author yoriy
 */
public class CountUnusedSprites extends ScriptImpl {


	@Override
	public void execute(final Player admin, final List<String> args) {
		Collection<DefaultCreature> allCreatures = SingletonRepository.getEntityManager().getDefaultCreatures();
		Collection<DefaultItem> allItems = SingletonRepository.getEntityManager().getDefaultItems();
		// TODO: implement spells processing
		//Collection<Spell> allSpells = SingletonRepository.getEntityManager().getSpells();
		final StringBuilder sb=new StringBuilder();

        /* items */

		final File dirItemSprites = new File("data/sprites/items");
		String[] itemclasses = dirItemSprites.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return dir.isDirectory();
			}
		}
		);

		for(final String f : itemclasses) {
			final File dirF = new File("data/sprites/items/"+f);
			String[] subclasses = dirF.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".png");
				};
			}
			);

			if(subclasses == null) continue;
			for(final String g: subclasses) {
				// have both class and subclass, check if we have such item in Stendhal world
				String realsubclass = g.substring(0, g.length()-4);
				boolean found = false;
				for(final DefaultItem h : allItems) {
                   if(h.getItemSubclass().equals(realsubclass)) {
                	   found = true;
                   }
				}
				if(found == false) {
					sb.append("found unused item: ("+ f + "/" + realsubclass +")\n");
				};
			}
		};


	/* mosters */

	final File dirMonsterSprites = new File("data/sprites/monsters");
	String[] monsterclasses = dirMonsterSprites.list(new FilenameFilter() {
		@Override
		public boolean accept(File dir, String name) {
			return dir.isDirectory();
		}
	}
	);

	for(final String f : monsterclasses) {
		final File dirF = new File("data/sprites/monsters/"+f);
		String[] subclasses = dirF.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".png");
			};
		}
		);

		if(subclasses == null) continue;
		for(final String g: subclasses) {
			// have both class and subclass, check if we have such item in Stendhal world
			String realsubclass = g.substring(0, g.length()-4);
			boolean found = false;
			for(final DefaultCreature h : allCreatures) {
               if(h.getCreatureSubclass().equals(realsubclass)) {
            	   found = true;
               }
			}
			if(found == false) {
				sb.append("found unused creature: ("+ f + "/" + realsubclass +")\n");
			};
		}
	};

admin.sendPrivateText("list of pictures: " + sb.toString());

	}
}
