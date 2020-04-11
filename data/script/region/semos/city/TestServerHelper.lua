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


local zone = "0_semos_city"
local npc = nil

local approvedItems = {
	["money"] = 100000,
	["mega potion"] = 100,
	["greater antidote"] = 100,
	["sedative"] = 100,
	["fish soup"] = 100,
	["home scroll"] = 100,
	["ados city scroll"] = 100,
	["fado city scroll"] = 100,
	["kirdneh city scroll"] = 100,
	["kalavan city scroll"] = 100,
	["nalwor city scroll"] = 100,
	["deniran city scroll"] = 100,
	["empty scroll"] = 100,
	["balloon"] = 100,
	["rainbow beans"] = 100,
	["twilight moss"] = 100,
	["bestiary"] = 1,
	["royal helmet"] = 1,
	["royal armor"] = 1,
	["royal legs"] = 1,
	["royal boots"] = 1,
	["royal shield"] = 1,
	["royal cloak"] = 1,
	["royal dagger"] = 1,
	["golden blade"] = 1,
}

local function getItemListString()
	local st = "I can give you "

	for key in pairs(approvedItems) do
		st = st .. "#'" .. key .. "', "
	end

	st = st .. "#'super royal dagger', and #'super golden blade'."
	return st
end

local approvedMaps = {
	["amazon"] = {"int_amazon_princess_hut", 10, 15},
	["athor"] = {"0_athor_island", 36, 73},
	["atlantis"] = {"-7_deniran_atlantis", 65, 65},
	["kika"] = {"6_kikareukin_islands", 106, 55},
	["magic city"] = {"-1_fado_great_cave_n_e3", 21, 42},
	["sedah"] = {"int_sedah_house2", 6, 9},
}

local function getMapListString()
	local st = "I can teleport you to "
	--local mapCount = #approvedMaps
	local mapCount = 0
	local idx = 0

	-- FIXME: not sure why "#approvedMaps" doesn't work
	for key in pairs(approvedMaps) do
		mapCount = mapCount + 1
	end

	for key in pairs(approvedMaps) do
		idx = idx + 1

		if idx == mapCount then
			st = st .. "and "
		end

		st = st .. "#'" .. key .."'"

		if idx < mapCount then
			st = st .. ", "
		end
	end

	st = st .. "."
	return st
end

local function createNPC()
	npc = entities:createSpeakerNPC("Aida")
	npc:setEntityClass("youngwitchnpc")
	npc:setPosition(17, 42)
	npc:setIdleDirection(Direction.DOWN)

	npc:addGreeting("Greetings! How may I #help you?")
	npc:addGoodbye("See ya!");
	local helpReply = "I can give you a #gift, teleport you to #visit some select areas, or teleport you to #meet with someone."
	npc:addHelp(helpReply)
	npc:addOffer(helpReply)

	local giftAction = actions:create(function(player, sentence, npc)
		local target = sentence:getTrimmedText():gsub("^%s*(.-)%s*$", "%1"):lower()

		local superGoldenBlade = target == "super golden blade"
		local superRoyalDagger = target == "super royal dagger"

		if superGoldenBlade then
			target = "golden blade"
		end
		if superRoyalDagger then
			target = "royal dagger"
		end

		if not entities.manager:isItem(target) then
			npc:say("That is not an item.")
			return
		end

		local quantity = approvedItems[target]
		if quantity == nil then
			npc:say("I cannot give you that.")
			return
		end

		local item = nil;
		if quantity > 1 then
			item = entities:getStackableItem(target)
			item:setQuantity(quantity)
		else
			item = entities:getItem(target)
			if superGoldenBlade or superRoyalDagger then
				item:put("atk", "50")
				item:put("lifesteal", "1.0")
				item:remove("min_level")
			end
		end

		-- TODO: check if item is nil

		if item:getName():find("royal") then
			item:remove("min_level")
		end

		item:put("bound", player:getName())
		if target == "bestiary" then
			item:put("owner", player:getName())
		end

		if player:getSlotToEquip(item) == nil then
			npc:say("You don't have room to carry that item.")
			return
		end

		-- FIXME: modified attributes "atk", "lifesteal", & "owner" are not restored after logout

		local response = "Here "
		if quantity > 1 then
			response = response .. "are your " .. tostring(quantity) .. " "
		else
			response = response .. "is your "
		end
		response = response .. grammar:plnoun(quantity, item:getName()) .. "."

		npc:say(response)
		player:equipOrPutOnGround(item)
	end)

	local visitAction = actions:create(function(player, sentence, npc)
		local target = sentence:getTrimmedText():gsub("^%s*(.-)%s*$", "%1"):lower()
		local details = approvedMaps[target]

		if details == nil then
			npc:say("I cannot teleport you to " .. target .. ".")
			return
		end

		player:teleport(details[1], details[2], details[3], nil, player)
		npc:setCurrentState(ConversationStates.IDLE)
	end)

	local meetAction = actions:create(function(player, sentence, npc)
		local targetName = sentence:getTrimmedText():gsub("^%s*(.-)%s*$", "%1"):lower()
		local target = entities:getPlayer(targetName)

		if target == nil then
			target = entities:getNPC(targetName)
		end

		if target == nil then
			npc:say("There is no one in this world with that name.")
			return
		end

		local zone = target:getZone()
		local x = target:getX()
		local y = target:getY()

		player:teleport(zone, x, y, nil, player)
		npc:setCurrentState(ConversationStates.IDLE)
	end)

	npc:add(ConversationStates.ATTENDING,
		"gift",
		nil,
		ConversationStates.QUESTION_1,
		getItemListString() .. " What would you like?",
		nil)

	npc:add(ConversationStates.ATTENDING,
		"visit",
		nil,
		ConversationStates.QUESTION_2,
		getMapListString() .. " Where would you like to go?",
		nil)

	npc:add(ConversationStates.ATTENDING,
		"meet",
		nil,
		ConversationStates.QUESTION_3,
		"Who would you like to meet?",
		nil)

	npc:add(ConversationStates.QUESTION_1,
		"",
		nil,
		ConversationStates.ATTENDING,
		nil,
		giftAction)

	npc:add(ConversationStates.QUESTION_2,
		"",
		nil,
		ConversationStates.ATTENDING,
		nil,
		visitAction)

	npc:add(ConversationStates.QUESTION_3,
		"",
		nil,
		ConversationStates.ATTENDING,
		nil,
		meetAction)
end


if properties:enabled("stendhal.testserver") then
	logger:info("Creating test server helper NPC")

	if game:setZone(zone) then
		createNPC()

		if npc ~= nil then
			game:add(npc)
		else
			logger:error("Could not create test server helper NPC")
		end
	else
		logger:error("Could not set zone: " .. zone)
	end
end
