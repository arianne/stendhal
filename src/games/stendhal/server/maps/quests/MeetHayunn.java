package games.stendhal.server.maps.quests;

import games.stendhal.server.StendhalRPWorld;
import games.stendhal.server.entity.item.StackableItem;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.player.Player;

/**
 * QUEST: Speak with Hayunn
 * PARTICIPANTS:
 * - Hayunn Naratha
 *
 * STEPS:
 * - Talk to Hayunn to activate the quest and keep speaking with Hayunn.
 *
 * REWARD:
 * - 10 XP (check that user's level is lower than 15)
 * - 5 gold coins
 *
 * REPETITIONS:
 * - As much as wanted.
 */
public class MeetHayunn extends AbstractQuest {

  private void step_1()
    {
 
    SpeakerNPC npc=npcs.get("Hayunn Naratha");

    npc.add(1,SpeakerNPC.YES_MESSAGES,null,50,"Well, back when I was a young adventurer, I right-clicked on my enemies and chose ATTACK. But, you may ask, what is the point behind risking my life to kill things? Yes?",null);

    npc.add(50,SpeakerNPC.YES_MESSAGES,null,51,"Ah-ha! Well, what I did next was to click on the corpse of my slain opponent and choose INSPECT. Then, after making sure I was close enough to the corpse to reach it, I dragged the items there into my bag. Can you guess how I identified what these objects were, and what they did?",null);

    npc.add(51,"no",null,52,"Well, it's obvious really; I right-clicked on the items and selected LOOK to get a description. Now, I know what you're thinking; how did I manage to survive for so long in the dungeons without getting killed?",null);

    npc.add(52,SpeakerNPC.YES_MESSAGES,null,53,"By making sure I ate regularly! By right-clicking a food item - either in my bag or on the ground - I was able to slowly regain my health with each bite. That takes time of course, and there are ways to regain your health instantly... want to hear?",null);

    npc.add(53,SpeakerNPC.YES_MESSAGES,null,54, "Once you've earned enough money, you should visit one of the local healers - Carmen or Ilisa - and buy a potion. Potions are very handy when you're alone in the deep dungeons. Did I tell you were the dungeon is yet?",null);

    npc.add(54,"no",null,55,"See this hole behind me? That's the entrance to the dungeons. The corridors are pretty narrow down there, so there's a trick to moving quickly and accurately, if you'd like to hear it.",null);

    npc.add(55,SpeakerNPC.YES_MESSAGES,null,56,"Simple, really; just double-click the place you want to move to. There's a lot more information than I can relate just off the top of my head... do you want to know where to read more?",null);

    npc.add(56,SpeakerNPC.YES_MESSAGES,null,0,null,
   new SpeakerNPC.ChatAction()
      {
      @Override
	public void fire(Player player, String text, SpeakerNPC engine)
        {

        int level=player.getLevel();
        String answer;
        if(level<15)
          {
          answer="Well, good luck in the dungeons! Here's hoping you find fame and glory, and keep watch for monsters!";
          StackableItem money=(StackableItem) StendhalRPWorld.get().getRuleManager().getEntityManager().getItem("money");
          money.setQuantity(5);
	  player.equip(money);

          player.addXP(10);

          player.notifyWorldAboutChanges();

          }
        else
          {
          answer="You know, you remind me of my younger self...";
          }
          // TODO generate that link automaticially
	  engine.say("You can find a list of all sorts of animals, monsters, and other foes at #http://arianne.sourceforge.net/wiki/index.php?title=StendhalBestiary\nYou can find out about experience points and levelling up at #http://arianne.sourceforge.net/wiki/index.php?title=LevelTables\nYou can read about some of the currently most powerful and successful warriors at #http://stendhal.game-host.org\n "+answer);
	 }
       });

     npc.add(new int[]{1,50,52,53,55},"no",null,1,"Oh well, I'm sure someone else will stop by for a chat soon.",null);
     npc.add(new int[]{51,54},"yes",null,1,"Hmm. You think I have nothing to teach you, eh? I see.",null);
    }

	@Override
	public void addToWorld() {
		super.addToWorld();
    step_1();
    }
  }

