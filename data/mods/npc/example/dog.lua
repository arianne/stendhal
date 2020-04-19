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


-- Example SilentNPC

logger:info("Loading Lua SilentNPC...")

-- Set zone to Semos City
if game:setZone("0_semos_city") then

	-- Create new NPC instance
	local dog = entities:createSilentNPC()
	dog:put("menu", "Pet|Use")
	dog:setName("Puppy");
	dog:setPosition(23, 54);
	dog:setDescription("You see a playful puppy.");
	dog:setEntityClass("animal/puppy");
	dog:setBaseSpeed(0.5);
	dog:moveRandomly();
	dog:setRandomMovementRadius(25, true);
	dog:setSounds({"dog-small-bark-1", "dog-small-bark-2"});

	-- Add the NPC to the world
	game:add(dog)

	logger:info("Lua SilentNPC loaded!")
else
	logger:warn("Failed to load Lua SilentNPC")
end
