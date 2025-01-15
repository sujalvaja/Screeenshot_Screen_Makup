package jp.juggler.v2mixup.ui.editor

import android.net.Uri
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.juggler.screenshotbutton.R
import jp.juggler.v2mixup.collageview.CollageViewFactory
import java.util.LinkedHashMap

class EditorViewModel : ViewModel() {

  val collageIconIdToType = LinkedHashMap<Int, CollageViewFactory.CollageLayoutType>()
  val ratioStringToValue = LinkedHashMap<String, Float>()
  val canvasAspectRatio = MutableLiveData<Float>()
  val imageUris = arrayOfNulls<Uri>(8)

  init {
    initAspectRatioMap()
    initCollageTypeMap()
    canvasAspectRatio.value = 1f
  }

  fun addImageUri(uri: Uri, position: Int) {
    if (position >= imageUris.size) Log.d(TAG, "Cannot save URI. Invalid position.")
    else imageUris[position] = uri
  }

  fun resetImageUris() {
    for (i in imageUris.indices) {
      imageUris[i] = null
    }
  }

  fun setAspectRatio(ratio: Float) {
    canvasAspectRatio.value = ratio
  }

  private fun initCollageTypeMap() {
    collageIconIdToType[R.drawable.ic_collage_2_image_vertical] =
      CollageViewFactory.CollageLayoutType.TWO_IMAGE_VERTICAL

    collageIconIdToType[R.drawable.ic_collage_2_image_horizontal] =
      CollageViewFactory.CollageLayoutType.TWO_IMAGE_HORIZONTAL

    collageIconIdToType[R.drawable.ic_collage_type_3image0] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_0

    collageIconIdToType[R.drawable.ic_collage_type_3image1] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_1

    collageIconIdToType[R.drawable.ic_collage_type_3image2] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_2

    collageIconIdToType[R.drawable.ic_collage_type_3image3] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_3

    collageIconIdToType[R.drawable.ic_collage_type_3image4] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_HORIZONTAL

    collageIconIdToType[R.drawable.ic_collage_type_3image5] =
      CollageViewFactory.CollageLayoutType.THREE_IMAGE_VERTICAL

    collageIconIdToType[R.drawable.ic_collage_type_4image0] =
      CollageViewFactory.CollageLayoutType.FOUR_IMAGE_0

    collageIconIdToType[R.drawable.ic_collage_type_4image1] =
      CollageViewFactory.CollageLayoutType.FOUR_IMAGE_1

    collageIconIdToType[R.drawable.ic_collage_type_4image2] =
      CollageViewFactory.CollageLayoutType.FOUR_IMAGE_2

    collageIconIdToType[R.drawable.ic_collage_type_4image3] =
      CollageViewFactory.CollageLayoutType.FOUR_IMAGE_3

    collageIconIdToType[R.drawable.ic_collage_type_4image4] =
      CollageViewFactory.CollageLayoutType.FOUR_IMAGE_4
  }

  private fun initAspectRatioMap() {
    ratioStringToValue["1:1"] = 1f

    ratioStringToValue["16:9"] = 16f / 9f
    ratioStringToValue["9:16"] = 9f / 16f

    ratioStringToValue["10:8"] = 10f / 8f
    ratioStringToValue["8:10"] = 8f / 10f

    ratioStringToValue["7:5"] = 7f / 5f
    ratioStringToValue["5:7"] = 5f / 7f

    ratioStringToValue["4:3"] = 4f / 3f
    ratioStringToValue["3:4"] = 3f / 4f

    ratioStringToValue["5:3"] = 5f / 3f
    ratioStringToValue["3:5"] = 3f / 5f

    ratioStringToValue["3:2"] = 3f / 2f
    ratioStringToValue["2:3"] = 2f / 3f
  }

  companion object {
    private const val TAG = "EditorViewModel"
  }
}