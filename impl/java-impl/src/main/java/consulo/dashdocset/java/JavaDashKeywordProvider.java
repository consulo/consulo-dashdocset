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

import com.intellij.java.language.JavaLanguage;
import com.intellij.java.language.impl.projectRoots.JavaSdkVersionUtil;
import com.intellij.java.language.projectRoots.JavaSdkVersion;
import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.dashdocset.DashKeywordProvider;
import consulo.language.Language;
import consulo.language.psi.PsiFile;
import jakarta.annotation.Nonnull;

import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 31.12.14
 */
@ExtensionImpl
public class JavaDashKeywordProvider implements DashKeywordProvider {
    @RequiredReadAction
    @Override
    public void findKeywords(@Nonnull Language language, @Nonnull PsiFile psiFile, @Nonnull Consumer<String> consumer) {
        JavaSdkVersion javaSdkVersion = JavaSdkVersionUtil.getJavaSdkVersion(psiFile);
        if (javaSdkVersion == null) {
            return;
        }
        // JDK_1_X
        String name = javaSdkVersion.name();
        String index = name.substring(6, name.length());
        consumer.accept("java" + index);
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return JavaLanguage.INSTANCE;
    }
}
