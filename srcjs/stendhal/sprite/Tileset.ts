/* $Id$ */
/***************************************************************************
 *                   (C) Copyright 2003-2021 - Stendhal                    *
 ***************************************************************************
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { Sprite } from "./Sprite";

/**
 * A tileset.
 */
export interface Tileset {

    /**
     * Get the number of tiles.
     *
     * @return The number of tiles.
     */
    getSize(): number;

    /**
     * Get the sprite for an index tile of a tileset.
     *
     * @param index
     *            The index with-in the tileset.
     *
     * @return A sprite, or <code>null</code> if no mapped sprite.
     */
    getSprite(index: number): Sprite;
}
