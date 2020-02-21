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


-- some non-interactive NPCs to populate Atlantis

local zoneName = "-7_deniran_atlantis"

if game:setZone(zoneName) then
	-- define paths & sprites
	local detail1 = {
		path = {
			{39, 24}, {44, 24}, {44, 22}, {47, 22}, {47, 21}, {54, 21}, {54, 19},
			{74, 19}, {74, 23}, {81, 23},
		},
		class = "atlantisfemale01npc",
	}
	local detail2 = {
		path = {
			{36, 105}, {40, 105}, {40, 108}, {81, 108}, {81, 107}, {85, 107},
			{85, 106}, {89, 106}, {89, 101}, {92, 101}, {92, 99},
		},
		class = "atlantismale02npc",
	}

	local npcList = {}

	for _, details in pairs({detail1, detail2}) do
		-- make entities walk the path backwards when reaching end
		local nodeCount = #details.path
		for idx = nodeCount - 1, 2, -1 do
			details.path[#details.path + 1] = details.path[idx]
		end

		local npc = npcHelper:createSilentNPC()
		npcHelper:setPathAndPosition(npc, details.path, true)
		npc:setEntityClass(details.class)
		table.insert(npcList, npc)
	end

	for _, npc in pairs(npcList) do
		game:add(npc)
	end
else
	logger:error("Could not set zone: " .. zoneName)
end
