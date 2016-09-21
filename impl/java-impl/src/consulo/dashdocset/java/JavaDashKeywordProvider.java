package consulo.dashdocset.java;

import org.jetbrains.annotations.NotNull;
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
	@NotNull
	@Override
	public String[] findKeywords(@NotNull Language language, @NotNull PsiFile psiFile)
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
