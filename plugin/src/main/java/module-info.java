/**
 * @author VISTALL
 * @since 01/06/2023
 */
module consulo.dashdocset {
    requires transitive consulo.dashdocset.api;

    requires consulo.application.api;
    requires consulo.code.editor.api;
    requires consulo.component.api;
    requires consulo.language.api;
    requires consulo.language.editor.api;
    requires consulo.logging.api;
    requires consulo.platform.api;
    requires consulo.process.api;
    requires consulo.project.api;
    requires consulo.ui.api;
    requires consulo.ui.ex.api;
    requires consulo.util.collection;
    requires consulo.util.lang;
}
