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
	local helpReply = "I can give you a #gift, teleport you to #visit some select areas, teleport you to #meet with someone,"
		.. " #summon an enemy for you to fight, or #inspect or #alter an attribute. Also, if you have an empty scroll, I can"
		.. " #mark it for you."
	npc:addHelp(helpReply)
	npc:addOffer(helpReply)


	local giftAction = actions:create(function(player, sentence, npc)
		local target = sentence:getTrimmedText():lower()

		local superItem = false
		if string.beginsWith(target, "super ") then
			local tmp = string.trim(target:sub(6, #target))
			if tmp == "golden blade" or tmp == "royal dagger" then
				superItem = true
				target = tmp
			end
		end

		if not entities.manager:isItem(target) then
			npc:say("\"" .. target .. "\" is not an item.")
			return
		end

		local item = entities:getStackableItem(target)
		local quantity = 1
		if item ~= nil then
			-- if stackable, give players 100
			quantity = 100
			item:setQuantity(quantity)
		else
			item = entities:getItem(target)
		end

		if item == nil then
			npc:say("Hmmmm... I'm sorry, there was a problem creating the " .. target .. ". Let's try something else.")
			return
		end

		if target == "bestiary" then
			-- convert to Bestiary item so item:setOwner() can be called instead of directly adding the attribute
			item = luajava.newInstance("games.stendhal.server.entity.item.Bestiary", item)
			item:setOwner(player:getName())
		end

		-- bind to player so can't be lost
		--item:put("bound", player:getName())

		if player:getSlotToEquip(item) == nil then
			npc:say("You don't have room to carry that item.")
			return
		end

		local response = "Here "
		if quantity > 1 then
			response = response .. "are " .. tostring(quantity) .. " "
		else
			response = response .. "is your "
		end
		if superItem then
			item:put("atk", "50")
			item:put("lifesteal", "1.0")

			response = response .. "super "
		end
		response = response .. grammar:plnoun(quantity, item:getName()) .. "."
		if superItem then
			-- FIXME: modified attributes "atk", "lifesteal", & "owner" are not restored after logout
			response = response .. " If you log off, your item will lose its \"super\" attributes."
		end

		npc:say(response)
		player:equipOrPutOnGround(item)
	end)

	local visitAction = actions:create(function(player, sentence, npc)
		local target = sentence:getTrimmedText():lower()
		local details = approvedMaps[target]

		if details == nil then
			npc:say("I cannot teleport you to " .. target .. ".")
			return
		end

		player:teleport(details[1], details[2], details[3], nil, player)
		npc:setCurrentState(ConversationStates.IDLE)
	end)

	local meetAction = actions:create(function(player, sentence, npc)
		local targetName = sentence:getTrimmedText():lower()
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

	local markAction = actions:create(function(player, sentence, npc)
		local scroll = player:getFirstEquipped("empty scroll")
		if scroll == nil then
			npc:say("You are not carrying an empty scroll.")
			return
		end

		local target = sentence:getTrimmedText():lower()
		local args = string.split(target, " ")

		local map = args[1]
		local x = args[2]
		local y = args[3]

		if map == nil then
			npc:say("I cannot #mark your scroll unless you tell me the name of the map.")
			return
		elseif game:getZone(map) == nil then
			npc:say("That map does not exist in this world. Please tell me a valid map so I can #mark your scroll.")
			return
		elseif string.startswith(map, "int_") then
			npc:say("For privacy reasons, I will not mark your scroll for an interior map. Wouldn't want you"
				.. " snooping around in someone else's house. What else can I do for you?")
			return
		end

		if x == nil then
			npc:say("I cannot #mark your scroll unless you tell me the X coordinate.")
			return
		end
		if y == nil then
			npc:say("I cannot #mark your scroll unless you tell me the Y coordinate.")
			return
		end

		local badCoord = nil
		if not string.isnumber(x) then
			badCoord = "X"
		elseif not string.isnumber(y) then
			badCoord = "Y"
		end

		if badCoord ~= nil then
			npc:say("The " .. badCoord .. " coordinate must be a number.")
			return
		end

		-- take one scroll & mark it
		player:drop(scroll)
		local marked = entities:getItem("marked scroll")
		marked:put("infostring", map .. " " .. x .. " " .. y)
		player:equipOrPutOnGround(marked)

		npc:say("Here is your scroll for " .. map .. ". What else can I do for you?")
	end)

	local summonAction = actions:create(function(player, sentence, npc)
		-- NOTE: rare & abnormal creatures can be summon by Aida
		local enemyName = sentence:getTrimmedText()

		local summoned = entities:summonCreature({
			name = enemyName,
			zone = zone,
			x = 15,
			y = 52,
			summoner = npc:getName(),
			raid = false})

		if summoned == 1 then
			npc:say("That is not an enemy found in this world.")
			return
		elseif summon == 2 then
			npc:say("I encountered a problem summoning that enemy to " .. zone .. ".")
			return
		end

		npc:say("I have summoned the " .. enemyName .." below. Please be careful.")
		npc:setCurrentState(ConversationStates.IDLE)
	end)

	local inspectAction = function(player, sentence, npc)
		local attributes = {"quest"}
		local attrCount = #attributes

		local trigger = sentence:getTrimmedText()
		if trigger:lower() == "inspect" then
			local sb = string.builder("I can ")
			local idx = 0
			for _, a in pairs(attributes) do
				sb:append("#'inspect " .. a .. "'")
				if idx == attrCount - 1 then
					sb:append(" ")
				elseif idx == attrCount - 2 then
					if attrCount == 2 then
						sb:append(" and ")
					else
						sb:append(", and ")
					end
				else
					sb:append(", ")
				end

				idx = idx + 1
			end

			sb:append("or any recognized player attribute.")

			npc:say(sb:toString())
			return
		end

		local parts = trigger:split(" ")
		table.remove(parts, 1)
		local attribute = parts[1]
		table.remove(parts, 1)
		local target = table.join(parts, " ")

		if player:has(attribute) then
			player:sendPrivateText("Inspected value of " .. attribute .. " for player " .. player:getName() .. ": " .. player:get(attribute))
			return
		end

		local attrLower = attribute:lower()
		if attrLower == "quest" then
			if target == nil or target == "" then
				npc:say("You must specify the quest ID you want to inspect.")
				return
			end

			local questState = player:getQuest(target)
			if questState == nil then
				questState = "null"
			end

			player:sendPrivateText(target .. " (" .. player:getName() .. "): " .. questState)
			return
		end

		npc:say("I'm afraid I am not familiar with the \"" .. attribute .. "\" attribute.")
	end

	local alterAction = function(player, sentence, npc)
		local attributes = {"quest"}
		local attrCount = #attributes

		local trigger = sentence:getTrimmedText()
		if trigger:lower() == "alter" then
			local sb = string.builder("I can ")
			local idx = 0
			for _, a in pairs(attributes) do
				sb:append("#'alter " .. a .. "'")
				if idx == attrCount - 1 then
					sb:append(" ")
				elseif idx == attrCount - 2 then
					if attrCount == 2 then
						sb:append(" and ")
					else
						sb:append(", and ")
					end
				else
					sb:append(", ")
				end

				idx = idx + 1
			end

			--sb:append("or any recognized player attribute.")
			sb:append(".")

			npc:say(sb:toString())
			return
		end

		local parts = trigger:split(" ")
		table.remove(parts, 1)
		local attribute = parts[1]
		table.remove(parts, 1)
		--local target = table.join(parts, " ")

		local attrLower = attribute:lower()
		if attrLower == "quest" then
			local target = parts[1]
			if target == nil or target == "" then
				npc:say("You must specify the quest ID you want to alter.")
				return
			end

			local prevState = player:getQuest(target)
			if prevState == nil then
				prevState = "null"
			end

			table.remove(parts, 1)
			local nextState = table.join(parts, " ")
			if nextState == "" then
				nextState = nil
			end

			player:setQuest(target, nextState)

			if nextState == nil then
				nextState = "null"
			end
			player:sendPrivateText(target .. " (" .. player:getName() .. "): changed from \"" .. prevState .. "\" to \"" .. nextState .. "\"")
			return
		end

		npc:say("I'm afraid I am not familiar with the \"" .. attribute .. "\" attribute.")
	end


	local hasScrollCondition = conditions:create("PlayerHasItemWithHimCondition", {"empty scroll"})

	npc:add(ConversationStates.ATTENDING,
		"gift",
		nil,
		ConversationStates.QUESTION_1,
		"I can give you any item. I can also give you a special #'super golden blade' or #'super royal dagger'. What would you like?",
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

	-- not carrying empty scroll
	npc:add(ConversationStates.ATTENDING,
		"mark",
		conditions:notCondition(hasScrollCondition),
		ConversationStates.ATTENDING,
		"You are not carrying an empty scroll.",
		nil)

	npc:add(ConversationStates.ATTENDING,
		"mark",
		hasScrollCondition,
		ConversationStates.QUESTION_4,
		"Please tell me the map and the X Y coordinates.",
		nil)

	npc:add(ConversationStates.ATTENDING,
		"summon",
		nil,
		ConversationStates.QUESTION_5,
		"What would you like me to summon?",
		nil)

	npc:add(ConversationStates.ATTENDING,
		"inspect",
		nil,
		ConversationStates.ATTENDING,
		nil,
		inspectAction)

	npc:add(ConversationStates.ATTENDING,
		"alter",
		nil,
		ConversationStates.ATTENDING,
		nil,
		alterAction)

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

	npc:add(ConversationStates.QUESTION_4,
		"",
		nil,
		ConversationStates.ATTENDING,
		nil,
		markAction)

	npc:add(ConversationStates.QUESTION_5,
		"",
		nil,
		ConversationStates.ATTENDING,
		nil,
		summonAction)
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
