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
	npcHelper:setPathAndPosition(zelan, nodes, true)

	-- NPC dialog
	zelan:addGreeting()
	zelan:addGoodbye()

	-- add Zelan to the world
	game:add(zelan)


	-- quest
	local quest = simpleQuest:create("unicorn_horn_for_Zelan", "Unicorn Horns for Zelan", "Zelan")
	quest:setDescription("Zelan needs help gathering unicorn horns.")
	quest:setRequestReply("Hello! I'm in need of some unicorn horns to make some daggers."
		.. " It is really dangerous in the woods surrounding Atlantis. If you are a brave sort"
		.. " I could really use some help gathering unicorn horns. Will you help me?")
	quest:setAcceptReply("Great! Be careful out there lots of large monsters,"
		.. " and those centaurs are really nasty")
	quest:setRewardReply("Thanks a bunch!")
	quest:setRejectReply("Thats ok, I will find someone else to help me.")
	quest:setItemToCollect("unicorn horn", 10)
	quest:setRepeatable(false)
	quest:setXPReward(50000)
	quest:setKarmaReward(5.0)
	quest:addItemReward("soup", 3)
	quest:addItemReward("money", 20000)
	quest:setRegion(Region.ATLANTIS)

	quest:register()
else
	logger:error("Could not set zone: " .. zoneName)
end
