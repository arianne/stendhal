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


-- leather needle that spawns on table

local zone = "int_deniran_tannery"

if game:setZone(zone) then
	-- respawns after 10 minutes
	local needleGrower = luajava.newInstance("games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint", "leather needle", 2000)
	needleGrower:setDescription("It looks like there was a small item sitting on this crate at some point.")
	needleGrower:setPosition(32, 13)
	game:add(needleGrower)
	needleGrower:setToFullGrowth()
else
	logger:error("Could not set zone: " .. zone)
end
