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


-- city

local zones = {
	"0_deniran_city",
	"0_deniran_city_s",
	"0_deniran_city_se",
	"0_deniran_city_s_e2",
	"0_deniran_city_sw",
	"0_deniran_city_e",
	"0_deniran_city_e2",
	"0_deniran_city_w",
	"0_deniran_river_se",
	"0_deniran_river_s_e2",
}

for _, z in pairs(zones) do
	if game:setZone(z) then
		setZoneMusic("night_town", 85)
	else
		logger:warn("Could not set zone " .. z .. "to create BackGroundMusicSource")
	end
end


-- forest

local zones = {
	"0_deniran_forest_n2_w",
	"0_deniran_forest_nw",
	"0_deniran_forest_n2",
	"0_deniran_forest_n",
	"0_deniran_forest_n2_e",
	"0_deniran_forest_ne",
	"0_deniran_forest_n2_e2",
	"0_deniran_forest_n_e2",
}

for _, z in pairs(zones) do
	if game:setZone(z) then
		setZoneMusic("spooky_forest", 85)
	else
		logger:warn("Could not set zone " .. z .. "to create BackGroundMusicSource")
	end
end
