package jp.juggler.v2mixup.di.app;



import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import jp.juggler.v2mixup.collageview.util.ImageUtil;

import kotlinx.coroutines.CoroutineDispatcher;

@Module
public abstract class ImageUtilModule {

  @Provides
  static ImageUtil provideImageUtil(
      @Named("thread_main") CoroutineDispatcher mainDispatcher,
      @Named("thread_default") CoroutineDispatcher defaultDispatcher,
      @Named("thread_io") CoroutineDispatcher ioDispatcher
  ) {
    return new ImageUtil(mainDispatcher, defaultDispatcher, ioDispatcher);
  }


}
