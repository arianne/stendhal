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

-- master script for defining global values visible to scripts & mods


-- classes to be bound to Lua objects
ConversationStates = luajava.bindClass("games.stendhal.server.entity.npc.ConversationStates")
ConversationPhrases = luajava.bindClass("games.stendhal.server.entity.npc.ConversationPhrases")
CollisionAction = luajava.bindClass("games.stendhal.server.entity.CollisionAction")
SkinColor = luajava.bindClass("games.stendhal.common.constants.SkinColor")
Direction = luajava.bindClass("games.stendhal.common.Direction")
DaylightPhase = luajava.bindClass("games.stendhal.server.core.rp.DaylightPhase")
Region = luajava.bindClass("games.stendhal.server.maps.Region")
MathHelper = luajava.bindClass("games.stendhal.common.MathHelper")


--- Helper function for creating ChatCondition instances.
newCondition = function(classname, ...)
	return luajava.newInstance("games.stendhal.server.entity.npc.condition." .. classname, ...)
end

--- Helper function for creating ChatAction instances.
newAction = function(classname, ...)
	return luajava.newInstance("games.stendhal.server.entity.npc.action." .. classname, ...)
end

--- Sets the background music for the zone.
--
-- @param filename
-- 		File basename excluding ".ogg" extensions.
-- @param volume
-- 		Volume level (default: 100)
setZoneMusic = function(filename, volume, x, y, radius)
	-- default volume
	if volume == nil then
		volume = 100
	end

	if x == nil then
		x = 1
	end
	if y == nil then
		y = 1
	end

	-- default radius
	if radius == nil then
		radius = 10000
	end

	local musicSource = luajava.newInstance("games.stendhal.server.entity.mapstuff.sound.BackgroundMusicSource", filename, radius, volume)
	musicSource:setPosition(x, y)
	game:add(musicSource)
end
