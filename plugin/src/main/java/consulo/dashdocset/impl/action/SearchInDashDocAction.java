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

package consulo.dashdocset.impl.action;

import consulo.annotation.access.RequiredReadAction;
import consulo.annotation.component.ActionImpl;
import consulo.annotation.component.ActionParentRef;
import consulo.annotation.component.ActionRef;
import consulo.annotation.component.ActionRefAnchor;
import consulo.application.progress.ProgressIndicator;
import consulo.application.progress.Task;
import consulo.codeEditor.Editor;
import consulo.codeEditor.SelectionModel;
import consulo.dashdocset.impl.DashBundle;
import consulo.dashdocset.DashKeywordProvider;
import consulo.dashdocset.impl.DashProvider;
import consulo.language.Language;
import consulo.language.editor.CommonDataKeys;
import consulo.language.inject.InjectedLanguageManager;
import consulo.language.psi.PsiComment;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.logging.Logger;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.Presentation;
import consulo.util.collection.ArrayUtil;
import consulo.util.lang.Pair;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VISTALL
 * @since 31.12.14
 */
@ActionImpl(id = "SearchInDashDoc", parents = @ActionParentRef(value = @ActionRef(id = "EditorPopupMenu"), anchor = ActionRefAnchor.AFTER, relatedToAction = @ActionRef(id = "$SearchWeb")))
public class SearchInDashDocAction extends AnAction {
    private static final Logger LOGGER = Logger.getInstance(SearchInDashDocAction.class);

    public SearchInDashDocAction() {
        DashProvider provider = DashProvider.getProvider();
        getTemplatePresentation().setText(DashBundle.message("action.search.in.docset", provider.name()));
        getTemplatePresentation().setIcon(provider.getIcon());
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Pair<String, String[]> queryInfo = findQueryInfo(anActionEvent);
        if (queryInfo == null) {
            return;
        }

        final DashProvider provider = DashProvider.getProvider();
        new Task.Modal(anActionEvent.getData(Project.KEY), DashBundle.message("action.searching.in.docset", queryInfo.getFirst(), provider.name()), false) {
            @Override
            public void run(@Nonnull ProgressIndicator indicator) {
                try {
                    provider.open(queryInfo.getSecond(), queryInfo.getFirst());
                }
                catch (Exception e) {
                    LOGGER.warn(e);
                }
            }
        }.queue();
    }

    @Nullable
    @RequiredReadAction
    private Pair<String, String[]> findQueryInfo(AnActionEvent anActionEvent) {
        Editor editor = anActionEvent.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            return null;
        }

        PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
        if (psiFile == null) {
            return null;
        }

        int caretOffset = editor.getCaretModel().getOffset();

        InjectedLanguageManager injectedLanguageManager = InjectedLanguageManager.getInstance(psiFile.getProject());

        Language language = null;
        PsiElement psiElement = injectedLanguageManager.findInjectedElementAt(psiFile, caretOffset);

        if (psiElement == null) {
            psiElement = psiFile.findElementAt(editor.getCaretModel().getOffset());
        }

        if (psiElement != null) {
            language = psiElement.getLanguage();
        }

        String query = null;
        SelectionModel selectionModel = editor.getSelectionModel();
        if (selectionModel.hasSelection()) {
            query = selectionModel.getSelectedText();
        }
        else {
            if (psiElement == null || psiElement instanceof PsiComment) {
                query = getWordAtCursor(editor);
            }
            else {
                query = psiElement.getText();
            }
        }

        if (query == null || language == null) {
            return null;
        }

        List<DashKeywordProvider> dashKeywordProviders = DashKeywordProvider.forLanguage(language);

        List<String> list = new ArrayList<>();
        for (DashKeywordProvider dashKeywordProvider : dashKeywordProviders) {
            dashKeywordProvider.findKeywords(language, psiFile, list::add);
        }

        if (list.isEmpty()) {
            return null;
        }
        return Pair.create(query, ArrayUtil.toStringArray(list));
    }

    @Nullable
    private String getWordAtCursor(Editor editor) {
        CharSequence editorText = editor.getDocument().getCharsSequence();
        int cursorOffset = editor.getCaretModel().getOffset();
        int editorTextLength = editorText.length();

        if (editorTextLength == 0) {
            return null;
        }

        if ((cursorOffset >= editorTextLength) || (cursorOffset > 1 && !Character.isJavaIdentifierPart(editorText.charAt(cursorOffset)) && Character
                .isJavaIdentifierPart(editorText.charAt(cursorOffset - 1)))) {
            cursorOffset--;
        }

        if (Character.isJavaIdentifierPart(editorText.charAt(cursorOffset))) {
            int start = cursorOffset;
            int end = cursorOffset;

            while (start > 0 && Character.isJavaIdentifierPart(editorText.charAt(start - 1))) {
                start--;
            }

            while (end < editorTextLength && Character.isJavaIdentifierPart(editorText.charAt(end))) {
                end++;
            }

            return editorText.subSequence(start, end).toString();
        }
        return null;
    }

    @Override
    public void update(AnActionEvent e) {
        Presentation presentation = e.getPresentation();

        presentation.setEnabledAndVisible(findQueryInfo(e) != null);
    }
}
