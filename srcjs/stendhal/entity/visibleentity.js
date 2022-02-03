/***************************************************************************
 *                   (C) Copyright 2003-2020 - Stendhal                    *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/


"use strict";

var marauroa = window.marauroa = window.marauroa || {};

var VisibleEntity = require("../../../build/ts/entity/VisibleEntity").VisibleEntity;

marauroa.rpobjectFactory["visible_entity"] = VisibleEntity;
marauroa.rpobjectFactory["plant_grower"] = VisibleEntity;
marauroa.rpobjectFactory["block"] = VisibleEntity;
