## Stendhal Lua Scripting HowTo

*NOTE: The Lua implementation & this documentation may not be complete.*

### Using the Logger

A global "logger" object is provided for accessing the logger instance.

Examples of usage:
```lua
logger:info("Info level message")
logger:warn("Warning level message")
logger:error("Error level message")
```

### Creating New Object Instances

To create a new instance of a class, use the `luajava.newInstance` method. If the
instantiation requires parameters, include them after the class:

```lua
-- create a new entity instance with no parameters
local npc = luajava.newInstance("games.stendhal.server.entity.npc.SpeakerNPC")

-- create a new entity instance with a parameter
local npc = luajava.newInstance("games.stendhal.server.entity.npc.SpeakerNPC", "Lua")
```

#### Adding Entities to the Game

Use `game:setSone` to set the zone to work with. Add any entities with `game:add`:

```lua
if game:setZone("0_semos_city") then
	local npc = luajava.newInstance("games.stendhal.server.entity.npc.SpeakerNPC", "Lua")
	game:add(npc)
end
```

#### Adding NPCs with npcHelper Object

`npcHelper` is an object to aid in creating NPCs. It is invoked as `npcHelper:createSpeakerNPC(<name>)`.
- There are two types of NPCs that can be created:
  1. `createSpeakerNPC(name)`
  2. `createSilentNPC()`
- The NPCs path can be set with `npcHelper:setPath(npc, nodes)`.
  - ***npc*** is the NPC instance.
  - ***nodes*** is a table of coordinates for the path the NPC should follow.

Example:
```lua
-- Set zone to Semos City
if game:setZone("0_semos_city") then

	-- Use helper object to create a new NPC
	local npc = npcHelper:createSpeakerNPC("Lua")
	npc:setEntityClass("littlegirlnpc")
	npc:setPosition(10, 55)
	npc:setBaseSpeed(0.1)
	npc:setCollisionAction(CollisionAction.STOP)

	local nodes = {
		{10, 55},
		{11, 55},
		{11, 56},
		{10, 56},
	}

	-- Use helper object to create NPC path
	npcHelper:setPath(npc, nodes)

	-- Dialogue
	npc:addJob("Actually, I am jobless.")
	npc:addGoodbye();

	-- Some custom replies using conditions & actions
	npc:add(
		ConversationStates.IDLE,
		ConversationPhrases.GREETING_MESSAGES,
		nil,
		ConversationStates.ATTENDING,
		"I am sad, because I do not have a job.",
		nil
	)
	npc:add(
		ConversationStates.ATTENDING,
		"Lua",
		newCondition("PlayerNextToCondition"),
		ConversationStates.ATTENDING,
		"Um, could you back up please? I can smell your breath.",
		newAction("NPCEmoteAction", "coughs", false)
	)
	npc:add(
		ConversationStates.ATTENDING,
		"Lua",
		newCondition("NotCondition", newCondition("PlayerNextToCondition")),
		ConversationStates.ATTENDING,
		"That's my name, don't wear it out!",
		newAction("NPCEmoteAction", "giggles", false)
	)

	-- Add the NPC to the world
	game:add(npc)
end
```

#### Adding Signs with Helper Functions

Signs can be created with the `game:createSign` helper function. It optionally
takes 1 argument: `true` (default) or `false`. If `true`, the sign will be visible
with collision. Otherwise it will be invisible without collision. These attributes
can also be changed with the `Sign:setResistance` & `Sign:setEntityClass` methods.

```lua
-- Set zone to Semos City
if game:setZone("0_semos_city") then
	-- Set up a sign for Lua
	local sign = game:createSign()
	sign:setEntityClass("signpost")
	sign:setPosition(12, 55)
	sign:setText("Meet Lua!")

	-- Add the sign to the world
	game:add(sign)
end
```

Some enumerations accessible to Lua engine:
- [ConversationStates](../../src/games/stendhal/server/entity/npc/ConversationStates.java)
- [ConversationPhrases](../../src/games/stendhal/server/entity/npc/ConversationPhrases.java)
- [CollisionAction](../../src/games/stendhal/server/entity/CollisionAction.java)
