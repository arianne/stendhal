
The animations used for map and weather tiles are defined in "animation.json".

The format of lines is:

  "tileset": ["index frame:frame[:frame]..."]

  , where the meaning of each field is:

    tileset:
      The path of the image file

    index:
      The index of the tile to be animated (0 based). The character * has
      special meaning, and indicates all the indices listed in the frames
      section (see the documentation of "frame"). The default delay in
      milliseconds between frames can be specified by appending @delay to
      the "index" specifier. If no delay is specified, the default delay
      of 500ms is used.

    frame:
      List of tiles in the animation sequence, in the order of display,
      separated by colons. A frame can appear more than once in the
      sequence. Like with the "index" specifier, delays can be specified
      for frames. The delay is used for the specific frame in question.
      If a frame does not specify a delay, the default delay of the
      sequence is used.

------------------------------------------------------------------------------

Examples:

  "ground/water/pool_grassy": ["0 0:3:6:3"]
      Animate the first tile of "pool_grassy", with tiles 3 and 6 so that
      the animation goes through tile 3 when alternating between 0 and 6.
      All the frames use the default delay of 500ms.

  "building/decoration/floor_sparkle": ["* 0:1:2"]
      Animate tiles 0, 1 and 2 of "floor_sparkle", so that the animation
      cycles through the 3 frames. Map can use any of the tiles to get
      the animation. Delay between frames is 500ms.

  "furniture/light/flames" ["*@300 0:1:2:3:4:5"]
      Similar to the previous example, but using 300ms delay between the
      frames.

  "sky/view_wizards_tower": ["* 81:83:115:117@3500"]
      Animate tiles 81, 83, 115 and 117 of "view_wizards_tower", in a
      cycle so that tile 117 is displayed for 3500ms, but the other
      frames use the default delay of 500ms.
