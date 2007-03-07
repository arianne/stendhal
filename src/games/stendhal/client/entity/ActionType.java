package games.stendhal.client.entity;

import games.stendhal.client.StendhalClient;
import marauroa.common.game.RPAction;

public enum ActionType {
	LOOK("look","Look"),
	INSPECT("inspect","Inspect"),
	ATTACK("attack","Attack"),
	STOP_ATTACK("stop attack","Stop Attack"),
	CLOSE("close","Close"),
	OPEN("open","Open"),
	OWN("own","Own"),
	USE("use","Use"),
	HARVEST("use","Harvest"),
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
	private final String actionCode;
	private final String actionRepresentation;
	
	ActionType(String actCode,String actionRep){
		actionCode=actCode;
		actionRepresentation=actionRep;
		
	}
	
	public static ActionType getbyRep(String code){
       for (ActionType at : ActionType.values()){
			if (at.actionRepresentation.equals(code)){
				return at;
			}
			
		}
		System.out.print(code);
		System.out.println("=code: not found");
		return null;
	}
	
	public String toString(){
		return actionCode;
	}
	public String getRepresentation(){
		return actionRepresentation;
	}
	
	public void send(RPAction rpaction){
		StendhalClient.get().send(rpaction);
	}
}
