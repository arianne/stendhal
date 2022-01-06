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


local zone_name = "int_deniran_pawn_shop"

if game:setZone(zone_name) then
	local goldenBladeGrower = luajava.newInstance("games.stendhal.server.entity.mapstuff.spawner.PassiveEntityRespawnPoint", "golden blade", 288000)
	goldenBladeGrower:setPosition(19, 3)

	game:add(goldenBladeGrower)
	goldenBladeGrower:setToFullGrowth()
else
	logger:error("could not set zone: " .. zone_name)
end
