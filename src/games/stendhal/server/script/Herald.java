package games.stendhal.server.script;

import games.stendhal.common.Direction;
import games.stendhal.server.core.engine.SingletonRepository;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.events.TurnListener;
import games.stendhal.server.core.events.TurnNotifier;
import games.stendhal.server.core.scripting.ScriptImpl;
import games.stendhal.server.core.scripting.ScriptingSandbox;
import games.stendhal.server.entity.player.Player;
import games.stendhal.server.entity.npc.ConversationStates;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.ChatAction;
import games.stendhal.server.entity.npc.condition.AdminCondition;
import games.stendhal.server.entity.npc.condition.NotCondition;
import games.stendhal.server.entity.npc.parser.Sentence;

import java.util.List;
import java.util.Arrays;
import java.util.LinkedList;
import java.lang.StringBuilder;

import org.apache.log4j.Logger;


/**
 * A herald which will tell news to citizens. 
 */
public class Herald extends ScriptImpl {

	// after some thinking, i decided to not implement here 
	// news records to file.
	private Logger logger = Logger.getLogger(Herald.class);
    private final int REQUIRED_ADMINLEVEL_INFO = 100;
    private final int REQUIRED_ADMINLEVEL_SET = 1000;
    private TurnNotifier turnNotifier = TurnNotifier.get();

    private final String HaveNoTime = "Hi, I have to do my job, so I have no time to speak with you, sorry.";
    private final String HiOldFriend = "Oh, you're here! Hi, my old friend, glad to see you.";
    private final String TooScared = "Oh, you are crazy, sure. I can't help you, the Emperor will kill us both for that.";
    private final String BadJoke = "Joke, yes? I like jokes, but not too much.";
    private final String FeelBad = "Oh, I don't know what is wrong with me, I'm not feeling very well... sorry, I can't help you...";
    private final String DontUnderstand = "Sorry, I don't understand you";
    private final String InfoOnly = "Oh, I think I can trust you enough to tell you my current announcements list. ";
    private final String WillHelp = "Sure, I will do for you all that you want."+
									" Tell me '#speech <time interval (seconds)> <time limit (seconds)> <text to speech>'. " +
									"If you want to remove one of my current announcements, "+
									"tell me '#remove <number of speech>'. "+
									"You can also ask me about current announcements, say '#info' for that.";
    
    private LinkedList<HeraldNews> heraldNews = new LinkedList<HeraldNews>();
   
    /**
     * class for herald announcements. 
     */
    class HeraldNews {
    private String news;
    private int interval;
    private int limit;
    private int counter;
    private int id;
    private HeraldListener tnl;  
    public String getNews(){
    	return(news);
    }
    public int getInterval(){
    	return(interval);
    }
    public int getLimit(){
    	return(limit);
    }
    public int getCounter(){
    	return(counter);
    }
    public int getid(){
    	return(id);
    }
    public HeraldListener getTNL(){
    	return(tnl);
    }
    public void SetCounter(int count){
    	this.counter=count;
    }
    
    /**
     * constructor for news
     * @param news - text to speech
     * @param interval - interval between speeches in seconds.
     * @param limit - time limit in seconds.
     * @param counter - counter of speeches
     * @param tnl - listener object
     * @param id - unique number to internal works with news.
     */
    public HeraldNews(String news, int interval, int limit, int counter, 
    		HeraldListener tnl, int id){
    	this.news=news;
    	this.interval=interval;
    	this.limit=limit;
    	this.counter=counter;
    	this.tnl=tnl;
    	this.id=id;
    }
    }
    
    /**
     * Herald turn listener object. 
     */
    class HeraldListener implements TurnListener{
    	private int id;
    	/**
    	 * function invokes by TurnNotifier each time when Herald have to speech.
    	 */
    	public void onTurnReached(int currentTurn) {
    		workWithCounters(id);
    		};
    		/**
    		 * tnl constructor.
    		 * @param i - id of news
    		 */
    	public HeraldListener(int i) {
    		id=i;
    	}
    }
    
    /**
     * invokes by /script -load Herald.class,
     * placing Herald near calling admin.
     */
	@Override
	public void load(final Player admin, final List<String> args, final ScriptingSandbox sandbox) {
		if (admin==null) {
			logger.error("Herald: called by null admin", new Throwable());
		}
		if (sandbox.getZone(admin).collides(admin.getX()+1, admin.getY())) {
			logger.info("Herald: place is occupied.");
			admin.sendPrivateText("Spot (right) near you is occupied, can't place Herald here.");
			return;
		}
		sandbox.setZone(admin.getZone());
		sandbox.add(GetHerald(sandbox.getZone(admin), admin.getX() + 1, admin.getY()));
	}

