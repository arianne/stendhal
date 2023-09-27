--[[
 ***************************************************************************
 *                    Copyright © 2020-2023 - Arianne                      *
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


local grocer = nil

local function addNPC()
	grocer = entities:create({
		type = "SpeakerNPC",
		name = "Jimbo",
		outfit = {
			layers = "dress=5,mouth=1,eyes=0,mask=1",
			colors = {
				skin = SkinColor.DARK,
				dress = 0x8b4513, -- saddle brown
				eyes = 0x228b22, -- forest green
				mask = 0xffffff -- white (doesn't work with glasses because they are fully black)
			}
		},
		pos = {25, 24},
		idleDir = Direction.UP
	})

	grocer:addGreeting()
	grocer:addGoodbye()
	grocer:addJob("I run the Deniran grocery store.")
	grocer:addOffer("Check the blackboard for the items I sell and their prices.")
	grocer:addHelp(grocer:getReply("offer"))
	grocer:addQuest("Sorry, there is nothing I need help with at this time.")

	game:add(grocer)
end

local function addSign()
	if grocer ~= nil then
		local sellSign = entities:create({
			type = "ShopSign",
			pos = {23, 23},
			name = "denirangrocersell",
			title = grocer:getName() .. "'s Shop (selling)",
			caption = "You can buy these things.",
			seller = true,
			class = "blackboard"
		})
		game:add(sellSign)
	end
end


local zone = "int_deniran_grocery_store"

if game:setZone(zone) then
	addNPC()
	addSign()
else
	logger:error("Could not set zone: " .. zone)
end
