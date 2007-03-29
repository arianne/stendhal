#!/usr/bin/php
<?php
// Raise Memory limit usage 
//ini_set('memory_limit', '64M');

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
            echo ' <layer name="'.$layer[0].'" width="'.$this->width.'" height="'.$this->height.'" opacity="'.$layer[1].'">'."\n";
            echo '   <data encoding="base64" compression="gzip">'."\n";
            
            $data='';
            
            foreach($layer[2] as $gid) {
                $b=($gid) & 0x000000FF;
                $data.=pack("C*",$b);

                $b=($gid >> 8) & 0x000000FF;
                $data.=pack("C*",$b);

                $b=($gid >> 16) & 0x000000FF;
                $data.=pack("C*",$b);

                $b=($gid >> 24) & 0x000000FF;
                $data.=pack("C*",$b);               
            }
            
            $encoded=base64_encode(gzencode($data));
            echo '     '.$encoded."\n";
            
            echo '   </data>'."\n";
            echo ' </layer>'."\n";
        }
        
        echo '</map>'."\n";
    }
};

$layer="";
$opacity=1;
$layerdata=array();

$map=new TiledMap();

function loadMapping($filename) {
    $mapping=array();
    $oldmapping=array();
    
    $content=file($filename);
    
    foreach($content as $line) {
        if(strrpos($line, "#") === false) {            
            $var=explode(":", trim($line));
            if(sizeof($var)!=5) {
                print "ERROR: ".$line;
                exit(-1);
            }
            list($oldtileset, $oldpos, $oldglobalpos, $tileset, $pos)=$var;
            $mapping[$oldglobalpos]=array($tileset, $pos);        
            $oldmapping[$oldglobalpos]=array($oldtileset, $oldpos);        
            }
    }
    
    return array($oldmapping,$mapping);
}

function startElement($parser, $name, $attrs) {
    global $layer, $opacity, $recordLayerData, $map;
    
    if($name=="MAP") {
        $map->width=$attrs['WIDTH'];
        $map->height=$attrs['HEIGHT'];
    }
    
    if($name=="LAYER") {
        $layer=$attrs['NAME'];
        
        $opacity=1;        
        if(isset($attrs['OPACITY'])) {
            $opacity=$attrs['OPACITY'];
        }
        $layerdata=array();
    }
    
    if($name=='DATA') {
        $recordLayerData=true;
    }
}

function getAmountOfTiles($tileset) {
    $tileset=ereg_replace('../../','',$tileset);
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
    global $layer, $opacity, $map, $layerdata, $recordLayerData;

    if($name=="LAYER") {
        $map->layers[]=array($layer, $opacity, $layerdata);
        $layerdata=array();
    }

    if($name=='DATA') {
        $recordLayerData=false;
    }
}

$recordLayerData=false;


// Define the gzdecode function
if (!function_exists('gzdecode')) {
        function gzdecode ($data) {
                // Check if data is GZIP'ed
                if (strlen($data) < 18 || strcmp(substr($data,0,2),"\x1f\x8b")) {
                        return false;
                }

                // Remove first 10 bytes
                $data = substr($data, 10);

                // Return regular data
                return gzinflate($data);
        }
}

function cdataElement($parser, $data) {
    global $recordLayerData, $mapping, $oldmapping, $map, $layerdata;

    if($recordLayerData) {
        $ugzd=gzdecode(base64_decode(trim($data)));
        $list=unpack("V*",$ugzd);
        
        $tiles=sizeof($list);
        for($i=1;$i<$tiles;$i++) {
            $gid=$list[$i];

            if($gid!=0) {
                list($tileset, $pos)=$mapping[$gid];        
                $newgid=tile($tileset, $pos);            
                // echo "$gid --> $tileset:$pos --> $newgid\n";
            
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
}



function loadTMX($filename) {
    $xml_parser = xml_parser_create();
    xml_set_element_handler($xml_parser, "startElement", "endElement");
    xml_set_character_data_handler($xml_parser, "cdataElement");
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


if(sizeof($argv)==1) {
    print("Map translator for Stendhal\n");
    print("Usage: \n");
    print("  translate mapname.tmx\n");
    exit(1);
}

list($oldmapping,$mapping)=loadMapping("mapping.txt");
loadTMX($argv[1]);
$map->buildXML();
?>
