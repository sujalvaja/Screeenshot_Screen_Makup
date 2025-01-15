package jp.juggler.v2mixup.di.app

import android.app.Application

import dagger.BindsInstance
import dagger.Component
import jp.juggler.v2mixup.di.activity.ActivityComponent
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(modules = [GlideModule::class, AppSubcomponents::class, ImageUtilModule::class])
interface AppComponent {

  @Component.Builder
  interface Builder {
    fun setMainDispatcher(
      @BindsInstance @Named("thread_main") dispatcher: CoroutineDispatcher): Builder

    fun setDefaultDispatcher(
      @BindsInstance @Named("thread_default") dispatcher: CoroutineDispatcher): Builder

    fun setIoDispatcher(
      @BindsInstance @Named("thread_io") dispatcher: CoroutineDispatcher): Builder

    fun setApplication(
      @BindsInstance application: Application): Builder

    fun build(): AppComponent
  }

  fun activityComponent(): ActivityComponent.Factory
  fun editorComponent(): EditorComponent.Factory

}