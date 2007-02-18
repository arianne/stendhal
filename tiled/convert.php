<?php

$tilesets=array(
  array("../../zelda_outside_0_chipset.png"  ,1    ,480),
  array("../../zelda_outside_1_chipset.png"  ,481  ,480),
  array("../../zelda_dungeon_0_chipset.png"  ,961  ,480),
  array("../../zelda_dungeon_1_chipset.png"  ,1441 ,480),
  array("../../zelda_interior_0_chipset.png" ,1921 ,480),
  array("../../zelda_objects_chipset.png"    ,2402 ,200),
  array("../../zelda_collision_chipset.png"  ,2602 ,2),
  array("../../zelda_building_0_tileset.png" ,2604 ,480),
  array("../../zelda_outside_2_chipset.png"  ,3084 ,480),
  array("../../zelda_interior_1_chipset.png" ,3564 ,480),
  array("../../zelda_monsters_chipset.png"   ,4044 ,480)
  );
  
  
foreach($tilesets as $t) {
  list($orig,$init,$size)=$t;
  $j=0;
  for($i=$init;$i<$init+$size;$i++,$j++) {
    print "$orig:$j:$i::\n";    
  }
}