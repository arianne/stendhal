
-- master script for defining global values visible to scripts & mods


-- classes to be bound to Lua objects
ConversationStates = luajava.bindClass("games.stendhal.server.entity.npc.ConversationStates")
ConversationPhrases = luajava.bindClass("games.stendhal.server.entity.npc.ConversationPhrases")
CollisionAction = luajava.bindClass("games.stendhal.server.entity.CollisionAction")
SkinColor = luajava.bindClass("games.stendhal.common.constants.SkinColor")
Direction = luajava.bindClass("games.stendhal.common.Direction")
DaylightPhase = luajava.bindClass("games.stendhal.server.core.rp.DaylightPhase")


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
setZoneMusic = function(filename, volume)
	-- default volume
	if volume == nil then
		volume = 100
	end

	local musicSource = luajava.newInstance("games.stendhal.server.entity.mapstuff.sound.BackgroundMusicSource", filename, 10000, volume)
	game:add(musicSource)
end
