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


-- Example SpeakerNPC

--[[ disabled
logger:info("Loading Lua SpeakerNPC...")

-- Set zone to Semos City
if game:setZone("0_semos_city") then

	-- Use helper object to create a new NPC
	local npc = npcHelper:createSpeakerNPC("Lua")
	npc:setEntityClass("littlegirlnpc")
	npc:setPosition(10, 55)
	npc:setBaseSpeed(0.1)
	npc:setCollisionAction(CollisionAction.STOP)

	local nodes = {
		{10, 55},
		{11, 55},
		{11, 56},
		{10, 56},
	}

	-- Use helper object to create NPC path
	npcHelper:setPath(npc, nodes)

	-- Dialogue
	npc:addJob("Actually, I am jobless.")
	npc:addGoodbye();

	-- Some custom replies using conditions & actions
	npc:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		nil,
		ConversationStates.ATTENDING,
		"I am sad, because I do not have a job.",
		nil
	)
	npc:add(
		ConversationStates.ATTENDING,
		"Lua",
		newCondition("PlayerNextToCondition"),
		ConversationStates.ATTENDING,
		"Um, could you back up please? I can smell your breath.",
		newAction("NPCEmoteAction", "coughs", false)
	)
	npc:add(
		ConversationStates.ATTENDING,
		"Lua",
		newCondition("NotCondition", newCondition("PlayerNextToCondition")),
		ConversationStates.ATTENDING,
		"That's my name, don't wear it out!",
		newAction("NPCEmoteAction", "giggles", false)
	)

	-- Set up a sign for Lua
	local sign = game:createSign()
	sign:setEntityClass("signpost")
	sign:setPosition(12, 55)
	sign:setText("Meet Lua!")

	-- Add the entities to the world
	game:add(npc)
	game:add(sign)

	logger:info("Lua SpeakerNPC loaded!")

	dofile("data/script/region/semos/city/quest.lua.example")
else
	logger:warn("Failed to load Lua SpeakerNPC")
end
--]]
