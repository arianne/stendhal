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
	local genericDesc = "You see a citizen of Atlantis."

	-- define silent NPCs
	local details = {
		{
			path = {
				{39, 24}, {44, 24}, {44, 22}, {47, 22}, {47, 21}, {54, 21}, {54, 19},
				{74, 19}, {74, 23}, {81, 23},
			},
			class = "atlantisfemale01npc",
			desc = genericDesc,
		},
		{
			path = {
				{36, 105}, {40, 105}, {40, 108}, {81, 108}, {81, 107}, {85, 107},
				{85, 106}, {89, 106}, {89, 101}, {92, 101}, {92, 99},
			},
			class = "atlantismale02npc",
			desc = genericDesc,
		},
		{
			path = {
				{49, 67}, {52, 67}, {52, 72}, {62, 72}, {62, 82}, {65, 82},
			},
			name = "butterfly",
			class = "animal/butterfly",
			resistance = 0,
			speed = 0.1,
			desc = "You see a butterfly peacefully gliding through the air.",
			flags = {"ignore_collision"},
		},
		{
			pos = {x=61, y=79},
			name = "bee",
			class = "../monsters/insect/killer_bee",
			dir = Direction.LEFT,
			resistance = 0,
			desc = "You see a bee gathering nectar from the flowers.",
			flags = {"active_idle"},
			sounds = {"bee-1"},
		},
	}

	for _, detail in pairs(details) do
		local npc = entities:createSilentNPC()

		if detail.path ~= nil then
			npc:setPathAndPosition(detail.path, true)
			npc:retracePath() -- make entities walk the path backwards when reaching end
		else
			npc:setPosition(detail.pos.x, detail.pos.y)
			npc:setDirection(detail.dir)
		end
		if detail.name ~= nil then
			npc:setName(detail.name)
		end
		if detail.resistance ~= nil then
			npc:setResistance(detail.resistance)
		end
		if detail.speed ~= nil then
			npc:setBaseSpeed(detail.speed)
		end
		if detail.ignorecollision then
			npc:setIgnoresCollision(true)
		end
		if detail.flags ~= nil then
			for _, flag in ipairs(detail.flags) do
				npc:put(flag, "")
			end
		end
		if detail.sounds ~= nil then
			npc:setSounds(detail.sounds)
		end

		npc:setEntityClass(detail.class)
		npc:setDescription(detail.desc)

		game:add(npc)
	end
else
	logger:error("Could not set zone: " .. zoneName)
end
