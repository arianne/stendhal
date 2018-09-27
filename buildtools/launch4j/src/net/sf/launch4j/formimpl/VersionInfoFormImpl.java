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

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;

import net.sf.launch4j.binding.Bindings;
import net.sf.launch4j.config.LanguageID;
import net.sf.launch4j.config.VersionInfo;
import net.sf.launch4j.form.VersionInfoForm;

/**
 * @author Copyright (C) 2006 Grzegorz Kowal
 */
public class VersionInfoFormImpl extends VersionInfoForm {

	public VersionInfoFormImpl(Bindings bindings, JFileChooser fc) {
		_languageCombo.setModel(new DefaultComboBoxModel(LanguageID.sortedValues()));
		bindings.addOptComponent("versionInfo", VersionInfo.class, _versionInfoCheck)
				.add("versionInfo.fileVersion", _fileVersionField)
				.add("versionInfo.productVersion", _productVersionField)
				.add("versionInfo.fileDescription", _fileDescriptionField)
				.add("versionInfo.internalName", _internalNameField)
				.add("versionInfo.originalFilename", _originalFilenameField)
				.add("versionInfo.productName", _productNameField)
				.add("versionInfo.txtFileVersion", _txtFileVersionField)
				.add("versionInfo.txtProductVersion", _txtProductVersionField)
				.add("versionInfo.companyName", _companyNameField)
				.add("versionInfo.copyright", _copyrightField)
				.add("versionInfo.trademarks", _trademarksField)
				.add("versionInfo.languageIndex", _languageCombo, VersionInfo.DEFAULT_LANGUAGE_INDEX)
		;
	}
}
