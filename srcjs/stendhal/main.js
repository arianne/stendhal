/***************************************************************************
 *                   (C) Copyright 2003-2023 - Stendhal                    *
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
var stendhal = window.stendhal = window.stendhal || {};

var Client = require("../../build/ts/Client").Client;
var singletons = singletons || require("../../build/ts/SingletonRepo").SingletonRepo;

var OutfitStore = require("../../build/ts/data/Outfit").OutfitStore;

var Ground = require("../../build/ts/entity/Ground").Ground;
var Zone = require("../../build/ts/entity/Zone").Zone;


stendhal.data = stendhal.data || {};
stendhal.data.cache = singletons.getCacheManager();
stendhal.data.cache.init();
stendhal.data.cstatus = singletons.getCStatus();
stendhal.data.cstatus.init();
stendhal.data.group = singletons.getGroupManager();
stendhal.data.outfit = new OutfitStore();
stendhal.data.sprites = singletons.getSpriteStore();
stendhal.data.map = singletons.getMap();

stendhal.config = stendhal.config || singletons.getConfigManager();
stendhal.paths = stendhal.paths || singletons.getPaths();
stendhal.session = stendhal.session || singletons.getSessionManager();

stendhal.ui = stendhal.ui || {}
stendhal.ui.equip = singletons.getInventory();
stendhal.ui.html = singletons.getHTMLManager();
stendhal.ui.touch = singletons.getTouchHandler();
stendhal.ui.soundMan = singletons.getSoundManager();
stendhal.ui.gamewindow = singletons.getViewPort();

stendhal.zone = new Zone();
stendhal.zone.ground = new Ground();

stendhal.main = new Client();

document.addEventListener('DOMContentLoaded', stendhal.main.startup);
window.addEventListener('error', stendhal.main.onerror);
