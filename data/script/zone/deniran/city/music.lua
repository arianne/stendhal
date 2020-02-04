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
		local musicSource = luajava.newInstance("games.stendhal.server.entity.mapstuff.sound.BackgroundMusicSource", "night_town", 10000, 85)
		game:add(musicSource)
	else
		logger:error("Could not set zone " .. z .. "to create BackGroundMusicSource")
	end
end