	/**
	 * function invokes by HeraldListener.onTurnReached each time when Herald have to speech.
	 * @param id - ID for news in news list
	 */
	public void workWithCounters(int id) {
		int index=-1;
		for (int i=0; i<heraldNews.size(); i++){
			if(heraldNews.get(i).getid()==id){
				index=i;
			}
		}
		if (index==-1) {
			logger.info("workWithCounters: id not found. ");
		}
		try {
		final int interval = heraldNews.get(index).getInterval();
		final int limit = heraldNews.get(index).getLimit();
		final String text = heraldNews.get(index).getNews();
		int counter = heraldNews.get(index).getCounter();
		HeraldListener tnl = heraldNews.get(index).getTNL();
		final SpeakerNPC npc = SingletonRepository.getNPCList().get("Herald");
		npc.say(text);
		counter++;
		turnNotifier.dontNotify(tnl);
		if(interval*counter<limit){
			heraldNews.get(index).SetCounter(counter);
			turnNotifier.notifyInSeconds(interval, tnl);
		} else {
			// it was last announce.
			heraldNews.remove(index);
		}
		} catch (IndexOutOfBoundsException ioobe) {
			logger.error("workWithCounters: index is out of bounds: "+Integer.toString(index)+
					     ", size "+Integer.toString(heraldNews.size())+
					     ", id "+Integer.toString(id),ioobe);
		}
	}

	/**
	 * kind of Herald constructor
	 * @param zone - zone to place Herald
	 * @param x - x coord in zone
	 * @param y - y coord in zone
	 * @return Herald NPC :-)
	 */
	private SpeakerNPC GetHerald(StendhalRPZone zone, int x, int y) {
		final SpeakerNPC npc = new SpeakerNPC("Herald") {
			
			/**
			 * npc says his job list
			 */
			class ReadNewsAction implements ChatAction {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc){
					if(heraldNews.size()==0){
						npc.say("My announcements list is empty.");
						return;
					}
					StringBuilder sb=new StringBuilder();
					sb.append("Here is list of my current announcements.");
					
					for(int i=0; i<(heraldNews.size());i++){
						// will add 1 to position numbers to show position 0 as 1.
						logger.info("info: index "+Integer.toString(i));
						try {
						final int left = heraldNews.get(i).getLimit()/heraldNews.get(i).getInterval()-
										 heraldNews.get(i).getCounter();
						sb.append(" #"+Integer.toString(i+1)+". (left "+
								  Integer.toString(left)+" times): "+
								"#Every #"+Integer.toString(heraldNews.get(i).getInterval())+
								" #seconds #to #"+Integer.toString(heraldNews.get(i).getLimit())+
								" #seconds: \""+heraldNews.get(i).getNews()+"\"");
						} catch (IndexOutOfBoundsException ioobe) {
							logger.error("ReadNewsAction: size of heraldNews = "+
									Integer.toString(heraldNews.size()), ioobe);
						}
						if(i!=(heraldNews.size()-1)){
							sb.append("; ");
						}
					}
					npc.say(sb.toString());
				}
			}
			
			/**
			 * npc adds new job to his job list
			 */
			class WriteNewsAction implements ChatAction {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc){
					final StringBuilder sb = new StringBuilder(sentence.getOriginalText());
					logger.info("Original sentence: "+sb.toString());
					final String args = sb.toString();
					final String[] starr = args.split(" ");	
					if(starr.length<2){
						npc.say("You forget time limit. I am mortal too and somewhat senile, you know.");
						return;
					}
					try {
						final int interval = Integer.parseInt(starr[1].trim());
						final int limit = Integer.parseInt(starr[2].trim());
						if(limit<interval){
							npc.say("I can count to "+Integer.toString(interval)+
									", and "+Integer.toString(limit)+" is less then " + Integer.toString(interval)+
									". Repeat please.");
							return;
						}
						try {
							String text = args.toString().trim().
								          substring(starr[0].length()).trim().
								          substring(starr[1].length()).trim().
								          substring(starr[2].length()).trim();
							final String out="Interval: "+Integer.toString(interval)+", limit: "+
							Integer.toString(limit)+", text: \""+text+"\"";
							npc.say("Ok, i recorded it. "+out);
							logger.info("Admin "+player.getName()+
										" added announcement: " +out);
							final HeraldListener tnl = new HeraldListener(heraldNews.size());
							heraldNews.add(new HeraldNews(text, interval, limit, 0, tnl, heraldNews.size()));
							turnNotifier.notifyInSeconds(interval,tnl);
						} catch (IndexOutOfBoundsException ioobe) {
						    	npc.say(FeelBad);
						    	logger.error("WriteNewsAction: Error while parsing sentence "+sentence.toString(), ioobe);
						}
					} catch (NumberFormatException nfe) {
							 npc.say(DontUnderstand);
							 logger.info("Error while parsing numbers. Interval and limit is: "+"("+starr[0]+"), ("+starr[1]+")");
							 return;
					}
				}
			}
	
