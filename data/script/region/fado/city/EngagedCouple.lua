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


local onGoodbye = function(entity)
	entity:setIdea("love")
end


local zone_name = "0_fado_city"

if game:setZone(zone_name) then
	local w_name = "Emma"
	local h_name = "Ari"

	local wifeToBe = entities:createSpeakerNPC(w_name)
	local husbandToBe = entities:createSpeakerNPC(h_name)

	wifeToBe:setOutfit("body=1,head=0,eyes=1,hair=23,dress=17")
	wifeToBe:setOutfitColor("eyes", 0xdc143c)
	wifeToBe:setOutfitColor("hair", 0x8a2be2)
	wifeToBe:setOutfitColor("dress", 0x008080)
	wifeToBe:setIdea("love")
	wifeToBe:setIgnorePlayers(true)
	wifeToBe:setOnRejectedAttack(function(attacker)
		if attacker ~= nil then
			husbandToBe:say(attacker:getName() .. ", please leave " .. w_name .. " alone.")
		else
			husbandToBe:say("Please leave " .. w_name .. " alone.")
		end
	end)
	wifeToBe:setPosition(75, 56)
	wifeToBe:setIdleDirection(Direction.LEFT)

	--[[
	wifeToBe:addGreeting("Hello.")
	wifeToBe:addGoodbye()
	wifeToBe:onGoodbye(function()
		wifeToBe:setIdea("love")
	end)
	]]

	husbandToBe:setOutfit("body=0,head=0,eyes=21,hair=11,dress=53")
	husbandToBe:setOutfitColor("eyes", 0x89cff0)
	husbandToBe:setOutfitColor("hair", 0x0d98ba)
	husbandToBe:setIdea("love")
	husbandToBe:setPosition(74, 56)
	husbandToBe:setIdleDirection(Direction.RIGHT)

	husbandToBe:addGreeting("Hello.")
	husbandToBe:addGoodbye()
	husbandToBe:onGoodbye(function()
		print("husbandToBe:onGoodbye")
		husbandToBe:setIdea("love")
		--husbandToBe:setOutfitColor("hair", Color.GREEN)
		--husbandToBe:put("idea", "love")
		--husbandToBe:notifyWorldAboutChanges()
	end)

	wifeToBe:setDescription("You see " .. w_name .. ". " .. w_name .. " sees " .. h_name .. ".")
	husbandToBe:setDescription("You see " .. h_name .. ". " .. h_name .. " sees " .. w_name .. ".")

	game:add(wifeToBe)
	game:add(husbandToBe)
else
	logger:error("could not set zone: " .. zone_name)
end
