package jp.juggler.v2mixup.di.editor

import dagger.Subcomponent
import jp.juggler.v2mixup.ui.editor.EditorFragment

@Subcomponent
interface EditorComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): EditorComponent
    }

    fun inject(fragment: EditorFragment)
}