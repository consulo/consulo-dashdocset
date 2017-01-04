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

package consulo.dashdocset.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import consulo.dashdocset.DashBundle;
import consulo.dashdocset.DashProvider;
import com.intellij.lang.Language;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.ArrayUtil;
import consulo.dashdocset.DashKeywordProvider;
import consulo.dashdocset.DashKeywordProviders;
import consulo.lombok.annotations.Logger;
import lombok.val;

/**
 * @author VISTALL
 * @since 31.12.14
 */
@Logger
public class SearchInDashDocAction extends AnAction
{
	public SearchInDashDocAction()
	{
		DashProvider provider = DashProvider.getProvider();
		getTemplatePresentation().setText(DashBundle.message("action.search.in.docset", provider.name()));
		getTemplatePresentation().setIcon(provider.getIcon());
	}

	@Override
	public void actionPerformed(AnActionEvent anActionEvent)
	{
		val queryInfo = findQueryInfo(anActionEvent);
		if(queryInfo == null)
		{
			return;
		}

		final DashProvider provider = DashProvider.getProvider();
		new Task.Modal(anActionEvent.getProject(), DashBundle.message("action.searching.in.docset", queryInfo.getFirst(), provider.name()), false)
		{
			@Override
			public void run(@NotNull ProgressIndicator indicator)
			{
				try
				{
					provider.open(queryInfo.getSecond(), queryInfo.getFirst());
				}
				catch(Exception e)
				{
					LOGGER.warn(e);
				}
			}
		}.queue();
	}

	@Nullable
	private Pair<String, String[]> findQueryInfo(AnActionEvent anActionEvent)
	{
		Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
		if(editor == null)
		{
			return null;
		}

		PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
		if(psiFile == null)
		{
			return null;
		}

		int caretOffset = editor.getCaretModel().getOffset();

		InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(psiFile.getProject());

		Language language = null;
		PsiElement psiElement = null;
		if(injectedLanguageManager != null)
		{
			psiElement = injectedLanguageManager.findInjectedElementAt(psiFile, caretOffset);
		}

		if(psiElement == null)
		{
			psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
		}

		if(psiElement != null)
		{
			language = psiElement.getLanguage();
		}

		String query = null;
		SelectionModel selectionModel = editor.getSelectionModel();
		if(selectionModel.hasSelection())
		{
			query = selectionModel.getSelectedText();
		}
		else
		{
			if(psiElement == null || psiElement instanceof PsiComment)
			{
				query = getWordAtCursor(editor);
			}
			else
			{
				query = psiElement.getText();
			}
		}

		if(query == null || language == null)
		{
			return null;
		}

		DashKeywordProvider[] dashKeywordProviders = DashKeywordProviders.INSTANCE.all(language);

		List<String> list = new ArrayList<String>();
		for(DashKeywordProvider dashKeywordProvider : dashKeywordProviders)
		{
			Collections.addAll(list, dashKeywordProvider.findKeywords(language, psiFile));
		}

		if(list.isEmpty())
		{
			return null;
		}
		return Pair.create(query, ArrayUtil.toStringArray(list));
	}

	@Nullable
	private String getWordAtCursor(Editor editor)
	{
		CharSequence editorText = editor.getDocument().getCharsSequence();
		int cursorOffset = editor.getCaretModel().getOffset();
		int editorTextLength = editorText.length();

		if(editorTextLength == 0)
		{
			return null;
		}

		if((cursorOffset >= editorTextLength) || (cursorOffset > 1 && !Character.isJavaIdentifierPart(editorText.charAt(cursorOffset)) && Character
				.isJavaIdentifierPart(editorText.charAt(cursorOffset - 1))))
		{
			cursorOffset--;
		}

		if(Character.isJavaIdentifierPart(editorText.charAt(cursorOffset)))
		{
			int start = cursorOffset;
			int end = cursorOffset;

			while(start > 0 && Character.isJavaIdentifierPart(editorText.charAt(start - 1)))
			{
				start--;
			}

			while(end < editorTextLength && Character.isJavaIdentifierPart(editorText.charAt(end)))
			{
				end++;
			}

			return editorText.subSequence(start, end).toString();
		}
		return null;
	}

	@Override
	public void update(AnActionEvent e)
	{
		Presentation presentation = e.getPresentation();

		presentation.setEnabledAndVisible(findQueryInfo(e) != null);
	}
}
