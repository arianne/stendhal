--[[
 ***************************************************************************
 *                 Copyright © 2020-2024 - Faiumoni e. V.                  *
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

for _, z in pairs(zones) do
	local zone = game:getZone(z)
	if zone then
		local attr = zone:getAttributes()
		attr:put("music", "pleasant_creek_loop")
		attr:put("music_volume", "0.85")
	else
		logger:warn("Could not set zone " .. z .. " to create BackGroundMusicSource")
	end
end
