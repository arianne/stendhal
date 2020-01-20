
-- master script for defining global values visible to scripts & mods


-- classes to be bound to Lua objects
ConversationStates = luajava.bindClass("games.stendhal.server.entity.npc.ConversationStates")
ConversationPhrases = luajava.bindClass("games.stendhal.server.entity.npc.ConversationPhrases")
CollisionAction = luajava.bindClass("games.stendhal.server.entity.CollisionAction")
SkinColor = luajava.bindClass("games.stendhal.common.constants.SkinColor")


--- Helper function for creating ChatCondition instances.
newCondition = function(classname, ...)
	return luajava.newInstance("games.stendhal.server.entity.npc.condition." .. classname, ...)
end

--- Helper function for creating ChatAction instances.
newAction = function(classname, ...)
	return luajava.newInstance("games.stendhal.server.entity.npc.action." .. classname, ...)
end
