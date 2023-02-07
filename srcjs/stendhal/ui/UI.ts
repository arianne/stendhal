/***************************************************************************
 *                (C) Copyright 2015-2023 - Faiumoni e. V.                 *
 ***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU Affero General Public License as        *
 *   published by the Free Software Foundation; either version 3 of the    *
 *   License, or (at your option) any later version.                       *
 *                                                                         *
 ***************************************************************************/

import { UIComponentEnum } from "./UIComponentEnum";
import { Component } from "./toolkit/Component";
import { SingletonFloatingWindow } from "./toolkit/SingletonFloatingWindow";

class UI {
	private wellKnownComponents: Map<UIComponentEnum, Component> = new Map();

	public createSingletonFloatingWindow(title: string, contentComponent: Component, x: number, y: number) {
		return new SingletonFloatingWindow(title, contentComponent, x, y);
	}

	public registerComponent(key: UIComponentEnum, component: Component) {
		this.wellKnownComponents.set(key, component);
	}

	public unregisterComponent(component: Component) {
		for (let entry of this.wellKnownComponents.entries()) {
			if (entry[1] === component) {
				this.wellKnownComponents.delete(entry[0]);
				return;
			}
		}
	}

	public get(key: UIComponentEnum): Component|undefined {
		return this.wellKnownComponents.get(key);
	}

	public getPageOffset() {
		const body = document.body;
		const delem = document.documentElement;
		const offsetX = window.pageXOffset || delem.scrollLeft || body.scrollLeft;
		const offsetY = window.pageYOffset || delem.scrollTop || body.scrollTop;

		return {x: offsetX, y: offsetY};
	}
}

export let ui = new UI();
