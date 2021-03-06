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

import java.util.List;

import javax.annotation.Nonnull;

import com.intellij.lang.Language;
import com.intellij.lang.LanguageExtension;
import com.intellij.util.containers.ContainerUtil;

/**
 * @author VISTALL
 * @since 31.12.14
 */
public class DashKeywordProviders extends LanguageExtension<DashKeywordProvider>
{
	public static final DashKeywordProviders INSTANCE = new DashKeywordProviders();

	public DashKeywordProviders()
	{
		super("consulo.dashdocset.keywordProvider");
	}

	@Nonnull
	public DashKeywordProvider[] all(@Nonnull Language language)
	{
		List<DashKeywordProvider> forThisLanguage = allForLanguage(language);
		List<DashKeywordProvider> forAnyLanguage = allForLanguage(Language.ANY);
		return ContainerUtil.mergeCollectionsToArray(forThisLanguage, forAnyLanguage, DashKeywordProvider.ARRAY_FACTORY);
	}
}
