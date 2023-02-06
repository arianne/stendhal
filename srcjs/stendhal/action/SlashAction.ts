/***************************************************************************
 *                   (C) Copyright 2021-2023 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

/**
 * abstract base class for user actions
 */
export abstract class SlashAction {
    abstract minParams: number;
    abstract maxParams: number;

    abstract execute(type: string, params: string[], remainder: string): boolean;

};
