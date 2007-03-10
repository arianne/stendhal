package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import marauroa.common.Log4J;
import marauroa.common.game.RPAction;

public enum ActionType {
	LOOK("look","Look"),
	INSPECT("inspect","Inspect"),
	ATTACK("attack","Attack"),
	STOP_ATTACK("stop","Stop attack"),
	CLOSE("use","Close"),
	OPEN("use","Open"),
	OWN("own","Own"),
	USE("use","Use"),
	HARVEST("use","Harvest"),
	PICK("use","Pick"),
	LEAVE_SHEEP("own","Leave sheep"),
	ADD_BUDDY("addbuddy","Add to Buddies"),
	ADMIN_INSPECT("inspect","(*)Inspect"),
	ADMIN_DESTROY("destroy","(*)Destroy"),
	ADMIN_ALTER("alter","(*)Alter"),
	DEBUG_SHOW_PATH("[show path]","ShowPath"),
	DEBUG_HIDE_PATH("[hide path]","HidePath"),
	DEBUG_ENABLE_WATCH("[enable watch]","Enable Watch"),
	DEBUG_DISABLE_WATCH("[disable watch]","Disable Watch"),
	SET_OUTFIT("set outfit","Set outfit");
	/**
	 *  the String send to the server, if so
	 */
	private final String actionCode;
	/**
	 *  the String which is shown to the user;
	 */
	private final String actionRepresentation;
	
	/**
	 * Constructor
	 * @param actCode the code to be sent to the server
	 * @param actionRep the String to be shown to the user
	 */
	private ActionType(String actCode,String actionRep){
		actionCode=actCode;
		actionRepresentation=actionRep;
		
	}
	
	/**
	 * finds the ActionType that belongs to a visual String representation 
	 * @param representation the menu String
	 * @return the Action Element or null if not found
	 */
	public static ActionType getbyRep(String representation){
       for (ActionType at : ActionType.values()){
			if (at.actionRepresentation.equals(representation)){
				return at;
			}
			
		}
		Log4J.getLogger(ActionType.class).error(representation +" =code: not found");
		return null;
	}
	
	/** 
	 * @return the command code for usage on server side 
	 **/
	public String toString(){
		return actionCode;
	}
	/**
	 * @return the String the user should see on the menu
	 */
	public String getRepresentation(){
		return actionRepresentation;
	}
	
	/**
	 * sends the requested action to the server
	 * @param rpaction action to be sent
	 */
	public void send(RPAction rpaction){
		StendhalClient.get().send(rpaction);
	}
}
