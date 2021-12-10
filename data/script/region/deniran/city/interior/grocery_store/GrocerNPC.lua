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


local grocer = nil

local function addNPC()
	grocer = entities:createSpeakerNPC("Jimbo")
	grocer:setOutfit("dress=5,mouth=1,eyes=0,mask=1")
	grocer:setOutfitColor("skin", SkinColor.DARK)
	grocer:setOutfitColor("dress", 0x8b4513) -- saddle brown
	grocer:setOutfitColor("eyes", 0x228b22) -- forest green
	grocer:setOutfitColor("mask", 0xffffff) -- white (doesn't work with glasses because they are fully black)
	grocer:setPosition(25, 24)
	grocer:setIdleDirection(Direction.UP)

	grocer:addGreeting()
	grocer:addGoodbye()
	grocer:addJob("I run the Deniran grocery store.")
	grocer:addOffer("Check the blackboard for the items I sell and their prices.")
	grocer:addHelp(grocer:getReply("offer"))
	grocer:addQuest("Sorry, there is nothing I need help with at this time.")

	local sellPrices = {
		{"olive oil", 135},
		{"vinegar", 135},
		{"lamp", 100},
	}
	-- FIXME: not working for Lua table as argument
	--merchants:addSeller(grocer, sellPrices, false)

	local shopName = "denirangrocerysell"

	-- add to shop list
	for _, item in pairs(sellPrices) do
		merchants.shops:add(shopName, item[1], item[2])
	end

	merchants:addSeller(grocer, merchants.shops:get(shopName), false)

	game:add(grocer)
end

local function addSign()
	if grocer ~= nil then
		local sellSign = entities:createShopSign("denirangrocerysell", grocer:getName() .. "'s Shop (selling)", "You can buy these things.")
		sellSign:setEntityClass("blackboard")
		sellSign:setPosition(23, 23)

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
