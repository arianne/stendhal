/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2010 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
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
	/**
	 * creates a new instance and initializes the templates
	 */
	public SupportMessageTemplatesFactory() {
		this.messageTemplates = new HashMap<String, String>();
		registerTemplates();
	}

	/**
	 * registers the available templates. use %s to personalize a template with the name of the asking player in the greeting. but you can only use %s once.
	 */
	private void registerTemplates() {
		addTemplate("$atlas", "Hi %s, we feel sorry that you got lost in the world of Stendhal. You can open the atlas of Faiumoni with the #/atlas command - thank you");
		addTemplate("$banprivate", "Hi %s, I am sorry but we cannot discuss bans of other players for privacy reasons.");
		addTemplate("$bugstracker","Hi %s, it sounds like you have found a new bug. Please could you create a bug report, details on how to do this are at #https://stendhalgame.org/wiki/SubmitBug - thank you very much.");
		addTemplate("$bugreport","Hi %s, it sounds like you have found a new bug. Please could you create a bug report, details on how to do this are at #https://stendhalgame.org/wiki/SubmitBug - thank you very much.");
		addTemplate("$faq", "Hi %s, you will find the answer to your question in the Stendhal FAQ. It's very helpful so please read it thoroughly! Use #/faq to open the Stendhal FAQ. Thanks for playing Stendhal!");
		addTemplate("$faqpvp","Hi %s, sorry to hear about that. Player attacks are actually within the rules of the game, and it is not something that support gets involved with for that reason. Please open the FAQ via #/faq and read carefully #http://stendhalgame.org/wiki/StendhalFAQ#Player_vs_Player - good luck for the future.");
		addTemplate("$faqsocial", "Hi %s, sorry to hear about that. Please read #https://stendhalgame.org/wiki/StendhalFAQ#Player_social_problems which covers some common problems.");
		addTemplate("$firewallserver", "Hi %s, I am sorry but we cannot help you with the configuration of your router or firewall. It is rather dangerous to modify those settings without knowing exactly what you are doing. So this should only be done by an experienced network administrator who will find instructions in the manual that came with the hardware router or operating system.");
		addTemplate("$gmreview", "Hi %s, that issue is closed for discussion with live support as it is more complex. Please email #gm-review@stendhalgame.org to discuss that issue.");
		addTemplate("$gmreviewreply", "Hi %s, email responses from #gm-review@stendhalgame.org can take up to four weeks, while the issue is independently reviewed.");
		addTemplate("$ignore","Hi %s, sorry to hear that you have had some problems with another player. Please try to ignore them. You can use #/ignore #playername to prevent chat messages.");
		addTemplate("$investigation","Hi %s, Stendhal is a game developed and run by volunteers. These kinds of investigations waste a lot of time. We're sorry but we cannot take this issue further.");
		addTemplate("$knownbug","Hi %s, thank you for telling us about this bug, we have found it ourselves too and it's already reported. Thank you though and please do keep reporting bugs if you see them!");
		addTemplate("$notsupport","Hi %s, sorry, but support cannot help with this issue. Please use #https://stendhalgame.org and the wiki #https://stendhalgame.org/wiki/Stendhal as information sources.");
		addTemplate("$password","Hi %s, you can change your password by logging in on #https://stendhalgame.org. When you are logged in on that page you will find a link called change password in the upper right area of the page. There you can change your password.");
		addTemplate("$rules","Hi %s, please read the Stendhal Rules, which will open via #/rules - thank you.");
		addTemplate("$spam","Hi %s, repeatedly saying the same thing over and over again is considered spamming, and this is against the rules of the game. Please do not spam, and please open the Stendhal Rules via #/rules, thank you.");
		addTemplate("$spamsupport","Hi %s, we use a special tool for support, so you don't have to worry about messages being lost. Please note that sending a lot of support messages will lower the priority of your query.");
		addTemplate("$thief","Hi %s, please ask Dagobert about #trading to learn how to trade safely and securely. Support cannot recover items lost through carelessness.");
		addTemplate("$wiki","Hi %s, this is a question which is answered on the Stendhal wiki, please look on #https://stendhalgame.org/wiki/Stendhal as this is full of useful information. Thanks for playing Stendhal.");
	}

	/**
	 * registers a template name with the corresponding text
	 *
	 * @param templateName
	 * @param templateText
	 */
	private void addTemplate(String templateName, String templateText) {
		StringBuilder nameBuilder = new StringBuilder();
		if(!templateName.startsWith(TEMPLATE_PREFIX)) {
			nameBuilder.append(TEMPLATE_PREFIX);
		}
		nameBuilder.append(templateName);
		messageTemplates.put(nameBuilder.toString(), templateText);
	}

	/**
	 * returns the map of templates
	 *
	 * @return a map of the template names as key and template text as value
	 */
	public Map<String, String> getTemplates() {
		return messageTemplates;
	}

}
