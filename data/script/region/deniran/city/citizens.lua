--[[
 ***************************************************************************
 *                       Copyright © 2021 - Arianne                        *
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


-- some non-interactive NPCs to wander around Deniran

local zones = {
	["0_deniran_city"] = {
		{ -- man walking house to house
			sprite = "youngnpc",
			path = {
				nodes = {
					{17,33},  {32,33},  {32,56},  {16,56},  {16,52},  {16,56},
					{32,56},  {32,75},  {38,75},  {38,76},  {44,76},  {44,77},
					{93,77},  {93,75}, {103,75}, {103,65},  {96,65},  {96,60},
				},
				retrace = true,
				speed = 0.4,
				on_collide = CollisionAction.STOP,
			},
		},
	},
	["0_deniran_city_e"] = {
		{ -- woman sitting on bench
			sprite = "womanonstoolnpc",
			desc = "You see a woman sitting on a bench.",
			pos = {x=53, y=55},
			facedir = Direction.DOWN,
		},
	},
	["0_deniran_city_e2"] = {
		{ -- old man walking around trees
			outfit = {
				layers = "body=0,head=0,eyes=5,hair=39,dress=53,mask=1,hat=6",
			},
			path = {
				nodes = {
					{73,47}, {73,34}, {47,34}, {47,47},
				},
			},
		},
	},
	["0_deniran_city_s"] = {
		{ -- woman walking along main path
			outfit = {
				layers = "body=2,head=0,eyes=8,dress=55,hair=3",
				colors = {hair=0x01e1ec, dress=0xffc000, eyes=0xee82ee},
			},
			path = {
				nodes = {
					{75,61}, {80,61}, {80,86}, {80,72}, {75,72},
				},
			},
		},
		{ -- man outside wall
			sprite = "groundskeepernpc",
			path = {
				nodes = {
					{77,100},  {29,100},   {29,85},    {4,85},   {4,100}, {87,100},
					 {87,96},  {126,96}, {126,111},  {87,111},  {87,100},
				},
				on_collide = CollisionAction.STOP,
			},
		},
	},
	["0_deniran_city_s_e2"] = {
		{ -- man walking house to house
			sprite = "holidaymakernpc",
			path = {
				nodes = {
					{22,96}, {22,101}, {29,101}, {29,100}, {45,100}, {45,106},
					{64,106},
				},
				retrace = true,
			},
		},
	},
	["0_deniran_city_se"] = {
		{ -- woman walking around pond
			outfit = {
				layers = "body=2,head=0,eyes=8,dress=971,mask=8,hair=14",
				colors = {eyes=Color.GREEN},
			},
			path = {
				nodes = {
					{65,29}, {45,29}, {45,36}, {42,36}, {42,44}, {46,44}, {46,48},
					{53,48}, {53,55}, {62,55}, {62,49}, {76,49}, {76,41}, {70,41},
					{70,36}, {65,36},
				},
				on_collide = CollisionAction.STOP,
			},
		},
	},
	["0_deniran_city_sw"] = {
		{ -- boy running around
			sprite = "childnpc",
			desc = "You see a young Deniran boy.",
			path = {
				nodes = {
					{47,15}, {47,34}, {46,34}, {46,59}, {125,59}, {125,9}, {69,9},
					{69,15},
				},
				speed = 0.6,
			},
		},
	},
	["0_deniran_city_w"] = {
		{ -- woman walking in orchard
			sprite = "girlnpc",
			path = {
				nodes = {
					{83,104}, {118,104}, {118,89}, {83,89},
				},
			},
		},
	},
}

for zone_name, entities_table in pairs(zones) do
	if not game:setZone(zone_name) then
		logger:error("could not set zone: " .. zone_name)
	else
		for _, data in ipairs(entities_table) do
			local citizen = entities:createSilentNPC()

			if data.desc == nil then
				data.desc = "You see a citizen of Deniran City."
			end
			citizen:setDescription(data.desc)

			if type(data.outfit) == "table" and data.outfit.layers ~= nil then
				citizen:setOutfit(data.outfit.layers)
				if type(data.outfit.colors) == "table" then
					for k, v in pairs(data.outfit.colors) do
						citizen:setOutfitColor(k, v)
					end
				end
			elseif data.sprite ~= nil then
				citizen:setEntityClass(data.sprite)
			end

			if type(data.path) == "table" and type(data.path.nodes) == "table" then
				citizen:setPathAndPosition(data.path.nodes, true)
				if data.path.retrace then
					citizen:setRetracePath()
				end
				if data.path.speed ~= nil then
					citizen:setBaseSpeed(data.path.speed)
				end

				citizen:setCollisionAction(data.path.on_collide or CollisionAction.REVERSE)
			elseif type(data.pos) == "table" then
				citizen:setPosition(data.pos.x, data.pos.y)
			end

			if data.facedir ~= nil then
				citizen:setDirection(data.facedir)
			end

			game:add(citizen)
		end
	end
end
