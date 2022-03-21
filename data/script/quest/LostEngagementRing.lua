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

local minLevel = 50
local karmaAcceptReward = 15
local karmaCompleteReward = 50

local ring_infostring = "Ari's ring"

-- location where ring may be on Athor island
local ring_locations = {
	{49, 30},
	{123, 3},
	{115, 122},
	{49, 106},
}

local questNotStartedCondition = conditions:create("QuestNotStartedCondition", {quest_slot})
local questCompletedCondition = conditions:create("QuestCompletedCondition", {quest_slot})
local questActiveCondition = conditions:create("QuestActiveCondition", {quest_slot})
local hasRingCondition = conditions:create("PlayerHasInfostringItemWithHimCondition", {
	"engagement ring",
	ring_infostring,
})
local hasKeyringCondition = conditions:create(function(player, sentence, npc)
	return player:hasFeature("keyring")
end)
local minLevelCondition = conditions:create("LevelGreaterThanCondition", {minLevel - 1})

--[[ FIXME: conditions:create is struggling with constructors that take varargs
local visitedAthorCondition = conditions:create("PlayerVisitedZonesCondition", {
	"0_athor_island",
})
]]
local visitedAthorCondition = luajava.newInstance(
	"games.stendhal.server.entity.npc.condition.PlayerVisitedZonesCondition",
	{"0_athor_island"})

local canStartCondition = conditions:andC({
	questNotStartedCondition,
	conditions:notC(questCompletedCondition),
	hasKeyringCondition,
	visitedAthorCondition,
	minLevelCondition,
})

local chooseRingLocation = function()
	return ring_locations[random:randUniform(1, 4)]
end

local setQuestAction = function(player, sentence, npc)
	player:addKarma(karmaAcceptReward)

	-- choose random location
	local selected = chooseRingLocation()
	player:setQuest(quest_slot, 0, selected[1])
	player:setQuest(quest_slot, 1, selected[2])
end

local resetQuestAction = function(player, sentence, npc)
	if player:hasQuest(quest_slot) then
		local slots = player:getQuest(quest_slot):split(";")
		local x = slots[1]
		local y = slots[2]

		if not string.isNumber(x) then
			x = slots[2]
			y = slots[3]
		end

		if not string.isNumber(x) or not string.isNumber(y) then
			logger:warn(quest_slot .. " quest: malformatted quest slot, setting"
				.. " new ring coordinates")

			local selected = chooseRingLocation()
			x = selected[1]
			y = selected[2]
		end

		player:setQuest(quest_slot, x .. ";" .. y)
	end
end

