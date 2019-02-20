/*
 * Copyright 2013-2017 consulo.io
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

package consulo.dashdocset.java;

import javax.annotation.Nonnull;
import consulo.dashdocset.DashKeywordProvider;
import com.intellij.lang.Language;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.JavaSdkVersionUtil;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public class JavaDashKeywordProvider implements DashKeywordProvider
{
	@Nonnull
	@Override
	public String[] findKeywords(@Nonnull Language language, @Nonnull PsiFile psiFile)
	{
		JavaSdkVersion javaSdkVersion = JavaSdkVersionUtil.getJavaSdkVersion(psiFile);
		if(javaSdkVersion == null)
		{
			return ArrayUtil.EMPTY_STRING_ARRAY;
		}
		// JDK_1_X
		String name = javaSdkVersion.name();
		String index = name.substring(6, name.length());
		return new String[] {"java" + index};
	}
}
