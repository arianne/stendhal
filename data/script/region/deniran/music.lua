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
		game:setMusic("night_town", {volume=85})
	else
		logger:warn("Could not set zone " .. z .. " to create BackGroundMusicSource")
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
		game:setMusic("spooky_forest", {volume=85})
	else
		logger:warn("Could not set zone " .. z .. " to create BackGroundMusicSource")
	end
end


-- caves

local zones = {
	"deniran_lost_caves_n2_w",
	"deniran_lost_caves_nw",
	"deniran_caves_w",
	"deniran_caves_sw",
	"deniran_lost_caves_n2",
	"deniran_lost_caves_n",
	"deniran_caves",
	"deniran_caves_s",
	"deniran_lost_caves_n2_e",
	"deniran_lost_caves_ne",
	"deniran_caves_e",
	"deniran_caves_se",
	"deniran_lost_caves_n2_e2",
	"deniran_lost_caves_n_e2",
	"deniran_caves_e2",
	"deniran_caves_s_e2",
}

for _, z in pairs(zones) do
	-- music intentionally not set on level -4 because of singing mermaids
	for _, level in pairs({"-1", "-2", "-3", "-5", "-6"}) do
		local zone = level .. "_" .. z

		if game:setZone(zone) then
			game:setMusic("casket", {volume=85})
		else
			logger:warn("Could not set zone " .. zone .. " to create BackGroundMusicSource")
		end
	end
end
