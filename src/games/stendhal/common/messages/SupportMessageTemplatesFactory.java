package games.stendhal.common.messages;

import java.util.HashMap;
import java.util.Map;

/**
 * provides a single point where to define support message templates
 * @author madmetzger
 *
 */
public class SupportMessageTemplatesFactory {
	
	private static final String TEMPLATE_PREFIX = "$";

	private final Map<String, String> messageTemplates;
	
	public SupportMessageTemplatesFactory() {
		this.messageTemplates = new HashMap<String, String>();
		registerTemplates();
	}

	private void registerTemplates() {
		addTemplate("$faq", "Hi %s, you will find the answer to your question in the Stendhal FAQ. It's very helpful so please read it thoroughly! #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ. Thanks for playing Stendhal!");
		addTemplate("$faqsocial", "Hi %s, sorry to hear about that. But unfortunately support is not here to help you with social problems unless it gets way out of hand. This issue is discussed further on the stendhal FAQ and how to deal with it is described there. Please read carefully #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ#Player_social_problems - hopefully the rest of your Stendhal experience will be more pleasant.");
		addTemplate("$ignore","Hi %s, sorry to hear that you have had some problems with another player. Please try to ignore them. You can use #/ignore #playername to prevent chat messages.");
		addTemplate("$faqpvp","Hi %s, sorry to hear about that. Player attacks are actually within the rules of the game, and it is not something that support gets involved with for that reason. Please read carefully #http://stendhal.game-host.org/wiki/index.php/StendhalFAQ#Player_vs_Player - good luck for the future.");
		addTemplate("$wiki","Hi %s, this is a question which is answered on the Stendhal wiki, please look on #http://stendhal.game-host.org/wiki/index.php/Stendhal as this is full of useful information. Thanks for playing Stendhal.");
		addTemplate("$knownbug","Hi %s, thank you for telling us about this bug, we have found it ourselves too and it's already reported. Thank you though and please do keep reporting bugs if you see them!");
		addTemplate("$bugstracker","Hi %s, it sounds like you have found a new bug. Please could you create a bug report, details on how to do this are at #http://stendhal.game-host.org/wiki/index.php/SubmitBug - thank you very much.");
		addTemplate("$rules","Hi %s, please read the Stendhal Rules at #http://stendhal.game-host.org/wiki/index.php/StendhalRuleSystem - thank you.");
		addTemplate("$notsupport","Hi %s, sorry, but support cannot help with this issue. Please use #http://stendhal.game-host.org and the wiki #http://stendhal.game-host.org/wiki/index.php/Stendhal as information sources.");
		addTemplate("$spam","Hi %s, repeatedly saying the same thing over and over again is considered spamming, and this is against the rules of the game. Please do not spam, and please read #http://stendhal.game-host.org/wiki/index.php/StendhalRuleSystem, thank you.");
		addTemplate("$password","Hi %s, you can change your password by logging in on #http://stendhal.game-host.org. When you are logged in on that page you will find a link called change password in the upper right area of the page. There you can change your password.");
	}

	private void addTemplate(String templateName, String templateText) {
		StringBuilder nameBuilder = new StringBuilder();
		if(!templateName.startsWith(TEMPLATE_PREFIX)) {
			nameBuilder.append(TEMPLATE_PREFIX);
		}
		nameBuilder.append(templateName);
		messageTemplates.put(nameBuilder.toString(), templateText);
	}
	
	public Map<String, String> getTemplates() {
		return messageTemplates;
	}

}
