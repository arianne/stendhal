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


local zones = {
	"0_semos_plains_n",
	"0_semos_plains_ne",
	"0_semos_plains_n_e2",
	"0_semos_plains_w",
	"0_semos_plains_s",
	"0_semos_village_w",
	"0_semos_road_e",
	"0_semos_road_se",
}

for _, zone in pairs(zones) do
	if game:setZone(zone) then
		game:setMusic("pleasant_creek_loop", {volume=85})
	else
		logger:warn("Could not set zone " .. zone .. " to create BackGroundMusicSource")
	end
end
