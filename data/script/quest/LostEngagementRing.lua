--[[
 ***************************************************************************
 *                       Copyright © 2022 - Arianne                        *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************
]]


-- disabled on main server
if not properties:enabled("stendhal.testserver") then
	do return end
end

--[[
	Stendhal quest: Lost Engagement Ring

	Steps:

	Reward:
	- karma
	- extended keyring (12 slots instead of 8)
]]

local quest_slot = "lost_engagement_ring"

local ari
local bride_name = "Emma"

local karmaAcceptReward = 15
local karmaCompleteReward = 50

local ring_infostring = "Ari's ring"

local questActiveCondition = conditions:create("QuestActiveCondition", {quest_slot})
local hasRingCondition = conditions:create("PlayerHasInfostringItemWithHimCondition", {
	"engagement ring",
	ring_infostring,
})

local check = {
	canStart = conditions:andC({
		conditions:create("QuestNotStartedCondition", {quest_slot}),
		conditions:create("QuestNotCompletedCondition", {quest_slot}),
		conditions:create(function(player, sentence, npc)
			return player:getFeature("keyring") ~= nil
		end),
	}),
	questActive = questActiveCondition,
	questCompleted = conditions:create("QuestCompletedCondition", {quest_slot}),
	canReward = hasRingCondition,
	cannotReward = conditions:notC(hasRingCondition),
}

local rewardAction = function(player, sentence, npc)
	npc:say("Thank you so much! As a reward, I give you this keyring. It is larger than the one you have.")

	player:addKarma(karmaCompleteReward)
	player:setFeature("keyring_ext", true)

	player:setQuest(quest_slot, "done")
end

local prepareNPC = function()
	ari = entities:getNPC("Ari")
	ari:setIgnorePlayers(false)
	ari:addGreeting("Hi there!")
	ari:addGoodbye()
end

local prepareRequestStep = function()
	-- TODO: add reply to "quest" when player does not have keyring

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		check.canStart,
		ConversationStates.QUEST_OFFERED,
		"I have lost my engagement ring, and I am too embarrassed to tell " .. bride_name .. ". Would you help me?",
		nil)

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		check.questCompleted,
		ConversationStates.ATTENDING,
		"I don't need any more help.",
		nil)

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		check.questActive,
		ConversationStates.ATTENDING,
		"You are already helping me find my engagement ring.",
		nil)

	ari:add(
		ConversationStates.QUEST_OFFERED,
		ConversationPhrases.NO_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"I don't want to leave " .. bride_name .. "'s side. I hope I can find someone to help me.",
		actions:create("DecreaseKarmaAction", {karmaAcceptReward}))

	ari:add(
		ConversationStates.QUEST_OFFERED,
		ConversationPhrases.YES_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"Thank you so much! Please let me know when you find my ring. And don't say anything to "
			.. bride_name .. ".",
		actions:create("SetQuestAndModifyKarmaAction", {quest_slot, "start", karmaAcceptReward}))
end

local prepareBringStep = function()
	ari:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		check.questActive,
		ConversationStates.QUESTION_1,
		"Did you find my ring?",
		nil)

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.NO_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"Please keep looking.",
		nil)

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		check.cannotReward,
		ConversationStates.IDLE,
		"You don't have my ring. Please, keep looking.",
		nil)

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		check.canReward,
		ConversationStates.IDLE,
		nil,
		{
			actions:create(rewardAction),
			actions:create("DropInfostringItemAction", {"engagement ring", ring_infostring}),
		})
end


quests:create(quest_slot, "Lost Engagement Ring"):register(function()
	prepareNPC()
	prepareRequestStep()
	prepareBringStep()
end)
