/***************************************************************************
 *                (C) Copyright 2015-2022 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { UIComponentEnum } from "./UIComponentEnum";
import { Component } from "./toolkit/Compontent";
import { SingletonFloatingWindow } from "./toolkit/SingletonFloatingWindow";

class UI {
	private wellKnownComponents: Map<UIComponentEnum, Component> = new Map();

	public createSingletonFloatingWindow(title: string, contentComponent: Component, x: number, y: number) {
		new SingletonFloatingWindow(title, contentComponent, x, y);
	}

	public registerComponent(key: UIComponentEnum, component: Component) {
		this.wellKnownComponents.set(key, component);
	}

	public unregisterComponent(key: UIComponentEnum, component: Component) {
		if (this.wellKnownComponents.get(key) === component) {
			this.wellKnownComponents.delete(key);
		}
	}

	public get(key: UIComponentEnum): Component|undefined {
		return this.wellKnownComponents.get(key);
	}
}

export let ui = new UI();