local rewardAction = function(player, sentence, npc)
	player:addKarma(karmaCompleteReward)
	player:setFeature("keyring", "3 4")

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
				questNotStartedCondition,
				conditions:orC({
					conditions:notC(hasKeyringCondition),
					conditions:notC(visitedAthorCondition),
					conditions:notC(minLevelCondition),
				}),
			},
			questCompletedCondition,
		}),
		ConversationStates.ATTENDING,
		"Hi there!",
		nil)

	-- player doesn't meet minimum level requirement
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		{
			questNotStartedCondition,
			conditions:notC(minLevelCondition),
		},
		ConversationStates.ATTENDING,
		"I don't think you have the experience to help me. Come back when"
			.. " you are stronger.",
		nil)

	-- player does not have keyring
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		{
			questNotStartedCondition,
			minLevelCondition,
			conditions:notC(hasKeyringCondition),
		},
		ConversationStates.ATTENDING,
		"I don't think you have the experience to help me. Maybe if you knew"
			.. " more about keyrings.",
		nil)

	-- player has not visited Athor
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		{
			questNotStartedCondition,
			minLevelCondition,
			hasKeyringCondition,
			conditions:notC(visitedAthorCondition),
		},
		ConversationStates.ATTENDING,
		"I don't think you have the experience to help me. Maybe if you were"
			.. " more familiar with Athor island.",
		nil)

	-- player has keyring & can start quest
	ari:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		canStartCondition,
		ConversationStates.ATTENDING,
		"Hey! You look like an experienced adventurer. Perhaps you could help me with a #task.",
		nil)

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		canStartCondition,
		ConversationStates.QUEST_OFFERED,
		"I have lost my engagement ring, and I am too embarrassed to tell " .. bride_name .. ". Would you help me?",
		nil)

	-- player has already completed quest
	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		questCompletedCondition,
		ConversationStates.ATTENDING,
		"Thank you, but I have everything I need now.",
		nil)

	ari:add(
		ConversationStates.ATTENDING,
		ConversationPhrases.QUEST_MESSAGES,
		questActiveCondition,
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
		questActiveCondition,
		ConversationStates.QUESTION_1,
		"Did you find my ring? If there is something I can do to #help, please let me know.",
		nil)

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.HELP_MESSAGES,
		nil,
		ConversationStates.IDLE,
		"I may have dropped it while walking along the beach. If that is the case,"
			.. " you may need something to dig in the sand or something that can"
			.. " detect metal. Maybe a local pawn shop has a tool you could use.",
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
		conditions:notC(hasRingCondition),
		ConversationStates.IDLE,
		"You don't have my ring. Please, keep looking.",
		nil)

	local finishMessage = "Thank you so much! As a reward, I will give you this keyring."
		.. " It is larger than the one you have."
	local finishAction = {
		actions:create("DropInfostringItemAction", {"engagement ring", ring_infostring}),
		actions:create(rewardAction),
	}

	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		hasRingCondition,
		ConversationStates.IDLE,
		finishMessage,
		finishAction)

	-- compatibility so players can say "done"
	ari:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.FINISH_MESSAGES,
		hasRingCondition,
		ConversationStates.IDLE,
		finishMessage,
		finishAction)

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


-- set up metal detector lender

-- we use a quest slot so player cannot borrow multiple metal detectors
local lender_slot = "sawyer_metal_detector"
local loanActive = conditions:create("QuestActiveCondition", {lender_slot})
local lend_phrases = {"loan", "lend", "metal detector", "borrow", "loaning", "lending", "borrowing"}

-- items that Sawyer will take in exchange for his metal detector
local collateral = {
	-- sword
	"black sword",
	"chaos dagger",
	"demon fire sword",
	"golden blade",
	"golden orc sword",
	"hell dagger",
	--"immortal sword",
	"imperator sword",
	"nihonto",
	"orcish sword",
	"royal dagger",
	"soul dagger",
	"xeno sword",

	-- axe
	"black halberd",
	"black scythe",
	"chaos axe",
	"durin axe",
	--"magic twoside axe",

	-- club
	"ice war hammer",
	"vulcano hammer",

	-- ranged
	"mithril bow",

	-- armor
	"black armor",
	"ice armor",
	--"magic plate armor",
	--"mainio armor",
	"mithril armor",
	"royal armor",

	-- boots
	--"black boots",
	--"magic plate boots",
	"mithril boots",
	"royal boots",

	-- cloak
	--"black cloak",
	"lich cloak",
	--"magic cloak",
	"mithril cloak",
	"royal cloak",
	"vampire cloak",

	-- helmet
	"black helmet",
	"liberty helmet",
	"magic chain helmet",
	"mithril helmet",
	"royal helmet",

	-- legs
	--"black legs",
	--"jewelled legs",
	--"magic plate legs",
	"mithril legs",
	"royal legs",

	-- ring
	--"emerald ring",
	"enhanced imperial ring",
	"imperial ring",
	"turtle shell ring",

	-- shield
	"black shield",
	"ice shield",
	--"magic plate shield",
	"mithril shield",
	"royal shield",
	--"xeno shield",
}

