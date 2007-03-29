#!/bin/bash

for img in `ls $1/*.png` 
do
  convert $img -crop $2 $img.tmp.png
  convert $img.tmp.png -trim +repage -resize "30x30>" -size 30x30 xc:white +swap -gravity center -composite -bordercolor black -border 1x1 $img.2.tmp.png
done

convert `ls $1/*.2.tmp.png` +append $3

rm $1/*.tmp.png
