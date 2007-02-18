<?php
class TiledMap {
    public $width=0;
    public $height=0;
    public $lastgid=1;
    public $tilesets=array();
    public $layers=array();
    
    public function buildXML() {
        echo '<?xml version="1.0"?>'."\n";
        echo '<map version="0.99b" orientation="orthogonal" width="'.$this->width.
            '" height="'.$this->height.'" tilewidth="32" tileheight="32">'."\n";
            
        foreach($this->tilesets as $tileset=>$gid) {
            echo ' <tileset name="'.$tileset.'" firstgid="'.$gid.'" tilewidth="32" tileheight="32">'."\n";
            echo '  <image source="'.$tileset.'" trans="ffffff"/>'."\n";
            echo ' </tileset>'."\n";
        }           

        foreach($this->layers as $layer) {
            echo ' <layer name="'.$layer[0].'" width="64" height="64">'."\n";
            echo ' <data>'."\n";
            foreach($layer[1] as $gid) {
                echo '   <tile gid="'.$gid.'"/>'."\n";
            }
            echo '   </data>'."\n";
            echo ' </layer>'."\n";
        }
        
        echo '</map>'."\n";
    }
};

$layer="";
$layerdata=array();

$map=new TiledMap();

function loadMapping($filename) {
    $mapping=array();
    $oldmapping=array();
    
    $content=file($filename);
    
    foreach($content as $line) {
        if(strrpos($line, "#") === false) {
            list($oldtileset, $oldpos, $oldglobalpos, $tileset, $pos)=explode(":", trim($line));
            $mapping[$oldglobalpos]=array($tileset, $pos);        
            $oldmapping[$oldglobalpos]=array($oldtileset, $oldpos);        
            }
    }
    
    return array($oldmapping,$mapping);
}

function startElement($parser, $name, $attrs) {
    global $layer, $mapping, $oldmapping, $map, $layerdata;
    
    if($name=="MAP") {
        $map->width=$attrs['WIDTH'];
        $map->height=$attrs['HEIGHT'];
    }
    
    if($name=="LAYER") {
        $layer=$attrs['NAME'];
        $layerdata=array();
    }
    
    if($name=="TILE") {
        $gid=$attrs['GID'];
        if($gid!=0) {
            list($tileset, $pos)=$mapping[$gid];        
            $newgid=tile($tileset, $pos);            
            //echo "$gid --> $tileset:$pos --> $newgid\n";
            
            if($tileset=='') {
                list($tileset, $pos)=$oldmapping[$gid];
                echo "MISSING: $tileset:$pos --> ".(int)($pos/30).":".($pos%30)."\n";
                exit(1);
            }
            
            $layerdata[]=$newgid;
        } else {
            $layerdata[]=0;
        }
    }
}

function getAmountOfTiles($tileset) {
    $tileset=ereg_replace("../../","",$tileset);
    list($width, $height) = getimagesize($tileset);
    return ($height/32)*($width/32);
}

function tile($tileset, $pos) {
    global $map;
    
    if(!isset($map->tilesets[$tileset])) {
        $map->tilesets[$tileset]=$map->lastgid;
        $map->lastgid+=getAmountOfTiles($tileset);
    }
    
    $gid=$map->tilesets[$tileset]+$pos;
    return $gid;
}

function endElement($parser, $name) {
    global $layer, $map, $layerdata;

    if($name=="LAYER") {
        $map->layers[]=array($layer, $layerdata);
    }
}

function loadTMX($filename) {
    $xml_parser = xml_parser_create();
    xml_set_element_handler($xml_parser, "startElement", "endElement");
    if (!($fp = fopen($filename, "r"))) {
        die("could not open XML input");
    }

    while ($data = fread($fp, 4096)) {
        if (!xml_parse($xml_parser, $data, feof($fp))) {
            die(sprintf("XML error: %s at line %d",
                xml_error_string(xml_get_error_code($xml_parser)),
                xml_get_current_line_number($xml_parser)));
        }
    }
    xml_parser_free($xml_parser);
}

list($oldmapping,$mapping)=loadMapping("mapping.txt");
loadTMX("tileset/test/oldtest.tmx");

$map->buildXML();
?>