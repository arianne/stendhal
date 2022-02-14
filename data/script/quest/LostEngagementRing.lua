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

	Notes:
	- If player loses ring, talk to Ari & say "lost" to reset quest slot.
]]

local quest_slot = "lost_engagement_ring"

local ari
local bride_name = "Emma"

local karmaAcceptReward = 15
local karmaCompleteReward = 50

local ring_infostring = "Ari's ring"

-- location where ring may be on Athor island
local ring_locations = {
	{49, 30},
	{123, 3},
	{115, 122},
	{46, 106},
}

local questActiveCondition = conditions:create("QuestActiveCondition", {quest_slot})
local hasRingCondition = conditions:create("PlayerHasInfostringItemWithHimCondition", {
	"engagement ring",
	ring_infostring,
})

local hasKeyringCondition = function(player, sentence, npc)
	return player:hasFeature("keyring")
end

local check = {
	canStart = conditions:andC({
		conditions:create("QuestNotStartedCondition", {quest_slot}),
		conditions:create("QuestNotCompletedCondition", {quest_slot}),
		conditions:create(hasKeyringCondition),
	}),
	questActive = questActiveCondition,
	questCompleted = conditions:create("QuestCompletedCondition", {quest_slot}),
	canReward = hasRingCondition,
	cannotReward = conditions:notC(hasRingCondition),
}

local setQuestAction = function(player, sentence, npc)
	player:addKarma(karmaAcceptReward)

	-- choose random location
	local selected = random:randUniform(1, 4)

	player:setQuest(quest_slot, 0, ring_locations[selected][1])
	player:setQuest(quest_slot, 1, ring_locations[selected][2])
end

local resetQuestAction = function(player, sentence, npc)
	if player:hasQuest(quest_slot) then
		local slots = player:getQuest(quest_slot):split(";")
		if slots[1] == "have_ring" then
			-- FIXME: should have a failsafe here in case slot string is malformatted
			player:setQuest(quest_slot, slots[2] .. ";" .. slots[3])
		end
	end
end

local rewardAction = function(player, sentence, npc)
	player:addKarma(karmaCompleteReward)
	player:setFeature("keyring_ext", true)

	local slots = player:getQuest(quest_slot):split(";")
	if #slots > 2 then
		player:setQuest(quest_slot, 0, "done")
	elseif #slots > 1 then
		-- quest slot might have been reset if player reported ring "lost"
		player:setQuest(quest_slot, "done;" .. slots[1] .. ";" .. slots[2])
	else
		-- failsafe
		player:setQuest(quest_slot, "done")
	end
end

local prepareNPC = function()
	ari = entities:getNPC("Ari")
	ari:setIgnorePlayers(false)
	ari:addGoodbye()
end

local prepareRequestStep = function()

	ari:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		conditions:orC({
			{
				conditions:create("QuestNotStartedCondition", {quest_slot}),
				conditions:notC(hasKeyringCondition),
			},
			check.questCompleted,
		}),
		ConversationStates.ATTENDING,
		"Hi there!",
		nil)

	-- player does not have keyring
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		{
			conditions:create("QuestNotStartedCondition", {quest_slot}),
			conditions:notC(hasKeyringCondition),
		},
		ConversationStates.ATTENDING,
		"I don't think you have the experience to help me. Maybe if you knew"
			.. " more about keyrings.",
		nil)

	-- player has keyring & can start quest
	ari:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		check.canStart,
		ConversationStates.ATTENDING,
		"Hey! You look like an experienced adventurer. Perhaps you could help me with a #task.",
		nil)

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		check.canStart,
		ConversationStates.QUEST_OFFERED,
		"I have lost my engagement ring, and I am too embarrassed to tell " .. bride_name .. ". Would you help me?",
		nil)

	-- player has already completed quest
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		check.questCompleted,
		ConversationStates.ATTENDING,
		"Thank you, but I have everything I need now.",
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

	-- player accepts quest
	ari:add(
		ConversationStates.QUEST_OFFERED,
		ConversationPhrases.YES_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"Thank you so much! I lost my ring while visiting Athor Island."
			.. " Please let me know when you find it. And don't say anything to "
			.. bride_name .. ".",
		setQuestAction)
end

local prepareBringStep = function()
	ari:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		check.questActive,
		ConversationStates.QUESTION_1,
		"Did you find my ring? If there is something I can do to #help, please let me know.",
		nil)

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.HELP_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"I may have dropped it while walking along the beach. If that is the case,"
			.. " you may need something to dig in the sand.",
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
		"Thank you so much! As a reward, I will give you this keyring. It is larger than the one you have.",
		{
			actions:create("DropInfostringItemAction", {"engagement ring", ring_infostring}),
			actions:create(rewardAction),
		})

	-- player reports that ring was misplaced

	ari:add(
		ConversationStates.QUESTION_1,
		"lost",
		conditions:notC(hasRingCondition),
		ConversationStates.IDLE,
		"You lost my lost engagement ring? That's kind of ironic."
			.. " I bet you dropped it near where you found it."
			.. " Check there again.",
		resetQuestAction)

	ari:add(
		ConversationStates.QUESTION_1,
		"lost",
		hasRingCondition,
		ConversationStates.IDLE,
		"You lost my lost engagement ring? That's kind of ironic."
			.. " I bet you're teasing me.",
		nil)
end


quests:create(quest_slot, "Lost Engagement Ring"):register(function()
	prepareNPC()
	prepareRequestStep()
	prepareBringStep()
end)
