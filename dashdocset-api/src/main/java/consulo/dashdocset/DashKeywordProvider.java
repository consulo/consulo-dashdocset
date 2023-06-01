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

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ComponentScope;
import consulo.annotation.component.ExtensionAPI;
import consulo.application.Application;
import consulo.component.extension.ExtensionPointCacheKey;
import consulo.language.Language;
import consulo.language.extension.ByLanguageValue;
import consulo.language.extension.LanguageExtension;
import consulo.language.extension.LanguageOneToMany;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 31.12.14
 */
@ExtensionAPI(ComponentScope.APPLICATION)
public interface DashKeywordProvider extends LanguageExtension {
    ExtensionPointCacheKey<DashKeywordProvider, ByLanguageValue<List<DashKeywordProvider>>> KEY =
            ExtensionPointCacheKey.create("DashKeywordProvider", LanguageOneToMany.build(true));

    @Nonnull
    static List<DashKeywordProvider> forLanguage(Language language) {
        return Application.get().getExtensionPoint(DashKeywordProvider.class).getOrBuildCache(KEY).requiredGet(language);
    }

    @RequiredReadAction
    void findKeywords(@Nonnull Language language, @Nonnull PsiFile psiFile, @Nonnull Consumer<String> consumer);
}
