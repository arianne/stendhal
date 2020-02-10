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


local zoneName = "-7_deniran_atlantis"

if game:setZone(zoneName) then
	local zelan = npcHelper:createSpeakerNPC("Zelan")

	-- NPC appearance & behavior
	zelan:setEntityClass("atlantismale01npc")
	zelan:setCollisionAction(CollisionAction.STOP)

	-- NPC location & path
	local nodes = {
		{63, 66},
		{75, 66},
	}
	npcHelper:setPath(zelan, nodes)
	zelan:setPosition(nodes[1][1], nodes[1][2])

	-- NPC dialog
	zelan:addGreeting()
	zelan:addGoodbye()

	-- add Zelan to the world
	game:add(zelan)
else
	logger:error("Could not set zone: " .. zoneName)
end
