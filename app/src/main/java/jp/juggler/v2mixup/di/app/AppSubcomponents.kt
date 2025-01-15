package jp.juggler.v2mixup.di.app

import jp.juggler.v2mixup.di.editor.EditorComponent
import dagger.Module
import jp.juggler.v2mixup.di.activity.ActivityComponent

@Module(subcomponents = [ActivityComponent::class, EditorComponent::class])
interface AppSubcomponents