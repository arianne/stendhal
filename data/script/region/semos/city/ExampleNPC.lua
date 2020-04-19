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

if properties:equals("stendhal.testserver", "junk") then
	logger:info("Loading example NPC created with Lua")

	-- set zone to Semos City
	if game:setZone("0_semos_city") then

		-- use helper object to create a new NPC
		local lua = entities:createSpeakerNPC("Lua")
		lua:setEntityClass("littlegirlnpc")

		local nodes = {
			{10, 55},
			{11, 55},
			{11, 56},
			{10, 56},
		}

		lua:setPathAndPosition(nodes, true)
		lua:setBaseSpeed(0.1)
		lua:setCollisionAction(CollisionAction.STOP)

		-- dialogue
		lua:addGreeting("Hi there!")
		lua:addGoodbye("Buh bye!");
		lua:addJob("I am an example of how to create an entity using the Lua scripting engine.")
		lua:addHelp("How can I help you? I am just a kid.")
		lua:addOffer("I have a small #task you could help me with.")

		-- some custom replies using conditions & actions

		lua:add(ConversationStates.ATTENDING,
			"Lua",
			conditions:create("PlayerNextToCondition"),
			ConversationStates.ATTENDING,
			"Um, could you back up please? I can smell your breath.",
			actions:create("NPCEmoteAction", {"coughs", false}))

		lua:add(ConversationStates.ATTENDING,
			"Lua",
			conditions:notCondition(conditions:create("PlayerNextToCondition")),
			ConversationStates.ATTENDING,
			"That's my name, don't wear it out!",
			actions:create("NPCEmoteAction", {"giggles", false}))

		-- set up a sign for Lua
		local sign = entities:createSign()
		sign:setEntityClass("signpost")
		sign:setPosition(12, 55)
		sign:setText("Meet Lua!")

		-- add the entities to the world
		game:add(lua)
		game:add(sign)

		-- load related quest
		dofile("../../../quest/ExampleQuest")

		logger:info("Lua SpeakerNPC loaded!")
	else
		logger:warn("Failed to load Lua SpeakerNPC")
	end
end
