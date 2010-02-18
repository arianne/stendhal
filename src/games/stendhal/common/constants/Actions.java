package games.stendhal.common.constants;


/**
 * contains constants needed for server and client to process actions.
 * Constants only used by either side should be declared on the side where they are used.
 *
 */
public final class Actions {
	//forsake
	public static final String SPECIES = "species";
	public static final String FORSAKE = "forsake";
	public static final String PET = "pet";
	public static final String SHEEP = "sheep";

	
	//push
	public static final String PUSH = "push";

		
	//knock
	public static final String KNOCK = "knock";

	//own
	public static final String OWN = "own";

	//list quests	
	public static final String LISTQUESTS = "listquests";
	
	//support
	public static final String SUPPORTANSWER = "supportanswer";

	//outfit
	public static final String OUTFIT = "outfit";

	
	//guild (if not removed at least be looked at while refactoring)
	public static final String INVITE_GUILD = "inviteGuild";
	public static final String GUILD = "guild";
	public static final String GUILDNAME = "guildname";
	public static final String PLAYERNAME = "playername";
	public static final String CREATEGUILD = "createguild";
	public static final String REMOVE_FROM_GUILD = "removeFromGuild";
	public static final String GUILDREMOVE = "guildremove";

	
	//teleclickmode
	
	public static final String MOVETO = "moveto";
	public static final String TELECLICKMODE = "teleclickmode";

	//tellall
	public static final String TELLALL = "tellall";

	//sentence
	public static final String SENTENCE = "sentence";

	//jail
	public static final String JAIL = "jail";

	//gag

	public static final String GAG = "gag";

	//playersquery
	public static final String WHERE = "where";
	public static final String WHO = "who";
	
	//inspect
	public static final String INSPECT = "inspect";

	//teleport
	public static final String ZONE = "zone";

	public static final String TELEPORT = "teleport";

	//teleportto
	public static final String TELEPORTTO = "teleportto";
	
	//ignore
	public static final String REASON = "reason";
	public static final String DURATION = "duration";
	
	//ghostmode
	public static final String INVISIBLE = "invisible";
	public static final String GHOSTMODE = "ghostmode";

	
	//lookaction
	public static final String LOOK = "look";
	
	//faceaction
	public static final String DIR = "dir";
	public static final String FACE = "face";
	
	//moveaction
	public static final String MOVE = "move";
	
	//away
	public static final String AWAY = "away";

	//CID submit
	public static final String CID = "cid";
	public static final String ID = "id";
	
	//CID List
	public static final String CIDLIST = "cidlist";
	
	//attack
	
	public static final String ATTACK = "attack";
	
	//destroy
	public static final String NAME = "name";
	
	//chataction
	public static final String SUPPORT = "support";
	public static final String TELL = "tell";
	public static final String CHAT = "chat";
	public static final String ANSWER = "answer";
	public static final String EMOTE = "emote";

	
	//summon
	public static final String CREATURE = "creature";
	public static final String SUMMON = "summon";

	//summonat
	public static final String AMOUNT = "amount";
	public static final String ITEM = "item";
	public static final String SLOT = "slot";

	public static final String SUMMONAT = "summonat";

	
	
	//buddy
	public static final String BUDDYONLINE = "1";
	public static final String BUDDY_OFFLINE = "0";
	
	public static final String GRUMPY = "grumpy";
	public static final String UNIGNORE = "unignore";
	public static final String REMOVEBUDDY = "removebuddy";
	public static final String IGNORE = "ignore";
	public static final String ADDBUDDY = "addbuddy";

	//adminlevel
	public static final String ATTR_HP = "hp";
	public static final String SUB = "sub";
	public static final String ADD = "add";
	public static final String SET = "set";
	public static final String TITLE = "title";
	public static final String ADMINLEVEL = "adminlevel";
	public static final String NEWLEVEL = "newlevel";

	//altercreature
	public static final String ALTERCREATURE = "altercreature";


	public static final String VALUE = "value";
	public static final String MODE = "mode";
	public static final String STAT = "stat";

	public static final String ALTER = "alter";

	// for listing e.g. ignore list
	public static final String LIST = "list";
	
	public static final String TARGET = "target";
	public static final String BASESLOT = "baseslot";
	public static final String BASEOBJECT = "baseobject";
	public static final String BASEITEM = "baseitem";
	public static final String USE = "use";
	public static final String TYPE = "type";
	public static final String X = "x";
	public static final String Y = "y";
	public static final String MINUTES = "minutes";
	public static final String MESSAGE = "message";
	public static final String TEXT = "text";

	private Actions() {
		// hide constructor
	}
}
