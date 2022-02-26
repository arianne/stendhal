--[[
 ***************************************************************************
 *                       Copyright © 2022 - Arianne                        *
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


local zone_name = "int_deniran_pawn_shop"

-- make this global so LostEngagementRing can access
sawyersShelf = {
	put = function(self, item, x, y)
		item:setBoundTo("Sawyer")
		item:setPosition(x, y)

		local zone = game:getZone(zone_name)
		if zone ~= nil then
			zone:add(item, false)
		else
			logger:error("could not get zone " .. zone_name
				.. " to place item " .. item:getName())
		end
	end,

	get = function(self, x, y, pickup)
		pickup = pickup ~= false -- default to true

		local zone = game:getZone(zone_name)
		if zone ~= nil then
			local item = zone:getEntityAt(x, y)
			if pickup and item ~= nil then
				zone:remove(item)
			end

			return item
		end
	end,

	getMetalDetector = function(self)
		return self:get(19, 5)
	end,

	-- in case metal detector is already loaned out
	getSpareMetalDetector = function(self)
		return entities:getItem("metal detector")
	end,

	returnMetalDetector = function(self, detector)
		-- don't add multiple metal detectors
		if self:get(19, 5, false) == nil then
			if detector == nil then
				detector = entities:getItem("metal detector")
				if detector == nil then
					logger:error("Could not create metal detector for Deniran pawn shop shelf")
					return
				end
			end

			self:put(detector, 19, 5)
		end
	end,
}

if game:setZone(zone_name) then
	local blade = entities:getItem("golden blade")
	if blade ~= nil then
		sawyersShelf:put(blade, 19, 3)
	else
		logger:error("Could not create golden blade for Deniran pawn shop shelf")
	end

	sawyersShelf:returnMetalDetector()

	-- add a note with info about using metal detector
	local note = entities:create({
		type = "Sign",
		pos = {17, 6},
		text = "Notes on using a metal detector:"
			.. "\n- Press the \"Scan\" button to turn it on."
			.. "\n- The device will beep if you are near something."
			.. "\n- The closer you are, the faster it beeps.",
		class = "../items/documents/paper",
	})

	game:add(note)
else
	logger:error("could not set zone: " .. zone_name)
end
