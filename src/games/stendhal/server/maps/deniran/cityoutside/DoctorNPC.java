package games.stendhal.server.maps.deniran.cityoutside;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import games.stendhal.server.core.config.ZoneConfigurator;
import games.stendhal.server.core.engine.StendhalRPZone;
import games.stendhal.server.core.pathfinder.FixedPath;
import games.stendhal.server.core.pathfinder.Node;
import games.stendhal.server.entity.npc.SpeakerNPC;

public class DoctorNPC implements ZoneConfigurator {

	
	@Override
	public void configureZone(final StendhalRPZone zone, final Map<String, String> attributes) {
		buildNPC(zone);
	}

	private SpeakerNPC buildNPC(final StendhalRPZone zone) {
		final SpeakerNPC npc = new SpeakerNPC("John Smith") {
			@Override
			protected void createPath() {
				final List<Node> nodes = new LinkedList<Node>();
				nodes.add(new Node(72, 8));
				nodes.add(new Node(83, 8));
				setPath(new FixedPath(nodes, true));
			}

			@Override
			protected void createDialog() {
				addGreeting("Oh, hello there!");
				addJob("I am inspector of fences or something.");
				addGoodbye("Be fantastic!");
				addReply(Arrays.asList("advice", "doctor", "promise", "kind", "cowardly", "pear", "pears"),
						"Never be cruel, never be cowardly. And never ever eat pears! Remember: Hate is always foolish and love is always wise.");
				addReply(Arrays.asList("faith", "love", "wedding", "marriage"),
						"Something I believe in, my faith: Love in all its forms is the most powerful weapon because love is a form of hope.");
				addReply(Arrays.asList("war", "army", "fight", "gun", "attack"),
						"When you fire that first shot, no matter how right you feel, you have no idea, who’s going to die! You don’t know, whose children are going to scream and burn! How many hearts will be broken! How many lives shattered! How much blood will spill, until everybody does, what they were always going to do from the very beginning! – Sit down and talk! ");
				addReply(Arrays.asList("tardis", "blue", "box"),
						"I stole it. Well, I borrowed it; I was always going to take it back. Oh, that box. Big and little at the same time, brand-new and ancient, and the bluest blue ever.");
				addReply(Arrays.asList("sonic", "screwdriver"),
						"It's a sonic screwdriver. See? It makes a noise.");
				addReply(Arrays.asList("regeneration", "death", "dying", "end", "change"),
						"We all change. We are all different people all through our lives, and that's okay, that's good. You've got to keep moving. So long as you remember all the people that you used to be.");
				addReply(Arrays.asList("good", "bad", "idiot", "mad"),
						"Am I a good man?");
				addReply(Arrays.asList("fact", "facts"),
						"The very powerful and the very stupid have one thing in common. Instead of altering their views to fit the facts, they alter the facts to fit their views... Which can be very uncomfortable, if you happen to be one of the facts, that needs altering.");
				addReply(Arrays.asList("9", "nine", "ninth", "Christopher", "Eccleston"),
						"Fantastic!");
				addReply(Arrays.asList("10", "ten", "David", "Tennant"),
						"Allons-y!");
				addReply(Arrays.asList("11", "eleven", "Matt", "Smith"),
						"Geronimo!");
			}

		};
		npc.setOutfit(0, 14, 0, 0, 0, 0, 33, 0, 0);
		npc.setDescription("You see a man who seems to be very interested in the fence.");
		npc.setPosition(83, 8);
		npc.initHP(100);
		zone.add(npc);
		return npc;
	}

	
/*
12  The Zygon Inversion 

So, let me ask you a question about this brave new world of yours.
When you've killed all the bad guys and when it's all perfect and just and fair,
when you have finally got it exactly the way you want it,
what are you going to do with the people like you?
The troublemakers
How are you going to protect your glorious revolution
from the next one?
 < We'll win
Oh, will you? Well, meybe... maybe you will win!
But nobody wins for long.
The wheel just keeps turning.
---------

< Do you know, what I see, Doctor?
< A box.
< A box with everything I need.
< A 50% chance.
<< For us, too.

And we're off! Fingers on buzzers! Are you feeling lucky?
Are you ready to play the game?
Who's going to be quickest?
Who's going to be luckiest?
<< This is not a game.
No, it's not a game, sweetheart, and I mean that most sincerely.
< Why are you doing this?
<< Yes, I'd quite like to know that, too. You set this up, why?
Because it's not a game, Kate.
This is a scale model of war...
every war every fought right there in front of you...
Because it'S always the same.
When you fire that first shot, no matter how right you feel, you have no idea who's goign to die!
You don't know whose children are going to scream and burn!
How many hearts will be broken!
How many lives shattered!
How much blood will spill until everybody does what they were always going to have to do from the very beginning, sit down and talk!
Listen to me. Listen, I just... I just want you to think.
Do you know what thinking is? It's just a fancy word for changing your mind.
< I will not change my mind.
Then you will die stupid.
Alternatively, you chould step away from that box.
You can walk right out of that door and you could stand your revolution down.
< No
< I'm not stopping this, Doctor.
< I started it. I will not stop it.
< You think they'll let me go after what I've done
You're all the same, you screaming ids, you know that?
"Look at me, I'm unforgiveable."
Well here's the unforeseeable. I forgive you...
after all you've done...
I forgive you.
< You don't understand.
< You will never understand.
I don't understand?
Are you kidding? Me? Of course I understand.
You mean, you call this a war`This funny little thing?
This is not a war!
I fought in a bigger war than you will ever know.
I did worse things than you could ever imagine.
And when I close my eyes...
I hear more screams than anyone could ever be able to count!
And you know what you do with all that pain?
Shall I tell you where you put it?
You hold it tight...
till it burns your hand...
and you say this:
No one else will ever have to live like this!
No one else will have to feel this pain!
Not on my watch!
- Kate closes box
Thank you.
Thank you.
 */

	
}
