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
package games.stendhal.server.core.events.achievements;

import games.stendhal.common.MathHelper;
import games.stendhal.server.entity.npc.condition.AgeGreaterThanCondition;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
/**
 * Factory for age related achievements
 *  
 * @author madmetzger
 */
public class AgeAchievementFactory extends AchievementFactory {
	
	@Override
	public Collection<Achievement> createAchievements() {
		List<Achievement> ageAchievements = new LinkedList<Achievement>();
		ageAchievements.add(createAchievement("age.day.one", "Played a day", "Play a complete day's time overall", 
				Achievement.EASY_BASE_SCORE, new AgeGreaterThanCondition(MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.week.one", "Played a week", "Play a comlete week's time overall", 
				Achievement.EASY_BASE_SCORE, new AgeGreaterThanCondition(MathHelper.MINUTES_IN_ONE_WEEK)));
		ageAchievements.add(createAchievement("age.month.one", "Played a month", "Play a complete month's time overall", 
				Achievement.EASY_BASE_SCORE, new AgeGreaterThanCondition(1*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.two", "Played two months", "Play two complete month's time overall", 
				Achievement.EASY_BASE_SCORE, new AgeGreaterThanCondition(2*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.three", "Played quarter a year", "Play a quarter of a year's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(3*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.four", "Played four months", "Play four complete month's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(4*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.five", "Played five months", "Play five complete month's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(5*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.six", "Played half a year", "Play half a year's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(6*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.seven", "Played seven months", "Play seven complete month's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(7*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.eight", "Played eight months", "Play eight complete month's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(8*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.nine", "Played nine months", "Play nine complete month's time overall", 
				Achievement.MEDIUM_BASE_SCORE, new AgeGreaterThanCondition(9*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.ten", "Played ten months", "Play ten complete month's time overall", 
				Achievement.HARD_BASE_SCORE, new AgeGreaterThanCondition(10*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.month.eleven", "Played eleven months", "Play eleven complete month's time overall", 
				Achievement.HARD_BASE_SCORE, new AgeGreaterThanCondition(11*30*MathHelper.MINUTES_IN_ONE_DAY)));
		ageAchievements.add(createAchievement("age.year.one", "Played a year", "Play a complete year's time overall", 
				Achievement.HARD_BASE_SCORE, new AgeGreaterThanCondition(365*MathHelper.MINUTES_IN_ONE_DAY)));
		return ageAchievements;
	}

	@Override
	protected Category getCategory() {
		return Category.AGE;
	}

} 