			/**
			 * npc removes one job from his job list
			 */
			class RemoveNewsAction implements ChatAction {
				public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc){
					String s= new String(sentence.getOriginalText());					
					final String args = s.trim();
					final String[] starr = args.split(" ");	
					if(starr.length<2){
						npc.say("Tell me the number of sentence to remove.");
						return;
					}
					final String number = starr[1];
					try {
						final int i = Integer.parseInt(number)-1;
						if (i<0){
							npc.say(BadJoke);
							return;
						}
						if (heraldNews.size()==0) {
							npc.say("I dont have announcements now.");
							return;
						}
						if(i>=(heraldNews.size())){
							npc.say("I have only "+ Integer.toString(heraldNews.size())+ 
									" announcements at the moment.");
							return;
						}
						logger.warn("Admin "+player.getName()+" removing announcement #"+
									Integer.toString(i)+": interval "+
									Integer.toString(heraldNews.get(i).getInterval())+", limit "+
									Integer.toString(heraldNews.get(i).getLimit())+", text \""+
									heraldNews.get(i).getNews()+"\"");
						turnNotifier.dontNotify(heraldNews.get(i).getTNL());
						heraldNews.remove(i);
						npc.say("Ok, already forget it.");
					} catch (NumberFormatException nfe) {
						logger.error("RemoveNewsAction: cant remove "+number+" speech.", nfe);
						npc.say(DontUnderstand);
						return;
					};
				}
			}		
			
			/**
			 * npc removes all jobs from his job list
			 */
			class ClearNewsAction implements ChatAction {
					public void fire(final Player player, final Sentence sentence, final SpeakerNPC npc){
						logger.info("ClearAllAction: Admin "+player.getName()+
									" cleared announcement list.");
						for (int i=0; i<heraldNews.size(); i++) {
							turnNotifier.dontNotify(heraldNews.get(i).getTNL());
						}
						if(heraldNews.size()!=0){
							npc.say("Ufff, I have now some time for rest. I heard, there is a gambling game in Semos city?");							
							heraldNews.clear();
						} else {
							npc.say("Oh, thank you for trying to help me, but I'm ok.");
						}
					}
			}
			
			/**
			 *  Finite states machine logic for herald.
			 */
			@Override
			public void createDialog() {
				add(ConversationStates.IDLE, 
					Arrays.asList("hi", "hola", "hello", "heya"),
					new NotCondition(new AdminCondition(REQUIRED_ADMINLEVEL_INFO)),
					ConversationStates.IDLE, 
					HaveNoTime,	null);
				add(ConversationStates.IDLE, 
					Arrays.asList("hi", "hola", "hello", "heya"),
					new AdminCondition(REQUIRED_ADMINLEVEL_INFO), 
					ConversationStates.ATTENDING, 
					HiOldFriend, null);
				add(ConversationStates.ATTENDING, 
					Arrays.asList("help"),
					new AdminCondition(REQUIRED_ADMINLEVEL_SET), 
					ConversationStates.ATTENDING, 
					WillHelp, null);
				add(ConversationStates.ATTENDING, 
					Arrays.asList("speech", "remove"),
					new NotCondition(new AdminCondition(REQUIRED_ADMINLEVEL_SET)), 
					ConversationStates.ATTENDING, 
					TooScared, null);
				add(ConversationStates.ATTENDING, 
					Arrays.asList("help"),
					new NotCondition(new AdminCondition(REQUIRED_ADMINLEVEL_SET)), 
					ConversationStates.ATTENDING, 
					InfoOnly, new ReadNewsAction());
				add(ConversationStates.ATTENDING, 
					Arrays.asList("info", "list", "tasks", "news"),
					new AdminCondition(REQUIRED_ADMINLEVEL_INFO), 
					ConversationStates.ATTENDING, 
					null, new ReadNewsAction());		
				add(ConversationStates.ATTENDING, 
					Arrays.asList("speech"),
					new AdminCondition(REQUIRED_ADMINLEVEL_SET), 
					ConversationStates.ATTENDING, 
					null, new WriteNewsAction());
				add(ConversationStates.ATTENDING, 
					Arrays.asList("remove"),
					new AdminCondition(REQUIRED_ADMINLEVEL_SET), 
					ConversationStates.ATTENDING, 
					null, new RemoveNewsAction());
				add(ConversationStates.ATTENDING, 
						Arrays.asList("clear"),
						new AdminCondition(REQUIRED_ADMINLEVEL_SET), 
						ConversationStates.ATTENDING, 
						null, new ClearNewsAction());
				addGoodbye();
			}
		}; 
		zone.assignRPObjectID(npc);
		npc.setEntityClass("heraldnpc");
		npc.setPosition(x, y);
		npc.initHP(100);
		npc.setDirection(Direction.LEFT);
		return(npc);
	}
}
