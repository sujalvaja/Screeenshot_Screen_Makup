package jp.juggler.v2mixup.di.activity

import dagger.Subcomponent
import jp.juggler.v2mixup.ui.MainActivity

@Subcomponent
interface ActivityComponent {

    @Subcomponent.Factory
    interface Factory {
        fun create(): ActivityComponent
    }

    fun inject(mainActivity: MainActivity)
}