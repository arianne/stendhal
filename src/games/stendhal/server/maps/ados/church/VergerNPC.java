/**
 *
 */
package games.stendhal.server.maps.ados.church;

import java.util.Map;

import games.stendhal.common.Direction;
import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.entity.RPEntity;
import games.stendhal.server.entity.npc.SpeakerNPC;
import games.stendhal.server.entity.npc.behaviour.impl.MonologueBehaviour;

/**
 * A praying NPC in ados church
 *
 * @author madmetzger and storyteller
 */
public class VergerNPC implements ZoneConfigurator {

	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		final String[] text = {"... You are not alone because there are so many people to become friends with...", "... Don't worry! Everybody has a bad day sometimes...", "... Ah, just think more positive and life will be much easier...", "... Always keep in mind: Everything is going fine...", "Thanks for being here and sharing your time with me.", "Take care of the bad, give hope to the sad..."};
		new MonologueBehaviour(buildNPC(zone), text, 3);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("Simon") {

			@Override
			protected void createDialog() {
				addGreeting("*whispering* Hello");
				addGoodbye("Bye... And remember: Life always goes on somehow... *whispering* Amen...");
				addOffer("For some people it is helpful to pray... But some others may need #help from another human... So just feel free to ask for it.");
				addHelp("Oh, you need help? Hmm... I can give you some suggestions what can help you if you feel sad or have bad thoughts. Well, you can #talk to someone about it or #write down your thoughts. Another thing to feel happier is to be #friendly to others.");
				addJob("Well, at the moment I have no job... That's why I worry about... Here in the church I am able to get my thoughts in line so I like to come here quite often. I hope that a great idea will come to my mind how to get a new job and I am praying for it...");
				addQuest("I have no quest for you but if you like, you can take a seat and pray together with me, that would be very nice.");
				addReply("talk","If you cannot solve your problems alone or if you just are not able to cope with your situation anymore it is very important that you talk to someone you trust! Tell him or her why you feel so bad or what your problem is. This person may have an idea what you can do to feel better. You know that you are not alone in this world when you have someone to talk to. Often it is just the need for someone who is by your side and you are feeling much better when you have somebody who is there for you.");
				addReply("friendly","There is a simple way to get happier in your life: Just be friendly to everyone! Other people will like you if you are nice to them and they will also be friendly to you then. Believe me, it is a real good feeling to know some people who like you and who became your friends just because they feel you are really a nice person.");
				addReply("write","It may help you to write down all your thoughts on a piece of paper. It doesn't need to be written perfectly because nobody needs to read it. Just write down what you think and feel. You may get a look what is going on in your mind if you see your thoughts in written words. This helps to understand what makes you sad or why you worry and then you can do something against it. Sometimes the problem has solved itself when you make up your mind about it.");
			}

			@Override
			protected void onGoodbye(RPEntity player) {
				setDirection(Direction.UP);
			}
		};

		npc.setEntityClass("vergernpc");
		npc.setDescription("You see Simon. He has closed his eyes and is praying silently, but sometimes you can hear him mumble a prayer...");
		npc.setPosition(29, 14);
		npc.setDirection(Direction.UP);
		npc.initHP(100);
		zone.add(npc);

		return npc;
	}

}
