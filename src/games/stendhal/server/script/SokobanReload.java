/* $Id$
 * $Log$
 */
package games.stendhal.server.script;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import games.stendhal.common.filter.FilterCriteria;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.entity.Entity;
import games.stendhal.server.entity.mapstuff.game.SokobanBoard;
import games.stendhal.server.entity.player.Player;

/**
 * reloads the sokoban data file
 *
 * @author hendrik
 */
public class SokobanReload extends ScriptImpl implements FilterCriteria<Entity> {
	private static Logger logger = Logger.getLogger(SokobanReload.class);

	@Override
	public void execute(Player admin, List<String> args) {
		super.execute(admin, args);
		StendhalRPZone zone = SingletonRepository.getRPWorld().getZone("0_semos_mountain_n2");
		List<Entity> filteredEntities = zone.getFilteredEntities(this);

		Object object = filteredEntities.get(0);
		try {
			Field field = SokobanBoard.class.getDeclaredField("levelData");
			field.setAccessible(true);
			field.set(object, load());
		} catch (Exception e) {
			logger.error(e, e);
		}
	}


	private String[] load() {
		try {
			int cnt = 0;
			InputStream stream = this.getClass().getResourceAsStream("sokoban2.txt");
			BufferedReader br = new BufferedReader(new InputStreamReader(stream));
			List<String> lines = new LinkedList<String>();
			String line = br.readLine();
			while (line != null) {
				lines.add(line);
				line = br.readLine();
				cnt++;
			}
			String[] levelData = lines.toArray(new String[cnt]);
			br.close();
			return levelData;
		} catch (IOException e) {
			logger.error(e, e);
		}
		return null;
	}


	@Override
	public boolean passes(Entity o) {
		return (o.getClass().getName() != null) && o.getClass().getName().indexOf("SokobanBoard") > 0;
	}
}
