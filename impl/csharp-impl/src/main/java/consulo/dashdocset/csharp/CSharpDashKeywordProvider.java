package consulo.dashdocset.csharp;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ExtensionImpl;
import consulo.csharp.lang.CSharpLanguage;
import consulo.dashdocset.DashKeywordProvider;
import consulo.language.Language;
import consulo.language.psi.PsiFile;
import consulo.module.Module;
import consulo.module.extension.ModuleExtension;
import jakarta.annotation.Nonnull;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author VISTALL
 * @since 01/06/2023
 */
@ExtensionImpl
public class CSharpDashKeywordProvider implements DashKeywordProvider {
    private Map<String, String> mapping = Map.of("mono-dotnet", "mono",
            "microsoft-dotnet", "dotnet",
            "unity3d-csharp-child", "unity3d");

    @RequiredReadAction
    @Override
    public void findKeywords(@Nonnull Language language, @Nonnull PsiFile psiFile, @Nonnull Consumer<String> consumer) {
        Module module = psiFile.getModule();
        if (module == null) {
            return;
        }

        for (Map.Entry<String, String> entry : mapping.entrySet()) {
            String moduleExtensionId = entry.getKey();
            String keyword = entry.getValue();

            ModuleExtension<?> extension = module.getExtension(moduleExtensionId);
            if (extension != null) {
                consumer.accept(keyword);
            }
        }
    }

    @Nonnull
    @Override
    public Language getLanguage() {
        return CSharpLanguage.INSTANCE;
    }
}
