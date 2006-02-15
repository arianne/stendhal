from games.stendhal.server import *
from games.stendhal.server.pathfinder import *
from games.stendhal.server.entity import *
from games.stendhal.server.entity.npc import *
from games.stendhal.server.scripting import *
from marauroa.common.game import *
from java.util import *

conf=None

class Configuration(StendhalPythonConfig):
    def __init__(self):
        global conf

        StendhalPythonConfig.__init__(self)
        self.shops={}
        conf=self

    def addShop(self,name, shop):
        def convertItemMap(shopItems):
            result=HashMap()
            for k in shopItems.keys():
                result.put(k,shopItems[k])

            return result        

        self.shops[name]=convertItemMap(shop)

    def getShop(self, name):
        return self.shops[name]

    def init(self):
        world=self.getWorld()
        rules=self.getRules()
        # On this point:
        # - All Zones has been added. You can add new zones by calling world.addArea
        # - All entities has been added. You can add new entities to zones.
        # - All NPC has been added. You can add new NPC to zones.
        # - All items and creatures definitions have been loaded. You CAN'T add any new creature or item here.
        #
        # Write your code here:
        #
        zone=world.getRPZone("0_semos_city")
        sign=Sign()
        zone.assignRPObjectID(sign)
        sign.setx(8)
        sign.sety(45)
        sign.setText("Jython example")
        zone.add(sign)
        print "Adding sign to zone"

        self.addShop("weapons",{"knife":15,
                   "small_axe":15,
                   "club":10,
                   "dagger":25,
                   "wooden_shield":25,
                   "dress":25,
                   "leather_helmet":25,
                   "leather_legs":30})

        self.addShop("healing",{"antidote":50,
			"minor_potion":100,
			"potion":250,
			"greater_potion":500})

        
        def pythonillaMethod(npc):
            global conf

            # Set the NPC path
            npc.initializePath([(10,49),(10,40)])

            # Create an outfit for this player
            npc.put("outfit",0)

            # Adds all the behaviour chat
            Behaviours.addGreeting(npc,"Hello and welcome to Stendhal, ask me for #help whenever your in trouble.")
            Behaviours.addJob(npc,"I have healing abilities and I #heal wounded players.")
            Behaviours.addHelp(npc,"I can #heal you and I also have a nice #offer of sellable curative items.")
            Behaviours.addReply(npc,"chat","Most people will #help you if you ask them, and you may also ask them about there #job. Some people also have a few #quests you could do for them.")
            Behaviours.addQuest(npc,"I have heard Sato is looking for fat sheeps, talk to Nishaya just west of here to buy a sheep.")
            Behaviours.addSeller(npc,Behaviours.SellerBehaviour(conf.getShop("healing")))
            Behaviours.addHealer(npc,0)
            Behaviours.addGoodbye(npc,"Good luck in your travels!")
            
        addNPC(zone, rules,"Pythonilla",pythonillaMethod)

        # On this point:
        # - Game is starting.
        
def addNPC(zone, rules, name, method):
    class ScriptNPC(PythonNPC):
        def __init__(self,method):
            PythonNPC.__init__(self)
            method(self)

        def initializePath(self, route):
            nodes=LinkedList()
            for node in route:
                self.pos=node[0],node[1]
                nodes.add(Path.Node(node[0],node[1]))
            self.setPath(nodes,1)

        def getInitialPos(self):
            return self.pos

    npc=ScriptNPC(method)
    zone.assignRPObjectID(npc)
    npc.setName(name);
    pos=npc.getInitialPos()
    npc.setx(pos[0])
    npc.sety(pos[1])
    npc.setBaseHP(100)
    npc.setHP(npc.getBaseHP())

    zone.add(npc)
    rules.addNPC(npc)

    return npc
