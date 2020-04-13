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
Color = luajava.bindClass("java.awt.Color")



-- ** table manipulation functions ** --

--- Cleans nil values from table.
table.clean = function(tbl)
	local tmp = {}
	for _, v in pairs(tbl) do
		tmp[#tmp+1] = v
	end

	return tmp
end

--- Appends values of a table into another table.
--
-- @param tbl1
-- 		The table to receive the new values.
-- @param tbl2
-- 		The table containing the new values.
table.concat = function(tbl1, tbl2)
	if type(tbl1) == "userdata" then
		tbl1 = arrays:arrayToTable(tbl1)
	end

	if type(tbl2) == "userdata" then
		tbl2 = arrays:arrayToTable(tbl2)
	end

	for _, v in pairs(tbl2) do
		table.insert(tbl1, v)
	end
end


-- ** string manipulation functions ** --

--- Remove leading & trailing whitespace from string.
--
-- http://lua-users.org/wiki/CommonFunctions
--
-- @param st
-- 		String to be manipulated.
-- @return
-- 		Copy of string with leading & trailing whitespace removed.
function string.trim(st)
	return (st:gsub("^%s*(.-)%s*$", "%1"))
end

--- Remove leading whitespace from string.
--
-- http://lua-users.org/wiki/CommonFunctions
--
-- @param st
-- 		String to be manipulated.
-- @return
-- 		Copy of string with leading whitespace removed.
function string.ltrim(st)
	return (st:gsub("^%s*", ""))
end

--- Remove trailing whitespace from string.
--
-- http://lua-users.org/wiki/CommonFunctions
--
-- @param st
-- 		String to be manipulated.
-- @return
-- 		Copy of string with trailing whitespace removed.
function string.rtrim(st)
	local n = #st
	while n > 0 and st:find("^%s", n) do n = n - 1 end
	return st:sub(1, n)
end


-- supplemental string method aliases
string.isnumber = string.isNumber
string.isNumeric = string.isNumber
string.isnumeric = string.isNumber
string.startswith = string.startsWith
string.beginsWith = string.startsWith
string.beginswith = string.startsWith
string.endswith = string.endsWith


return true
