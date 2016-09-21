/*
 * Copyright 2013-2014 must-be.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package consulo.dashdocset;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import consulo.dashdocset.moduleExtension.ModuleExtensionMappingEP;
import com.intellij.lang.Language;
import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import consulo.module.extension.ModuleExtension;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public class ModuleExtensionDashKeywordProvider implements DashKeywordProvider
{
	private static final ExtensionPointName<ModuleExtensionMappingEP> EP_NAME = ExtensionPointName.create("consulo.dashdocset.moduleExtensionMapping");

	@NotNull
	@Override
	public String[] findKeywords(@NotNull Language language, @NotNull PsiFile psiFile)
	{
		Module moduleForPsiElement = ModuleUtilCore.findModuleForPsiElement(psiFile);
		if(moduleForPsiElement == null)
		{
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}

		List<String> keywords = new ArrayList<String>(5);
		ModuleExtensionMappingEP[] extensions = EP_NAME.getExtensions();
		for(ModuleExtensionMappingEP mappingEP : extensions)
		{
			if(StringUtil.isEmpty(mappingEP.language) || Comparing.equal(mappingEP.language, language.getID()))
			{
				ModuleExtension<?> extension = ModuleUtilCore.getExtension(moduleForPsiElement, mappingEP.moduleExtensionId);
				if(extension != null)
				{
					keywords.add(mappingEP.keyword);
				}
			}
		}
		return ArrayUtil.toStringArray(keywords);
	}
}