local loanMetalDetector = function(player, lender, detector, offer, bound_to, info_s)
	-- player walked away before receiving metal detector
	if lender:getAttending() == nil then
		lender:say("Where did you go? Oh well, guess I'll just put this back on the"
			.. " shelf.")
		sawyersShelf:returnMetalDetector(detector)
		lender:setCurrentState(ConversationStates.IDLE)
		return
	end

	-- problem with metal detector item
	if detector == nil then
		lender:say("Uh oh! It seems my metal detector is broken. Sorry pal. Please open"
			.. " a bug report https://stendhalgame.org/development/bug.html")
		lender:setCurrentState(ConversationStates.ATTENDING)
		return
	end

	local traded = player:getFirstEquipped(offer)

	if traded == nil then
		lender:say("Ha! Thought you could trick me?")
		lender:setCurrentState(ConversationStates.ATTENDING)
		return
	end

	-- handle items with infostring & bound items
	local bound_to = traded:getBoundTo() or ""
	local info_s = traded:getInfoString() or ""

	local slot_state = offer .. ";" .. bound_to .. ";" .. info_s

	detector:setInfoString("Sawyer;" .. slot_state)
	detector:setBoundTo(player:getName())
	detector:setUndroppableOnDeath(true)

	player:setQuest(lender_slot, slot_state)
	player:drop(traded)
	player:equipOrPutOnGround(detector)

	lender:say("Okay, here you go. My metal detector for your " .. offer
		.. ". Be careful with it. If it gets lost, you won't be able to"
		.. " #return it and I will keep your " .. offer .. ". Anything"
		.. " else I can help you with?")
	lender:setCurrentState(ConversationStates.ATTENDING)
end

local handleLendRequest = function(player, sentence, lender)
	local offer = sentence:getTrimmedText()

	-- FIXME: using table.contains function breaks ChatAction.fire method created with actions:create

	-- player ends conversation
	--if not table.contains(arrays:toTable(ConversationPhrases.GOODBYE_MESSAGES), offer:lower()) then
	for _, msg in ipairs(arrays:toTable(ConversationPhrases.GOODBYE_MESSAGES)) do
		if offer:lower() == msg then
			lender:getEntity():endConversation()
			return
		end
	end

	-- player decides not to trade
	-- FIXME: arrays:toTable does not parse ConversationPhrases.NO_MESSAGES correctly
	for _, msg in ipairs({"no", "nope", "nothing", "none"}) do
		if offer:lower() == msg then
			lender:say("Okay then. What else can I help you with?")
			return
		end
	end

	-- Sawyer will not accept offered item
	--if not table.contains(collateral, offer) then
	local accepted = false
	for _, acceptable in ipairs(collateral) do
		if offer == acceptable then
			accepted = true
			break
		end
	end

	if not accepted then
		lender:say("Hmmm, I'm not interested in that. What else you got?")
		lender:setCurrentState(ConversationStates.QUESTION_1)
		return;
	end

	-- player isn't carrying the item
	if not player:isEquipped(offer) then
		lender:say("You're not even carrying one of those. Come on, what do you have?")
		lender:setCurrentState(ConversationStates.QUESTION_1)
		return
	end

	local detector = sawyersShelf:getMetalDetector()

	-- metal detector from shelf was already loaned out
	if detector == nil then
		lender:say("Hmmmm. I guess I forgot that I already loaned it out...")
		lender:say("!me is thinking.")
		lender:setCurrentState(ConversationStates.BUSY)

		game:runAfter(10, function()
			lender:say("Oh! That's right, I keep a spare just in case.")
			detector = sawyersShelf:getSpareMetalDetector()

			game:runAfter(5, function()
				loanMetalDetector(player, lender, detector, offer,
					bound_to, info_s)
			end)
		end)

		return
	end

	-- metal detector from shelf
	loanMetalDetector(player, lender, detector, offer, bound_to, info_s)
end

