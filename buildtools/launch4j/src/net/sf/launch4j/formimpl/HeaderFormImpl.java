/*
	Launch4j (http://launch4j.sourceforge.net/)
	Cross-platform Java application wrapper for creating Windows native executables.

	Copyright (c) 2004, 2015 Grzegorz Kowal
	All rights reserved.

	Redistribution and use in source and binary forms, with or without modification,
	are permitted provided that the following conditions are met:
	
	1. Redistributions of source code must retain the above copyright notice,
	   this list of conditions and the following disclaimer.
	
	2. Redistributions in binary form must reproduce the above copyright notice,
	   this list of conditions and the following disclaimer in the documentation
	   and/or other materials provided with the distribution.
	
	3. Neither the name of the copyright holder nor the names of its contributors
	   may be used to endorse or promote products derived from this software without
	   specific prior written permission.
	
	THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
	AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
	THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
	ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
	FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
	(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
	LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED
	AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
	OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
	OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

/*
 * Created on May 1, 2006
 */
package net.sf.launch4j.formimpl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JRadioButton;

import net.sf.launch4j.binding.Binding;
import net.sf.launch4j.binding.Bindings;
import net.sf.launch4j.config.Config;
import net.sf.launch4j.config.ConfigPersister;
import net.sf.launch4j.form.HeaderForm;

/**
 * @author Copyright (C) 2006 Grzegorz Kowal
 */
public class HeaderFormImpl extends HeaderForm {
	private final Bindings _bindings;

	public HeaderFormImpl(Bindings bindings) {
		_bindings = bindings;
		_bindings.add("headerTypeIndex", new JRadioButton[] { _guiHeaderRadio,
															_consoleHeaderRadio,
															_jniGuiHeaderRadio,
															_jniConsoleHeaderRadio })
				.add("headerObjects", "customHeaderObjects", _headerObjectsCheck,
															_headerObjectsTextArea)
				.add("libs", "customLibs", _libsCheck, _libsTextArea);

		_guiHeaderRadio.setActionCommand(Config.GUI_HEADER);
		_consoleHeaderRadio.setActionCommand(Config.CONSOLE_HEADER);
		_jniGuiHeaderRadio.setActionCommand(Config.JNI_GUI_HEADER_32);
		_jniConsoleHeaderRadio.setActionCommand(Config.JNI_CONSOLE_HEADER_32);

		ActionListener headerTypeActionListener = new HeaderTypeActionListener();
		_guiHeaderRadio.addActionListener(headerTypeActionListener);
		_consoleHeaderRadio.addActionListener(headerTypeActionListener);
		_jniGuiHeaderRadio.addActionListener(headerTypeActionListener);
		_jniConsoleHeaderRadio.addActionListener(headerTypeActionListener);
		
		_headerObjectsCheck.addActionListener(new HeaderObjectsActionListener());
		_libsCheck.addActionListener(new LibsActionListener());
	}
	
	private void updateLibs() {
		if (!_libsCheck.isSelected()) {
			ConfigPersister.getInstance().getConfig().setLibs(null);
			Binding b = _bindings.getBinding("libs");
			b.put(ConfigPersister.getInstance().getConfig());
		}
	}

	private class HeaderTypeActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Config c = ConfigPersister.getInstance().getConfig();
			c.setHeaderType(e.getActionCommand());

			if (!_headerObjectsCheck.isSelected()) {
				Binding b = _bindings.getBinding("headerObjects");
				b.put(c);
				updateLibs();
			}
		}
	}

	private class HeaderObjectsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (!_headerObjectsCheck.isSelected()) {
				ConfigPersister.getInstance().getConfig().setHeaderObjects(null);
				Binding b = _bindings.getBinding("headerObjects");
				b.put(ConfigPersister.getInstance().getConfig());
			}
		}
	}

	private class LibsActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			updateLibs();
		}
	}
}
