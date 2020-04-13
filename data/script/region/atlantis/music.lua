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
	"deniran_atlantis_n2_w",
	"deniran_atlantis_nw",
	"deniran_atlantis_w",
	"deniran_atlantis_sw",
	"deniran_atlantis_n2",
	"deniran_atlantis_n",
	"deniran_atlantis",
	"deniran_atlantis_s",
	"deniran_atlantis_n2_e",
	"deniran_atlantis_ne",
	"deniran_atlantis_e",
	"deniran_atlantis_se",
}

for _, z in pairs(zones) do
	local zone = "-7_" .. z

	if game:setZone(zone) then
		game:setMusic("settlement_of_the_frontier", {volume=85})
	else
		logger:warn("Could not set zone " .. zone .. " to create BackGroundMusicSource")
	end
end
