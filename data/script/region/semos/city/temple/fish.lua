--[[
 ***************************************************************************
 *                       Copyright © 2021 - Arianne                        *
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


-- fishy swimming in fountain

local zone_name = "int_semos_temple"

if game:setZone(zone_name) then
	local fishy = entities:createSilentNPC()
	fishy:setEntityClass("animal/fish_roach")
	fishy:setDescription("You see a fish.")
	fishy:setVisibility(50)
	fishy:setPathAndPosition({
		{13,15}, {13,18},
		{10,18}, {10,15},
	}, true)
	fishy:setIgnoresCollision(true)
	fishy:setBaseSpeed(0.1)

	game:add(fishy)
else
	logger:error("Could not set zone: " .. zone_name)
end
