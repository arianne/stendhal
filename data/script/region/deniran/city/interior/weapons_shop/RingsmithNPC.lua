--[[
 ***************************************************************************
 *                       Copyright © 2020 - Arianne                        *
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


local zone = "int_deniran_weapons_shop"

local ringsmith = nil
local questSlot = "raven_forge_ring"
local waitTime = MathHelper.MINUTES_IN_ONE_HOUR * 6
local ring = "enhanced imperial ring"

local requirements = {
	{"imperial ring", 1},
	{"turtle shell ring", 1},
	{"unicorn horn", 15},
	{"money", 260000},
}

local function sayBringMessage(npc)
	sb = newStringBuilder()

	reqCount = #requirements
	for idx, item in ipairs(requirements) do
		if idx > 1 then
			sb:append(", ")

			if idx == reqCount then
				sb:append("and ")
			end
		end

		local itemName = item[1]
		local quant = item[2]
		itemName = grammar:plnoun(quant, itemName)
		if itemName ~= "money" then
			itemName = "#'" .. itemName .. "'"
		end

		sb:append(tostring(quant) .. " " .. itemName)
	end

	if npc == nil then
		return sb:toString()
	else
		sb:insert(0, "Bring me ")
		sb:append(" and I can make you something special.")
		ringsmith:say(sb:toString())
	end
end

if game:setZone(zone) then
	ringsmith = npcHelper:createSpeakerNPC("Raven")
	ringsmith:setOutfit("body=1,head=0,eyes=24,dress=52,hair=13")
	--ringsmith:setOutfitColor("eyes", Color.GREEN)
	ringsmith:setOutfitColor("eyes", 0x1f6521)
	ringsmith:setOutfitColor("hair", Color.RED)

	-- path
	local nodes = {
		{24, 12},
		{31, 12},
		{31, 6},
		{24, 6},
	}
	npcHelper:setPathAndPosition(ringsmith, nodes, true)

	-- dialogue
	ringsmith:addGreeting()
	ringsmith:addGoodbye()
	ringsmith:addQuest("I have no task for you at this time.")
	ringsmith:addJob("I #forge special items.")

	local hasItemsCondition = {}
	local startAction = {
		newAction("SetQuestToTimeStampAction", questSlot),
		newAction("SayTimeRemainingAction", questSlot, waitTime, "Okay, I will get started. Please come back in ",
			"And be sure to ask me about your #ring."),
		newAction("PlaySoundAction", "coins-01"),
	}

	for _, item in ipairs(requirements) do
		table.insert(hasItemsCondition, newCondition("PlayerHasItemWithHimCondition", item[1], item[2]))
		table.insert(startAction, newAction("DropItemAction", item[1], item[2]))
	end

	local rewardAction = {
		newAction("EquipItemAction", ring),
		newAction("SetQuestAction", questSlot, nil),
		-- FIXME: need to call player.incProducedCountForItem
	}

	local forgePhrases = {"forge", "ring"}
	-- FIXME: concatenating multiple tables at once doesn't work
	--table.concat(forgePhrases, ConversationPhrases.HELP_MESSAGES, ConversationPhrases.OFFER_MESSAGES)
	table.concat(forgePhrases, ConversationPhrases.HELP_MESSAGES)
	table.concat(forgePhrases, ConversationPhrases.OFFER_MESSAGES)

	ringsmith:add(ConversationStates.ATTENDING,
		forgePhrases,
		{
			newCondition("QuestNotActiveCondition", questSlot),
			newNotCondition(hasItemsCondition),
		},
		ConversationStates.ATTENDING,
		"Bring me " .. sayBringMessage() .. " and I can make you something special.",
		nil)

	ringsmith:add(ConversationStates.ATTENDING,
		forgePhrases,
		{
			newCondition("QuestNotActiveCondition", questSlot),
			hasItemsCondition,
		},
		ConversationStates.QUESTION_1,
		"I will make you a special item for " .. sayBringMessage() .. ". Do you want me to start?",
		nil)

	ringsmith:add(ConversationStates.QUESTION_1,
		ConversationPhrases.NO_MESSAGES,
		nil,
		ConversationStates.ATTENDING,
		"Okay.",
		nil)

	ringsmith:add(ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		newNotCondition(hasItemsCondition),
		ConversationStates.ATTENDING,
		"You seem to have dropped something.",
		nil)

	ringsmith:add(ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		hasItemsCondition,
		ConversationStates.IDLE,
		nil,
		startAction)

	ringsmith:add(ConversationStates.ATTENDING,
		forgePhrases,
		{
			newCondition("QuestActiveCondition", questSlot),
			newNotCondition("TimePassedCondition", questSlot, waitTime),
		},
		ConversationStates.ATTENDING,
		nil,
		newAction("SayTimeRemainingAction", questSlot, waitTime, "Your ring is not ready. Please come back in"))

	ringsmith:add(ConversationStates.ATTENDING,
		forgePhrases,
		{
			newCondition("QuestActiveCondition", questSlot),
			newCondition("TimePassedCondition", questSlot, waitTime),
		},
		ConversationStates.ATTENDING,
		"Here is your " .. ring .. ".",
		rewardAction)

	game:add(ringsmith)
else
	logger:error("Could not set zone: " .. zone)
end
