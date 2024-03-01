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
	"0_ados_city_n2",
	"0_ados_city_n",
	"0_ados_city",
	"0_ados_city_s",
}

for _, zoneName in pairs(zones) do
	local zone = game:getZone(zoneName)
	if zone then
		local attr = zone:getAttributes()
		attr:put("music", "market_day")
		attr:put("music_volume", "0.6")
	else
		logger:warn("Could not set zone " .. zoneName .. " to create BackGroundMusicSource")
	end
end


-- music is only on part of these maps
for _, zoneName in pairs({"0_ados_wall", "0_ados_wall_n"}) do
	if game:setZone(zoneName) then
		game:setMusic("market_day", {volume=60, x=127, y=64, radius=96})
	else
		logger:warn("Could not set zone " .. zoneName .. " to create BackGroundMusicSource")
	end
end

local zoneName = "0_ados_wall_n2"
if game:setZone(zoneName) then
	game:setMusic("market_day", {volume=60, x=127, y=127, radius=96})
else
	logger:warn("Could not set zone " .. zoneName .. " to create BackGroundMusicSource")
end

local zoneName = "0_ados_wall_s"
if game:setZone(zoneName) then
	game:setMusic("market_day", {volume=60, x=127, y=0, radius=96})
else
	logger:warn("Could not set zone " .. zoneName .. " to create BackGroundMusicSource")
end