local handleReturnRequest = function(player, sentence, lender)
	if not player:isEquipped("metal detector") then
		lender:say("You aren't even carryng a metal detector.")
		return
	end

	local detector = player:getFirstEquipped("metal detector")
	local detector_info = (detector:getInfoString() or ""):split(";")

	-- Sawyer doesn't recognize the metal detector
	if #detector_info == 0 or detector_info[1] ~= "Sawyer" then
		lender:say("This isn't mine. Take it back. I want MY metal detector.")
		return
	end

	local item_name = detector_info[2]
	local bound_to = detector_info[3]
	local info_s = detector_info[4]

	local item = entities:getItem(item_name)

	-- problem with item
	if item == nil then
		lender:say("Uh oh! It seems I have broken your " .. item_name .. "."
			.. " Sorry pal. Maybe you could contact #/support and get someone"
			.. " to fix it for me.")
		return
	end

	if bound_to ~= "" then
		item:setBoundTo(bound_to)
	end

	if info_s ~= "" then
		item:setInfoString(info_s)
	end

	player:drop(detector)
	player:equipOrPutOnGround(item)
	player:setQuest(lender_slot, nil)

	sawyersShelf:returnMetalDetector(detector)
	lender:say("Okay. Here is your " .. item_name .. ". Good as new. Is there"
		.. " anything else I can help you with?")
end

local prepareMetalDetectorLender = function()
	local lender = entities:getNPC("Sawyer")
	if lender == nil then
		logger:error("Cannot set up metal detector lender Sawyer for Lost Engagement Ring quest")
		return
	end

	-- hint to players that they can borrow metal detector
	lender:addOffer("I also have some items that I don't mind #loaning out.")

	lender:add(
		ConversationStates.ATTENDING,
		lend_phrases,
		conditions:notC(loanActive),
		ConversationStates.QUESTION_1,
		"So, you want to borrow my metal detector? Well, I don't lend things out"
			.. " without some form of collateral. What would you like to leave"
			.. " behind?",
		nil)

	lender:add(
		ConversationStates.ATTENDING,
		lend_phrases,
		loanActive,
		ConversationStates.ATTENDING,
		"I've already loaned you my metal detector. You can #return it whenever you like.",
		nil)

	lender:add(
		ConversationStates.QUESTION_1,
		"",
		conditions:notC(loanActive),
		ConversationStates.ATTENDING,
		nil,
		handleLendRequest)

	lender:add(
		ConversationStates.ATTENDING,
		"return",
		conditions:notC(loanActive),
		ConversationStates.ATTENDING,
		"Return what? I haven't loaned anything to you.",
		nil)

	lender:add(
		ConversationStates.ATTENDING,
		"return",
		loanActive,
		ConversationStates.QUESTION_1,
		"You want to return my metal detector?",
		nil)

	lender:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.NO_MESSAGES,
		loanActive,
		ConversationStates.ATTENDING,
		"Okay. What else can I help you with?",
		nil)

	lender:add(
		ConversationStates.QUESTION_1,
		ConversationPhrases.YES_MESSAGES,
		loanActive,
		ConversationStates.ATTENDING,
		nil,
		handleReturnRequest)
end


local quest = quests:create(quest_slot, "Lost Engagement Ring",
	"A couple to be married in Fado City needs help.")
quest:setMinLevel(minLevel)
quest:setRegion(Region.FADO_CITY)
quest:setNPCName("Ari")

quest:setHistoryFunction(function(player)
	local history = {}

	local state = player:getQuest(quest_slot, 0)
	if state == "done" then
		table.insert(history, "I found Ari's ring. Now he is ready to wed with Emma.")
	elseif state == "found_ring" then
		if player:isEquippedWithInfostring("engagement ring", ring_infostring) then
			table.insert(history, "I have found Ari's ring and should bring it to him.")
		else
			table.insert(history, "I found Ari's ring, but I seem to have misplaced it."
				.. " I should report to Ari that I <em>lost</em> it.")
		end
	else
		table.insert(history, "Ari has lost his engagement ring while vacationing on Athor Island.")
		if player:isEquipped("metal detector") then
			table.insert(history, "I may be able to find it with this metal detector.")
		elseif player:isEquipped("shovel") then
			table.insert(history, "I may be able to find it with this shovel.")
		else
			table.insert(history, "I will need a tool to find it.")
		end
	end

	return history
end)

quest:register(function()
	prepareNPC()
	prepareRequestStep()
	prepareBringStep()
	prepareMetalDetectorLender()
end)
