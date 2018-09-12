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

package consulo.dashdocset;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.Language;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayFactory;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public interface DashKeywordProvider
{
	public static final DashKeywordProvider[] EMPTY_ARRAY = new DashKeywordProvider[0];

	public static ArrayFactory<DashKeywordProvider> ARRAY_FACTORY = new ArrayFactory<DashKeywordProvider>()
	{
		@NotNull
		@Override
		public DashKeywordProvider[] create(int count)
		{
			return count == 0 ? EMPTY_ARRAY : new DashKeywordProvider[count];
		}
	};

	@NotNull
	String[] findKeywords(@NotNull Language language, @NotNull PsiFile psiFile);
}
