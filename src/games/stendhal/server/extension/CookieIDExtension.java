package games.stendhal.server.extension;

import java.util.HashMap;
import java.util.Map;
import marauroa.common.game.RPAction;

import org.apache.log4j.Logger;

import games.stendhal.server.actions.ActionListener;
import games.stendhal.server.actions.CommandCenter;
import games.stendhal.server.actions.admin.AdministrationAction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.Task;
import games.stendhal.server.entity.player.Player;

/* Setup:
 * server.ini
 * server_extension=cid
 * cid=games.stendhal.server.extension.CookieIDExtension
 * 
 * Commands:
 * * the /listids GM command will group together accounts that share the same computer,
 * logged out players are saved, server restart clears
 */

public class CookieIDExtension extends StendhalServerExtension implements
		ActionListener {

	private static final Logger logger = Logger.getLogger(CookieIDExtension.class);
	
	//Key is ID, value contains list of names
	final Map<String, String> idList = new HashMap<String, String>();
	
	//Key is name, value is ID
	final Map<String, String> nameList = new HashMap<String, String>();
	
	public CookieIDExtension() {
		super();
		logger.info("CookieIDExtension starting...");
		CommandCenter.register("cid", this);
		CommandCenter.register("listids", this, 50);
	}

	@Override
	public void init() {
		// this extension has no specific init code, everything is
		// implemented as /commands that are handled onAction
	}
	
	public void onAction(final Player player, final RPAction action) {
		final String type = action.get("type");

		if (type.equals("cid")) {
			onSendID(player, action);
		} else if (type.equals("listids")) {
			onListIDs(player, action);
		}
	}
	
	private void onSendID(final Player player, final RPAction action) {

		if (action.has("id")) {
			
			final String CID = action.get("id");
			final String pName = player.getName();
			
			
			if (idList.containsKey(CID)) {
				if(idList.get(CID).contains("," + pName + ",")) {
					return;
				}
				final String tempid = idList.get(CID) + pName + ",";
				idList.put(CID, tempid);
			} else {
				idList.put(CID, "," + pName + ",");
			}
			
			nameList.put(pName, CID);
		}
		
	}
	
	private void onListIDs(final Player admin, final RPAction action) {

		if (!AdministrationAction.isPlayerAllowedToExecuteAdminCommand(admin,
				"listids", true)) {
			return;
		}
		
		admin.sendPrivateText("One line per Online Player");
		
		SingletonRepository.getRuleProcessor().getOnlinePlayers().forAllPlayersExecute(
				
			new Task<Player>() {

			public void execute(final Player player) {
				
				String pName = player.getName();
				if (nameList.containsKey(pName)) {
					String tid = nameList.get(pName);
					if (idList.containsKey(tid)) {
						String group = idList.get(tid);
						admin.sendPrivateText("One Computer(" + pName + "): " + group);
						//admin.sendPrivateText(tid);
					}
				}
			}
			
		});
		
	
	}
}
