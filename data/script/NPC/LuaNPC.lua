
logger:info("Loading LuaNPC...")

-- Set zone to Semos City
if game:setZone("0_semos_city") then

  -- Create a new NPC
  local npc = stendhal:createNPC("Lua")
  npc:setEntityClass("littlegirlnpc")
  npc:setPosition(10, 55)
  npc:setSpeed(0.1)
  nodes = {
    {10, 55},
    {11, 55},
    {11, 56},
    {10, 56},
  }

  -- Use helper class to create NPC path
  stendhal:setEntityPath(npc, nodes)

  -- Dialogue
  npc:addJob("Actually, I am jobless.")
  npc:add(ConversationStates.IDLE, ConversationPhrases.GREETING_MESSAGES, nil, ConversationStates.ATTENDING, "I am sad, because I do not have a job.", nil)
  npc:addGoodbye();

  -- Add the NPC to the world
  game:add(npc)
end

logger:info("LuaNPC loaded!")
